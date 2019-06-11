package gcm.client.controllers;

import gcm.client.bin.ClientGUI;
import gcm.commands.AddOneToAttractionAndUpdateMapImageCommand;
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

    private int X;
    private int Y;

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
     * @param primaryStage
     * @throws IOException
     */
    public static void loadView(Stage primaryStage) throws IOException {
        URL url = MainScreenController.class.getResource("/gcm/client/views/AddExitingAttractionToMap.fxml");
        AnchorPane pane = FXMLLoader.load(url);
        Scene scene = new Scene(pane);
        // setting the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("GCM 2019");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * exit the window once you finish
     * @param event
     */
    @FXML
    void finishJop(ActionEvent event) {
        if (this.map==null || this.attraction==null){
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
            mapImg.setImage(image);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please Choose Attraction,\nBefore you click on the map image!");
            alert.show();
            mapImg.setOnMouseClicked(e -> {

                Xcord.setText(String.valueOf(e.getX()));
                Ycord.setText(String.valueOf(e.getY()));
                X = (int) Math.round(e.getX());
                Y = (int) Math.round(e.getY());
                mapImg.setOnMouseClicked(null);

                try {
                    ByteArrayOutputStream s = new ByteArrayOutputStream();
                    ImageIO.write(createImageWithText(), "jpg", s);
                    byte[] res = s.toByteArray();

                    BufferedImage bImage2 = ImageIO.read(new ByteArrayInputStream(res));
                    Image image2 = SwingFXUtils.toFXImage(bImage2, null);
                    mapImg.setImage(image2);
                    s.close();


                    Input input2 = new AddOneToAttractionAndUpdateMapImageCommand.Input(this.map.getId(), this.attraction, res);
                    Response response2 = ClientGUI.getClient().sendInputAndWaitForResponse(input2);
                    response2.getOutput(AddOneToAttractionAndUpdateMapImageCommand.Output.class);

                } catch (Exception e1) {
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
     * @param event
     */
    @FXML
    void chooseAttraction(ActionEvent event) {
        if (this.map==null){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Choose Map First");
            alert.show();
            return;
        }
        try {
            this.attraction = AdminTablePickerAttractionForCityController.loadViewAndWait(new Stage(), this.map.getCityId());
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
        this.accessible.setText(attraction.getAccessibleSpecial() ? "YES" : "NO");
        this.Description.setText(attraction.getDescription());

    }

    /**
     * add the attraction to the map image
     * @return
     * @throws IOException
     */
    private BufferedImage createImageWithText() throws IOException {

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(mapImg.getImage(), null);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(BLACK);


        g2d.setFont(new Font("SansSerif", Font.BOLD, 60));
        g2d.drawString("•" + Attraction_Name.getText(), bufferedImage.getWidth() * X / 672, bufferedImage.getHeight() * Y / 376);

        //ImageIO.write(bufferedImage, "png", new File("afterAdding.png") );
        g2d.dispose();

        return bufferedImage;
    }


}

