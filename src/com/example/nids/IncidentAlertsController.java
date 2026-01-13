package com.example.nids;

import alerts.Alert;
import alerts.AlertSeverity;
import alerts.AlertStatus;
import alerts.Incident;
import databse.AlertDAO;
import databse.IncidentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IncidentAlertsController {

    @FXML
    private Label lblIncidentId;
    @FXML
    private Label lblIncidentStatus;
    @FXML
    private Label lblIncidentSeverity;
    @FXML
    private Label lblIncidentTitle;
    @FXML
    private TextField txtAlertSearch;
    @FXML
    private ComboBox<String> cmbAlertSeverity;
    @FXML
    private TableView<AlertTableModel> tblAlerts;
    @FXML
    private TableColumn<AlertTableModel, String> colATimestamp;
    @FXML
    private TableColumn<AlertTableModel, String> colASrcIp;
    @FXML
    private TableColumn<AlertTableModel, String> colADstIp;
    @FXML
    private TableColumn<AlertTableModel, String> colAPort;
    @FXML
    private TableColumn<AlertTableModel, String> colAType;
    @FXML
    private TableColumn<AlertTableModel, String> colASeverity;
    @FXML
    private TableColumn<AlertTableModel, String> colAMessage;
    @FXML
    private TextArea txtAlertDetails;
    @FXML
    private Label lblAlertsCount;

    private AlertDAO alertDAO = new AlertDAO();
    private IncidentDAO incidentDAO = new IncidentDAO();
    private ObservableList<AlertTableModel> alertData = FXCollections.observableArrayList();
    private FilteredList<AlertTableModel> filteredAlerts;
    private Incident selectedIncident;

    @FXML
    private void initialize() {
        setupTableColumns();
        setupSeverityComboBox();
        setupFilters();
        
        // Set up table selection listener
        tblAlerts.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayAlertDetails(newVal);
            }
        });
    }

    private void setupTableColumns() {
        colATimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colASrcIp.setCellValueFactory(new PropertyValueFactory<>("sourceIP"));
        colADstIp.setCellValueFactory(new PropertyValueFactory<>("destinationIP"));
        colAPort.setCellValueFactory(new PropertyValueFactory<>("port"));
        colAType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colASeverity.setCellValueFactory(new PropertyValueFactory<>("severity"));
        colAMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
    }

    private void setupSeverityComboBox() {
        cmbAlertSeverity.getItems().addAll("All", "Critical", "High", "Medium", "Low");
        cmbAlertSeverity.setValue("All");
        cmbAlertSeverity.setOnAction(e -> applyFilters());
    }

    private void setupFilters() {
        filteredAlerts = new FilteredList<>(alertData, p -> true);
        tblAlerts.setItems(filteredAlerts);

        txtAlertSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    public void setSelectedIncident(Incident incident) {
        this.selectedIncident = incident;
        if (incident != null) {
            updateIncidentDetails(incident);
            loadAlertsForIncident(incident.getId());
        } else {
            clearDetails();
        }
    }

    private void updateIncidentDetails(Incident incident) {
        lblIncidentId.setText(incident.getId());
        lblIncidentTitle.setText(incident.getTitle());
        lblIncidentSeverity.setText(incident.getSeverity().name());
        lblIncidentStatus.setText(incident.isResolved() ? "Resolved" : "Active");
    }

    private void clearDetails() {
        lblIncidentId.setText("—");
        lblIncidentTitle.setText("Sélectionne un incident à gauche.");
        lblIncidentSeverity.setText("—");
        lblIncidentStatus.setText("—");
        alertData.clear();
        updateCount();
    }

    private void loadAlertsForIncident(String incidentId) {
        try {
            List<Alert> alerts = alertDAO.findByIncidentId(incidentId);
            alertData.clear();
            for (Alert alert : alerts) {
                alertData.add(new AlertTableModel(alert));
            }
            applyFilters();
            updateCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyFilters() {
        String searchText = txtAlertSearch.getText().toLowerCase();
        String selectedSeverity = cmbAlertSeverity.getValue();

        filteredAlerts.setPredicate(alert -> {
            boolean matchesText = searchText.isEmpty() ||
                    alert.getSourceIP().toLowerCase().contains(searchText) ||
                    alert.getDestinationIP().toLowerCase().contains(searchText) ||
                    alert.getType().toLowerCase().contains(searchText) ||
                    alert.getMessage().toLowerCase().contains(searchText);

            boolean matchesSeverity = selectedSeverity == null ||
                    selectedSeverity.equals("All") ||
                    alert.getSeverity().equals(selectedSeverity);

            return matchesText && matchesSeverity;
        });
        updateCount();
    }

    private void updateCount() {
        lblAlertsCount.setText(filteredAlerts.size() + " alerte(s)");
    }

    private void displayAlertDetails(AlertTableModel alert) {
        StringBuilder details = new StringBuilder();
        details.append("ID: ").append(alert.getType()).append("\n");
        details.append("Timestamp: ").append(alert.getTimestamp()).append("\n");
        details.append("Source IP: ").append(alert.getSourceIP()).append("\n");
        details.append("Destination IP: ").append(alert.getDestinationIP()).append("\n");
        details.append("Type: ").append(alert.getType()).append("\n");
        details.append("Severity: ").append(alert.getSeverity()).append("\n");
        details.append("Message: ").append(alert.getMessage()).append("\n");
        txtAlertDetails.setText(details.toString());
    }

    @FXML
    private void onRefreshAlerts() {
        if (selectedIncident != null) {
            loadAlertsForIncident(selectedIncident.getId());
        }
    }

    @FXML
    private void onAssignIncident() {
        if (selectedIncident == null) {
            showAlert("Aucun incident sélectionné", "Veuillez sélectionner un incident.");
            return;
        }
        // TODO: Implement assignment logic
        showAlert("Information", "Fonctionnalité d'assignation à implémenter.");
    }

    @FXML
    private void onResolveIncident() {
        if (selectedIncident == null) {
            showAlert("Aucun incident sélectionné", "Veuillez sélectionner un incident.");
            return;
        }
        
        try {
            incidentDAO.updateStatus(selectedIncident.getId(), "RESOLVED", true);
            selectedIncident.closeIncident();
            updateIncidentDetails(selectedIncident);
            showAlert("Succès", "Incident résolu avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la résolution de l'incident: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert dialog =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        dialog.showAndWait();
    }


    // Reuse AlertTableModel from DashboardController
    public static class AlertTableModel {
        private String timestamp;
        private String sourceIP;
        private String destinationIP;
        private String port;
        private String type;
        private String severity;
        private String message;

        public AlertTableModel(Alert alert) {
            this.timestamp = alert.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            this.sourceIP = alert.getSourceIP();
            this.destinationIP = alert.getDestinationIP();
            this.port = "N/A";
            this.type = alert.getType();
            this.severity = alert.getSeverity().name();
            this.message = alert.getType();
        }

        public String getTimestamp() { return timestamp; }
        public String getSourceIP() { return sourceIP; }
        public String getDestinationIP() { return destinationIP; }
        public String getPort() { return port; }
        public String getType() { return type; }
        public String getSeverity() { return severity; }
        public String getMessage() { return message; }
    }
}

