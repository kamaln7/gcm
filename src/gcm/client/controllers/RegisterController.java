package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.RegisterUserCommand;
import gcm.commands.Response;
import gcm.database.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = RegisterController.class.getResource("/gcm/client/views/Register.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Register for GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void registerButtonClick(ActionEvent event) {
        String username = usernameField.getText(),
                password = passwordField.getText(),
                email = emailField.getText(),
                phone = phoneField.getText();

        Input input = new RegisterUserCommand.Input(username, password, email, phone);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            RegisterUserCommand.Output output = response.getOutput(RegisterUserCommand.Output.class);

            System.out.printf("%s", output.user.getId());
            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        } catch (User.AlreadyExists e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Username already exists.");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void openLoginView(ActionEvent event) {
        try {
            LoginController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
        }
    }
}

