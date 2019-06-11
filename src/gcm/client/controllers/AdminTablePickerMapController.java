package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.GetAllMapsWithCityTitleCommand;
import gcm.commands.Input;
import gcm.commands.Response;
import gcm.database.models.Map;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.HashMap;
import java.util.ResourceBundle;

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

    private Map map;

    private java.util.Map<String, TreeItem<Object>> cityRoots;
    TreeItem<Object> root = new TreeItem<>();

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
        publishedCol.setCellValueFactory(cellData -> {
            Object o = cellData.getValue();
            if (!(o instanceof Map)) {
                return null;
            }

            return new SimpleStringProperty(((Map) o).getVerification() ? "Yes" : "No");
        });

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
                        if (empty) {
                            setGraphic(null);
                        } else if (this.getTreeTableRow().getItem() instanceof WorkaroundCityThing) {
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
        stage.setTitle("Choose a Map - GCM 2019");
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
            cityRoots = new HashMap<>();
            // insert maps into city roots
            root.getChildren().clear();
            output.maps.forEach(this::addToTreeTableView);
            treeTableView.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }

    private void addToTreeTableView(Map map) {
        String cityTitle = map._extraInfo.get("cityTitle");
        TreeItem<Object> root = cityRoots.computeIfAbsent(cityTitle, this::createTreeItemForCity);
        root.getChildren().add(new TreeItem<>(map));
    }

    private TreeItem<Object> createTreeItemForCity(String cityTitle) {
        TreeItem<Object> item = new TreeItem<>(new WorkaroundCityThing(cityTitle));
        item.setExpanded(true);
        root.getChildren().add(item);
        return item;
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

    @FXML
    private void openNewMapWindow(ActionEvent event) {
        try {
            Map map = AddMapController.loadViewAndWait(new Stage());

            if (map != null) {
                addToTreeTableView(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ClientGUI.showErrorTryAgain();
        }
    }
}
