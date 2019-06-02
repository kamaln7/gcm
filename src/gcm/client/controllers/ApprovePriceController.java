package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ApprovePriceController {
    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ApprovePrice.fxml");
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
        /*String cityName = cityField.getText();
        String countyName = countryField.getText();
*/
        Input input = new ApprovePriceCommand.Input();

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ApprovePriceCommand.Output output = response.getOutput(ApprovePriceCommand.Output.class);

            while(output.rs.next()){
                oblist.add(new City(output.rs.getString("name"),
                        output.rs.getString("country"), output.rs.getDouble("purchase_price"),
                        output.rs.getDouble("subscription_price"),
                        output.rs.getDouble("new_purchase_price"),
                        output.rs.getDouble("new_sub_price")));

            }


        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }

        tableList.setItems(oblist);


    }
    /*
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
    }*/
    @FXML
    private TableView tableList;

    private ObservableList<City> oblist = FXCollections.observableArrayList();

    @FXML
    private TableColumn<City, String> city;

    @FXML
    private TableColumn<City, String> country;

    @FXML
    private TableColumn<City, Double> old_purchase;

    @FXML
    private TableColumn<City, Double> new_purchase;

    @FXML
    private TableColumn<City, Double> old_sub;

    @FXML
    private TableColumn<City, Double> new_sub;

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

