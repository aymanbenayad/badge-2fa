package ma.ensias.badge.client;

import javax.smartcardio.*;
import java.util.Date;

public class LogManager {

    public void readLogs(CardChannel channel) {
        try {
            ResponseAPDU resp = channel.transmit(new CommandAPDU(0x00, 0x40, 0x00, 0x00));
            if (resp.getSW() == 0x9000) {
                byte[] data = resp.getData();
                System.out.println("--- HISTORIQUE D'ACCES ---");
                for (int i = 0; i < data.length - 8; i += 9) {
                    long timestamp = 0;
                    for (int j = 0; j < 8; j++) {
                        timestamp = (timestamp << 8) + (data[i + j] & 0xFF);
                    }
                    byte status = data[i + 8];
                    
                    if (timestamp != 0) {
                        String statusStr = (status == (byte)0xAA) ? "ACCES ACCORDE" : "ECHEC D'ACCES";
                        System.out.println(new Date(timestamp) + " : " + statusStr);
                    }
                }
            } else {
                System.out.println("Erreur lecture logs: " + Integer.toHexString(resp.getSW()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}