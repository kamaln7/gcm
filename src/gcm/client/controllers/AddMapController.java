package gcm.client.controllers;
//aaa

import gcm.client.bin.ClientGUI;
import gcm.commands.AddMapCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.City;
import gcm.database.models.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AddMapController {


    @FXML
    private TextArea description_field;

    @FXML
    private TextField title_field;

    @FXML
    private TextField city_field;
    private City city;

    @FXML
    private TextField imgPath;

    private byte[] imageBytes;

    private Map map;


    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddMap.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Map - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static Map loadViewAndWait(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddMap.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        AddMapController controller = loader.getController();
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Map - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.showAndWait();
        return controller.getMap();
    }

    /**
     * add thw new map
     * @param event
     */
    @FXML
    void addMapToDB(ActionEvent event) {
        if (city==null || description_field.getText().equals("") ||title_field.getText().equals(null) || imageBytes==null){
            Alert alert = new Alert(Alert.AlertType.ERROR, "please fill all the fields");
            alert.show();
            return;
        }
        String description = description_field.getText();
        String title = title_field.getText();
        Integer cityId = city.getId();

        Input input = new AddMapCommand.Input(title, description, "v1", imageBytes, cityId);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            AddMapCommand.Output output = response.getOutput(AddMapCommand.Output.class);
            (new Alert(Alert.AlertType.INFORMATION, "Map successfully added!")).show();

            Map map = output.map;
            map._extraInfo.put("cityTitle", String.format("%s, %s", city.getName(), city.getCountry()));
            setMap(map);
        } catch (Map.AlreadyExists e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can not add city already exist");
            alert.show();
        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No City with that name in the DB");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    /**
     * choose the image
     * @param event
     */
    @FXML
    void fileChooser(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.png", "*.jpeg"));
        File f = fc.showOpenDialog(null);

        if (f != null) {
            try {
                BufferedImage bImage = ImageIO.read(f);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ImageIO.write(bImage, "jpg", bos);
                imageBytes = bos.toByteArray();
                imgPath.setText(f.getAbsolutePath());
            } catch (IOException e) {
                ClientGUI.showErrorTryAgain();
                e.printStackTrace();
            }
        }
    }

    /**
     * choose a city
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

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
        ((Stage) description_field.getScene().getWindow()).close();
    }
}
