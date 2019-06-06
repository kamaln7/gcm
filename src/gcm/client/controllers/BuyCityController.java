package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.AddCityToDataBaseCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.City;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.xml.soap.Text;
import java.io.IOException;
import java.net.URL;

public class BuyCityController {

    @FXML
    private Text subscriptionprice;


    @FXML
    void addsubscriptiontoDB(ActionEvent event) {

    }


    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/BuyCityCity.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }



}

