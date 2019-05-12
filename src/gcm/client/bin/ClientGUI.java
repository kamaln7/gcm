package gcm.client.bin;

import gcm.client.Client;
import gcm.client.controllers.LoginController;
import gcm.database.models.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

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

    @Override
    // start the JavaFX GUI
    public void start(Stage primaryStage) throws Exception {
        setPrimaryStage(primaryStage);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                try {
                    client.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Platform.exit();
                System.exit(0);
            }
        });

        LoginController.loadView(primaryStage);
        client.chatIF.display("Started GUI");
    }
}
