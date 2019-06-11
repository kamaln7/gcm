package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.FindPurchaseByUserIDCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.Purchase;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ShowPurchaseHistoryController implements Initializable {
    @FXML
    private TableView tableView;

    @FXML
    private TableColumn<Purchase, String> dateCol;

    @FXML
    private TableColumn<Purchase, String> priceCol;

    @FXML
    private TableColumn<Purchase, String> cityCol;

    private ObservableList oblist = FXCollections.observableArrayList();

    public void show(int currentUserID) {
        Input input = new FindPurchaseByUserIDCommand.Input(currentUserID);
        try {
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            FindPurchaseByUserIDCommand.Output output = response.getOutput(FindPurchaseByUserIDCommand.Output.class);

            oblist.setAll(output.purchases);
        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }

    }

    public static void loadView(Stage stage, int currentUserID) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/ShowPurchaseHistory.fxml");
        FXMLLoader loader = new FXMLLoader(url);

        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setTitle("Purchase History - GCM 2019");
        ShowPurchaseHistoryController controller = loader.getController();

        controller.show(currentUserID);

        // setting the stage
        stage.setScene(scene);

        stage.setResizable(true);

        stage.showAndWait();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setItems(oblist);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        cityCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()._extraInfo.get("cityTitle")));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
}
