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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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

    @FXML
    private VBox titledPanesVBox;
    @FXML
    private TitledPane citiesTitledPane;
    @FXML
    private TitledPane attractionsTitledPane;

    //This method is called upon fxml load
    public void initialize(URL location, ResourceBundle resources) {
        CityListCell.setWithBuyButton(ClientGUI.getCurrentUser().hasExactRole("user"));

        citiesList.setItems(citiesListItems);
        citiesList.setCellFactory((Callback<ListView<CityWithMapsList>, CityListCell>) listView -> new CityListCell() {
            {
                prefWidthProperty().bind(titledPanesVBox.widthProperty().subtract(10));
                setMaxWidth(Control.USE_PREF_SIZE);
            }
        });
        attractionsList.setItems(attractionsListItems);
        attractionsList.setCellFactory((Callback<ListView<AttractionWithMapsList>, AttractionListCell>) listView -> new AttractionListCell() {
            {
                prefWidthProperty().bind(titledPanesVBox.widthProperty().subtract(10));
                setMaxWidth(Control.USE_PREF_SIZE);
            }
        });

        citiesTitledPane.expandedProperty().addListener(
                (obs, wasExpanded, isNowExpanded) -> {
                    VBox.setVgrow(citiesTitledPane, isNowExpanded ? Priority.ALWAYS : Priority.NEVER);
                    citiesTitledPane.setMaxHeight(isNowExpanded ? Double.MAX_VALUE : 26);
                }
        );
        attractionsTitledPane.expandedProperty().addListener(
                (obs, wasExpanded, isNowExpanded) ->
                {
                    VBox.setVgrow(attractionsTitledPane, isNowExpanded ? Priority.ALWAYS : Priority.NEVER);
                    attractionsTitledPane.setMaxHeight(isNowExpanded ? Double.MAX_VALUE : 26);
                }
        );
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
            attractionsListItems.setAll(
                    output.attractions
                            .parallelStream()
                            .map(a -> new AttractionWithMapsList(output.attractionMaps.get(a.getId()), a))
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Couldn't load results.").show();
        }
    }

    static class CityListCell extends ListCell<CityWithMapsList> {
        private static Boolean withBuyButton = true;

        public static void setWithBuyButton(Boolean withBuyButton) {
            CityListCell.withBuyButton = withBuyButton;
        }

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
                cdc.setWithBuyButton(withBuyButton);
                setGraphic(cdc);
            }
        }
    }

    @FXML
    private void listViewMouseClick(MouseEvent e) {
        citiesList.getSelectionModel().clearSelection();
        attractionsList.getSelectionModel().clearSelection();
    }

    static class AttractionListCell extends ListCell<AttractionWithMapsList> {
        @Override
        protected void updateItem(AttractionWithMapsList attraction, boolean empty) {
            super.updateItem(attraction, empty);

            if (empty) {
                setGraphic(null);
            } else {
                AttractionDetailCardController adc = new AttractionDetailCardController();
                adc.setAttraction(attraction.getAttraction());
                adc.setMaps(attraction.getMaps());
                adc.prefWidthProperty().bind(widthProperty().subtract(30));
                adc.setMaxWidth(Control.USE_PREF_SIZE);
                setGraphic(adc);
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

    private static class AttractionWithMapsList {
        private List<Map> maps;
        private Attraction attraction;

        public AttractionWithMapsList(List<Map> maps, Attraction attraction) {
            this.maps = maps;
            this.attraction = attraction;
        }

        public List<Map> getMaps() {
            return maps;
        }

        public Attraction getAttraction() {
            return attraction;
        }
    }
}
