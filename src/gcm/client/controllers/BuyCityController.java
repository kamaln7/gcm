package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import gcm.database.models.Subscription;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
        double price = city.getSubscriptionPrice();

        Date temp;


        Date from_date = new Date();
        Calendar to_date = Calendar.getInstance();
        to_date.setTime(from_date);
        to_date.add(Calendar.MONTH, 6);



        Input input1 = new FindSubscriptionCommand.Input(user_id, city_id,from_date);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input1);
            FindSubscriptionCommand.Output output = response.getOutput(FindSubscriptionCommand.Output.class);
            // new Alert(Alert.AlertType.WARNING, "Subscriptio already exists and ends at + " + output.subscription.getToDate()).show();
            try {
                RenewSubscriptionController.loadView(ClientGUI.getPrimaryStage(), output.subscription, city.getSubscriptionPrice());
            }
            catch (Exception e) {
                ClientGUI.showErrorTryAgain();
                e.printStackTrace();
            }
        }
        catch (Subscription.NotFound e){ // if not found then insert a new subscription
            Input input = new AddSubscriptionToDataBaseCommand.Input(user_id, city_id, from_date, to_date.getTime(), price, false);
            try {
                Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
                AddSubscriptionToDataBaseCommand.Output output = response.getOutput(AddSubscriptionToDataBaseCommand.Output.class);
                new Alert(Alert.AlertType.INFORMATION, " your subscription end at: " + to_date.getTime()).show();
                try {
                    MainScreenController.loadView(ClientGUI.getPrimaryStage());
                }catch (Exception e1){}
            }
            catch (Exception e1) {
                ClientGUI.showErrorTryAgain();
                e.printStackTrace();
            }
        }

        catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }







    }


    @FXML
    void addpurchasetoDB(ActionEvent event) {

        int city_id = city.getId();
        int user_id = ClientGUI.getCurrentUser().getId();
        double price = city.getPurchasePrice();

        Input input = new AddPurchaseToDataBaseCommand.Input(user_id,city_id,price);

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            AddPurchaseToDataBaseCommand.Output output = response.getOutput(AddPurchaseToDataBaseCommand.Output.class);


            // show the attractions to the costumer
            ShowBoughtCityAttractionsController.loadView(new Stage(),this.city.getId());
            //show the maps to the costumer
            ShowBoughtCityMapsController.loadView(new Stage(),this.city.getId());

        }  catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }



    }


    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) subscriptionprice.getScene().getWindow();
        // do what you have to do
        stage.close();
    }



}

