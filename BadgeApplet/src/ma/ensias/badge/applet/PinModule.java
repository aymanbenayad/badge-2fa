package ma.ensias.badge.applet;

import javacard.framework.*;

public class PinModule {
    
    private OwnerPIN pin;
    
    public PinModule() {
        pin = new OwnerPIN((byte) 3, (byte) 4);
        byte[] defaultPin = { 0x00, 0x04, 0x00, 0x07 };
        pin.update(defaultPin, (short) 0, (byte) 4);
    }

    public boolean verify(byte[] buffer, short offset, byte length) {
        return pin.check(buffer, offset, length);
    }

    public boolean isValidated() {
        return pin.isValidated();
    }

    public byte getTriesRemaining() {
        return pin.getTriesRemaining();
    }

    public void reset() {
        pin.reset();
    }
}