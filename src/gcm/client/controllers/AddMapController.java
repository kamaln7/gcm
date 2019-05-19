package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import gcm.database.models.Map;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AddMapController {

    @FXML
    private TextArea description_field;

    @FXML
    private TextField title_field;

    @FXML
    private TextField one_off_price_field;

    @FXML
    private TextField subscription_price_field;

    @FXML
    private TextField version_field;

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddMap.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    @FXML
    void addMapToDB(ActionEvent event) {
        String description = description_field.getText();
        String title = title_field.getText();
        String version = version_field.getText();
        try {
            if (!validate(one_off_price_field.getText()) || !validate(subscription_price_field.getText())) {
                throw new Map.WrongType();
            }
        }catch (Map.WrongType e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "The price fields are numbers only");
            alert.show();
            return;
        }
        int one_off_price = Integer.parseInt(one_off_price_field.getText());
        int subscription_price = Integer.parseInt(subscription_price_field.getText());

        Input input = new AddMapCommand.Input(title,description,version,one_off_price,subscription_price);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            AddMapCommand.Output output = response.getOutput(AddMapCommand.Output.class);

            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        } catch (Map.AlreadyExists e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Can not add city already exist");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }
    private boolean validate(String text)
    {
        return text.matches("[0-9]*");
    }

}

