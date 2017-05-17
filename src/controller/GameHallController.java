package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Invoker;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class GameHallController implements Initializable {
    private boolean showing = false;
    private String hostname = "";
    private String sessionID = "";
    @FXML
    private Button newSessionButton;
    @FXML
    private AnchorPane invitationPane;
    @FXML
    private TextArea invitationTextArea;
    private FutureTask<Void> invitationTask;
    private Stage stage;

    void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        invitationTask = new FutureTask<>(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    //info = hostname + " " + sessionID
                    String info = Invoker.getInvatation();
                    if (!showing) {
                        showing = true;
                        hostname = info.split(" ")[0];
                        sessionID = info.split(" ")[1];
                        showInvitation(hostname);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });
        Thread invitationThread = new Thread(invitationTask);
        invitationThread.setDaemon(true);
        invitationThread.start();
    }

    private void showInvitation(String hostname) {
        Platform.runLater(() -> {
            invitationPane.setVisible(true);
            invitationTextArea.setText(hostname + " invite you to join his game");
        });
    }

    @FXML
    private void newSessionHandler() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/hostSessionUI.fxml"));
            Parent newRoot = loader.load();
            HostSessionController controller = loader.getController();
            controller.setStage(stage);
            stage.setScene(new Scene(newRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void acceptButtonHandler() {
        FutureTask<Integer> joinSessionTask = new FutureTask<>(() -> Invoker.joinSession(hostname, sessionID));
        Thread joinSessionThread = new Thread(joinSessionTask);
        joinSessionThread.setDaemon(true);
        joinSessionThread.start();
        Thread adminJoinSessionThread = new Thread(() -> {
            int result;
            try {
                result = joinSessionTask.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                result = -1;
            }
            if (result == 1) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("../view/guestSessionUI.fxml"));
                    Parent root = loader.load();
                    GuestSessionController controller = loader.getController();
                    controller.setStage(stage);
                    Platform.runLater(() -> {
                        stage.setScene(new Scene(root));
                        stage.show();
                    });
                    invitationTask.cancel(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (result == 0) {
                Platform.runLater(() -> invitationPane.setVisible(false));
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Session Full", ButtonType.CLOSE);
                alert.showAndWait();
            } else if (result == -1) {
                Platform.runLater(() -> invitationPane.setVisible(false));
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Network Error", ButtonType.CLOSE);
                alert.showAndWait();
            }

        });
        adminJoinSessionThread.setDaemon(true);
        adminJoinSessionThread.start();
    }

    @FXML
    private void refuseButtonHandler() {
        showing = false;
        invitationPane.setVisible(false);
    }
}
