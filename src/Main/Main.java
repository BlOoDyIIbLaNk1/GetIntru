package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Si ton fichier s'appelle login.fxml et est à la racine de resources
    	Parent root = FXMLLoader.load(getClass().getResource("/fxml/incident_alerts_view.fxml"));

        Scene scene = new Scene(root);
        primaryStage.setTitle("NIDS - Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
