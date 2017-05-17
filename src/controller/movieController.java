package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class movieController implements Initializable {
    private MediaPlayer player;
    @FXML
    private MediaView movieMediaView;
    @FXML
    private AnchorPane pane;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String path = "file:///" + getClass().getResource("/resources/movie.mp4").getPath();
        Media media = new Media(path);
        player = new MediaPlayer(media);
        player.setOnEndOfMedia(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../view/loginUI.fxml"));
                Parent root = loader.load();
                LoginController controller = loader.getController();
                controller.setStage(stage);
                player.stop();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        movieMediaView.setMediaPlayer(player);
        player.setAutoPlay(true);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        System.out.println("keypress");
        if (event.getCode() == KeyCode.ESCAPE) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../view/loginUI.fxml"));
                Parent root = loader.load();
                LoginController controller = loader.getController();
                controller.setStage(stage);
                player.stop();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
