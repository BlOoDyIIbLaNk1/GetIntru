package Security;

import java.util.Random;

public class LogGenerateur implements Runnable {

    private DetectionEngine engine;
    private Random random = new Random();

    public LogGenerateur(DetectionEngine engine) {
        this.engine = engine;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(2000); // toutes les 2 secondes

                String ip = "192.168.1." + (random.nextInt(50) + 1);
                String eventType;
                String message;

                int r = random.nextInt(4);

                switch (r) {
                    case 0:
                        eventType = "LOGIN_FAILED";
                        message = "Failed SSH login attempt";
                        break;
                    case 1:
                        eventType = "LOGIN_SUCCESS";
                        message = "User logged in successfully";
                        break;
                    case 2:
                        eventType = "PORT_SCAN";
                        message = "Multiple ports scanned";
                        break;
                    default:
                        eventType = "MALWARE";
                        message = "Malware signature detected";
                        break;
                }

                LogEntry log = new LogEntry(ip, eventType, message);
                engine.processLog(log);

            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
