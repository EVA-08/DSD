package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import model.Invoker;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CreateNewAccountController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField emailField;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label warningLabel;
    @FXML
    private AnchorPane root;
    private Stage stage;

    void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void confirmHandler() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        warningLabel.setVisible(true);
        warningLabel.setTextFill(Paint.valueOf("red"));
        if (username == null || username.length() == 0 || password == null || password.length() == 0 ||
                confirmPassword == null || confirmPassword.length() == 0 || email == null || email.length() == 0) {
            warningLabel.setText("All the fields must be filled");
        } else if (!password.equals(confirmPassword)) {
            warningLabel.setText("The two passwords must be same");
        } else if (!email.matches("[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+")) {
            warningLabel.setText("E-mail format error");
        } else {
            warningLabel.setVisible(false);
            root.setDisable(true);
            // Start a Thread to validate the new account info
            FutureTask<Integer> validateNewAccountTast = new FutureTask<>(() -> {
                try {
                    return Invoker.createNewAccount(username, password, email);
                } catch (InterruptedException e) {
                    return -1;
                }
            });
            Thread validateNewAccountThread = new Thread(validateNewAccountTast);
            validateNewAccountThread.setDaemon(true);
            validateNewAccountThread.start();

            Thread adminThread = new Thread(() -> {
                Integer result;
                try {
                    result = validateNewAccountTast.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    result = null;
                }
                Platform.runLater(() -> {
                    root.setDisable(false);
                    warningLabel.setVisible(true);
                });
                if (result == null || result == -1) {
                    Platform.runLater(() -> warningLabel.setText("Network error"));
                } else if (result == 1) {
                    Platform.runLater(() -> {
                        warningLabel.setTextFill(Paint.valueOf("green"));
                        warningLabel.setText("Succeed");
                    });
                } else if (result == 0) {
                    Platform.runLater(() -> warningLabel.setText("Username was used"));
                }
            });
            adminThread.setDaemon(true);
            adminThread.start();
        }
    }

    @FXML
    private void cancelHandler() {
        stage.close();
    }

}
