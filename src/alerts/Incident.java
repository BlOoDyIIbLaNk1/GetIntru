package alerts;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Incident {

    private String id;
    private String title;
    private String description;
    private List<Alert> alerts;
    private LocalDateTime createdAt;
    private boolean resolved;
    private AlertSeverity severity = AlertSeverity.Low;


    public Incident(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.alerts = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.resolved = false;
    }

    // Ajouter une alerte à l'incident
    public void addAlert(Alert alert) {
        this.alerts.add(alert);
        alert.setStatus(AlertStatus.ESCALATED);
    }

    // Clôturer l'incident
    public void closeIncident() {
        this.resolved = true;
        for (Alert alert : alerts) {
            alert.setStatus(AlertStatus.CLOSED);
        }
    }

    // Getters
    public String getId() {
        return id;
    }
    
    public String getDescription() {
    	return description;
    	}

    public String getTitle() {
        return title;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isResolved() {
        return resolved;
    }

    @Override
    public String toString() {
        return "Incident{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", alertsCount=" + alerts.size() +
                ", resolved=" + resolved +
                '}';
    }
    
    public AlertSeverity getSeverity() { return severity; }
    public void setSeverity(AlertSeverity severity) { this.severity = severity; }
}
