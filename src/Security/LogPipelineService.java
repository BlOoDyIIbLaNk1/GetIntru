package Security;

import alerts.Alert;
import alerts.CorrelationEngine;
import alerts.Incident;
import databse.AlertDAO;
import databse.IncidentDAO;

import java.sql.SQLException;

public class LogPipelineService {

    private final DetectionEngine detectionEngine;
    private final CorrelationEngine correlationEngine;
    private final IncidentDAO incidentDAO;
    private final AlertDAO alertDAO;

    public LogPipelineService(DetectionEngine detectionEngine,
                              CorrelationEngine correlationEngine,
                              IncidentDAO incidentDAO,
                              AlertDAO alertDAO) {
        this.detectionEngine = detectionEngine;
        this.correlationEngine = correlationEngine;
        this.incidentDAO = incidentDAO;
        this.alertDAO = alertDAO;
    }

    public void handleLog(LogEntry log) throws SQLException {
        Alert alert = detectionEngine.processLog(log);
        if (alert == null) return;

        Incident incident = correlationEngine.ingest(alert);

        // Si incident nouveau => 1ère alerte dans la liste
        if (incident.getAlerts().size() == 1) {
            incidentDAO.insertIncident(incident);
        } else {
            incidentDAO.updateSeverity(incident.getId(), incident.getSeverity());
        }

        alertDAO.insertAlert(alert, incident.getId());
    }
}
