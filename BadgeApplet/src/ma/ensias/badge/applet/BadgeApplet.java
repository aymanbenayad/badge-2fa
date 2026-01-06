package ma.ensias.badge.applet;

import javacard.framework.*;

public class BadgeApplet extends Applet {

    private static final byte INS_VERIFY = 0x20;
    private static final byte INS_AUTH   = 0x30;
    private static final byte INS_LOGS   = 0x40;

    private PinModule pinModule;
    private CryptoModule cryptoModule;
    private SessionModule sessionModule;
    private TraceModule traceModule;

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new BadgeApplet().register(bArray, (short) (bOffset + 1), bArray[bOffset]);
    }

    protected BadgeApplet() {
        pinModule = new PinModule();
        cryptoModule = new CryptoModule();
        sessionModule = new SessionModule();
        traceModule = new TraceModule();
    }

    public void process(APDU apdu) {
        if (selectingApplet()) {
            return;
        }

        byte[] buffer = apdu.getBuffer();
        byte ins = buffer[ISO7816.OFFSET_INS];

        switch (ins) {
            case INS_VERIFY:
                handleVerify(apdu);
                break;
            case INS_AUTH:
                handleAuth(apdu);
                break;
            case INS_LOGS:
                handleGetLogs(apdu);
                break;
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }

    private void handleVerify(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        byte len = (byte) apdu.setIncomingAndReceive();
        
        if (pinModule.verify(buffer, ISO7816.OFFSET_CDATA, len)) {
            sessionModule.startSession((short) 0);
        } else {
            sessionModule.closeSession();
            ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
        }
    }

    private void handleAuth(APDU apdu) {
        if (!pinModule.isValidated() || !sessionModule.isSessionActive()) {
            ISOException.throwIt(ISO7816.SW_CONDITIONS_NOT_SATISFIED);
        }

        byte[] buffer = apdu.getBuffer();
        short len = apdu.setIncomingAndReceive();
        
        short outLen = cryptoModule.encryptChallenge(buffer, ISO7816.OFFSET_CDATA, len, buffer, (short) 0);
        
        traceModule.addLog(buffer, (short) 0, outLen);
        
        apdu.setOutgoingAndSend((short) 0, outLen);
    }

    private void handleGetLogs(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short len = traceModule.retrieveLogs(buffer, (short) 0);
        apdu.setOutgoingAndSend((short) 0, len);
    }
}