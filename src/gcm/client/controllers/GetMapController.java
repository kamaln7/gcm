package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.FindMapByTitleCommand;
import gcm.commands.Input;
import gcm.commands.LoginUserCommand;
import gcm.commands.Response;
import gcm.database.models.Map;
import gcm.database.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.xml.soap.Text;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class GetMapController {


    @FXML
    private Button showMap;

    @FXML
    private TextField title;


    @FXML
    void showMap(ActionEvent event) {

        Input input = new FindMapByTitleCommand.Input(title.getText());

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindMapByTitleCommand.Output output = response.getOutput(FindMapByTitleCommand.Output.class);


            ShowMapController.loadView(ClientGUI.getPrimaryStage(), output.map);
        } catch (User.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect login details.");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void back(ActionEvent event) {
        try {
            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        }catch (Exception e){

        }
    }



    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/GetMap.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}

