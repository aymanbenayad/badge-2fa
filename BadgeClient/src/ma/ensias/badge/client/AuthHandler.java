package ma.ensias.badge.client;

import javax.smartcardio.*;
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class AuthHandler {

	private static final byte[] RSA_MOD = { (byte)0xA2, (byte)0x8A, (byte)0xCE, (byte)0xFA, (byte)0xF9, (byte)0xD7, (byte)0x09, (byte)0xF2, (byte)0x7E, (byte)0x45, (byte)0x1F, (byte)0xC8, (byte)0x71, (byte)0xE2, (byte)0x6B, (byte)0xFB, (byte)0x64, (byte)0x10, (byte)0x48, (byte)0xC4, (byte)0x4A, (byte)0x06, (byte)0xFE, (byte)0xFA, (byte)0x19, (byte)0xE6, (byte)0xFD, (byte)0x4F, (byte)0x61, (byte)0xFB, (byte)0x3E, (byte)0xCA, (byte)0x11, (byte)0x42, (byte)0x52, (byte)0x98, (byte)0xDE, (byte)0x76, (byte)0xEB, (byte)0x48, (byte)0xD0, (byte)0xB4, (byte)0xDD, (byte)0x2F, (byte)0xDA, (byte)0x09, (byte)0x25, (byte)0x2D, (byte)0xE2, (byte)0x73, (byte)0x50, (byte)0xB5, (byte)0x44, (byte)0xC5, (byte)0xC2, (byte)0x8B, (byte)0x8A, (byte)0x07, (byte)0x52, (byte)0x0F, (byte)0x8F, (byte)0x20, (byte)0xEF, (byte)0xFD, (byte)0x58, (byte)0x5D, (byte)0xB4, (byte)0x13, (byte)0x35, (byte)0x6C, (byte)0x53, (byte)0x43, (byte)0x90, (byte)0x03, (byte)0xAD, (byte)0x91, (byte)0xB1, (byte)0x1D, (byte)0x7B, (byte)0xF3, (byte)0x6F, (byte)0x6C, (byte)0x75, (byte)0x10, (byte)0x22, (byte)0xD0, (byte)0x95, (byte)0x66, (byte)0x9C, (byte)0xA3, (byte)0xA7, (byte)0x75, (byte)0xFA, (byte)0xE3, (byte)0xFF, (byte)0xE6, (byte)0xBF, (byte)0x3D, (byte)0x8B, (byte)0x7F, (byte)0x50, (byte)0xB5, (byte)0x0C, (byte)0xC7, (byte)0x10, (byte)0xBF, (byte)0xD1, (byte)0x3D, (byte)0x36, (byte)0x58, (byte)0x06, (byte)0xE6, (byte)0x3C, (byte)0x71, (byte)0xB9, (byte)0xDF, (byte)0x53, (byte)0x7B, (byte)0xBB, (byte)0xB6, (byte)0x74, (byte)0x52, (byte)0x28, (byte)0x0B, (byte)0xD2, (byte)0x32, (byte)0x4C, (byte)0xD7 };
	private static final byte[] RSA_EXP = { (byte)0x01, (byte)0x00, (byte)0x01 };

    private byte[] getTimeBytes() {
        long now = System.currentTimeMillis();
        byte[] time = new byte[8];
        for (int i = 7; i >= 0; i--) {
            time[i] = (byte)(now & 0xFF);
            now >>= 8;
        }
        return time;
    }

    public boolean verifyPin(CardChannel channel, String pinStr) {
        try {
            byte[] time = getTimeBytes();
            byte[] pinBytes = new byte[pinStr.length()];
            for (int i = 0; i < pinStr.length(); i++) {
                pinBytes[i] = (byte) Character.getNumericValue(pinStr.charAt(i));
            }
            byte[] data = new byte[8 + pinBytes.length];
            System.arraycopy(time, 0, data, 0, 8);
            System.arraycopy(pinBytes, 0, data, 8, pinBytes.length);
            ResponseAPDU resp = channel.transmit(new CommandAPDU(0x00, 0x20, 0x00, 0x00, data));
            return resp.getSW() == 0x9000;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean authenticate(CardChannel channel, String pinStr) {
        try {
            byte[] challenge = new byte[16];
            new Random().nextBytes(challenge);

            KeyFactory kf = KeyFactory.getInstance("RSA");
            PublicKey publicKey = kf.generatePublic(new RSAPublicKeySpec(new BigInteger(1, RSA_MOD), new BigInteger(1, RSA_EXP)));

            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedChallenge = rsaCipher.doFinal(challenge);

            byte[] payload = new byte[136];
            System.arraycopy(encryptedChallenge, 0, payload, 0, 128);
            System.arraycopy(getTimeBytes(), 0, payload, 128, 8);

            ResponseAPDU resp = channel.transmit(new CommandAPDU(0x00, 0x30, 0x00, 0x00, payload));
            if (resp.getSW() != 0x9000) return false;

            return Arrays.equals(challenge, resp.getData());
        } catch (Exception e) {
            return false;
        }
    }
}