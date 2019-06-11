package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.commands.ShowUserInfoCommand;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class ShowUserInfoController implements Initializable {

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ShowUserInfo.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public class UsersInfo {
        private SimpleIntegerProperty id, purchases, subscriptions;
        private SimpleStringProperty firstName, lastName, userName, email, phone;
        private SimpleObjectProperty<LocalDate> date;


        public UsersInfo(int id, String firstName, String lastName, String userName, String email, String phone, Date date, int purchases, int subscriptions) {
            this.id = new SimpleIntegerProperty(id);
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.userName = new SimpleStringProperty(userName);
            this.email = new SimpleStringProperty(email);
            this.phone = new SimpleStringProperty(phone);
            this.date = new SimpleObjectProperty(date);
            this.purchases = new SimpleIntegerProperty(purchases);
            this.subscriptions = new SimpleIntegerProperty(subscriptions);
        }

        public int getId() {
            return id.get();
        }

        public SimpleIntegerProperty idProperty() {
            return id;
        }

        public int getPurchases() {
            return purchases.get();
        }

        public SimpleIntegerProperty purchasesProperty() {
            return purchases;
        }

        public int getSubscriptions() {
            return subscriptions.get();
        }

        public SimpleIntegerProperty subscriptionsProperty() {
            return subscriptions;
        }

        public String getFirstName() {
            return firstName.get();
        }

        public SimpleStringProperty firstNameProperty() {
            return firstName;
        }

        public String getLastName() {
            return lastName.get();
        }

        public SimpleStringProperty lastNameProperty() {
            return lastName;
        }

        public String getUserName() {
            return userName.get();
        }

        public SimpleStringProperty userNameProperty() {
            return userName;
        }

        public String getEmail() {
            return email.get();
        }

        public SimpleStringProperty emailProperty() {
            return email;
        }

        public String getPhone() {
            return phone.get();
        }

        public SimpleStringProperty phoneProperty() {
            return phone;
        }

        public LocalDate getDate() {
            return date.get();
        }

        public SimpleObjectProperty<LocalDate> dateProperty() {
            return date;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Input input = new ShowUserInfoCommand.Input();
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ShowUserInfoCommand.Output output = response.getOutput(ShowUserInfoCommand.Output.class);

            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            userSinceColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            purchasesColumn.setCellValueFactory(new PropertyValueFactory<>("purchases"));
            subscriptionsColumn.setCellValueFactory(new PropertyValueFactory<>("subscriptions"));

            ObservableList<UsersInfo> oblist = FXCollections.observableArrayList();

            for (int i = 0; i < output.userInfoList.size(); i++) {

                oblist.add(new UsersInfo(output.userInfoList.get(i).getId(),
                        output.userInfoList.get(i).getFirstName(), output.userInfoList.get(i).getLastName(),
                        output.userInfoList.get(i).getUserName(), output.userInfoList.get(i).getEmail(),
                        output.userInfoList.get(i).getPhone(), output.userInfoList.get(i).getDate(),
                        output.userInfoList.get(i).getPurchases(), output.userInfoList.get(i).getSubscriptions()));
            }
            table.setItems(oblist);

            // 1. Wrap the ObservableList in a FilteredList (initially display all data).

            FilteredList<UsersInfo> filteredData = new FilteredList<>(oblist, p -> true);

            // 2. Set the filter Predicate whenever the filter changes.
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(UserInfo -> {
                    // If filter text is empty, display all data.
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    // Compare user filter text.
                    String lowerCaseFilter = newValue.toLowerCase();

                    if (UserInfo.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches first name.
                    } else if (UserInfo.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches last name.
                    } else if (UserInfo.getUserName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches user name.
                    } else if (Integer.toString(UserInfo.getId()).contains(newValue)) {
                        return true; // Filter matches ID.
                    } else if (UserInfo.getPhone().contains(newValue)) {
                        return true; // Filter matches Phone.
                    } else if (UserInfo.getEmail().contains(newValue)) {
                        return true; // Filter matches E-mail.
                    }
                    return false; // Does not match.
                });
            });

            // 3. Wrap the FilteredList in a SortedList.
            SortedList<UsersInfo> sortedData = new SortedList<>(filteredData);

            // 4. Bind the SortedList comparator to the TableView comparator.
            sortedData.comparatorProperty().bind(table.comparatorProperty());

            // 5. Add sorted (and filtered) data to the table.
            table.setItems(sortedData);
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }

    }

    int getPosition() {
        TablePosition pos = table.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
        UsersInfo item = table.getItems().get(row);
        return item.getId();
    }

    @FXML
    void ShowActiveSubscriptions(ActionEvent event) {
        int UserId = getPosition();
        try {
            ActiveSubscriptionsController.loadView(new Stage(), UserId);
        } catch (IOException e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }


    @FXML
    void ShowPurchases(ActionEvent event) {
        int UserId = getPosition();
        try {
            ShowPurchaseHistoryController.loadView(new Stage(), UserId);
        } catch (IOException e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }

    @FXML
    void ShowSubscribtionsHistory(ActionEvent event) {
        int UserId = getPosition();
        try {
            ShowSubscriptionHistoryController.loadView(new Stage(), UserId);
        } catch (IOException e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }


    @FXML
    private TableView<UsersInfo> table;

    @FXML
    private TableColumn<UsersInfo, Integer> idColumn;

    @FXML
    private TableColumn<UsersInfo, String> firstNameColumn;

    @FXML
    private TableColumn<UsersInfo, String> lastNameColumn;

    @FXML
    private TableColumn<UsersInfo, String> userNameColumn;

    @FXML
    private TableColumn<UsersInfo, String> emailColumn;

    @FXML
    private TableColumn<UsersInfo, String> phoneColumn;

    @FXML
    private TableColumn<UsersInfo, Date> userSinceColumn;

    @FXML
    private TableColumn<UsersInfo, Integer> purchasesColumn;

    @FXML
    private TableColumn<UsersInfo, Integer> subscriptionsColumn;

    @FXML
    private TextField searchField;


}
