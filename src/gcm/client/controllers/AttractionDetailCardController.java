package gcm.client.controllers;

import gcm.database.models.Attraction;
import gcm.database.models.Map;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AttractionDetailCardController extends AnchorPane {
    @FXML
    private Text titleText;

    @FXML
    private Text mapsText;
    @FXML
    private Text cityTitleText;

    @FXML
    private TilePane mapsTilePane;

    private Attraction attraction;

    public AttractionDetailCardController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gcm/client/views/AttractionDetailCard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAttraction(Attraction attraction) {
        if (attraction == null) return;
        this.attraction = attraction;

        titleText.setText(attraction.getName());
        cityTitleText.setText(String.format("Located in city %s", attraction._extraInfo.get("cityTitle")));
    }

    public void setMaps(List<Map> maps) {
        if (maps == null) {
            mapsTilePane.getChildren().clear();
            return;
        }
        mapsText.setText(String.format("In %s maps", maps.size()));

        mapsTilePane.getChildren().setAll(
                maps
                        .parallelStream()
                        .map(map -> {
                            MapDetailCardController md = new MapDetailCardController();
                            md.setMap(map);

                            return md;
                        })
                        .collect(Collectors.toList())
        );
    }
}
