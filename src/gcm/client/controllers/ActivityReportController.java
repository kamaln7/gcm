package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.ActivityReportCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.City;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.ResourceBundle;

public class ActivityReportController{
    @FXML
    private TableView<City> table;
    @FXML
    private TableColumn<City, Integer> cityIdColumn;
    @FXML
    private TableColumn<City, String> cityColumn;

    @FXML
    private TableColumn<City, String> countryColumn;

    @FXML
    private TableColumn<City, String> mapsColumn;

    @FXML
    private TableColumn<City, String> purchasesColumn;

    @FXML
    private TableColumn<City, String> subscriptionsColumn;

    @FXML
    private TableColumn<City, String> renewalsColumn;

    @FXML
    private TableColumn<City, String> viewsColumn;

    @FXML
    private TableColumn<City, String> downloadsColumn;
    @FXML
    private TextField searchField;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;




    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ActivityReport.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Activity Report - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Fill table with data about all cities
     *
     * @param event
     */
    @FXML
    void showResults(ActionEvent event) {
        if (fromDate.getValue() == null || toDate.getValue() == null || toDate.getValue().isBefore(fromDate.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You have to choose a valid date range!");
            alert.show();
            return;
        }
        Date from = selectFromDate(event);
        Date to = selectToDate(event);
        Input input = new ActivityReportCommand.Input(from, to);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ActivityReportCommand.Output output = response.getOutput(ActivityReportCommand.Output.class);
            ObservableList<City> oblist = FXCollections.observableArrayList();
            cityIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            cityColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
            mapsColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("mapsCount")));
            purchasesColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("purchasesCount")));
            subscriptionsColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("subscriptionsCount")));
            renewalsColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("renewalsCount")));
            viewsColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("viewsCount")));
            downloadsColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("downloadsCount")));
            for(int i = 0 ; i < output.cities.size() ; i++)
            {
                oblist.add(output.cities.get(i));
            }
            table.setItems(oblist);
            /**
             * Filter TableView by input text in search field
             * by city name, country, id
             */
            // 1. Wrap the ObservableList in a FilteredList (initially display all data).
            FilteredList<City> filteredData = new FilteredList<>(oblist, p -> true);

            // 2. Set the filter Predicate whenever the filter changes.
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(City -> {
                    // If filter text is empty, display all data.
                    if (newValue == null || newValue.isEmpty()) {


                        return true;
                    }

                    // Compare city filter text.
                    String lowerCaseFilter = newValue.toLowerCase();

                    if (City.getName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches city.
                    } else if (City.getCountry().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches country.
                    } else if (Integer.toString(City.getId()).contains(newValue)) {
                        return true; // Filter matches cityID.
                    }
                    return false; // Does not match.
                });
            });

            // 3. Wrap the FilteredList in a SortedList.
            SortedList<City> sortedData = new SortedList<>(filteredData);

            // 4. Bind the SortedList comparator to the TableView comparator.
            sortedData.comparatorProperty().bind(table.comparatorProperty());

            // 5. Add sorted (and filtered) data to the table.
            table.setItems(sortedData);

        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }



    @FXML
    Date selectFromDate(ActionEvent event) {
        return LocalDateToDate(fromDate.getValue(), true);
    }

    @FXML
    Date selectToDate(ActionEvent event) {
        return LocalDateToDate(toDate.getValue(), false);
    }

    private static Date LocalDateToDate(LocalDate localDate, Boolean startOfDay) {
        ZonedDateTime zonedDateTime = startOfDay
                ? localDate.atStartOfDay(ZoneId.systemDefault())
                : localDate.atStartOfDay(ZoneId.systemDefault()).plusDays(1);

        return Date.from(zonedDateTime.toInstant());
    }



}

