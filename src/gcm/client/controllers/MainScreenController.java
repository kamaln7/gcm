package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    @FXML
    private Text userDetailsText;

    //This method is called upon fxml load
    public void initialize(URL location, ResourceBundle resources) {
        userDetailsText.setText(String.format(
                "Welcome to GCM, %s!",
                ClientGUI.getCurrentUser().getUsername()
        ));
    }

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/MainScreen.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}

