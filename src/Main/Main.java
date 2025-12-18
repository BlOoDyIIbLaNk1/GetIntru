package Main;
import java.time.LocalDateTime;
import alerts.*;
import Users.*;

public class Main {
    public static void main(String[] args) {

        // 1️⃣ Création d’un utilisateur SOC L1
        SOCUSER analystL1 = new SOCUSER(
                "U001",
                "analyst_l1",
                "password123",
                SOCROLE.L1
        );

        System.out.println("User connected: " + analystL1.getUsername());
        System.out.println("Role: " + analystL1.getRole());

        // 2️⃣ Création d’une alerte (simulée IDS)
        Alert alert = new Alert("A001","SSH Brute Force",AlertSeverity.High,"192.168.1.1","10.0.0.5","open");

        System.out.println("\n--- Alert Created ---");
        System.out.println(alert);

        // 3️⃣ Analyst L1 accuse réception de l’alerte

        System.out.println("\n--- Alert After SOC Action ---");
        System.out.println(alert);
    }
}