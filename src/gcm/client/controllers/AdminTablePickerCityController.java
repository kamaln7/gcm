package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.CitySearchCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminTablePickerCityController implements Initializable {
    @FXML
    public TableView tableView;
    @FXML
    private TableColumn<City, Integer> idCol;
    @FXML
    private TableColumn<City, String> nameCol;
    @FXML
    private TableColumn<City, String> countryCol;
    @FXML
    private TableColumn<City, Void> buttonCol;
    private ObservableList<City> cities = FXCollections.observableArrayList();

    private City city;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));

        // button
        buttonCol.setCellFactory(new Callback<TableColumn<City, Void>, TableCell<City, Void>>() {
            @Override
            public TableCell<City, Void> call(TableColumn<City, Void> param) {
                final TableCell<City, Void> cell = new TableCell<City, Void>() {
                    private final Button btn = new Button("Choose");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            City city = getTableView().getItems().get(getIndex());
                            setCity(city);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });

        tableView.setItems(cities);
        // load cities
        loadCitiesFromServer();
    }

    public static City loadViewAndWait(Stage stage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AdminTablePickerCity.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);
        // setting the stage
        stage.setScene(scene);
        stage.setTitle("Choose a City");
        stage.setResizable(true);
        stage.showAndWait();

        return loader.<AdminTablePickerCityController>getController().getCity();
    }

    private void loadCitiesFromServer() {
        try {
            Input input = new CitySearchCommand.Input("");
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            CitySearchCommand.Output output = response.getOutput(CitySearchCommand.Output.class);

            this.cities.setAll(output.cities);
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
        ((Stage) tableView.getScene().getWindow()).close();
    }

    @FXML
    private void openNewCityWindow(ActionEvent event) {
        try {
            City city = AddCityController.loadViewAndWait(new Stage());

            if (city != null) {
                this.cities.add(city);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }
}
