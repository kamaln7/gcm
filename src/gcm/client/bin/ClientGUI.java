package gcm.client.bin;

import gcm.client.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;

public class ClientGUI extends Application {
    private static Client client;

    public static void setClient(Client c) {
        ClientGUI.client = c;
    }

    public static Client getClient() {
        return ClientGUI.client;
    }

    @Override
    // start the JavaFX GUI
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getResource("/gcm/client/views/Login.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
        client.chatIF.display("Started GUI");
    }
}
