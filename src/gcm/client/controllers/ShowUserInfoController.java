package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.ResourceBundle;

public class ShowUserInfoController{//} implements Initializable {

    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/SHowUserInfo.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public class UsersInfo{
        private  SimpleIntegerProperty id, purchases, subscriptions;
        private  SimpleStringProperty firstName, lastName, userName, email, phone;
        private SimpleObjectProperty<LocalDate> date;


        public UsersInfo(int id, String firstName, String lastName, String userName, String email, String phone, Date date, int purchases, int subscriptions)
        {
            this.id=new SimpleIntegerProperty(id);
            this.firstName = new SimpleStringProperty(firstName);
            this.lastName = new SimpleStringProperty(lastName);
            this.userName = new SimpleStringProperty(userName);
            this.email = new SimpleStringProperty(email);
            this.date = new SimpleObjectProperty(date);
            this.purchases=new SimpleIntegerProperty(purchases);
            this.subscriptions=new SimpleIntegerProperty(subscriptions);
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
    }/*
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Input input = new ReviewPendingPriceChangesCommand.Input();
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ReviewPendingPriceChangesCommand.Output output = response.getOutput(ReviewPendingPriceChangesCommand.Output.class);

            id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
            city_column.setCellValueFactory(new PropertyValueFactory<>("city_country"));
            purchase_column.setCellValueFactory(new PropertyValueFactory<>("purchase_price"));
            sub_column.setCellValueFactory(new PropertyValueFactory<>("sub_price"));

            ObservableList<CityPrices> oblist = FXCollections.observableArrayList();
            for(int i=0;i<output.result.size(); i ++)
                oblist.add(new CityPrices(output.result.get(i).getId(),
                        output.result.get(i).getName()+", " + output.result.get(i).getCountry(),
                        output.result.get(i).getPurchasePrice() + " -> " + output.result.get(i).getNewPurchasePrice() ,
                        output.result.get(i).getSubscriptionPrice()+ " -> " + output.result.get(i).getNewSubscriptionPrice()));

            tableList.setItems(oblist);

        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }
    /*@FXML
    void getPrice(ActionEvent event) {

        Input input = new ReviewPendingPriceChangesCommand.Input();
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ReviewPendingPriceChangesCommand.Output output = response.getOutput(ReviewPendingPriceChangesCommand.Output.class);

            id_column.setCellValueFactory(new PropertyValueFactory<>("id"));
            city_column.setCellValueFactory(new PropertyValueFactory<>("city_country"));
            purchase_column.setCellValueFactory(new PropertyValueFactory<>("purchase_price"));
            sub_column.setCellValueFactory(new PropertyValueFactory<>("sub_price"));

            ObservableList<CityPrices> oblist = FXCollections.observableArrayList();
            for(int i=0;i<output.result.size(); i ++)
                oblist.add(new CityPrices(output.result.get(i).getId(),
                        output.result.get(i).getName()+", " + output.result.get(i).getCountry(),
                        output.result.get(i).getPurchasePrice() + " -> " + output.result.get(i).getNewPurchasePrice() ,
                        output.result.get(i).getSubscriptionPrice()+ " -> " + output.result.get(i).getNewSubscriptionPrice()));

            tableList.setItems(oblist);

        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }
    }
*/



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
    private TableColumn<UsersInfo, Date> usersinceColumn;

    @FXML
    private TableColumn<UsersInfo, Integer> purchasesColumn;

    @FXML
    private TableColumn<UsersInfo, Integer> subscriptionsColumn;

    @FXML
    private TextField search;

    @FXML
    void ShowPurchases(ActionEvent event) {

    }

    @FXML
    void ShowSubscribtions(ActionEvent event) {

    }

    @FXML
    void search(ActionEvent event) {

    }

}

