package gcm.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMainScreenController {
    @FXML
    void createCity(ActionEvent event) {
        try {
            AddCityController.loadView(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void createMap(ActionEvent event) {
        try {
            AddMapController.loadView(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void editMap(ActionEvent event) {
        try {
            MapEditOptionsController.loadView(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void getMap(ActionEvent event) {
        try {
            GetMapController.loadView(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void changePrice(ActionEvent event) {
        try {
            ChangePriceController.loadView(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void Approve(ActionEvent event) {
        try {
            ApprovePriceController.loadView(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

