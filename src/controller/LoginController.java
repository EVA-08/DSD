package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Invoker;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static javafx.scene.paint.Color.color;

public class LoginController implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private AnchorPane root;
    @FXML
    private Circle circle0;
    @FXML
    private Circle circle1;
    @FXML
    private Circle circle2;
    @FXML
    private Circle circle3;
    @FXML
    private Circle circle4;
    @FXML
    private Circle circle5;
    @FXML
    private Circle circle6;
    @FXML
    private Circle circle7;

    @FXML
    private Label warningLabel;
    @FXML
    private Hyperlink createNewAccountLink;


    private Stage stage;

    @FXML
    private void loginHandler() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        if (username == null || username.length() == 0 ||
                password == null || password.length() == 0) {
            warningLabel.setVisible(true);
            warningLabel.setText("Please input your username and password");
        } else {
            //Start a thread to validate user info.
            warningLabel.setVisible(false);
            root.setDisable(true);
            FutureTask<Integer> validateLoginTask = new FutureTask<>(() -> {
                try {
                    return Invoker.validateLogin(username, password);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return -1;
                }
            });
            Thread validateLoginThread = new Thread(validateLoginTask);
            validateLoginThread.setDaemon(true);
            validateLoginThread.start();
            //Start a thread to render login animation.
            Thread showLoginAnimationThread = new Thread(this::showLoginAnimation);
            showLoginAnimationThread.setDaemon(true);
            showLoginAnimationThread.start();
            //Start a thread to wait for the validation of user info,
            //and then stop the animation and show the corresponding dialog.
            Thread adminThread = new Thread(() -> {
                Integer result;
                try {
                    result = validateLoginTask.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    result = null;
                }
                showLoginAnimationThread.interrupt();
                Platform.runLater(() -> {
                    root.setDisable(false);
                    warningLabel.setVisible(true);
                });
                if (result == null || result == -1) {
                    Platform.runLater(() -> warningLabel.setText("Network Error"));
                } else if (result == 0) {
                    Platform.runLater(() -> warningLabel.setText("Wrong username or password"));
                } else if (result == 1) {
                    warningLabel.setVisible(false);
                    Platform.runLater(this::enterGameHall);
                }
            });
            adminThread.setDaemon(true);
            adminThread.start();
        }
    }

    @FXML
    private void loginButtonOnMouseEntered() {
        DropShadow dropShadow = new DropShadow();
        loginButton.setEffect(dropShadow);
    }

    @FXML
    private void loginButtonOnMouseExited() {
        loginButton.setEffect(null);
    }

    private void showLoginAnimation() {
        final int circleNumber = 8;
        final int smallRadius = 8;
        final Color smallColor = color(0.5, 0.5, 0.5, 0.5);
        final int largeRadius = 13;
        final Color largeColor = color(0.8, 0.8, 0.8, 0.5);
        List<Circle> circles = new ArrayList<>(circleNumber);
        circles.add(circle0);
        circles.add(circle1);
        circles.add(circle2);
        circles.add(circle3);
        circles.add(circle4);
        circles.add(circle5);
        circles.add(circle6);
        circles.add(circle7);
        Platform.runLater(() -> {

            for (Circle circle : circles) {
                circle.setVisible(true);
                circle.setRadius(smallRadius);
                circle.setFill(smallColor);
            }
        });
        try {
            while (!Thread.currentThread().isInterrupted()) {
                for (int i = 0; i < circleNumber; i++) {
                    Circle circle = circles.get(i);
                    Platform.runLater(() -> {
                        circle.setRadius(largeRadius);
                        circle.setFill(largeColor);
                    });
                    TimeUnit.MILLISECONDS.sleep(100);
                    Platform.runLater(() -> {
                        circle.setRadius(smallRadius);
                        circle.setFill(smallColor);
                    });
                }
            }
        } catch (InterruptedException e) {
            System.out.println("animation interrupted");
        } finally {
            Platform.runLater(() -> {
                for (Circle circle : circles) {
                    circle.setVisible(false);
                }
            });
            System.out.println("animation over");
        }
    }

    @FXML
    private void createNewAccountHandler() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/createNewAccountUI.fxml"));
            Parent newRoot = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(newRoot));
            CreateNewAccountController controller = loader.getController();
            controller.setStage(stage);
            stage.initOwner(this.stage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }

    private void enterGameHall() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/gameHallUI.fxml"));
            Parent root = loader.load();
            GameHallController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Thread musicThread = new Thread(() -> {
            System.out.println(getClass().getResource("/").getPath());
            String path = "file:///" + getClass().getResource("/resources/backgroundMusic.mp3").getPath();
            System.out.println(path);
            Media media = new Media(path);
            MediaPlayer player = new MediaPlayer(media);
            player.setCycleCount(Integer.MAX_VALUE);
            player.setAutoPlay(true);
        });
        musicThread.setDaemon(true);
        musicThread.start();
    }
}
