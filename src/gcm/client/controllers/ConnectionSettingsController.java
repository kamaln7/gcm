package gcm.client.controllers;

import gcm.client.Client;
import gcm.client.Settings;
import gcm.client.bin.ClientGUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionSettingsController implements Initializable {
    @FXML
    private TextField portTF;
    @FXML
    private TextField hostTF;
    @FXML
    private Button connectBtn;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Settings settings = ClientGUI.getClient().getSettings();
        hostTF.setText(settings.getHost());
        portTF.setText(String.valueOf(settings.getPort()));
    }

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = LoginController.class.getResource("/gcm/client/views/ConnectionSettings.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect to GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    private void connectAction(ActionEvent actionEvent) {
        try {
            connectBtn.setDisable(true);
            connectBtn.setText("Connecting...");

            String host = hostTF.getText(),
                    port = portTF.getText();
            if (host.equals("") || port.equals("")) {
                (new Alert(Alert.AlertType.ERROR, "You need to fill in the host and port.")).show();
                return;
            }

            Client client = ClientGUI.getClient();
            client.setHost(host);
            client.setPort(Integer.valueOf(port));
            client.start();

            LoginController.loadView(ClientGUI.getPrimaryStage());

        } catch (IOException e) {
            e.printStackTrace();
            (new Alert(Alert.AlertType.ERROR, "Couldn't connect to server: " + e.getMessage())).show();
        } finally {
            connectBtn.setDisable(false);
            connectBtn.setText("Connect");
        }
    }
}
