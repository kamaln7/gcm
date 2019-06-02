package gcm.client.controllers;
//aaa
import gcm.client.bin.ClientGUI;
import gcm.commands.*;
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
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class AddMapController {

    @FXML
    private TextArea description_field;

    @FXML
    private TextField title_field;


    @FXML
    private TextField city_field;

    @FXML
    private TextField version_field;

    @FXML
    private TextField imgPath;

    private byte[] imageBytes;


    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddMap.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    @FXML
    void addMapToDB(ActionEvent event) {
        String description = description_field.getText();
        String title = title_field.getText();
        String version = version_field.getText();
        String cityName = city_field.getText();


        Input input = new AddMapCommand.Input(title, description, version, imageBytes, cityName);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            AddMapCommand.Output output = response.getOutput(AddMapCommand.Output.class);

            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        } catch (Map.AlreadyExists e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can not add city already exist");
            alert.show();
        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No City with that name in the DB");
            alert.show();
        }catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }



    private boolean validate(String text)
    {
        return text.matches("[0-9]*");
    }

    @FXML
    void fileChooser(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.png"));
        File f = fc.showOpenDialog(null);

        if (f != null){
            imgPath.setText(f.getAbsolutePath());
            try {
                imageBytes = Files.readAllBytes(f.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Could not read image");
                alert.show();
            }
        }
    }
}

