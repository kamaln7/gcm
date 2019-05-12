package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.LoginUserCommand;
import gcm.commands.Response;
import gcm.database.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginController {
    @FXML
    private Hyperlink openRegisterViewBtn;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = LoginController.class.getResource("/gcm/client/views/Login.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login to GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void loginButtonClick(ActionEvent event) {
        String username = usernameField.getText(),
                password = passwordField.getText();

        Input input = new LoginUserCommand.Input(username, password);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            LoginUserCommand.Output output = response.getOutput(LoginUserCommand.Output.class);

            System.out.printf("%s", output.user.getEmail());
            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        } catch (User.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Incorrect login details.");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openRegisterView(ActionEvent event) {
        try {
            RegisterController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
        }
    }
}

