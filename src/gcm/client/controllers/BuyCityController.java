package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class BuyCityController {

    @FXML
    private Text subscriptionprice;

    @FXML
    private Text purchaseprice;

    private City city;

    public void setPrice(City city){

        purchaseprice.setText("subscription price is: " + city.getSubscriptionPrice());
        subscriptionprice.setText("purchase price is: " + city.getPurchasePrice());

    }

    public void setCity(City city){

        this.city = city;
    }






    public static void loadView(Stage primaryStage, City city) throws IOException {
        URL url = BuyCityController.class.getResource("/gcm/client/views/BuyCity.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        BuyCityController controller = loader.getController();
        controller.setPrice(city);

        controller.setCity(city);


        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void addsubscriptiontoDB(ActionEvent event) {

        int city_id = city.getId();
        int user_id = ClientGUI.getCurrentUser().getId();

        Date from_date = new Date();
        Calendar to_date = Calendar.getInstance();
        to_date.setTime(from_date);
        to_date.add(Calendar.MONTH, 6);

        Input input = new AddSubscriptionToDataBaseCommand.Input(user_id, city_id, from_date, to_date.getTime());

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            AddCityToDataBaseCommand.Output output = response.getOutput(AddCityToDataBaseCommand.Output.class);

        }  catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }

    }


    @FXML
    void addpurchasetoDB(ActionEvent event) {

        int city_id = city.getId();
        int user_id = ClientGUI.getCurrentUser().getId();

        Input input = new AddPurchaseToDataBaseCommand.Input(user_id,city_id);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            AddPurchaseToDataBaseCommand.Output output = response.getOutput(AddPurchaseToDataBaseCommand.Output.class);


            new Alert(Alert.AlertType.WARNING, "Saed get this.city and show the maps").show();
            //ShowCitiesController.loadView(ClientGUI.getPrimaryStage(),this.city);

        }  catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }



    }


    @FXML
    void backtomainscreen(ActionEvent event) {

        try {
            MainScreenController.loadView(ClientGUI.getPrimaryStage());
        }catch (Exception e){

        }

    }



}

