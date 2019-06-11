package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.Input;
import gcm.commands.ReadMapImageById;
import gcm.commands.Response;
import gcm.database.models.Attraction;
import gcm.database.models.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import static java.awt.Color.BLACK;

public class AddExistingAttractionToMapController {

    @FXML
    private TextField mapTF;

    @FXML
    private AnchorPane pane;

    @FXML
    private ImageView mapImg;

    @FXML
    private TextField Xcord;

    @FXML
    private TextField Ycord;

    @FXML
    private TextField attractionTF;

    @FXML
    private Text Attraction_Type;

    @FXML
    private Text Attraction_Name;

    @FXML
    private Text Attraction_Location;

    @FXML
    private Text accessible;

    @FXML
    private Text Description;


    private Map map;
    private Attraction attraction;

    private double X;
    private double Y;
    private Image originalImage;
    private boolean clicked = false;

    /**
     * initialize the viewer
     */
    public void initialize() {
        try {
            BufferedImage bufferedImage = ImageIO.read(this.getClass().getResourceAsStream("/gcm/client/staticFiles/thumb-1920-44975.jpg"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            mapImg.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * load the viewer
     *
     * @param primaryStage
     * @throws IOException
     */
    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddExitingAttractionToMap.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Add Attraction to Map - GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * exit the window once you finish
     *
     * @param event
     */
    @FXML
    void finishJop(ActionEvent event) {
        if (this.map == null || this.attraction == null || !clicked) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "please choose map or attraction first");
            alert.show();
            return;
        }
        Stage stage = (Stage) pane.getScene().getWindow();
        // do what you have to do
        stage.close();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "attraction added successfully");
        alert.show();
    }

    /**
     * choose map
     *
     * @param event
     */
    @FXML
    void chooseMap(ActionEvent event) {
        try {
            this.map = AdminTablePickerMapController.loadViewAndWait(new Stage());
            this.mapTF.setText(String.format("%s - %s", this.map.getTitle(), this.map._extraInfo.get("cityTitle")));

            Input input = new ReadMapImageById.Input(this.map.getId());
            Response response = ClientGUI.getClient().sendInputAndWaitForResponse(input);
            ReadMapImageById.Output output = response.getOutput(ReadMapImageById.Output.class);

            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(output.imgBytes));
            Image image = SwingFXUtils.toFXImage(bImage, null);
            originalImage = image;
            mapImg.setImage(image);

            mapImg.setOnMouseClicked(e -> {
                Xcord.setText(String.valueOf(e.getX()));
                Ycord.setText(String.valueOf(e.getY()));
                X = e.getX();
                Y = e.getY();

                try {
                    ByteArrayOutputStream s = new ByteArrayOutputStream();
                    ImageIO.write(createImageWithText(), "jpg", s);
                    byte[] res = s.toByteArray();

                    BufferedImage bImage2 = ImageIO.read(new ByteArrayInputStream(res));
                    Image image2 = SwingFXUtils.toFXImage(bImage2, null);
                    mapImg.setImage(image2);
                    s.close();
                    clicked = true;
                } catch (Exception e1) {
                    ClientGUI.showErrorTryAgain();
                    e1.printStackTrace();
                }
            });

        } catch (Exception e) {
            ClientGUI.showErrorTryAgain();
            e.printStackTrace();
        }

    }

    /**
     * choose the attraction
     *
     * @param event
     */
    @FXML
    void chooseAttraction(ActionEvent event) {
        if (this.map == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Choose map first");
            alert.show();
            return;
        }

        try {
            Attraction attraction = AdminTablePickerAttractionForCityController.loadViewAndWait(new Stage(), this.map.getCityId());
            if (attraction == null) return;

            this.attraction = attraction;
            this.attractionTF.setText(attraction.getName());
            setAttractionValues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * set the attraction values
     */
    private void setAttractionValues() {
        this.Attraction_Name.setText(attraction.getName());
        this.Attraction_Type.setText(attraction.getType());
        this.Attraction_Location.setText(attraction.getLocation());
        this.accessible.setText(attraction.getAccessibleSpecial() ? "Yes" : "No");
        this.Description.setText(attraction.getDescription());
    }

    /**
     * add the attraction to the map image
     *
     * @return
     * @throws IOException
     */
    private BufferedImage createImageWithText() throws IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(BLACK);

        double aspectRatio = bufferedImage.getWidth() / bufferedImage.getHeight();
        double realWidth = Math.min(mapImg.getFitWidth(), mapImg.getFitHeight() * aspectRatio);
        double realHeight = Math.min(mapImg.getFitHeight(), mapImg.getFitWidth() / aspectRatio);

        g2d.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2d.drawString("•" + this.attraction.getName(), Math.round(bufferedImage.getWidth() * X / realWidth), Math.round(bufferedImage.getHeight() * Y / realHeight));
        g2d.dispose();

        return bufferedImage;
    }


}

