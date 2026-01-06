package ma.ensias.badge.applet;

import javacard.framework.*;

public class TraceModule {
    
    private byte[] logStorage;
    private short logIndex;
    private static final short MAX_SIZE = 1017;
    private static final short ENTRY_SIZE = 9;

    public TraceModule() {
        logStorage = new byte[MAX_SIZE];
        logIndex = 0;
    }

    public void addLog(byte[] timeData, short timeOffset, boolean isSuccess) {
        if ((short)(logIndex + ENTRY_SIZE) > MAX_SIZE) {
            logIndex = 0;
        }
        Util.arrayCopy(timeData, timeOffset, logStorage, logIndex, (short) 8);
        logStorage[(short)(logIndex + 8)] = isSuccess ? (byte)0xAA : (byte)0xEE;
        logIndex += ENTRY_SIZE;
    }

    public short retrieveLogs(byte[] outBuffer, short outOffset) {
        Util.arrayCopy(logStorage, (short)0, outBuffer, outOffset, logIndex);
        return logIndex;
    }
    
    public byte[] getBuffer() {
        return logStorage;
    }
    
    public short getCurrentSize() {
        return logIndex;
    }
}