package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Invoker;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.FutureTask;

public class GuestSessionController implements Initializable {
    @FXML
    private ChoiceBox<String> mapChoiceBox;
    @FXML
    private ChoiceBox<String> ruleChoiceBox;
    @FXML
    private ChoiceBox<String> cardChoiceBox;
    @FXML
    private ChoiceBox<Integer> playerNumberChoiceBox;

    @FXML
    private Group optionsGroup;
    @FXML
    private Label warningLabel;
    @FXML
    private ListView<String> playersListView;
    @FXML
    private Button readyButton;

    private Stage stage;
    private FutureTask<Void> adminPlayersListTask = new FutureTask<>(() -> {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                List<String> playersList = Invoker.getPlayersList();
                Platform.runLater(() -> playersListView.getItems().setAll(playersList));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    });
    private FutureTask<Void> adminOptionsListTask = new FutureTask<>(() -> {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                List<String> optionsList = Invoker.getOptions();
                Platform.runLater(() -> {
                    mapChoiceBox.setValue(optionsList.get(0));
                    ruleChoiceBox.setValue(optionsList.get(1));
                    cardChoiceBox.setValue(optionsList.get(2));
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    });

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mapChoiceBox.setItems(FXCollections.observableArrayList("map1", "map2"));
        mapChoiceBox.setValue("map1");
        ruleChoiceBox.setItems(FXCollections.observableArrayList("rule1", "rule2"));
        ruleChoiceBox.setValue("rule1");
        cardChoiceBox.setItems(FXCollections.observableArrayList("card1", "card2"));
        cardChoiceBox.setValue("card1");
        playerNumberChoiceBox.setItems(FXCollections.observableArrayList(1, 2));
        playerNumberChoiceBox.setValue(1);

        Thread adminPlayersThread = new Thread(adminPlayersListTask);
        adminPlayersThread.setDaemon(true);
        adminPlayersThread.start();

        Thread adminOptionsThread = new Thread(adminOptionsListTask);
        adminOptionsThread.setDaemon(true);
        adminOptionsThread.start();
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void readyButtonHandler() {

    }

    @FXML
    private void backButtonHandler() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("../view/gameHallUI.fxml"));
            Parent root = loader.load();
            GameHallController controller = loader.getController();
            controller.setStage(stage);
            Platform.runLater(() -> {
                stage.setScene(new Scene(root));
                stage.show();
            });
            adminOptionsListTask.cancel(true);
            adminPlayersListTask.cancel(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
