package databse;

import databse.DBConnexion;
import alerts.Alert;
import alerts.AlertSeverity;
import alerts.AlertStatus;

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class AlertDAO {

    public void insertAlert(Alert alert, String incidentId) throws SQLException {
        String sql = "INSERT INTO alerts (id, type, severity, status, source_ip, destination_ip, message, event_time, incident_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DBConnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, alert.getId());
            ps.setString(2, alert.getType());
            ps.setString(3, alert.getSeverity().name());
            ps.setString(4, alert.getStatus().name());
            ps.setString(5, alert.getSourceIP());
            ps.setString(6, alert.getDestinationIP());
            ps.setString(7, ""); // tu peux rajouter un champ message dans Alert si tu veux
            ps.setTimestamp(8, Timestamp.valueOf(alert.getDate()));
            ps.setString(9, incidentId);
            ps.executeUpdate();
        }
    }

    public void updateStatus(String alertId, AlertStatus status) throws SQLException {
        String sql = "UPDATE alerts SET status = ? WHERE id = ?";

        try (Connection c = DBConnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setString(2, alertId);
            ps.executeUpdate();
        }
    }

    public List<Alert> findByIncidentId(String incidentId) throws SQLException {
        String sql = "SELECT id, type, severity, status, source_ip, destination_ip, event_time FROM alerts WHERE incident_id = ?";
        List<Alert> list = new ArrayList<>();

        try (Connection c = DBConnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, incidentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    String type = rs.getString("type");
                    AlertSeverity sev = AlertSeverity.valueOf(rs.getString("severity"));
                    AlertStatus st = AlertStatus.valueOf(rs.getString("status"));
                    String src = rs.getString("source_ip");
                    String dst = rs.getString("destination_ip");
                    Timestamp ts = rs.getTimestamp("event_time");

                    Alert a = new Alert(id, type, sev, src, dst);
                    a.setStatus(st);
                    // la date de l'alert dans ton modèle est mise à now() dans le constructeur;
                    // si tu veux exactement celle de la DB, modifie la classe pour accepter LocalDateTime en paramètre.
                    list.add(a);
                }
            }
        }
        return list;
    }
    public void closeAlertsByIncidentId(String incidentId) throws SQLException {
        String sql = "UPDATE alerts SET status = ? WHERE incident_id = ?";
        try (Connection c = DBConnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, AlertStatus.CLOSED.name());
            ps.setString(2, incidentId);
            ps.executeUpdate();
        }
    }

}
