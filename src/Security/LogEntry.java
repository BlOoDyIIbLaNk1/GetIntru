package Security;

import java.time.LocalDateTime;

public class LogEntry {

    private LocalDateTime timestamp;
    private String sourceIP;
    private String eventType;
    private String message;

    public LogEntry(String sourceIP, String eventType, String message) {
        this.timestamp = LocalDateTime.now(); // auto
        this.sourceIP = sourceIP;
        this.eventType = eventType;
        this.message = message;
    }

    // Getters uniquement (log immutable)
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public String getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " +
               "IP=" + sourceIP +
               " | TYPE=" + eventType +
               " | MSG=" + message;
    }
}
