package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.*;
import gcm.database.models.City;
import gcm.database.models.User;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ShowSubscriptionHistoryController {
    @FXML
    private TableView<SubscriptionHistory> tableView;

    @FXML
    private TableColumn<SubscriptionHistory, Integer> idCol;

    @FXML
    private TableColumn<SubscriptionHistory, String> dateCol;

    @FXML
    private TableColumn<SubscriptionHistory, Double> priceCol;

    @FXML
    private TableColumn<SubscriptionHistory, String> cityCol;

    @FXML
    private TableColumn<SubscriptionHistory, String> newCol;

    @FXML
    private TableColumn<SubscriptionHistory, String> fromCol;

    @FXML
    private TableColumn<SubscriptionHistory, String> toCol;

    public User myUser;
    public int myUserID;

    public class SubscriptionHistory{
        private SimpleIntegerProperty id;
        private  SimpleStringProperty city_country;
        private SimpleDoubleProperty purchase_price;
        private SimpleStringProperty date;
        private SimpleStringProperty renew;
        private SimpleStringProperty fromDate;
        private SimpleStringProperty toDate;


        public SubscriptionHistory(int id, String city, double purchase_price, String date, String renew, String fromDate, String toDate)
        {
            this.id=new SimpleIntegerProperty(id);
            this.city_country = new SimpleStringProperty(city);
            this.purchase_price = new SimpleDoubleProperty(purchase_price);
            this.date =  new SimpleStringProperty(date);
            this.renew = new SimpleStringProperty(renew);
            this.fromDate = new SimpleStringProperty(fromDate);
            this.toDate = new SimpleStringProperty(toDate);



        }

        public int getId() {return id.get();}
        public SimpleIntegerProperty idProperty() {return id;}
        public String getCity_country() {return city_country.get();}
        public SimpleStringProperty city_countryProperty() {return city_country; }
        public double getPurchase_price() {return purchase_price.get();}
        public SimpleDoubleProperty purchase_priceProperty() {return purchase_price;}
        public String getrenew() {return renew.get();}
        public SimpleStringProperty renewProperty() {return renew; }
        public String getFromdate() {return fromDate.get();}
        public SimpleStringProperty fromDateProperty() {return fromDate; }
        public String gettoDate() {return toDate.get();}
        public SimpleStringProperty toDateProperty() {return toDate; }
        public String getdate() {return date.get();}
        public SimpleStringProperty dateProperty() {return date; }

    }




    public void show(int currentUserID) {


        Input input = new FindSubscriptionByUserIDCommand.Input(currentUserID);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindSubscriptionByUserIDCommand.Output output = response.getOutput(FindSubscriptionByUserIDCommand.Output.class);

            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            priceCol.setCellValueFactory(new PropertyValueFactory<>("purchase_price"));
            cityCol.setCellValueFactory(new PropertyValueFactory<>("city_country"));
            newCol.setCellValueFactory(new PropertyValueFactory<>("renew"));
            fromCol.setCellValueFactory(new PropertyValueFactory<>("fromDate"));
            toCol.setCellValueFactory(new PropertyValueFactory<>("toDate"));

            ObservableList<SubscriptionHistory> oblist = FXCollections.observableArrayList();
            for(int i=0;i<output.subscriptions.size(); i ++) {
                String cityname;
                String countryname;
                String date;

                Input input2 = new FindCityByIDCommand.Input(output.subscriptions.get(i).getCityId());
                try{
                    Response response1 = ClientGUI.getClient().sendInputAndWaitForResponse(input2);
                    FindCityByIDCommand.Output output1 = response1.getOutput(FindCityByIDCommand.Output.class);

                    cityname = output1.city.getName();
                    countryname = output1.city.getCountry();


                    oblist.add(new SubscriptionHistory(output.subscriptions.get(i).getId(),
                            cityname + ", " + countryname,
                            output.subscriptions.get(i).getPrice(),
                            output.subscriptions.get(i).getCreatedAt().toString(),
                            output.subscriptions.get(i).getRenew() ? "Renew " : "New",
                            output.subscriptions.get(i).getFromDate().toString(),
                            output.subscriptions.get(i).getToDate().toString()));
                }
                catch (City.NotFound e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
                    alert.show();
                } catch (Exception e) {
                    ClientGUI.showErrorTryAgain();
                    e.printStackTrace();
                }

            }

            tableView.setItems(oblist);

        } catch (City.NotFound e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "City is not found");
            alert.show();
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }


    }

    public static void loadView(Stage stage,int currentUserID) throws IOException {

        URL url = MainScreenController.class.getResource("/gcm/client/views/ShowSubscriptionHistory.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        AnchorPane pane = loader.load();

        Scene scene = new Scene(pane);

         ShowSubscriptionHistoryController controller = loader.getController();

         controller.show(currentUserID);



        // setting the stage
        stage.setScene(scene);

        stage.setResizable(true);




        stage.showAndWait();

    }


}
