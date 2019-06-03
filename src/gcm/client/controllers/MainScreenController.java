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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gcm/client/views/AddCity.fxml"));
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
    void createMap(ActionEvent event) {
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
    void editMap(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gcm/client/views/MapEditOptions.fxml"));
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
            GetMapController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void logout(ActionEvent event) {
        try {
            LoginController.loadView(ClientGUI.getPrimaryStage());
        }catch (Exception e){

        }
    }




    @FXML
    void changePrice(ActionEvent event) {
        try {
            ChangePriceController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void Approve(ActionEvent event) {
        try {
            ApprovePriceController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

