package Main;

import Security.BackupService;
import Security.DetectionEngine;
import Security.LogGenerateur;
import Security.LogPipelineService;
import alerts.CorrelationEngine;
import databse.AlertDAO;
import databse.IncidentDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private BackupService backupService;

    @Override
    public void start(Stage primaryStage) throws Exception {

        // --- BACKEND ---
        DetectionEngine detectionEngine = new DetectionEngine();
        CorrelationEngine correlationEngine = new CorrelationEngine();
        IncidentDAO incidentDAO = new IncidentDAO();
        AlertDAO alertDAO = new AlertDAO();

        LogPipelineService pipeline = new LogPipelineService(
                detectionEngine, correlationEngine, incidentDAO, alertDAO
        );

        // ✅ MODIFIÉ: BackupService simple (SEUL correlationEngine)
        backupService = new BackupService(correlationEngine);  // ← CHANGÉ ICI
        backupService.startDailyBackup();

        // Log generator
        Thread t = new Thread(new LogGenerateur(pipeline));
        t.setDaemon(true);
        t.start();

        // --- UI ---
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        primaryStage.setTitle("NIDS - Login");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (backupService != null) {
            backupService.stop();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
