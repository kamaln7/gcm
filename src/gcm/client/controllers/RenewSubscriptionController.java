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

public class RenewSubscriptionController {

    @FXML
    private Text renewtext;

    private Subscription subscription;

    private double price;

    public void setText(Subscription subscription){

        renewtext.setText("you already have subscription for this city and ends at : " + subscription.getToDate());


    }



    public void setSubscription(Subscription subscription){

        this.subscription = subscription;
    }


    public void setPrice(double price)
    {
        this.price = price;
    }










    public static void loadView(Stage primaryStage, Subscription subscription, double price) throws IOException {
        URL url = RenewSubscriptionController.class.getResource("/gcm/client/views/RenewSubscription.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        RenewSubscriptionController controller = loader.getController();
        controller.setText(subscription);

        controller.setSubscription(subscription);

        controller.setPrice(price);


        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    void renewSubscription(ActionEvent event) {

        int city_id = subscription.getCityId();
        int user_id = ClientGUI.getCurrentUser().getId();
        double newprice = this.price * 0.9;

        Date from_date = subscription.getToDate();
        Calendar to_date = Calendar.getInstance();
        to_date.setTime(from_date);
        to_date.add(Calendar.MONTH, 6);






        Input input = new AddSubscriptionToDataBaseCommand.Input(user_id, city_id, from_date, to_date.getTime(), newprice );

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            AddSubscriptionToDataBaseCommand.Output output = response.getOutput(AddSubscriptionToDataBaseCommand.Output.class);

            new Alert(Alert.AlertType.INFORMATION, "now your subscription end at: " + to_date.getTime()).show();
            try {
                MainScreenController.loadView(ClientGUI.getPrimaryStage());
            }catch (Exception e){

            }

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

