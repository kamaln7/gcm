package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.AddMapCommand;
import gcm.commands.Input;
import gcm.commands.Response;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.xml.soap.Text;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ShowMapController {

    @FXML
    private  Text title;

    @FXML
    private  Text cityId;


    @FXML
    private  Text version;

    @FXML
    private  Text description;

    @FXML
    private ImageView img;

    @FXML
    void back(ActionEvent event) {
        try {
            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        }catch (Exception e){

        }
    }

    public void setMapFields(Map map) {
        title.setTextContent(map.getTitle());
        version.setTextContent(map.getVersion());
        description.setTextContent(map.getDescription());
        cityId.setTextContent(String.valueOf(map.getCityId()));
    }


    public static void loadView(Stage primaryStage, Map map) throws IOException {
        URL url = ShowMapController.class.getResource("/gcm/client/views/ShowMap.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        ShowMapController controller = loader.getController();
        controller.setMapFields(map);

        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();

    }
}



