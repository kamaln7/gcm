package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.AddCityToDataBaseCommand;
import gcm.commands.Input;
import gcm.commands.LoginUserCommand;
import gcm.commands.Response;
import gcm.database.models.City;
import gcm.database.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class AddCityController {
    @FXML
    private TextField subscription_price_field;

    @FXML
    private TextField purchase_price_field;
    @FXML
    private TextField namefield;

    @FXML
    private TextField countryfield;

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddCity.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    @FXML
    void addCityToDB(ActionEvent event) {
        String name = namefield.getText();
        String county = countryfield.getText();
        if (!validate(subscription_price_field.getText()) || !validate(purchase_price_field.getText())){
            Alert alert = new Alert(Alert.AlertType.ERROR, "prices are Numbers only");
            alert.show();
            return;
        }
        double subscription_price = Double.parseDouble(subscription_price_field.getText());
        double purchase_price = Double.parseDouble(purchase_price_field.getText());



        Input input = new AddCityToDataBaseCommand.Input(name, county, subscription_price, purchase_price);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            AddCityToDataBaseCommand.Output output = response.getOutput(AddCityToDataBaseCommand.Output.class);

            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        } catch (City.AlreadyExists e) {
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
