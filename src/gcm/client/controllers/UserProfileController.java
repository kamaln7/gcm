package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.database.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserProfileController implements Initializable {
    @FXML
    private Text first_name_field;

    @FXML
    private Text last_name_field;

    @FXML
    private Text username_field;

    @FXML
    private Text email_field;

    @FXML
    private Text phone_field;

    @FXML
    private HBox buttonsBox;

    User myUser;

    @FXML
    void active_subscriptions(ActionEvent event) {
        try {
            ActiveSubscriptionsController.loadView(new Stage(), myUser.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void purchase_history(ActionEvent event) {
        try {
            ShowPurchaseHistoryController.loadView(new Stage(), myUser.getId());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void subscription_history(ActionEvent event) {
        try {
            ShowSubscriptionHistoryController.loadView(new Stage(), myUser.getId());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    public void setFields(User user) {
        myUser = user;
        first_name_field.setText(myUser.getFirst_name());
        last_name_field.setText(myUser.getLast_name());
        username_field.setText(myUser.getUsername());
        email_field.setText(myUser.getEmail());
        phone_field.setText(myUser.getPhone());
    }

    public static void loadView(Stage primaryStage, User user) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/UserProfile.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        UserProfileController controller = loader.getController();
        controller.setFields(user);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Your Profile - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (ClientGUI.getCurrentUser().hasRole("employee")) {
            buttonsBox.setVisible(false);
        }
    }
}

