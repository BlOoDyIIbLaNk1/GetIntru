package alerts;

import java.time.LocalDateTime;


public class Alert {

    private String id;
    private String type;
    private AlertSeverity severity;
    private String sourceIP;
    private String destinationIP;
    private LocalDateTime date;
    private AlertStatus status;

    public Alert() {}

    // Alerte générée automatiquement par le système (IDS)
    public Alert(String id, String type, AlertSeverity severity,
                 String sourceIP, String destinationIP) {

        this.id = id;
        this.type = type;
        this.severity = severity;
        this.sourceIP = sourceIP;
        this.destinationIP = destinationIP;
        this.date = LocalDateTime.now();       // ⬅️ auto
        this.status = AlertStatus.NEW;         // ⬅️ auto
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public AlertStatus getStatus() {
        return status;
    }

    // Seul le status peut changer
    public void setStatus(AlertStatus status) {
        this.status = status;
    }
    public void updateSeverity(AlertSeverity newSeverity) {
        if (this.status != AlertStatus.NEW) {
            this.severity = newSeverity;
        }}
    }


  

