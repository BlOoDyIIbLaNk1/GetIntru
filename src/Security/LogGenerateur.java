package Security;

import java.sql.SQLException;
import java.util.Random;

public class LogGenerateur implements Runnable {

    private final LogPipelineService pipeline;
    private final Random random = new Random();

    public LogGenerateur(LogPipelineService pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(10000);

                String ip = "192.168.1." + (random.nextInt(5) + 1);

                String eventType;
                String message;

                int r = random.nextInt(4);
                switch (r) {
                    case 0 -> { eventType = "LOGIN_FAILED"; message = "Failed SSH login attempt"; }
                    case 1 -> { eventType = "LOGIN_SUCCESS"; message = "User logged in successfully"; }
                    case 2 -> { eventType = "PORT_SCAN"; message = "Multiple ports scanned"; }
                    default -> { eventType = "MALWARE"; message = "Malware signature detected"; }
                }

                LogEntry log = new LogEntry(ip, eventType, message);
                pipeline.handleLog(log);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // bonne pratique: restaurer l'interruption
                break;
            } catch (SQLException e) {
                e.printStackTrace();
                // si la DB tombe, soit break, soit continue (au choix)
                break;
            }
        }
    }
}
