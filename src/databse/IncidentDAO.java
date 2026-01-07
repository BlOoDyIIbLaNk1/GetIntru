package databse;

import databse.DBConnexion;
import alerts.AlertSeverity;
import alerts.AlertStatus;
import alerts.Incident;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {

    public void insertIncident(Incident incident) throws SQLException {
        String sql = "INSERT INTO incidents (id, title, description, severity, status, resolved, assigned_to_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DBConnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, incident.getId());
            ps.setString(2, incident.getTitle());
            ps.setString(3, incident.getDescription());
            ps.setString(4, incident.getSeverity().name());
            ps.setString(5, AlertStatus.NEW.name()); // ou un statut propre pour les incidents
            ps.setBoolean(6, incident.isResolved());
            ps.setString(7, null); // pas assigné au début
            ps.executeUpdate();
        }
    }

    public void updateSeverity(String incidentId, AlertSeverity severity) throws SQLException {
        String sql = "UPDATE incidents SET severity = ? WHERE id = ?";

        try (Connection c = DBConnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, severity.name());
            ps.setString(2, incidentId);
            ps.executeUpdate();
        }
    }

    public void updateStatus(String incidentId, String status, boolean resolved) throws SQLException {
        String sql = "UPDATE incidents SET status = ?, resolved = ? WHERE id = ?";

        try (Connection c = DBConnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setBoolean(2, resolved);
            ps.setString(3, incidentId);
            ps.executeUpdate();
        }
    }

    public List<Incident> findAll() throws SQLException {
        String sql = "SELECT id, title, description, severity, status, resolved, created_at FROM incidents ORDER BY created_at DESC";
        List<Incident> list = new ArrayList<>();

        try (Connection c = DBConnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String desc = rs.getString("description");
                AlertSeverity sev = AlertSeverity.valueOf(rs.getString("severity"));
                boolean resolved = rs.getBoolean("resolved");

                Incident inc = new Incident(id, title, desc);
                inc.setSeverity(sev);
                if (resolved) {
                    inc.closeIncident();
                }
                list.add(inc);
            }
        }
        return list;
    }
}
