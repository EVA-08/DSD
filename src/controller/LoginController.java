package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import model.Invoker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static javafx.scene.paint.Color.color;

public class LoginController {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
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
    private void loginHandler() {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        if (username == null || username.length() == 0 ||
                password == null || password.length() == 0) {
            Alert alertDialog = new Alert(AlertType.NONE,
                    "Please input your username and password", ButtonType.CLOSE);
            alertDialog.showAndWait();
        } else {
            //Start a thread to validate user info.
            @SuppressWarnings("unchecked")
            FutureTask<Integer> validateUserInfoTask = new FutureTask<>(() -> {
                try {
                    return Invoker.validateUserInfo(username, password);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return -1;
                }
            });
            Thread validateUserInfoThread = new Thread(validateUserInfoTask);
            validateUserInfoThread.setDaemon(true);
            validateUserInfoThread.start();
            //Start a thread to render login animation.
            Thread showLoginAnimationThread = new Thread(this::showLoginAnimation);
            showLoginAnimationThread.setDaemon(true);
            showLoginAnimationThread.start();
            //Start a thread to wait for the validation of user info,
            //and then stop the animation and show the corresponding dialog.
            Thread adminThread = new Thread(() -> {
                Integer result;
                try {
                    result = validateUserInfoTask.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    result = null;
                }
                showLoginAnimationThread.interrupt();
                if (result == null) {
                    Platform.runLater(() -> {
                        Alert alertDialog = new Alert(AlertType.INFORMATION,
                                "Network Error", ButtonType.CLOSE);
                        alertDialog.showAndWait();
                    });
                } else if (result == 0) {
                    Platform.runLater(() -> {
                        Alert alertDialog = new Alert(AlertType.INFORMATION,
                                "Wrong username or password", ButtonType.CLOSE);
                        alertDialog.showAndWait();
                    });
                } else {
                    System.out.println("1");
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
            root.setDisable(true);
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
                root.setDisable(false);
            });
            System.out.println("animation over");
        }
    }
}
