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

import java.sql.SQLException;
import java.util.List;

public class IncidentsController {

    @FXML
    private TextField txtIncidentSearch;
    @FXML
    private ListView<Incident> lstIncidents;
    @FXML
    private Label lblIncidentCount;

    private IncidentDAO incidentDAO = new IncidentDAO();
    private ObservableList<Incident> incidentData = FXCollections.observableArrayList();
    private FilteredList<Incident> filteredIncidents;

    @FXML
    private void initialize() {
        setupFilters();
        loadIncidents();
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

