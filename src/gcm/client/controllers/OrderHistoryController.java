package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.database.models.City;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class OrderHistoryController {

    public static void loadView(Stage primaryStage, City city) throws IOException {
        URL url = OrderHistoryController.class.getResource("/gcm/client/views/OrderHistory.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);


        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void active_subscriptions(ActionEvent event) {
        try {
            ActiveSubscriptionsController.loadView(new Stage(), ClientGUI.getCurrentUser().getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void purchase_history(ActionEvent event) {
        try {
            ShowPurchaseHistoryController.loadView(new Stage(), ClientGUI.getCurrentUser().getId());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void subscription_history(ActionEvent event) {
        try {
            ShowSubscriptionHistoryController.loadView(new Stage(), ClientGUI.getCurrentUser().getId());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }
}

