package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.RegisterUserCommand;
import gcm.commands.Response;
import gcm.database.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RegisterController implements Initializable {
    @FXML
    private TextField ccNumberTF;
    @FXML
    private TextField ccCVVTF;
    @FXML
    private ChoiceBox<Integer> ccMonthCB;
    @FXML
    private ChoiceBox<Integer> ccYearCB;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField first_name_field;
    @FXML
    private TextField last_name_field;


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
                phone = phoneField.getText(),
                first_name = first_name_field.getText(),
                last_name = last_name_field.getText(),
                ccNumber = ccNumberTF.getText(),
                ccCVV = ccCVVTF.getText();

        Integer ccMonth = ccMonthCB.getSelectionModel().getSelectedItem(),
                ccYear = ccYearCB.getSelectionModel().getSelectedItem();

        Input input = new RegisterUserCommand.Input(username, password, email, phone, first_name, last_name, ccNumber, ccCVV, ccMonth, ccYear);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            RegisterUserCommand.Output output = response.getOutput(RegisterUserCommand.Output.class);

            ClientGUI.setCurrentUser(output.user);
            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        } catch (User.AlreadyExists e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Username already exists.");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void openLoginView(ActionEvent event) {
        try {
            LoginController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameField.setTextFormatter(new TextFormatter<String>(change -> {
            if (!change.isAdded()) {
                return change;
            }

            // if the text will be more longer than 255, exit
            if (change.getControlNewText().length() > 255) {
                return null;
            }

            // otherwise go on
            return change;
        }));

        Integer currentYear = Integer.parseInt(new SimpleDateFormat("yy").format(new Date()));
        ccMonthCB.getItems().setAll(IntStream.range(1, 13).parallel().mapToObj(Integer::valueOf).collect(Collectors.toList()));
        ccYearCB.getItems().setAll(IntStream.range(currentYear, currentYear + 6).parallel().mapToObj(Integer::valueOf).collect(Collectors.toList()));
    }
}

