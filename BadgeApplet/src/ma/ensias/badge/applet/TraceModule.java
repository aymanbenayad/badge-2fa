package ma.ensias.badge.applet;

import javacard.framework.*;

public class TraceModule {
    
    private byte[] logStorage;
    private short logIndex;

    public TraceModule() {
        logStorage = new byte[256];
        logIndex = 0;
    }

    public void addLog(byte[] data, short offset, short length) {
        if ((short)(logIndex + length) > 256) {
            logIndex = 0;
        }
        Util.arrayCopy(data, offset, logStorage, logIndex, length);
        logIndex += length;
    }

    public short retrieveLogs(byte[] outBuffer, short outOffset) {
        Util.arrayCopy(logStorage, (short)0, outBuffer, outOffset, logIndex);
        return logIndex;
    }
}