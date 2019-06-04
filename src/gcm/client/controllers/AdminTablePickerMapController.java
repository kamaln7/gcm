package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.GetAllMapsWithCityTitleCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.Map;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AdminTablePickerMapController implements Initializable {
    @FXML
    public TreeTableView treeTableView;
    @FXML
    private TreeTableColumn<Object, String> idCol;
    @FXML
    private TreeTableColumn<Object, String> titleCol;
    @FXML
    private TreeTableColumn<Object, String> descriptionCol;
    @FXML
    private TreeTableColumn<Object, String> versionCol;
    @FXML
    private TreeTableColumn<Object, String> publishedCol;
    @FXML
    private TreeTableColumn<Object, Void> buttonCol;
//    private ObservableList<Map> maps = FXCollections.observableArrayList();

    private Map map;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idCol.setCellValueFactory(cellData -> {
            Object o = cellData.getValue().getValue();
            if (o instanceof WorkaroundCityThing) {
                return new SimpleObjectProperty<>(((WorkaroundCityThing) o).id);
            } else if (o instanceof Map) {
                return new SimpleObjectProperty<String>(((Map) o).getId().toString());
            } else {
                return new SimpleObjectProperty<>("-1");
            }
        });
        titleCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("description"));
        versionCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("version"));
        publishedCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("published"));

        // button
        buttonCol.setCellFactory(new Callback<TreeTableColumn<Object, Void>, TreeTableCell<Object, Void>>() {
            @Override
            public TreeTableCell<Object, Void> call(TreeTableColumn<Object, Void> param) {
                final TreeTableCell<Object, Void> cell = new TreeTableCell<Object, Void>() {
                    private final Button btn = new Button("Choose");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Map map = (Map) getTreeTableRow().getItem();
                            setMap(map);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || !(this.getTreeTableRow().getItem() instanceof Map)) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });

        treeTableView.setShowRoot(false);
        // load cities
        loadMapsFromServer();
    }

    public static Map loadViewAndWait(Stage stage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AdminTablePickerMap.fxml");
        FXMLLoader loader = new FXMLLoader(url);
        AnchorPane pane = loader.load();
        Scene scene = new Scene(pane);
        // setting the stage
        stage.setScene(scene);
        stage.setTitle("Choose a Map");
        stage.setResizable(true);
        stage.showAndWait();

        return loader.<AdminTablePickerMapController>getController().getMap();
    }

    private void loadMapsFromServer() {
        try {
            Input input = new GetAllMapsWithCityTitleCommand.Input();
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            GetAllMapsWithCityTitleCommand.Output output = response.getOutput(GetAllMapsWithCityTitleCommand.Output.class);

            // list of cities
            java.util.Map<String, TreeItem<Object>> cityRoots = output.maps
                    .stream()
                    .map(map -> map._extraInfo.get("cityTitle"))
                    .distinct()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            cityTitle -> {
                                TreeItem<Object> item = new TreeItem<>(new WorkaroundCityThing(cityTitle));
                                item.setExpanded(true);
                                return item;
                            }
                    ));
            // insert maps into city roots
            for (Map map : output.maps) {
                TreeItem<Object> root = cityRoots.get(map._extraInfo.get("cityTitle"));
                root.getChildren().add(new TreeItem<>(map));
            }
            TreeItem<Object> root = new TreeItem<>();
            root.getChildren().setAll(cityRoots.values());
            treeTableView.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
        ((Stage) treeTableView.getScene().getWindow()).close();
    }

    private class WorkaroundCityThing {
        public String id;

        public WorkaroundCityThing(String id) {
            this.id = id;
        }
    }
}
