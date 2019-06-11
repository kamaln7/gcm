package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.FindAttractionsByCityIdCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.Attraction;
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
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminTablePickerAttractionForCityController implements Initializable {
    @FXML
    public TableView tableView;
    @FXML
    private TableColumn<Attraction, Integer> idCol;
    @FXML
    private TableColumn<Attraction, String> nameCol;
    @FXML
    private TableColumn<Attraction, String> descriptionCol;
    @FXML
    private TableColumn<Attraction, String> typeCol;
    @FXML
    private TableColumn<Attraction, String> locationCol;
    @FXML
    private TableColumn<Attraction, String> accessibleCol;
    @FXML
    private TableColumn<Attraction, Void> buttonCol;

    @FXML
    private TextField filterTF;
    private ObservableList<Attraction> attractions = FXCollections.observableArrayList();
    private FilteredList<Attraction> filteredAttractions = new FilteredList<>(attractions);
    private SortedList<Attraction> sortedFilteredAttractions = new SortedList<>(filteredAttractions);

    private Attraction attraction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        accessibleCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAccessibleSpecial() ? "Yes" : "No"));

        // button
        buttonCol.setCellFactory(new Callback<TableColumn<Attraction, Void>, TableCell<Attraction, Void>>() {
            @Override
            public TableCell<Attraction, Void> call(TableColumn<Attraction, Void> param) {
                final TableCell<Attraction, Void> cell = new TableCell<Attraction, Void>() {
                    private final Button btn = new Button("Choose");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Attraction attraction = getTableView().getItems().get(getIndex());
                            setAttraction(attraction);
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
        filterTF.textProperty().addListener((observable, oldValue, newValue) -> filteredAttractions.setPredicate(attraction -> {
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = newValue.toLowerCase();
            return (attraction.getName().toLowerCase().contains(lowerCaseFilter)
                    || attraction.getDescription().toLowerCase().contains(lowerCaseFilter)
                    || attraction.getLocation().toLowerCase().contains(lowerCaseFilter)
                    || attraction.getType().toLowerCase().contains(lowerCaseFilter));
        }));

        sortedFilteredAttractions.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedFilteredAttractions);
    }

    public static Attraction loadViewAndWait(Stage stage, Integer cityId) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AdminTablePickerAttractionForCity.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);
        // setting the stage
        stage.setScene(scene);
        stage.setTitle("Choose an Attraction");
        stage.setResizable(true);


        AdminTablePickerAttractionForCityController c = loader.getController();
        c.loadAttractionsFromServer(cityId);
        stage.showAndWait();
        return c.getAttraction();
    }

    public void loadAttractionsFromServer(Integer cityId) {
        try {
            Input input = new FindAttractionsByCityIdCommand.Input(cityId);
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindAttractionsByCityIdCommand.Output output = response.getOutput(FindAttractionsByCityIdCommand.Output.class);

            this.attractions.setAll(output.attractions);
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
        ((Stage) tableView.getScene().getWindow()).close();
    }

    @FXML
    private void openNewAttractionWindow(ActionEvent event) {
        try {
            Attraction attraction = AddAttractionController.loadViewAndWait(new Stage());

            if (attraction != null) {
                this.attractions.add(attraction);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }
}
