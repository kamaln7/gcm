package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.ChangePriceCommand;
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

public class ChangePriceController {
    private City city;
    @FXML
    private TextField cityField;

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ChangePrice.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Change City Price");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void UpdatePrice(ActionEvent event) {
        double new_purchase_price = Double.parseDouble(NewPurchasePriceField.getText());
        double new_sub_price = Double.parseDouble(NewSubPriceField.getText());

        Input input = new ChangePriceCommand.Input(this.city.getId(), new_purchase_price, new_sub_price);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            response.getOutput(ChangePriceCommand.Output.class);

            new Alert(Alert.AlertType.INFORMATION, "Price change request sent!").showAndWait();
            ((Stage) NewPurchasePriceField.getScene().getWindow()).close();
        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    private TextField NewSubPriceField;

    @FXML
    private TextField NewPurchasePriceField;
    @FXML
    private TextField PurchasePriceField;
    @FXML
    private TextField SubPriceField;

    public void openCityPicker(ActionEvent actionEvent) {
        try {
            City city = AdminTablePickerCityController.loadViewAndWait(new Stage());
            this.city = city;
            cityField.setText(city.toString());
            PurchasePriceField.setText(String.format("%.2f", this.city.getPurchasePrice()));
            SubPriceField.setText(String.format("%.2f", this.city.getSubscriptionPrice()));
            NewPurchasePriceField.setText(String.format("%.2f", this.city.getNewPurchasePrice()));
            NewSubPriceField.setText(String.format("%.2f", this.city.getNewSubscriptionPrice()));
        } catch (IOException e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }
}

