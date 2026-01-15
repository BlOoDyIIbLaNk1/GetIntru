package Security;

import alerts.CorrelationEngine;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class BackupService implements Runnable {  // ← INTERFACE Runnable
    
    private final CorrelationEngine correlationEngine;
    private boolean running = true;  // ← Pour arrêter proprement
    private Thread thread;  // ← TON THREAD NORMAL
    
    public BackupService(CorrelationEngine correlationEngine) {
        this.correlationEngine = correlationEngine;
    }
    
    public void startDailyBackup() {
        thread = new Thread(this);  // ← CRÉE THREAD NORMAL
        thread.setName("Backup-Thread");  // ← NOM CLAIR
        thread.start();  // ← DÉMARRE
        System.out.println(" Backup thread NORMAL démarré");
    }
    
    @Override
    public void run() {  // ← MÉTHODE run() du thread
        while (running) {
            saveIncidents();  // ← SAUVEGARDE
            try {
                Thread.sleep(10000);  // ← ATTEND 10s (pour test)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void stop() {
        running = false;
        thread.interrupt();
        System.out.println("Backup thread arrêté");
    }
    
    private void saveIncidents() {
        try {
            List<?> incidents = correlationEngine.getAllIncidents();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String file = "backup_" + timestamp + ".txt";
            
            try (FileWriter w = new FileWriter(file)) {
                w.write("BACKUP " + LocalDateTime.now() + "\n");
                w.write("Incidents: " + incidents.size() + "\n");
                for (Object i : incidents) {
                    w.write(i.toString() + "\n");
                }
            }
            System.out.println("OK: " + file + " (" + incidents.size() + ")");
        } catch (IOException e) {
            System.err.println("Backup erreur");
        }
    }
}
