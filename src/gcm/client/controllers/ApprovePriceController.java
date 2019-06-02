package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class ApprovePriceController {
    private ObservableList<City> oblist = FXCollections.observableArrayList();
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


        Input input = new ApprovePriceCommand.Input();
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ApprovePriceCommand.Output output = response.getOutput(ApprovePriceCommand.Output.class);


        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }


    }

    @FXML
    private TableView tableList;



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

/*
@Override
public void initialize(URL url, ResourceBundle rb)
{


    try {
        ResultSet rs = City.findUnapproved();


            while (rs.next()){

                // print each line of data to check if it's right
                System.out.println(rs.getString("name") + rs.getDouble("city"));

                // make a new city object with each row in the database CityDB
                oblist.add(new City(rs.getString("name"),
                        rs.getString("country"), rs.getDouble("old_purchase"),
                        rs.getDouble("old_sub"),
                        rs.getDouble("new_purchase"),
                        rs.getDouble("new_sub")));

               // City cty = new City(result.getString("cityname"), "" + result.getDouble("population"));


            }




    } catch (City.NotFound e) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
        alert.show();
    } catch (Exception e) {
        ClientGUI.showErrorTryAgain();
        e.printStackTrace();
    }




}*/

}

