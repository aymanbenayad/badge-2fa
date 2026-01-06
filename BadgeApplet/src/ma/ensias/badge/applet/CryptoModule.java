package ma.ensias.badge.applet;

import javacard.framework.*;
import javacard.security.*;
import javacardx.crypto.*;

public class CryptoModule {
    
    private AESKey key;
    private Cipher cipher;
    
    public CryptoModule() {
        byte[] keyData = { 
            (byte)0x4A, (byte)0xE6, (byte)0x1B, (byte)0x9D, 
            (byte)0x55, (byte)0x8C, (byte)0x2F, (byte)0x33, 
            (byte)0xA1, (byte)0x08, (byte)0x77, (byte)0xDE, 
            (byte)0xBC, (byte)0x92, (byte)0xF0, (byte)0x14 
        };
        key = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
        key.setKey(keyData, (short) 0);
        cipher = Cipher.getInstance(Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, false);
    }

    public short encryptChallenge(byte[] inBuffer, short inOffset, short inLength, byte[] outBuffer, short outOffset) {
        cipher.init(key, Cipher.MODE_ENCRYPT);
        return cipher.doFinal(inBuffer, inOffset, inLength, outBuffer, outOffset);
    }
}