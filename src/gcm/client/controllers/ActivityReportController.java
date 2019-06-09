package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.ActivityReportCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.commands.ReviewPendingPriceChangesCommand;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class ActivityReportController {

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ActivityReport.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public class ActivityReport{
        private  SimpleStringProperty cityName, countryName;
        private  SimpleIntegerProperty cityID, mapsNo, purchasesNo, subscriptionsNo, renewalsNo, viewsNo, downloadsNo;

        public ActivityReport(int cityId, String city, String country, int maps, int purchases, int subscriptions, int renewals, int views, int downloads)
        {
            this.cityID = new SimpleIntegerProperty(cityId);
            this.cityName = new SimpleStringProperty(city);
            this.countryName = new SimpleStringProperty(country);
            this.mapsNo=new SimpleIntegerProperty(maps);
            this.purchasesNo=new SimpleIntegerProperty(purchases);
            this.subscriptionsNo=new SimpleIntegerProperty(subscriptions);
            this.renewalsNo=new SimpleIntegerProperty(renewals);
            this.viewsNo=new SimpleIntegerProperty(views);
            this.downloadsNo=new SimpleIntegerProperty(downloads);
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
    @FXML
    void showResults(ActionEvent event) {

        Input input = new ActivityReportCommand.Input(selectFromDate(event), selectToDate(event));
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ActivityReportCommand.Output output = response.getOutput(ActivityReportCommand.Output.class);

            cityIdColumn.setCellValueFactory(new PropertyValueFactory<>("cityID"));
            cityColumn.setCellValueFactory(new PropertyValueFactory<>("cityName"));
            countryColumn.setCellValueFactory(new PropertyValueFactory<>("countryName"));
            mapsColumn.setCellValueFactory(new PropertyValueFactory<>("mapsNo"));
            purchasesColumn.setCellValueFactory(new PropertyValueFactory<>("purchasesNo"));
            subscriptionsColumn.setCellValueFactory(new PropertyValueFactory<>("subscriptionsNo"));
            renewalsColumn.setCellValueFactory(new PropertyValueFactory<>("renewalsNo"));
            viewsColumn.setCellValueFactory(new PropertyValueFactory<>("viewsNo"));
            downloadsColumn.setCellValueFactory(new PropertyValueFactory<>("downloadsNo"));

            ObservableList<ActivityReport> oblist = FXCollections.observableArrayList();

            for(int i=0;i<output.activityReportList.size(); i ++) {

                oblist.add(new ActivityReport(output.activityReportList.get(i).getCityID(),
                        output.activityReportList.get(i).getCity(), output.activityReportList.get(i).getCountryName(),
                        output.activityReportList.get(i).getMapsNo(), output.activityReportList.get(i).getPurchasesNo(),
                        output.activityReportList.get(i).getSubscriptionsNo(), output.activityReportList.get(i).getRenewalsNo(),
                        output.activityReportList.get(i).getViewsNo(), output.activityReportList.get(i).getDownloadsNo()));
            }
            table.setItems(oblist);
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
                    }
                    else if (Integer.toString(ActivityReport.getCityID()).contains(newValue)) {
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

        }
         catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }
void search(){

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
    LocalDate selectFromDate(ActionEvent event) {
        return fromDate.getValue();
    }

    @FXML
    LocalDate selectToDate(ActionEvent event) {
        return toDate.getValue();
    }
    @FXML
    void search(ActionEvent event) {

    }



}

