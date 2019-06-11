package gcm.client.controllers;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.RegisterUserCommand;
import gcm.commands.Response;
import gcm.commands.UpdateUserCommand;
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

public class EditProfileController implements Initializable {
    @FXML
    private TextField ccNumberTF;
    @FXML
    private TextField ccCVVTF;
    @FXML
    private ChoiceBox<Integer> ccMonthCB;
    @FXML
    private ChoiceBox<Integer> ccYearCB;

    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField first_name_field;
    @FXML
    private TextField last_name_field;


    public static void loadView(Stage primaryStage) throws IOException {
        URL url = EditProfileController.class.getResource("/gcm/client/views/EditProfile.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Register for GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void saveButtonClick(ActionEvent event) {
        String email = emailField.getText(),
                phone = phoneField.getText(),
                first_name = first_name_field.getText(),
                last_name = last_name_field.getText(),
                ccNumber = ccNumberTF.getText(),
                ccCVV = ccCVVTF.getText();

        Integer ccMonth = ccMonthCB.getSelectionModel().getSelectedItem(),
                ccYear = ccYearCB.getSelectionModel().getSelectedItem();

        Input input = new UpdateUserCommand.Input(ClientGUI.getCurrentUser().getId(),email, phone, first_name, last_name, ccNumber, ccCVV, ccMonth, ccYear);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            UpdateUserCommand.Output output = response.getOutput(UpdateUserCommand.Output.class);
            ClientGUI.setCurrentUser(output.user);
            close();
        } catch (User.AlreadyExists e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Username already exists.");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }
    private void close(){
        Stage stage = (Stage) first_name_field.getScene().getWindow();
        // do what you have to do
        stage.close();
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
       User user = ClientGUI.getCurrentUser();
       first_name_field.setText(user.getFirst_name());
       last_name_field.setText(user.getLast_name());
       emailField.setText(user.getEmail());
       phoneField.setText(user.getPhone());
       ccNumberTF.setText(user.getCcNumber());
       ccCVVTF.setText(user.getCcCVV());
        Integer currentYear = Integer.parseInt(new SimpleDateFormat("yy").format(new Date()));
        ccMonthCB.getItems().setAll(IntStream.range(1, 13).parallel().mapToObj(Integer::valueOf).collect(Collectors.toList()));
        ccYearCB.getItems().setAll(IntStream.range(currentYear, currentYear + 6).parallel().mapToObj(Integer::valueOf).collect(Collectors.toList()));
        ccMonthCB.setValue(user.getCcMonth());
        ccYearCB.setValue(user.getCcYear());
    }
}

