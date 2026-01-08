package ma.ensias.badge.applet;

import javacard.framework.*;
import javacard.security.*;

public class BadgeApplet extends Applet {

    private static final byte INS_VERIFY = 0x20;
    private static final byte INS_AUTH   = 0x30;
    private static final byte INS_LOGS   = 0x40;

    private PinModule pinModule;
    private CryptoModule cryptoModule;
    private SessionModule sessionModule;
    private TraceModule traceModule;
    private MessageDigest sha256;

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new BadgeApplet().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
    }

    protected BadgeApplet() {
        pinModule = new PinModule();
        cryptoModule = new CryptoModule();
        sessionModule = new SessionModule();
        traceModule = new TraceModule();
        sha256 = MessageDigest.getInstance(MessageDigest.ALG_SHA_256, false);
    }

    public void deselect() {
        pinModule.reset();
        sessionModule.closeSession();
        cryptoModule.clearKey();
    }

    public void process(APDU apdu) {
        if (selectingApplet()) return;
        byte[] buffer = apdu.getBuffer();
        byte ins = buffer[ISO7816.OFFSET_INS];
        switch (ins) {
            case INS_VERIFY: handleVerify(apdu); break;
            case INS_AUTH: handleAuth(apdu); break;
            case INS_LOGS: handleGetLogs(apdu); break;
            default: ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    private void handleVerify(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte len = (byte) apdu.setIncomingAndReceive();
        if (len < 8) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        if (pinModule.getTriesRemaining() == 0) ISOException.throwIt((short) 0x6983);
        if (pinModule.verify(buffer, (short)(ISO7816.OFFSET_CDATA + 8), (byte)(len - 8))) {
            sha256.doFinal(buffer, (short)(ISO7816.OFFSET_CDATA + 8), (short)(len - 8), buffer, (short)0);
            cryptoModule.loadKey(buffer, (short)0);
            sessionModule.startSession();
        } else {
            traceModule.addLog(buffer, ISO7816.OFFSET_CDATA, false);
            sessionModule.closeSession();
            ISOException.throwIt((short) (0x63C0 | pinModule.getTriesRemaining()));
        }
    }

    private void handleAuth(APDU apdu) {
        if (!pinModule.isValidated() || !sessionModule.isSessionActive()) {
            ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }
        byte[] buffer = apdu.getBuffer();
        short len = apdu.setIncomingAndReceive();
        if (len < 136) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

        short outLen = cryptoModule.decryptChallenge(buffer, ISO7816.OFFSET_CDATA, (short) 128, buffer, (short) 0);
        traceModule.addLog(buffer, (short)(ISO7816.OFFSET_CDATA + 128), true);
        apdu.setOutgoingAndSend((short) 0, outLen);
    }

    private void handleGetLogs(APDU apdu) {
        if (!sessionModule.isSessionActive()) ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        byte[] logBuffer = traceModule.getBuffer();
        short logSize = traceModule.getCurrentSize();
        apdu.setOutgoing();
        apdu.setOutgoingLength(logSize);
        apdu.sendBytesLong(logBuffer, (short) 0, logSize);
    }
}