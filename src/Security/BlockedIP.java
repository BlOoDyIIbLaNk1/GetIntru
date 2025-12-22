package Security;

import java.time.LocalDateTime;

public class BlockedIP {

    private String ipAddress;
    private String reason;
    private LocalDateTime blockedAt;
    private String blockedBy; // SOC_LEADER

    public BlockedIP(String ipAddress, String reason, String blockedBy) {
        this.ipAddress = ipAddress;
        this.reason = reason;
        this.blockedBy = blockedBy;
        this.blockedAt = LocalDateTime.now(); // auto timestamp
    }

    // Getters
    public String getIpAddress() {
        return ipAddress;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public String getBlockedBy() {
        return blockedBy;
    }

    @Override
    public String toString() {
        return "BlockedIP{" +
                "ipAddress='" + ipAddress + '\'' +
                ", reason='" + reason + '\'' +
                ", blockedAt=" + blockedAt +
                ", blockedBy='" + blockedBy + '\'' +
                '}';
    }
}
