package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.commands.SearchCityOrAttractionCommand;
import gcm.database.models.Attraction;
import gcm.database.models.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchCityOrAttractionController implements Initializable {
    @FXML
    private TextField searchQueryTF;

    @FXML
    private ListView citiesList;
    private ObservableList citiesListItems = FXCollections.observableArrayList();

    @FXML
    private ListView attractionsList;
    private ObservableList attractionsListItems = FXCollections.observableArrayList();

    //This method is called upon fxml load
    public void initialize(URL location, ResourceBundle resources) {
        citiesList.setItems(citiesListItems);
        citiesList.setCellFactory((Callback<ListView<City>, CityListCell>) listView -> new CityListCell());
        attractionsList.setItems(attractionsListItems);
        attractionsList.setCellFactory((Callback<ListView<City>, AttractionListCell>) listView -> new AttractionListCell());
    }

    @FXML
    void searchButtonClick(ActionEvent event) {
        String query = searchQueryTF.getText();
        this.searchAndView(query);
    }

    private void searchAndView(String searchQuery) {
        Input input = new SearchCityOrAttractionCommand.Input(searchQuery);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            SearchCityOrAttractionCommand.Output output = response.getOutput(SearchCityOrAttractionCommand.Output.class);

            citiesListItems.setAll(output.cities);
            attractionsListItems.setAll(output.attractions);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Couldn't load results.").show();
        }
    }

    static class CityListCell extends ListCell<City> {
        protected City city;

        @Override
        protected void updateItem(City item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
            } else {
                this.city = item;
                setText(String.format(
                        "%s, %s\n\n%s maps\n%s attractions\n%s tours\n\n%s",
                        city.getName(),
                        city.getCountry(),
                        city._extraInfo.getOrDefault("mapCount", "X"),
                        city._extraInfo.getOrDefault("attractionCount", "X"),
                        city._extraInfo.getOrDefault("tourCount", "X"),
                        city.getDescription()
                ));
            }
        }
    }

    static class AttractionListCell extends ListCell<Attraction> {
        protected Attraction attraction;

        @Override
        protected void updateItem(Attraction item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setText(null);
            } else {
                this.attraction = item;
                setText(String.format("%s", attraction.getName()));
            }
        }
    }
}
