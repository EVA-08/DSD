package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

/**
 * Created by EVA-08 on 2017/5/12.
 */
public class GameHallController implements Initializable {
    boolean showing = false;
    String hostname = "";
    String sessionID = "";
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
        invitationTask = new FutureTask<Void>(() -> {
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
            loader.setLocation(getClass().getResource("../view/sessionUI.FXML"));
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
        FutureTask<Integer> joinSessionTask = new FutureTask<Integer>(() -> {
            return Invoker.joinSession(hostname, sessionID);
        });
        Thread adminJoinSession = new Thread(() -> {
            try {
                int result = joinSessionTask.get(5, TimeUnit.SECONDS);
                if (result == 1) {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("../view/guestSessionUI.fxml"));

                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void refuseButtonHandler() {
        showing = false;
        invitationPane.setVisible(false);
    }
}
