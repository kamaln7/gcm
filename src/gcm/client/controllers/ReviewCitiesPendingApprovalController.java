package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.FindCitiesPendingApprovalCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.Attraction;
import gcm.database.models.Map;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReviewCitiesPendingApprovalController implements Initializable {

    public TableView<Map> mapsTV;
    public TableView<Attraction> attractionsTV;
    public ListView<java.util.Map.Entry<Integer, String>> citiesLV;
    public Button approveBtn;
    public Button rejectBtn;

    private ObservableList<java.util.Map.Entry<Integer, String>> citiesList = FXCollections.observableArrayList();
    private ObservableList<Map> mapsList = FXCollections.observableArrayList();
    private ObservableList<Attraction> attractionsList = FXCollections.observableArrayList();

    private FindCitiesPendingApprovalCommand.Output dataOutput;

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = LoginController.class.getResource("/gcm/client/views/ReviewCitiesPendingApproval.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Review Cities Pending Content Approval - GCM 2019");
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        citiesLV.setPlaceholder(new Label("No cities pending approval."));
        mapsTV.setPlaceholder(new Label("No maps."));
        attractionsTV.setPlaceholder(new Label("No attractions."));

        citiesLV.setItems(citiesList);
        citiesLV.setCellFactory(cell -> new CityListCell());
        mapsTV.setItems(mapsList);
        attractionsTV.setItems(attractionsList);

        // disable buttons when no city is selected
        BooleanBinding selectedCityBinding = citiesLV.getSelectionModel().selectedItemProperty().isNull();
        rejectBtn.disableProperty().bind(selectedCityBinding);
        approveBtn.disableProperty().bind(selectedCityBinding);

        // add handler when clicking on city
        citiesLV.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                attractionsList.clear();
                mapsList.clear();
                return;
            }

            Integer cityId = newValue.getKey();
            attractionsList.setAll(dataOutput.attractions.stream().filter(a -> a.getCityId() == cityId).collect(Collectors.toList()));
            mapsList.setAll(dataOutput.maps.stream().filter(a -> a.getCityId() == cityId).collect(Collectors.toList()));
        }));

        // load data
        loadData();
    }

    private void loadData() {
        try {
            Input input = new FindCitiesPendingApprovalCommand.Input();
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            dataOutput = response.getOutput(FindCitiesPendingApprovalCommand.Output.class);

            citiesList.setAll(dataOutput.cities.entrySet());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    public void approveAction(ActionEvent actionEvent) {
    }

    public void rejectAction(ActionEvent actionEvent) {
    }

    private static class CityListCell extends ListCell<java.util.Map.Entry<Integer, String>> {
        @Override
        protected void updateItem(java.util.Map.Entry<Integer, String> entry, boolean empty) {
            if (empty || entry == null) {
                setText(null);
            } else {
                setText(entry.getValue());
            }
        }
    }
}
