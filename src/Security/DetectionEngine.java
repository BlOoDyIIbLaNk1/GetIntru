package Security;

import alerts.Alert;
import alerts.AlertSeverity;
import alerts.AlertStatus;

import java.util.HashMap;
import java.util.Map;

public class DetectionEngine {

    private Blacklist blacklist;
    private Map<String, Integer> failedLoginCounter;

    public DetectionEngine() {
        this.blacklist = new Blacklist();
        this.failedLoginCounter = new HashMap<>();
    }

    // Analyse un log entrant
    public Alert processLog(LogEntry log) {

        String ip = log.getSourceIP();

        // Si IP déjà blacklistée
        if (blacklist.isBlocked(ip)) {
            return createAlert("BLACKLISTED_IP",
                    AlertSeverity.Critical, ip);
        }

        switch (log.getEventType()) {

            case "LOGIN_FAILED":
                failedLoginCounter.put(ip,
                        failedLoginCounter.getOrDefault(ip, 0) + 1);

                if (failedLoginCounter.get(ip) >= 3) {
                    blacklist.blockIP(ip,
                            "Brute force detected",
                            "AUTO");

                    return createAlert("BRUTE_FORCE",
                            AlertSeverity.Medium, ip);
                }
                break;

            case "PORT_SCAN":
                blacklist.blockIP(ip,
                        "Port scanning activity",
                        "AUTO");

                return createAlert("PORT_SCAN",
                        AlertSeverity.High, ip);

            case "MALWARE":
                blacklist.blockIP(ip,
                        "Malware detected",
                        "AUTO");

                return createAlert("MALWARE",
                        AlertSeverity.Critical, ip);

            default:
                break;
        }

        return null; // aucun problème détecté
    }

    // Création centralisée d’alerte
    private Alert createAlert(String type,
                              AlertSeverity severity,
                              String sourceIP) {

        return new Alert(
                "ALERT-" + System.currentTimeMillis(),
                type,
                severity,
                sourceIP,
                "SERVER"
        );
    }

    public Blacklist getBlacklist() {
        return blacklist;
    }
}
