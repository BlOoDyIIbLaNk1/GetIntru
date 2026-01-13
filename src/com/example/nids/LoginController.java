package com.example.nids;

import Users.SOCUSER;
import databse.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Label lblError;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void onLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        try {
            Optional<SOCUSER> userOpt = userDAO.findByUsernameAndPassword(username, password);
            if (userOpt.isPresent()) {
                // Login successful - navigate to Dashboard
                navigateToDashboard();
            } else {
                showError("Nom d'utilisateur ou mot de passe incorrect.");
            }
        } catch (SQLException e) {
            showError("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onReset() {
        txtUsername.clear();
        txtPassword.clear();
        lblError.setText("");
    }

    @FXML
    private void onGoToRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Register.fxml"));
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("NIDS - Inscription");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("NIDS - Dashboard");
            stage.setResizable(true);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors du chargement du dashboard.");
        }
    }

    private void showError(String message) {
        lblError.setText(message);
    }
}

