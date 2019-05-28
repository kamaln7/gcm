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
    private static Text title;

    @FXML
    private static Text one_off_price;

    @FXML
    private static Text subscription_Price;

    @FXML
    private static Text version;

    @FXML
    private Text description;

    @FXML
    private ImageView img;

    @FXML
    void back(ActionEvent event) {
        try {
            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        }catch (Exception e){

        }
    }



    public  void loadView(Stage primaryStage, Map map) throws IOException {
        title.setTextContent(map.getTitle());
        one_off_price.setTextContent(String.valueOf(map.getOne_off_price()));
        subscription_Price.setTextContent(String.valueOf(map.getSubscription_price()));
        version.setTextContent(map.getVersion());
        description.setTextContent(map.getDescription());


        File file = new File(map.getImg());
        Image image = new Image(file.toURI().toString());
        img = new ImageView(image);


        URL url = MainScreenController.class.getResource("/gcm/client/views/ShowMap.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}

