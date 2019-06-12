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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class ActivityReportController {

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
     * Class to fill table view of Activity Report
     */
    public class ActivityReport {
        private SimpleStringProperty cityName, countryName;
        private SimpleIntegerProperty cityID, mapsNo, purchasesNo, subscriptionsNo, renewalsNo, viewsNo, downloadsNo;

        public ActivityReport(int cityId, String city, String country, int maps, int purchases, int subscriptions, int renewals, int views, int downloads) {
            this.cityID = new SimpleIntegerProperty(cityId);
            this.cityName = new SimpleStringProperty(city);
            this.countryName = new SimpleStringProperty(country);
            this.mapsNo = new SimpleIntegerProperty(maps);
            this.purchasesNo = new SimpleIntegerProperty(purchases);
            this.subscriptionsNo = new SimpleIntegerProperty(subscriptions);
            this.renewalsNo = new SimpleIntegerProperty(renewals);
            this.viewsNo = new SimpleIntegerProperty(views);
            this.downloadsNo = new SimpleIntegerProperty(downloads);
        }

        public String getCityName() {
            return cityName.get();
        }

        public String getCountryName() {
            return countryName.get();
        }

        public SimpleStringProperty countryNameProperty() {
            return countryName;
        }

        public int getCityID() {
            return cityID.get();
        }

        public SimpleIntegerProperty cityIDProperty() {
            return cityID;
        }

        public SimpleStringProperty cityNameProperty() {
            return cityName;
        }

        public int getMapsNo() {
            return mapsNo.get();
        }

        public SimpleIntegerProperty mapsNoProperty() {
            return mapsNo;
        }

        public int getPurchasesNo() {
            return purchasesNo.get();
        }

        public SimpleIntegerProperty purchasesNoProperty() {
            return purchasesNo;
        }

        public int getSubscriptionsNo() {
            return subscriptionsNo.get();
        }

        public SimpleIntegerProperty subscriptionsNoProperty() {
            return subscriptionsNo;
        }

        public int getRenewalsNo() {
            return renewalsNo.get();
        }

        public SimpleIntegerProperty renewalsNoProperty() {
            return renewalsNo;
        }

        public int getViewsNo() {
            return viewsNo.get();
        }

        public SimpleIntegerProperty viewsNoProperty() {
            return viewsNo;
        }

        public int getDownloadsNo() {
            return downloadsNo.get();
        }

        public SimpleIntegerProperty downloadsNoProperty() {
            return downloadsNo;
        }


    }

    /**
     * Fill ObservableList
     */
    void ObservableList() {
        cityIdColumn.setCellValueFactory(new PropertyValueFactory<>("cityID"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("cityName"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("countryName"));
        mapsColumn.setCellValueFactory(new PropertyValueFactory<>("mapsNo"));
        purchasesColumn.setCellValueFactory(new PropertyValueFactory<>("purchasesNo"));
        subscriptionsColumn.setCellValueFactory(new PropertyValueFactory<>("subscriptionsNo"));
        renewalsColumn.setCellValueFactory(new PropertyValueFactory<>("renewalsNo"));
        viewsColumn.setCellValueFactory(new PropertyValueFactory<>("viewsNo"));
        downloadsColumn.setCellValueFactory(new PropertyValueFactory<>("downloadsNo"));
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
        Input input = new ActivityReportCommand.Input(from, to, -1);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ActivityReportCommand.Output output = response.getOutput(ActivityReportCommand.Output.class);
            ObservableList<ActivityReport> oblist = FXCollections.observableArrayList();

            for (int i = 0; i < output.cities.size(); i++) {
                oblist.add(new ActivityReport(output.cities.get(i).getId(),
                        output.cities.get(i).getName(), output.cities.get(i).getCountry(),
                        output.maps.get(i), output.purchases.get(i),
                        output.subscriptions.get(i), output.renewals.get(i),
                        output.views.get(i), output.downloads.get(i)));
            }
            table.setItems(oblist);
            /**
             * Filter TableView by input text in search field
             * by city name, country, id
             */
            // 1. Wrap the ObservableList in a FilteredList (initially display all data).
            FilteredList<ActivityReport> filteredData = new FilteredList<>(oblist, p -> true);

            // 2. Set the filter Predicate whenever the filter changes.
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(ActivityReport -> {
                    // If filter text is empty, display all data.
                    if (newValue == null || newValue.isEmpty()) {


                        return true;
                    }

                    // Compare city filter text.
                    String lowerCaseFilter = newValue.toLowerCase();

                    if (ActivityReport.getCityName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches city.
                    } else if (ActivityReport.getCountryName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches country.
                    } else if (Integer.toString(ActivityReport.getCityID()).contains(newValue)) {
                        return true; // Filter matches cityID.
                    }
                    return false; // Does not match.
                });
            });

            // 3. Wrap the FilteredList in a SortedList.
            SortedList<ActivityReport> sortedData = new SortedList<>(filteredData);

            // 4. Bind the SortedList comparator to the TableView comparator.
            sortedData.comparatorProperty().bind(table.comparatorProperty());

            // 5. Add sorted (and filtered) data to the table.
            table.setItems(sortedData);

        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    /**
     * Cohoose one city by CityPicker
     *
     * @param event
     */
    @FXML
    void chooseCity(ActionEvent event) {
        if (fromDate.getValue() == null || toDate.getValue() == null || toDate.getValue().isBefore(fromDate.getValue())) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You have to choose a valid date range!");
            alert.show();
            return;
        }
        Date from = selectFromDate(event);
        Date to = selectToDate(event);
        try {
            City city = AdminTablePickerCityController.loadViewAndWait(new Stage());
            if (city == null) return;
            Input input = new ActivityReportCommand.Input(from, to, city.getId());
            try {
                Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
                ActivityReportCommand.Output output = response.getOutput(ActivityReportCommand.Output.class);
                ObservableList<ActivityReport> oblist = FXCollections.observableArrayList();

                oblist.add(new ActivityReport(output.cities.get(0).getId(),
                        output.cities.get(0).getName(), output.cities.get(0).getCountry(),
                        output.maps.get(0), output.purchases.get(0),
                        output.subscriptions.get(0), output.renewals.get(0),
                        output.views.get(0), output.downloads.get(0)));
                table.setItems(oblist);

            } catch (Exception e) {
                ClientGUI.showErrorTryAgain();
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }

    @FXML
    private TableView<ActivityReport> table;
    @FXML
    private TableColumn<ActivityReport, Integer> cityIdColumn;
    @FXML
    private TableColumn<ActivityReport, String> cityColumn;

    @FXML
    private TableColumn<ActivityReport, String> countryColumn;


    @FXML
    private TableColumn<ActivityReport, Integer> mapsColumn;

    @FXML
    private TableColumn<ActivityReport, Integer> purchasesColumn;

    @FXML
    private TableColumn<ActivityReport, Integer> subscriptionsColumn;

    @FXML
    private TableColumn<ActivityReport, Integer> renewalsColumn;

    @FXML
    private TableColumn<ActivityReport, Integer> viewsColumn;

    @FXML
    private TableColumn<ActivityReport, Integer> downloadsColumn;
    @FXML
    private TextField searchField;

    @FXML
    private DatePicker fromDate;

    @FXML
    private DatePicker toDate;

    @FXML
    Date selectFromDate(ActionEvent event) {
        return LocalDateToDate(fromDate.getValue());
    }

    @FXML
    Date selectToDate(ActionEvent event) {
        return LocalDateToDate(toDate.getValue());
    }

    private static Date LocalDateToDate(LocalDate localDate) {
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        return Date.from(instant);
    }

    @FXML
    void search(ActionEvent event) {

    }


}

