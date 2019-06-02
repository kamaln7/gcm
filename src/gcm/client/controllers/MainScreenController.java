package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    @FXML
    public Tab adminTab;

    @FXML
    private Text userDetailsText;

    //This method is called upon fxml load
    public void initialize(URL location, ResourceBundle resources) {
        // set welcome text
        userDetailsText.setText(String.format(
                "Welcome, %s!",
                ClientGUI.getCurrentUser().getUsername()
        ));

        // show admin tab if has access
        adminTab.setDisable(!ClientGUI.getCurrentUser().hasRole("employee"));
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

    @FXML
    void createCity(ActionEvent event) {
        try {
            AddCityController.loadView(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void createMap(ActionEvent event) {
        try {
            AddMapController.loadView(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void editMap(ActionEvent event) {
        try {
            MapEditOptionsController.loadView(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void logOut(ActionEvent actionEvent) {
        try {
            ClientGUI.getClient().logout();
            LoginController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void getMap(ActionEvent event) {
        try {
            ClientGUI.getClient().logout();
            GetMapController.loadView(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

