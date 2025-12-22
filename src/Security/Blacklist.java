package Security;

import java.util.ArrayList;
import java.util.List;

public class Blacklist {

    private List<BlockedIP> blockedIPs;

    public Blacklist() {
        this.blockedIPs = new ArrayList<>();
    }

    // Ajouter une IP à la blacklist
    public void blockIP(String ipAddress, String reason, String blockedBy) {
        if (!isBlocked(ipAddress)) {
            blockedIPs.add(new BlockedIP(ipAddress, reason, blockedBy));
        }
    }

    // Vérifier si une IP est bloquée
    public boolean isBlocked(String ipAddress) {
        for (BlockedIP ip : blockedIPs) {
            if (ip.getIpAddress().equals(ipAddress)) {
                return true;
            }
        }
        return false;
    }

    // Supprimer une IP de la blacklist
    public void unblockIP(String ipAddress) {
        blockedIPs.removeIf(ip -> ip.getIpAddress().equals(ipAddress));
    }

    // Retourner toutes les IP bloquées
    public List<BlockedIP> getBlockedIPs() {
        return blockedIPs;
    }
}
