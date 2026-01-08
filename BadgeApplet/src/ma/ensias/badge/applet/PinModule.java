package ma.ensias.badge.applet;

import javacard.framework.*;

public class PinModule {
    
    private OwnerPIN pin;
    
    public PinModule() {
        pin = new OwnerPIN((byte) 5, (byte) 9);
        byte[] defaultPin = { 4, 0, 4, 4, 0, 7, 4, 0, 9 };
        pin.update(defaultPin, (short) 0, (byte) 9);
    }

    public boolean verify(byte[] buffer, short offset, byte length) {
        return pin.check(buffer, offset, length);
    }

    public boolean isValidated() {
        return pin.isValidated();
    }

    public void reset() {
        pin.reset();
    }

    public byte getTriesRemaining() {
        return pin.getTriesRemaining();
    }
}