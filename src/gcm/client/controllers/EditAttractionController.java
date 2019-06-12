package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.commands.UpdateAttractionCommand;
import gcm.database.models.Attraction;
import gcm.database.models.City;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class EditAttractionController {

    @FXML
    private TextField attractionTF;

    @FXML
    private Text Attraction_Name;

    @FXML
    private TextField Attraction_Location;

    @FXML
    private TextArea description;

    @FXML
    public ChoiceBox<String> attraction_choiceBox;

    @FXML
    private ChoiceBox<String> accessible_choiceBox;
    @FXML
    private TextField city_field;

    private City city;
    private Attraction attraction;

    private void setAttractionValues() {
        Attraction_Name.setText(attraction.getName());
        Attraction_Location.setText(attraction.getLocation());
        description.setText(attraction.getDescription());
        accessible_choiceBox.setValue(attraction.getAccessibleSpecial() ? "Yes" : "No");
        attraction_choiceBox.setValue(attraction.getType());
    }

    /**
     * initialize the viewer
     */
    public void initialize() {
        attraction_choiceBox.getItems().setAll(Attraction.types);
        attraction_choiceBox.getSelectionModel().selectFirst();

        accessible_choiceBox.getItems().add("Yes");
        accessible_choiceBox.getItems().add("No");
        accessible_choiceBox.setValue("No");
    }

    /**
     * load the viewer
     */
    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/EditAttraction.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Attraction - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * load the viewer
     */
    public static Attraction loadViewAndWait(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddAttraction.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        EditAttractionController controller = loader.getController();
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Attraction - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.showAndWait();
        return controller.getAttraction();
    }

    /**
     * choose a city
     *
     * @param actionEvent
     */
    public void openCityPicker(ActionEvent actionEvent) {
        try {
            City city = AdminTablePickerCityController.loadViewAndWait(new Stage());
            this.city = city;
            city_field.setText(city.toString());
        } catch (IOException e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }

    /**
     * choose the attraction
     *
     * @param event
     */
    @FXML
    void chooseAttraction(ActionEvent event) {
        if (this.city == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Choose city first");
            alert.show();
            return;
        }

        try {
            Attraction attraction = AdminTablePickerAttractionForCityController.loadViewAndWait(new Stage(), this.city.getId());
            if (attraction == null) return;

            this.attraction = attraction;
            this.attractionTF.setText(attraction.getName());
            setAttractionValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * close the viewer
     *
     * @param event
     */
    @FXML
    private void finishJop(ActionEvent event) {
        if (this.city == null || this.attraction == null || this.Attraction_Name.getText().equals("") || this.Attraction_Location.getText().equals("") || this.description.getText().equals("")) {
            (new Alert(Alert.AlertType.INFORMATION, "Select an attraction first and fill in all the fields.")).show();
            return;
        }
        try {
            boolean accessibility = getAccessibility();

            Input input2 = new UpdateAttractionCommand.Input(attraction.getId(), attraction_choiceBox.getValue(), Attraction_Location.getText(), accessibility, description.getText());
            Response response2 = ClientGUI.getClient().sendInputAndWaitForResponse(input2);
            UpdateAttractionCommand.Output output2 = response2.getOutput(UpdateAttractionCommand.Output.class);

            (new Alert(Alert.AlertType.INFORMATION, "Attraction edit request is sent and now awaiting approval!")).show();
            close();
        } catch (Attraction.AlreadyExists x) {
            (new Alert(Alert.AlertType.ERROR, "Attraction already exists.")).show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    private void close() {
        Stage stage = (Stage) attraction_choiceBox.getScene().getWindow();
        // do what you have to do
        stage.close();
    }


    private boolean getAccessibility() {
        if (accessible_choiceBox.getValue().equals("Yes")) {
            return true;
        }
        return false;
    }

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
        ((Stage) description.getScene().getWindow()).close();
    }
}

