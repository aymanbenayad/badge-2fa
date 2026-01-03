package ma.ensias.badge.client;

import java.net.InetSocketAddress;
import java.util.Arrays;
import javax.smartcardio.*;

public class ProtocolManager {

    private static final byte[] APPLET_AID = { (byte)0x59, (byte)0x59, (byte)0x59, (byte)0x59, (byte)0x59, (byte)0x00 };
    private Card card;

    public CardChannel connect() throws Exception {
        TerminalFactory tf = TerminalFactory.getInstance("SocketCardTerminalFactoryType",
                Arrays.asList(new InetSocketAddress("localhost", 9025)),
                "SocketCardTerminalProvider");
        CardTerminal ct = tf.terminals().list().get(0);
        card = ct.connect("T=1");
        return card.getBasicChannel();
    }

    public void selectApplet(CardChannel channel) throws Exception {
        CommandAPDU cmd = new CommandAPDU(0x00, 0xA4, 0x04, 0x00, APPLET_AID);
        ResponseAPDU resp = channel.transmit(cmd);
        if (resp.getSW() != 0x9000) {
            throw new Exception("Applet selection failed");
        }
    }

    public ResponseAPDU send(CardChannel channel, int cla, int ins, int p1, int p2, byte[] data) throws Exception {
        CommandAPDU cmd;
        if (data != null) {
            cmd = new CommandAPDU(cla, ins, p1, p2, data);
        } else {
            cmd = new CommandAPDU(cla, ins, p1, p2);
        }
        return channel.transmit(cmd);
    }

    public void close() {
        try {
            if (card != null) card.disconnect(false);
        } catch (Exception e) {}
    }
}