package alerts;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Regroupe les alertes en incidents (corrélation) et maintient l'état en mémoire.
 */
public class CorrelationEngine {

    /** Si null => pas de fenêtre de temps (on regroupe toujours). */
    private final Duration correlationWindow;

    /** Un incident "actif" par clé (dernier incident créé pour cette clé). */
    private final Map<CorrelationKey, Incident> activeByKey = new HashMap<>();

    /** Historique complet (tous les incidents). */
    private final List<Incident> allIncidents = new ArrayList<>();

    public CorrelationEngine() {
        this(null);
    }

    public CorrelationEngine(Duration correlationWindow) {
        this.correlationWindow = correlationWindow;
    }

    /** Point d'entrée: ajoute une alerte et retourne l'incident affecté (créé ou existant). */
    public Incident ingest(Alert alert) {
        if (alert == null) return null;

        CorrelationKey key = new CorrelationKey(alert.getSourceIP(), alert.getType());
        Incident current = activeByKey.get(key);

        // 1) Si pas d'incident actif => créer
        if (current == null) {
            Incident created = createIncidentFor(alert, key);
            attachAlert(created, alert);
            activeByKey.put(key, created);
            allIncidents.add(created);
            return created;
        }

        // 2) Si fenêtre activée, vérifier si on peut encore corréler dedans
        if (correlationWindow != null && isOutsideWindow(current, alert.getDate())) {
            Incident created = createIncidentFor(alert, key);
            attachAlert(created, alert);
            activeByKey.put(key, created);
            allIncidents.add(created);
            return created;
        }

        // 3) Sinon on ajoute à l'incident courant
        attachAlert(current, alert);
        return current;
    }

    /** Si l'incident est résolu, on ne l'utilise plus comme "actif" (optionnel). */
    public void markResolved(Incident incident) {
        if (incident == null) return;
        incident.closeIncident();

        // Nettoyage des "actifs" : si une entrée pointe sur cet incident, on la retire
        activeByKey.entrySet().removeIf(e -> e.getValue() == incident);
    }

    public List<Incident> getAllIncidents() {
        return Collections.unmodifiableList(allIncidents);
    }

    public Collection<Incident> getActiveIncidents() {
        return Collections.unmodifiableCollection(activeByKey.values());
    }

    // ----------------- helpers -----------------

    private Incident createIncidentFor(Alert alert, CorrelationKey key) {
        String id = "INC-" + System.currentTimeMillis();
        String title = "Incident: " + alert.getType() + " from " + alert.getSourceIP();
        String description = "Auto-correlated incident for key=" + key;
        return new Incident(id, title, description); // si tu veux correlationKey dans Incident, adapte constructeur
    }

    private void attachAlert(Incident incident, Alert alert) {
        // On escalade l'alerte car elle entre dans un incident
        incident.addAlert(alert);

        // Recalcule severity de l'incident (max)
        recomputeIncidentSeverity(incident);
    }

    private boolean isOutsideWindow(Incident incident, LocalDateTime alertTime) {
        // Ici on se base sur createdAt. Si tu veux updatedAt, adapte.
        LocalDateTime from = incident.getCreatedAt();
        Duration delta = Duration.between(from, alertTime);
        return delta.compareTo(correlationWindow) > 0; // > fenêtre => trop tard
    }

    private void recomputeIncidentSeverity(Incident incident) {
    	AlertSeverity max = incident.getAlerts().stream()
    		    .map(Alert::getSeverity)
    		    .max(Comparator.comparingInt(AlertSeverity::ordinal))
    		    .orElse(AlertSeverity.Low);

        incident.setSeverity(max);
    }
}
