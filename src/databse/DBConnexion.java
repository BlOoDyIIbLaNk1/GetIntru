package databse;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnexion {

  private static final String URL =
      "jdbc:mysql://localhost:3306/getintru_bd?useSSL=false&serverTimezone=UTC";
  private static final String USER = "root";
  private static final String PASSWORD = "";

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASSWORD);
  }

  public static void main(String[] args) {
    try (Connection c = DBConnexion.getConnection()) {
      System.out.println("Connexion à MySQL réussie !");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
