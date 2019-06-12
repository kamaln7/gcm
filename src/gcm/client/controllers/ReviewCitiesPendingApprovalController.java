package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.Attraction;
import gcm.database.models.Map;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ReviewCitiesPendingApprovalController implements Initializable {

    // cities list view
    public ListView<java.util.Map.Entry<Integer, String>> citiesLV;

    // buttons
    public Button approveBtn;
    public Button rejectBtn;

    // maps table
    public TableView<Map> mapsTV;
    public TableColumn<Map, Integer> mapsIDCol;
    public TableColumn<Map, String> mapsTitleCol, mapsDescriptionCol, mapsPublishedCol;
    public TableColumn<Map, Void> mapsImgCol;

    // attractions table
    public TableView<Attraction> attractionsTV;
    public TableColumn<Attraction, Integer> attractionsIDCol;
    public TableColumn<Attraction, String> attractionsNameCol, attractionsDescriptionCol, attractionsTypeCol, attractionsLocationCol, attractionsAccessibleCol;

    // observable lists
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
        // placeholders
        citiesLV.setPlaceholder(new Label("No cities pending approval."));
        mapsTV.setPlaceholder(new Label("No maps."));
        attractionsTV.setPlaceholder(new Label("No attractions."));

        // observable list bindings
        citiesLV.setItems(citiesList);
        citiesLV.setCellFactory(cell -> new CityListCell());
        mapsTV.setItems(mapsList);
        attractionsTV.setItems(attractionsList);

        // maps table
        mapsIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mapsTitleCol.setCellValueFactory(cell -> {
            Map map = cell.getValue();
            return new SimpleStringProperty(formatStringChange(map.getTitle(), map.getTitleNew()));
        });
        mapsDescriptionCol.setCellValueFactory(cell -> {
            Map map = cell.getValue();
            return new SimpleStringProperty(formatStringChange(map.getDescription(), map.getDescriptionNew()));
        });
        mapsImgCol.setCellFactory(new Callback<TableColumn<Map, Void>, TableCell<Map, Void>>() {
            @Override
            public TableCell<Map, Void> call(TableColumn<Map, Void> param) {
                final TableCell<Map, Void> cell = new TableCell<Map, Void>() {
                    private final Button currentBtn = new Button("Current");
                    private final Button newBtn = new Button("New");
                    private final HBox hBox = new HBox();

                    {
                        currentBtn.setOnAction((ActionEvent event) -> {
                            Map map = getTableView().getItems().get(getIndex());
                            openImageWindow(map.getTitle(), map.getImg());
                        });
                        newBtn.setOnAction((ActionEvent event) -> {
                            Map map = getTableView().getItems().get(getIndex());
                            openImageWindow(map.getTitle(), map.getImgNew());
                        });
                        hBox.setSpacing(3);
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Map map = getTableView().getItems().get(getIndex());
                            hBox.getChildren().setAll(currentBtn);
                            if (!(map.getImgNew() == null || map.getImgNew().isEmpty())) {
                                // show new button only if image has changed
                                hBox.getChildren().add(newBtn);
                            }

                            setGraphic(hBox);
                        }
                    }
                };
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        mapsPublishedCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getVerification() ? "Yes" : concatArrow("No", "Yes")));

        // attractions table
        attractionsIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        attractionsNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        attractionsDescriptionCol.setCellValueFactory(cell -> {
            Attraction attraction = cell.getValue();
            return new SimpleStringProperty(formatStringChange(attraction.getDescription(), attraction.getDescriptionNew()));
        });
        attractionsTypeCol.setCellValueFactory(cell -> {
            Attraction attraction = cell.getValue();
            return new SimpleStringProperty(formatStringChange(attraction.getType(), attraction.getTypeNew()));
        });
        attractionsLocationCol.setCellValueFactory(cell -> {
            Attraction attraction = cell.getValue();
            return new SimpleStringProperty(formatStringChange(attraction.getLocation(), attraction.getLocationNew()));
        });
        attractionsAccessibleCol.setCellValueFactory(cell -> {
            Attraction attraction = cell.getValue();
            return new SimpleStringProperty(formatStringChange(
                    attraction.getAccessibleSpecial() ? "Yes" : "No",
                    (attraction.getAccessibleSpecialNew() == null) ? null : attraction.getAccessibleSpecialNew() ? "Yes" : "No"
            ));
        });

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

    private void openImageWindow(String title, String imagePath) {
        try {
            Input input = new ReadMapImageByPath.Input(imagePath);
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ReadMapImageByPath.Output output = response.getOutput(ReadMapImageByPath.Output.class);

            ShowImageFullWindowController.loadViewAndWait(title, output.imgBytes);
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    private static String formatStringChange(String oldVal, String newVal) {
        return (newVal == null || newVal.isEmpty()) ? oldVal : concatArrow(oldVal, newVal);
    }

    private static String concatArrow(String s1, String s2) {
        return s1 + " -> " + s2;
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

    private java.util.Map.Entry<Integer, String> getSelectedCity() {
        return citiesLV.getSelectionModel().getSelectedItem();
    }

    public void approveAction(ActionEvent actionEvent) {
        makeDecision(getSelectedCity(), true);
    }

    public void rejectAction(ActionEvent actionEvent) {
        makeDecision(getSelectedCity(), false);
    }

    public void makeDecision(java.util.Map.Entry<Integer, String> city, Boolean approved) {
        try {
            Input input = new MakeDecisionOnCityPendingApprovalCommand.Input(city.getKey(), approved);
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            response.getOutput(MakeDecisionOnCityPendingApprovalCommand.Output.class);

            (new Alert(
                    Alert.AlertType.INFORMATION,
                    "City " + city.getValue() + " " + (approved ? "approved" : "rejected") + "."
            )).show();

            citiesLV.getSelectionModel().clearSelection();
            citiesList.remove(city);
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    private static class CityListCell extends ListCell<java.util.Map.Entry<Integer, String>> {
        @Override
        protected void updateItem(java.util.Map.Entry<Integer, String> entry, boolean empty) {
            super.updateItem(entry, empty);

            if (empty || entry == null) {
                setText(null);
            } else {
                setText(entry.getValue());
            }
        }
    }
}
