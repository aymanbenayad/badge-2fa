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

    public void close() {
        try {
            if (card != null) card.disconnect(false);
        } catch (Exception e) {}
    }
}