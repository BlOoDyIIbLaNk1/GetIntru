package com.example.nids;

import alerts.Alert;
import alerts.AlertSeverity;
import alerts.Incident;
import databse.AlertDAO;
import databse.IncidentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {

    @FXML
    private Label lblTotal;
    @FXML
    private Label lblCritical;
    @FXML
    private Label lblHigh;
    @FXML
    private Label lblCritical1; // Medium
    @FXML
    private Label lblCritical11; // Low
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cmbSeverity;
    @FXML
    private Button btnAddDemo;
    @FXML
    private Button btnClear;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private TableView<AlertTableModel> tblAlerts;
    @FXML
    private TableColumn<AlertTableModel, String> colTimestamp;
    @FXML
    private TableColumn<AlertTableModel, String> colSrcIp;
    @FXML
    private TableColumn<AlertTableModel, String> colDstIp;
    @FXML
    private TableColumn<AlertTableModel, String> colPort;
    @FXML
    private TableColumn<AlertTableModel, String> colType;
    @FXML
    private TableColumn<AlertTableModel, String> colSeverity;
    @FXML
    private TableColumn<AlertTableModel, String> colMessage;

    private IncidentDAO incidentDAO = new IncidentDAO();
    private AlertDAO alertDAO = new AlertDAO();
    private ObservableList<AlertTableModel> alertData = FXCollections.observableArrayList();
    private FilteredList<AlertTableModel> filteredAlerts;

    @FXML
    private void initialize() {
        setupTableColumns();
        setupSeverityComboBox();
        setupFilters();
        loadData();
    }

    private void setupTableColumns() {
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colSrcIp.setCellValueFactory(new PropertyValueFactory<>("sourceIP"));
        colDstIp.setCellValueFactory(new PropertyValueFactory<>("destinationIP"));
        colPort.setCellValueFactory(new PropertyValueFactory<>("port"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colSeverity.setCellValueFactory(new PropertyValueFactory<>("severity"));
        colMessage.setCellValueFactory(new PropertyValueFactory<>("message"));
    }

    private void setupSeverityComboBox() {
        cmbSeverity.getItems().addAll("All", "Critical", "High", "Medium", "Low");
        cmbSeverity.setValue("All");
        cmbSeverity.setOnAction(e -> applyFilters());
    }

    private void setupFilters() {
        filteredAlerts = new FilteredList<>(alertData, p -> true);
        tblAlerts.setItems(filteredAlerts);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private void loadData() {
        try {
            // Load incidents for KPIs
            List<Incident> incidents = incidentDAO.findAll();
            updateKPIs(incidents);

            // Load alerts from incidents
            loadAlertsFromIncidents(incidents);

            // Update chart
            updateChart(incidents);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    private void loadAlertsFromIncidents(List<Incident> incidents) {
        alertData.clear();
        try {
            for (Incident incident : incidents) {
                List<Alert> alerts = alertDAO.findByIncidentId(incident.getId());
                for (Alert alert : alerts) {
                    alertData.add(new AlertTableModel(alert));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateKPIs(List<Incident> incidents) {
        long total = incidents.size();
        long critical = incidents.stream().filter(i -> i.getSeverity() == AlertSeverity.Critical).count();
        long high = incidents.stream().filter(i -> i.getSeverity() == AlertSeverity.High).count();
        long medium = incidents.stream().filter(i -> i.getSeverity() == AlertSeverity.Medium).count();
        long low = incidents.stream().filter(i -> i.getSeverity() == AlertSeverity.Low).count();

        lblTotal.setText(String.valueOf(total));
        lblCritical.setText(String.valueOf(critical));
        lblHigh.setText(String.valueOf(high));
        lblCritical1.setText(String.valueOf(medium));
        lblCritical11.setText(String.valueOf(low));
    }

    private void updateChart(List<Incident> incidents) {
        lineChart.getData().clear();
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        
        // Simple chart showing incidents over time
        int tick = 0;
        for (Incident incident : incidents) {
            series.getData().add(new XYChart.Data<>(tick++, 1));
        }
        
        lineChart.getData().add(series);
    }

    private void applyFilters() {
        filteredAlerts.setPredicate(alert -> {
            String searchText = txtSearch.getText().toLowerCase();
            String selectedSeverity = cmbSeverity.getValue();

            // Text filter
            boolean matchesText = searchText.isEmpty() ||
                    alert.getSourceIP().toLowerCase().contains(searchText) ||
                    alert.getDestinationIP().toLowerCase().contains(searchText) ||
                    alert.getType().toLowerCase().contains(searchText) ||
                    alert.getSeverity().toLowerCase().contains(searchText) ||
                    alert.getMessage().toLowerCase().contains(searchText);

            // Severity filter
            boolean matchesSeverity = selectedSeverity == null || 
                    selectedSeverity.equals("All") || 
                    alert.getSeverity().equals(selectedSeverity);

            return matchesText && matchesSeverity;
        });
    }

    @FXML
    private void onAddDemo() {
        // Navigate to register or add user functionality
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Register.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("NIDS - Ajouter Utilisateur");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onClear() {
        txtSearch.clear();
        cmbSeverity.setValue("All");
        applyFilters();
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert dialog =
                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        dialog.showAndWait();
    }


    // Inner class for table model
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
            this.port = "N/A"; // Alert model doesn't have port
            this.type = alert.getType();
            this.severity = alert.getSeverity().name();
            this.message = alert.getType(); // Using type as message since Alert doesn't have message field
        }

        // Getters
        public String getTimestamp() { return timestamp; }
        public String getSourceIP() { return sourceIP; }
        public String getDestinationIP() { return destinationIP; }
        public String getPort() { return port; }
        public String getType() { return type; }
        public String getSeverity() { return severity; }
        public String getMessage() { return message; }
    }
}

