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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    @FXML
    public Tab adminTab;

    @FXML
    private Text userDetailsText;

    @FXML
    private Pane cityInfoPane;

    @FXML
    private ListView citiesList;
    private ObservableList citiesListItems = FXCollections.observableArrayList();

    @FXML
    private ListView cityMapsList;
    private ObservableList cityMapsListItems = FXCollections.observableArrayList();

    @FXML
    private Label cityInfoLabel;

    @FXML
    private TextField searchQueryTF;

    //This method is called upon fxml load
    public void initialize(URL location, ResourceBundle resources) {
        // set welcome text
        userDetailsText.setText(String.format(
                "Welcome, %s!",
                ClientGUI.getCurrentUser().getUsername()
        ));

        // show admin tab if has access
        adminTab.setDisable(!ClientGUI.getCurrentUser().hasRole("employee"));
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

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/MainScreen.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
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

    @FXML
    void createCity(ActionEvent event) {
        try {
            AddCityController.loadView(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void createMap(ActionEvent event) {
        try {
            AddMapController.loadView(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void editMap(ActionEvent event) {
        try {
            MapEditOptionsController.loadView(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logOut(ActionEvent actionEvent) {
        try {
            ClientGUI.getClient().logout();
            LoginController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void getMap(ActionEvent event) {
        try {
            GetMapController.loadView(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @FXML
    void changePrice(ActionEvent event) {
        try {
            ChangePriceController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void Approve(ActionEvent event) {
        try {
            ApprovePriceController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

