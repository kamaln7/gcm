package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.database.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainScreenController implements Initializable {
    @FXML
    private Text userDetailsText;

    @FXML
    private TabPane mainTabPane;

    //This method is called upon fxml load
    public void initialize(URL location, ResourceBundle resources) {
        User user = ClientGUI.getCurrentUser();
        // set welcome text
        userDetailsText.setText(String.format(
                "Welcome, %s!",
                user.getUsername()
        ));

        // show admin tab if has access
        if (user.hasRole("employee")) {
            try {
                Tab tab = new Tab();
                tab.setText("Admin");
                tab.setContent(FXMLLoader.load(this.getClass().getResource("/gcm/client/views/AdminMainScreen.fxml")));
                mainTabPane.getTabs().add(tab);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // show order history tab only if user is a user role
        if (user.hasExactRole("user")) {
            try {
                Tab tab = new Tab();
                tab.setText("Order History");
                tab.setContent(FXMLLoader.load(this.getClass().getResource("/gcm/client/views/OrderHistory.fxml")));
                mainTabPane.getTabs().add(tab);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Tab tab = new Tab();
            tab.setText("Profile");

            URL url = MainScreenController.class.getResource("/gcm/client/views/UserProfile.fxml");
            FXMLLoader loader = new FXMLLoader(url);
            tab.setContent(loader.load());

            UserProfileController controller = loader.getController();
            controller.setFields(ClientGUI.getCurrentUser());

            mainTabPane.getTabs().add(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/MainScreen.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setTitle("GCM 2019");
        primaryStage.centerOnScreen();
        primaryStage.show();
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
}

