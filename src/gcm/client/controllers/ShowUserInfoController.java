package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.commands.ShowUserInfoCommand;
import gcm.database.models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class ShowUserInfoController implements Initializable {


    @FXML
    private TableView<User> table;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private TableColumn<User, String> userNameColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> phoneColumn;

    @FXML
    private TableColumn<User, Date> userSinceColumn;

    @FXML
    private TableColumn<User, String> purchasesColumn;

    @FXML
    private TableColumn<User, String> subscriptionsColumn;

    @FXML
    private TextField searchField;
    ObservableList<User> oblist = FXCollections.observableArrayList();


    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ShowUserInfo.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Users Report - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setItems(oblist);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        userSinceColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        purchasesColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("purchasesCount")));
        subscriptionsColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("subscriptionsCount")));

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<User> filteredData = new FilteredList<>(oblist, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                // If filter text is empty, display all data.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare user filter text.
                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getFirst_name().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (user.getLast_name().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches last name.
                } else if (user.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches user name.
                } else if (Integer.toString(user.getId()).contains(newValue)) {
                    return true; // Filter matches ID.
                } else if (user.getPhone().contains(newValue)) {
                    return true; // Filter matches Phone.
                } else if (user.getEmail().contains(newValue)) {
                    return true; // Filter matches E-mail.
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<User> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(table.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        table.setItems(sortedData);

        loadUsers();
    }

    private void loadUsers() {
        Input input = new ShowUserInfoCommand.Input();
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ShowUserInfoCommand.Output output = response.getOutput(ShowUserInfoCommand.Output.class);

            oblist.setAll(output.usersList);
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }

    int getPosition() {
        if (table.getSelectionModel().getSelectedCells().isEmpty())
            return -1;
        TablePosition pos = table.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
        User item = table.getItems().get(row);
        return item.getId();
    }

    @FXML
    void ShowActiveSubscriptions(ActionEvent event) {
        int UserId = getPosition();
        if (UserId == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There is no chosen data!!");
            alert.show();
        } else {
            try {
                ActiveSubscriptionsController.loadView(new Stage(), UserId);
            } catch (IOException e) {
                e.printStackTrace();
                ClientGUI.showErrorTryAgain();
            }
        }
    }


    @FXML
    void ShowPurchases(ActionEvent event) {
        int UserId = getPosition();
        if (UserId == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There is no chosen data!!");
            alert.show();
        } else {
            try {
                ShowPurchaseHistoryController.loadView(new Stage(), UserId);
            } catch (IOException e) {
                e.printStackTrace();
                ClientGUI.showErrorTryAgain();
            }
        }
    }

    @FXML
    void ShowSubscribtionsHistory(ActionEvent event) {
        int UserId = getPosition();
        if (UserId == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There is no chosen data!!");
            alert.show();
        } else {
            try {
                ShowSubscriptionHistoryController.loadView(new Stage(), UserId, false);
            } catch (IOException e) {
                e.printStackTrace();
                ClientGUI.showErrorTryAgain();
            }
        }
    }
}
