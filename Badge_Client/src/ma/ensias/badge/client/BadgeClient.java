package ma.ensias.badge.client;

import javax.smartcardio.*;
import java.util.Scanner;

public class BadgeClient {

    public static void main(String[] args) {
        ProtocolManager protocol = new ProtocolManager();
        AuthHandler auth = new AuthHandler();
        LogManager logger = new LogManager();
        Scanner scanner = new Scanner(System.in);
        CardChannel channel = null;

        try {
            channel = protocol.connect();
            protocol.selectApplet(channel);
            System.out.println("Connected to Badge System.");

            boolean running = true;
            while (running) {
                System.out.println("\n1. Login (PIN + AES)");
                System.out.println("2. Read Logs");
                System.out.println("3. Exit");
                System.out.print("> ");

                String input = scanner.nextLine();

                switch (input) {
                    case "1":
                        System.out.print("Enter PIN: ");
                        String pin = scanner.nextLine();
                        if (auth.verifyPin(channel, pin)) {
                            System.out.println("PIN Verified. Performing 2FA...");
                            if (auth.authenticate(channel)) {
                                System.out.println("ACCESS GRANTED. Door Open.");
                            } else {
                                System.out.println("ACCESS DENIED. Crypto Fail.");
                            }
                        } else {
                            System.out.println("ACCESS DENIED. Invalid PIN.");
                        }
                        break;
                    case "2":
                        logger.readLogs(channel);
                        break;
                    case "3":
                        running = false;
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
            try {
                if (channel != null) {
                    channel.getCard().disconnect(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}