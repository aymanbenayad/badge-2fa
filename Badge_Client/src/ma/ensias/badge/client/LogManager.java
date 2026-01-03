package ma.ensias.badge.client;

import javax.smartcardio.*;

public class LogManager {

    public void readLogs(CardChannel channel) {
        try {
            ResponseAPDU resp = channel.transmit(new CommandAPDU(0x00, 0x40, 0x00, 0x00));
            if (resp.getSW() == 0x9000) {
                byte[] data = resp.getData();
                StringBuilder sb = new StringBuilder();
                for (byte b : data) {
                    sb.append(String.format("%02X ", b));
                }
                System.out.println("Raw Logs: " + sb.toString());
            } else {
                System.out.println("Failed to read logs: " + Integer.toHexString(resp.getSW()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}