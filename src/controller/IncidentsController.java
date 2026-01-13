package controller;

import alerts.Incident;

import databse.IncidentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.sql.SQLException;
import java.util.List;

public class IncidentsController {

	@FXML 
	private controller.IncidentAlertsController alertsViewController;
	@FXML
    private TextField txtIncidentSearch;
    @FXML
    private ListView<Incident> lstIncidents;
    @FXML
    private Label lblIncidentCount;
    
    
    private String pendingSelectIncidentId;
    private IncidentDAO incidentDAO = new IncidentDAO();
    private ObservableList<Incident> incidentData = FXCollections.observableArrayList();
    private FilteredList<Incident> filteredIncidents;

    @FXML
    private void initialize() {
        setupFilters();
        loadIncidents();

        lstIncidents.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (alertsViewController != null) {
                alertsViewController.setSelectedIncident(selected);
            }
        });
    }

    @FXML
    private void onBackToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Dashboard.fxml"));
            Stage stage = (Stage) lstIncidents.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("NIDS - Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void trySelectPendingIncident() {
        if (pendingSelectIncidentId == null) return;

        for (Incident inc : incidentData) {
            if (inc.getId().equals(pendingSelectIncidentId)) {
                lstIncidents.getSelectionModel().select(inc);
                lstIncidents.scrollTo(inc);
                pendingSelectIncidentId = null;
                return;
            }
        }
    }
    
    public void selectIncidentById(String incidentId) {
        this.pendingSelectIncidentId = incidentId;
        trySelectPendingIncident();
    }

    private void setupFilters() {
        filteredIncidents = new FilteredList<>(incidentData, p -> true);
        lstIncidents.setItems(filteredIncidents);

        txtIncidentSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilter());
    }

    private void loadIncidents() {
        try {
            List<Incident> incidents = incidentDAO.findAll();
            incidentData.setAll(incidents);
            updateCount();
            trySelectPendingIncident();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void applyFilter() {
        String searchText = txtIncidentSearch.getText().toLowerCase();
        filteredIncidents.setPredicate(incident -> {
            if (searchText.isEmpty()) {
                return true;
            }
            return incident.getId().toLowerCase().contains(searchText) ||
                   incident.getTitle().toLowerCase().contains(searchText) ||
                   incident.getSeverity().name().toLowerCase().contains(searchText) ||
                   String.valueOf(incident.isResolved()).toLowerCase().contains(searchText);
        });
        updateCount();
    }

    private void updateCount() {
        lblIncidentCount.setText(filteredIncidents.size() + " incident(s)");
    }

    @FXML
    private void onRefreshIncidents() {
        loadIncidents();
    }
}

