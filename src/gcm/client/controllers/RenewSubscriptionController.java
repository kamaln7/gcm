package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.AddSubscriptionToDataBaseCommand;
import gcm.commands.FindSubscriptionCommand;
import gcm.commands.Input;
import gcm.commands.Response;
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


    private Subscription subscription, newSubscription;

    private double price;


    public void setText(Subscription subscription) {
        renewtext.setText("You already have a subscription for this city. It ends at: " + subscription.getToDate());
    }

    public void setSubscription(Subscription subscription) {

        this.subscription = subscription;
    }


    public void setPrice(double price) {
        this.price = price;
    }

    public static Subscription loadView(Stage primaryStage, Subscription subscription, double price) throws IOException {
        return loadView(primaryStage, subscription, price, false);
    }

    public static Subscription loadView(Stage primaryStage, Subscription subscription, double price, Boolean wait) throws IOException {
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
        primaryStage.setTitle("Renew Subscription - GCM 2019");
        primaryStage.setResizable(false);
        if (wait) {
            primaryStage.showAndWait();
        } else {
            primaryStage.show();
        }
        return controller.getNewSubscription();
    }

    @FXML
    /**
     * checks if the user already made a renew, if not then he renew the subscription for another 6 months
     */
    void renewSubscription(ActionEvent event) {

        int city_id = subscription.getCityId();
        int user_id = ClientGUI.getCurrentUser().getId();
        double newprice = this.price * 0.9;

        Date from_date = subscription.getToDate();
        Calendar to_date = Calendar.getInstance();
        to_date.setTime(from_date);
        to_date.add(Calendar.MONTH, 6);
        Calendar temp_date = Calendar.getInstance();
        temp_date.setTime(from_date);
        temp_date.add(Calendar.SECOND, 1);

        Input input1 = new FindSubscriptionCommand.Input(user_id, city_id, temp_date.getTime());

        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input1);
            FindSubscriptionCommand.Output output = response.getOutput(FindSubscriptionCommand.Output.class);
            new Alert(Alert.AlertType.INFORMATION, "You already renewed your subscription for this city. You need to renew the latest subscription!").show();
        } catch (Subscription.NotFound e) {
            Input input = new AddSubscriptionToDataBaseCommand.Input(user_id, city_id, from_date, to_date.getTime(), price, true);
            try {
                Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
                AddSubscriptionToDataBaseCommand.Output output = response.getOutput(AddSubscriptionToDataBaseCommand.Output.class);
                newSubscription = output.subscription;
                new Alert(Alert.AlertType.INFORMATION, "Your new subscription ends at: " + to_date.getTime()).show();
            } catch (Exception e1) {
                ClientGUI.showErrorTryAgain();
                e.printStackTrace();
            }
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        } finally {
            ((Stage) renewtext.getScene().getWindow()).close();
        }


    }

    @FXML
    void close(ActionEvent event) {

        Stage stage = (Stage) renewtext.getScene().getWindow();
        // do what you have to do
        stage.close();

    }


    public Subscription getNewSubscription() {
        return newSubscription;
    }
}

