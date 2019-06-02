package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.FindMapByTitleAndVersionCommand;
import gcm.commands.FindMapByTitleCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class MapEditOptionsController {



    @FXML
    private TextField title_field;

    @FXML
    private TextField version_field;


    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/MapEditOptions.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void addMap(ActionEvent event) {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gcm/client/views/AddMap.fxml"));
        Parent root1 = null;
        try {
            root1 =  fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();
    }

    @FXML
    void addAttractionToMap(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gcm/client/views/AddAttraction.fxml"));
        Parent root1 = null;
        try {
            root1 =  fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(root1));
        stage.show();

    }



    @FXML
    void editMapDescription(ActionEvent event) {

    }

//    @FXML
//    void back(ActionEvent event) {
//        try {
//            MainScreenController.loadView(ClientGUI.getPrimaryStage());
//        }catch (Exception e){
//
//        }
//    }

}

