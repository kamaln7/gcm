package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.FindActiveSubscriptionByUserIDCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.Subscription;
import gcm.database.models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ActiveSubscriptionsController implements Initializable {
    @FXML
    private TableView<Subscription> tableList;

    @FXML
    private TableColumn<Subscription, String> city_country_column;

    @FXML
    private TableColumn<Subscription, String> expiration_date_column;

    private ObservableList oblist = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableList.setItems(oblist);
        city_country_column.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("cityTitle")));
        expiration_date_column.setCellValueFactory(new PropertyValueFactory<>("toDate"));
    }

    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) tableList.getScene().getWindow();
        // do what you have to do
        stage.close();
    }

    @FXML
    void showCityMapsAttractionsTours(ActionEvent event) {
        TablePosition pos = tableList.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
        Subscription item = tableList.getItems().get(row);

        try {
            // show the attractions to the costumer
            ShowBoughtCityAttractionsController.loadView(new Stage(), item.getCityId());
            //show the maps to the costumer
            ShowBoughtCityMapsController.loadView(new Stage(), item.getCityId());
            //show the Tours to the costumer
            ShowBoughtCityToursController.loadView(new Stage(), item.getCityId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCities(User user) {
        //command
        Input input = new FindActiveSubscriptionByUserIDCommand.Input(user.getId());
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindActiveSubscriptionByUserIDCommand.Output output = response.getOutput(FindActiveSubscriptionByUserIDCommand.Output.class);

            oblist.setAll(output.subscriptions);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void loadView(Stage primaryStage, User user) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ActiveSubscriptions.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);

        ActiveSubscriptionsController controller = loader.getController();
        controller.setCities(user);

        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Active Subscriptions - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}

