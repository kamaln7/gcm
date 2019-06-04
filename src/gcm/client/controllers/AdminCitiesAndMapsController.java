package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.CitySearchCommand;
import gcm.commands.FindMapsByCityIdCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.City;
import gcm.database.models.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminCitiesAndMapsController implements Initializable {

    @FXML
    private AnchorPane cityInfoPane;

    @FXML
    private ListView citiesList;
    private ObservableList citiesListItems = FXCollections.observableArrayList();

    @FXML
    private ListView cityMapsList;
    private ObservableList cityMapsListItems = FXCollections.observableArrayList();

    @FXML
    private Label cityInfoLabel;


    //This method is called upon fxml load
    public void initialize(URL location, ResourceBundle resources) {
        cityInfoPane.setOpacity(0);

        citiesList.setItems(citiesListItems);
        citiesList.setCellFactory((Callback<ListView<City>, CityListCell>) listView -> new CityListCell());
        cityMapsList.setItems(cityMapsListItems);
        cityMapsList.setCellFactory((Callback<ListView<City>, MapListCell>) listView -> new MapListCell());

        // get list of cities
        loadListOfCities("");
    }

    private void loadListOfCities(String searchQuery) {
        CitySearchCommand.Input input = new CitySearchCommand.Input(searchQuery);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            CitySearchCommand.Output output = response.getOutput(CitySearchCommand.Output.class);

            citiesListItems.setAll(output.cities);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Couldn't load list of cities.").show();
        }
    }

    @FXML
    void citiesListSelectCity(MouseEvent event) {
        City city = (City) citiesList.getSelectionModel().getSelectedItem();
        if (city == null) {
            return;
        }
        // selected city, load info

        Input input = new FindMapsByCityIdCommand.Input(city.getId());
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindMapsByCityIdCommand.Output output = response.getOutput(FindMapsByCityIdCommand.Output.class);

            cityMapsListItems.setAll(output.maps);

            cityInfoLabel.setText(String.format("%s, %s\n\n%d maps",
                    city.getName(),
                    city.getCountry(),
                    output.maps.size()));
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
        cityInfoPane.setOpacity(1);
    }

    @FXML
    void cityMapsListSelectMap(MouseEvent event) {
        Map map = (Map) cityMapsList.getSelectionModel().getSelectedItem();
        if (map == null) {
            return;
        }
        // selected map, load view
        new Alert(Alert.AlertType.INFORMATION, "You selected map " + map.getTitle()).show();
    }

    static class CityListCell extends ListCell<City> {
        protected City city;

        @Override
        protected void updateItem(City item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                setText(null);
            } else {
                this.city = item;
                setText(String.format("%s, %s", city.getName(), city.getCountry()));
            }
        }
    }

    static class MapListCell extends ListCell<Map> {
        protected Map map;

        @Override
        protected void updateItem(Map item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                setText(null);
            } else {
                this.map = item;
                setText(this.map.getTitle());
            }
        }
    }
}
