package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.ChangePriceCommand;
import gcm.commands.FindCityCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.City;
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

public class AprovePriceController {
    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ChangePrice.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    @FXML
    void getPrice(ActionEvent event) {
        String cityName = cityField.getText();
        String countyName = countryField.getText();

        Input input = new FindCityCommand.Input(cityName, countyName);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindCityCommand.Output output = response.getOutput(FindCityCommand.Output.class);
            Double price1 = output.city.getPurchase_price();
            Double price2 = output.city.getSubscription_price();
            PurchasePriceField.setText(String.format("%.2f",price1));
            SubPriceField.setText(String.format("%.2f",price2));

        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }


    }
    @FXML
    void UpdatePrice(ActionEvent event) {
        String cityName = cityField.getText();
        String countyName = countryField.getText();
        double new_purchase_price = Double.parseDouble(NewPurchasePriceField.getText());
        double new_sub_price = Double.parseDouble(NewSubPriceField.getText());

        Input input = new ChangePriceCommand.Input(cityName, countyName, new_purchase_price, new_sub_price);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ChangePriceCommand.Output output = response.getOutput(ChangePriceCommand.Output.class);

            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }
    @FXML
    private TableView<cities> tableList;

    @FXML
    void ApprovePrice(ActionEvent event) {

    }

    @FXML
    void DeclinePrice(ActionEvent event) {

    }


    @FXML
    void back(ActionEvent event) {
        try {
            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

