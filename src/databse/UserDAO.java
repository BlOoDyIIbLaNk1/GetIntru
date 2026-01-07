package databse;

import Users.SOCUSER;
import Users.SOCROLE;

import java.sql.*;
import java.util.Optional;

public class UserDAO {

    // Insert simple (utilise l'id que tu donnes dans le code)
    public void insertUser(SOCUSER user) throws SQLException {
        String sql = "INSERT INTO users (id, username, password, role) VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, user.getId());
            ps.setString(2, user.getUsername());
            ps.setString(3, user.getPwd());
            ps.setString(4, user.getRole().name());
            ps.executeUpdate();
        }
    }

    // Pour le login (username + password)
    public Optional<SOCUSER> findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT id, username, password, role FROM users WHERE username = ? AND password = ?";
        try (Connection c = DBConnexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("id");
                    String uname = rs.getString("username");
                    String pwd = rs.getString("password");
                    SOCROLE role = SOCROLE.valueOf(rs.getString("role"));
                    SOCUSER user = new SOCUSER(id, uname, pwd, role);
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }
}
