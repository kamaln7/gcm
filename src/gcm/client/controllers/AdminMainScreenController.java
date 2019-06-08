package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
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
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void createMap(ActionEvent event) {
        try {
            AddMapController.loadView(new Stage());
        } catch (IOException e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void editMap(ActionEvent event) {
        try {
            MapEditOptionsController.loadView(new Stage());
        } catch (IOException e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void getMap(ActionEvent event) {
        try {
            GetMapController.loadView(new Stage());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }


    @FXML
    void changePrice(ActionEvent event) {
        try {
            ChangePriceController.loadView(new Stage());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }


    @FXML
    void Approve(ActionEvent event) {
        try {
            ApprovePriceController.loadView(new Stage());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    public void addAttraction(ActionEvent actionEvent) {
        try {
            AddAttractionController.loadView(new Stage());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }
    @FXML
    void addExistingAttraction(ActionEvent event) {
        try {
            AddExistingAttractionToMapController.loadView(new Stage());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void createTour(ActionEvent event) {
        try {
            AddTourController.loadView(new Stage());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

}

