package ma.ensias.badge.client;

import javax.smartcardio.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Arrays;
import java.util.Random;

public class AuthHandler {

    private static final byte[] AES_KEY = { 
        0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 
        0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F 
    };

    public boolean verifyPin(CardChannel channel, String pinStr) {
        try {
            byte[] pinBytes = new byte[pinStr.length()];
            for (int i = 0; i < pinStr.length(); i++) {
                pinBytes[i] = (byte) Character.getNumericValue(pinStr.charAt(i));
            }
            ResponseAPDU resp = channel.transmit(new CommandAPDU(0x00, 0x20, 0x00, 0x00, pinBytes));
            return resp.getSW() == 0x9000;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean authenticate(CardChannel channel) {
        try {
            byte[] challenge = new byte[16];
            new Random().nextBytes(challenge);

            ResponseAPDU resp = channel.transmit(new CommandAPDU(0x00, 0x30, 0x00, 0x00, challenge));
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