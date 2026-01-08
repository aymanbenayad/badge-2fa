package ma.ensias.badge.client;

import javax.smartcardio.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class AuthHandler {

    private static final byte[] RSA_MOD = { (byte)0xD7, (byte)0xA8, (byte)0xC3, (byte)0xFC, (byte)0xAD, (byte)0xEF, (byte)0x31, (byte)0x70, (byte)0x35, (byte)0x1D, (byte)0x41, (byte)0x60, (byte)0x14, (byte)0x7B, (byte)0x37, (byte)0x6B, (byte)0x8F, (byte)0x8D, (byte)0xC0, (byte)0x33, (byte)0x94, (byte)0xED, (byte)0x5A, (byte)0x77, (byte)0x36, (byte)0x6A, (byte)0xF4, (byte)0xC4, (byte)0xFD, (byte)0xE3, (byte)0xAF, (byte)0xBE, (byte)0xD3, (byte)0x80, (byte)0x15, (byte)0xC0, (byte)0x3F, (byte)0xCD, (byte)0x31, (byte)0xDA, (byte)0xFF, (byte)0x1E, (byte)0xD0, (byte)0xE8, (byte)0x67, (byte)0x08, (byte)0x1F, (byte)0xE1, (byte)0x52, (byte)0x86, (byte)0xED, (byte)0x84, (byte)0xF7, (byte)0xA7, (byte)0x9B, (byte)0x8C, (byte)0x3F, (byte)0x2A, (byte)0x1B, (byte)0x28, (byte)0x01, (byte)0x68, (byte)0xC3, (byte)0x01, (byte)0x64, (byte)0x72, (byte)0x8D, (byte)0xF5, (byte)0xFB, (byte)0x08, (byte)0xB9, (byte)0xF2, (byte)0x7D, (byte)0xD2, (byte)0xAE, (byte)0xEC, (byte)0x81, (byte)0xE9, (byte)0xFD, (byte)0x8E, (byte)0x20, (byte)0x3A, (byte)0x21, (byte)0x65, (byte)0xC6, (byte)0x64, (byte)0xFE, (byte)0x27, (byte)0x07, (byte)0x71, (byte)0x24, (byte)0x6E, (byte)0x62, (byte)0xA8, (byte)0x78, (byte)0xE3, (byte)0xAA, (byte)0x8A, (byte)0xC1, (byte)0x73, (byte)0xA8, (byte)0xC1, (byte)0x7E, (byte)0xE7, (byte)0x3E, (byte)0xE4, (byte)0x68, (byte)0xE2, (byte)0x30, (byte)0xF5, (byte)0xCC, (byte)0x7A, (byte)0x38, (byte)0x4C, (byte)0x80, (byte)0x4A, (byte)0x4C, (byte)0x95, (byte)0x48, (byte)0xBE, (byte)0x01, (byte)0xFA, (byte)0x12, (byte)0xD9, (byte)0xA9, (byte)0x9C, (byte)0xC5, (byte)0x6F };
    private static final byte[] ENC_D = { (byte)0x90, (byte)0x4F, (byte)0x80, (byte)0x11, (byte)0x4E, (byte)0xBB, (byte)0x61, (byte)0xCB, (byte)0xA4, (byte)0x48, (byte)0xC9, (byte)0xAB, (byte)0xD9, (byte)0x32, (byte)0x63, (byte)0x73, (byte)0x47, (byte)0x2A, (byte)0x52, (byte)0xF7, (byte)0x0A, (byte)0x8A, (byte)0xA6, (byte)0x27, (byte)0x4B, (byte)0xCE, (byte)0x57, (byte)0x4D, (byte)0xFA, (byte)0xA0, (byte)0x27, (byte)0x67, (byte)0xD7, (byte)0xB7, (byte)0x0E, (byte)0x5A, (byte)0x83, (byte)0x8E, (byte)0x30, (byte)0x39, (byte)0x29, (byte)0xF1, (byte)0x81, (byte)0xCB, (byte)0x21, (byte)0xAC, (byte)0x4A, (byte)0x9F, (byte)0x38, (byte)0xA6, (byte)0x7E, (byte)0x0C, (byte)0x22, (byte)0xB0, (byte)0xE1, (byte)0x20, (byte)0x7A, (byte)0x6F, (byte)0x30, (byte)0x6D, (byte)0xE4, (byte)0x30, (byte)0x2C, (byte)0xA3, (byte)0x73, (byte)0x14, (byte)0x69, (byte)0x11, (byte)0x9A, (byte)0xDD, (byte)0x4B, (byte)0x02, (byte)0xAD, (byte)0x7D, (byte)0xB5, (byte)0xC5, (byte)0xCB, (byte)0xB4, (byte)0xC9, (byte)0xAA, (byte)0xEA, (byte)0xD3, (byte)0x5B, (byte)0x6D, (byte)0x09, (byte)0x9C, (byte)0xA6, (byte)0x6C, (byte)0x4C, (byte)0xFB, (byte)0x5E, (byte)0xBF, (byte)0x53, (byte)0xAA, (byte)0x8F, (byte)0xC7, (byte)0x14, (byte)0x0F, (byte)0xD0, (byte)0x2C, (byte)0x6B, (byte)0x2E, (byte)0x73, (byte)0xB2, (byte)0xAB, (byte)0x1B, (byte)0x35, (byte)0x33, (byte)0xDC, (byte)0x18, (byte)0x3B, (byte)0xF3, (byte)0xA4, (byte)0xAE, (byte)0x91, (byte)0x0F, (byte)0xD0, (byte)0xB9, (byte)0xE8, (byte)0xD8, (byte)0xB4, (byte)0x0B, (byte)0x1E, (byte)0xE1, (byte)0xCD, (byte)0x17, (byte)0x62, (byte)0xF8, (byte)0x47, (byte)0x8C, (byte)0xD7, (byte)0x5A, (byte)0xB5, (byte)0x84, (byte)0x44, (byte)0x2B, (byte)0x95, (byte)0x16, (byte)0x65, (byte)0xAD, (byte)0xBE, (byte)0xF4, (byte)0x82, (byte)0x82, (byte)0x65, (byte)0xB3, (byte)0xB6, (byte)0xCB, (byte)0x41, (byte)0x88, (byte)0x37, (byte)0x0C, (byte)0xB4, (byte)0xD1, (byte)0x2C, (byte)0xF7, (byte)0x42, (byte)0xF6, (byte)0x67, (byte)0x96 };

    private byte[] getTimeBytes() {
        long now = System.currentTimeMillis();
        byte[] time = new byte[8];
        for (int i = 7; i >= 0; i--) {
            time[i] = (byte)(now & 0xFF);
            now >>= 8;
        }
        return time;
    }

    private byte[] decryptComponent(byte[] encryptedData, byte[] aesKey) throws Exception {
        byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);
        byte[] data = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new IvParameterSpec(iv));
        return cipher.doFinal(data);
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
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] aesKey = Arrays.copyOfRange(sha256.digest(pinStr.getBytes()), 0, 16);

            byte[] d = decryptComponent(ENC_D, aesKey);
            
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = kf.generatePrivate(new RSAPrivateKeySpec(new BigInteger(1, RSA_MOD), new BigInteger(1, d)));

            byte[] challenge = new byte[16];
            new Random().nextBytes(challenge);
            byte[] payload = new byte[24];
            System.arraycopy(challenge, 0, payload, 0, 16);
            System.arraycopy(getTimeBytes(), 0, payload, 16, 8);

            ResponseAPDU resp = channel.transmit(new CommandAPDU(0x00, 0x30, 0x00, 0x00, payload));
            if (resp.getSW() != 0x9000) return false;

            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            
            return Arrays.equals(challenge, rsaCipher.doFinal(resp.getData()));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}