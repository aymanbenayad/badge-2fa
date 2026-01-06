package ma.ensias.badge.client;

import javax.smartcardio.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Arrays;
import java.util.Random;

public class AuthHandler {

    private static final byte[] AES_KEY = { 
        (byte)0x4A, (byte)0xE6, (byte)0x1B, (byte)0x9D, 
        (byte)0x55, (byte)0x8C, (byte)0x2F, (byte)0x33, 
        (byte)0xA1, (byte)0x08, (byte)0x77, (byte)0xDE, 
        (byte)0xBC, (byte)0x92, (byte)0xF0, (byte)0x14 
    };

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

    public boolean authenticate(CardChannel channel) {
        try {
            byte[] challenge = new byte[16];
            new Random().nextBytes(challenge);
            byte[] time = getTimeBytes();

            byte[] payload = new byte[24];
            System.arraycopy(challenge, 0, payload, 0, 16);
            System.arraycopy(time, 0, payload, 16, 8);

            ResponseAPDU resp = channel.transmit(new CommandAPDU(0x00, 0x30, 0x00, 0x00, payload));
            if (resp.getSW() != 0x9000) return false;

            byte[] cardResponse = resp.getData();
            
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(AES_KEY, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(new byte[16]));
            
            byte[] expected = cipher.doFinal(challenge);
            
            return Arrays.equals(expected, cardResponse);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}