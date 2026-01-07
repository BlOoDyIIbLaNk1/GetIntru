package alerts;

import java.util.Objects;

/** Clé de corrélation: sourceIP + type. */
public final class CorrelationKey {

    private final String sourceIP;
    private final String type;

    public CorrelationKey(String sourceIP, String type) {
        this.sourceIP = sourceIP;
        this.type = type;
    }

    public String getSourceIP() { return sourceIP; }
    public String getType() { return type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CorrelationKey)) return false;
        CorrelationKey that = (CorrelationKey) o;
        return Objects.equals(sourceIP, that.sourceIP) &&
               Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceIP, type);
    }

    @Override
    public String toString() {
        return "CorrelationKey{sourceIP='" + sourceIP + "', type='" + type + "'}";
    }
}
