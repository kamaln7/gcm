package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.database.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminMainScreenController implements Initializable {

    @FXML
    private TabPane adminTabPane;
    @FXML
    private Tab adminContentManagerTab;
    @FXML
    private Tab adminCompanyManagerTab;
    @FXML
    private Tab adminContentEmployeeTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = ClientGUI.getCurrentUser();
        if (!user.hasRole("content_manager")) {
            adminTabPane.getTabs().remove(adminContentManagerTab);
        }
        if (!user.hasRole("company_manager")) {
            adminTabPane.getTabs().remove(adminCompanyManagerTab);
        }
    }

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
    void changePrice(ActionEvent event) {
        try {
            ChangePriceController.loadView(new Stage());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }


    @FXML
    void showUserInfo(ActionEvent event) {
        try {
            ShowUserInfoController.loadView(new Stage());
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

    @FXML
    void showActivityReport(ActionEvent event) {
        try {
            ActivityReportController.loadView(new Stage());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    void Edit_Attraction(ActionEvent event) {
        try {
            EditAttractionController.loadView(new Stage());
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    @FXML
    private void openCityPendingChanges(ActionEvent event) {
        ClientGUI.showErrorTryAgain("not yet");
    }
}

