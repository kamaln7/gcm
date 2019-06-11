package gcm.client.bin;

import gcm.client.Client;
import gcm.client.controllers.ConnectionSettingsController;
import gcm.database.models.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    private static Client client;
    private static Stage primaryStage;
    private static User currentUser = null;

    public static void setClient(Client c) {
        client = c;
    }

    public static Client getClient() {
        return client;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage ps) {
        primaryStage = ps;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        ClientGUI.currentUser = currentUser;
    }

    public static void showErrorTryAgain() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "There was an error. Please try again.");
        alert.show();
    }

    @Override
    // start the JavaFX GUI
    public void start(Stage primaryStage) throws Exception {
        setPrimaryStage(primaryStage);

        ConnectionSettingsController.loadView(primaryStage);
        client.chatIF.display("Started GUI");

        primaryStage.setOnCloseRequest(t -> {
            try {
                client.logout();
                client.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
    }
}
