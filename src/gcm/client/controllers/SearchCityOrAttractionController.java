package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.commands.SearchCityOrAttractionCommand;
import gcm.database.models.Attraction;
import gcm.database.models.City;
import gcm.database.models.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
        citiesList.setCellFactory((Callback<ListView<CityWithMapsList>, CityListCell>) listView -> new CityListCell() {
            {
                prefWidthProperty().bind(citiesList.widthProperty().subtract(4));
                setMaxWidth(Control.USE_PREF_SIZE);
            }
        });
        attractionsList.setItems(attractionsListItems);
        attractionsList.setCellFactory((Callback<ListView<CityWithMapsList>, AttractionListCell>) listView -> new AttractionListCell());
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

            citiesListItems.setAll(
                    output.cities
                            .parallelStream()
                            .map(c -> new CityWithMapsList(output.cityMaps.get(c.getId()), c))
                            .collect(Collectors.toList())
            );
            attractionsListItems.setAll(output.attractions);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Couldn't load results.").show();
        }
    }

    static class CityListCell extends ListCell<CityWithMapsList> {
        @Override
        protected void updateItem(CityWithMapsList city, boolean empty) {
            super.updateItem(city, empty);

            if (empty) {
                setGraphic(null);
            } else {
                CityDetailCardController cdc = new CityDetailCardController();
                cdc.setCity(city.getCity());
                cdc.setMaps(city.maps);
                cdc.prefWidthProperty().bind(widthProperty().subtract(30));
                cdc.setMaxWidth(Control.USE_PREF_SIZE);
                setGraphic(cdc);
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

    private static class CityWithMapsList {
        private List<Map> maps;
        private City city;

        public CityWithMapsList(List<Map> maps, City city) {
            this.maps = maps;
            this.city = city;
        }

        public List<Map> getMaps() {
            return maps;
        }

        public City getCity() {
            return city;
        }
    }
}
