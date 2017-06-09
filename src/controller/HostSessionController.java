package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.GameState;
import model.Invoker;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HostSessionController implements Initializable {
    @FXML
    private ChoiceBox<String> mapChoiceBox;
    @FXML
    private ChoiceBox<String> ruleChoiceBox;
    @FXML
    private ChoiceBox<String> cardChoiceBox;
    @FXML
    private ChoiceBox<Integer> playerNumberChoiceBox;
    @FXML
    private Button changeButton;
    @FXML
    private Group optionsGroup;
    @FXML
    private Label warningLabel;
    @FXML
    private ListView<String> playersListView;
    @FXML
    private Button inviteButton;
    @FXML
    private Button startButton;

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

    private Stage stage;

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
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void changeOptionsHandler() {
        warningLabel.setVisible(false);
        String map = mapChoiceBox.getValue();
        String rule = ruleChoiceBox.getValue();
        String card = cardChoiceBox.getValue();
        int playerNumber = playerNumberChoiceBox.getValue();
        optionsGroup.setDisable(true);
        warningLabel.setVisible(false);
        //start a thread to submit option changes
        FutureTask<Integer> changeOptionsTask = new FutureTask<>(() -> {
            try {
                return Invoker.changeOptions(map, rule, card);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return -1;
            }
        });
        Thread changeOptionsThread = new Thread(changeOptionsTask);
        changeOptionsThread.setDaemon(true);
        changeOptionsThread.start();
        Thread adminThread = new Thread(() -> {
            try {
                int result = changeOptionsTask.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    warningLabel.setVisible(true);
                    warningLabel.setText("Network Error");
                });
            }
            Platform.runLater(() -> optionsGroup.setDisable(false));
        });
        adminThread.setDaemon(true);
        adminThread.start();
    }


    @FXML
    private void inviteButtonHandler() {
        warningLabel.setVisible(false);
        inviteButton.setDisable(true);
        FutureTask<List<String>> getAvailablePlayersTask =
                new FutureTask<>(Invoker::getAvailablePlayersList);
        Thread getAvailablePlayersThread = new Thread(getAvailablePlayersTask);
        getAvailablePlayersThread.setDaemon(true);
        getAvailablePlayersThread.start();
        Thread adminGetAvailablePlayers = new Thread(() -> {
            List<String> result = null;
            try {
                result = getAvailablePlayersTask.get(5, TimeUnit.SECONDS);
            } catch (InterruptedException | TimeoutException | ExecutionException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    warningLabel.setVisible(true);
                    warningLabel.setText("Network Error");
                });
            }
            Platform.runLater(() -> inviteButton.setDisable(false));

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("../view/inviteUI.fxml"));
                Parent root = loader.load();
                InviteController controller = loader.getController();
                controller.setAvailablePlayersListView(result);
                Platform.runLater(() -> {
                    Stage newStage = new Stage();
                    newStage.initModality(Modality.WINDOW_MODAL);
                    newStage.initOwner(stage);
                    newStage.setScene(new Scene(root));
                    newStage.showAndWait();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        adminGetAvailablePlayers.setDaemon(true);
        adminGetAvailablePlayers.start();
    }

    @FXML
    private void startButtonHandler() {
        //consider about that a player will leave or a player will come
        sendGameSetup();
        sendGameState();
    }

    private void sendGameSetup() {
        String[] colors = {"#000000", "#0000FF", "#00FF00", "#00FFFF",
                "#FF0000", "#FF00FF", "#FFFF00", "#FFFFFF"};
        List<GameSetup.PlayerColorPair> players = new ArrayList<>();
        for (int i = 0; i < playersListView.getItems().size(); i++) {
            players.add(new GameSetup.PlayerColorPair(playersListView.getItems().get(i), colors[i]));
        }
        Map<String, Boolean> rules = new HashMap<>();
        for (String rule : ruleChoiceBox.getItems()) {
            rules.put(rule, false);
        }
        String rule = ruleChoiceBox.getValue();
        rules.put(rule, true);
        GameSetup gameSetup = new GameSetup(mapChoiceBox.getValue(), rules, players);
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(gameSetup);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(json);
    }

    private void sendGameState() {
        final int normalCardNum = 42;
        final int wildCardNum = 2;
        final int cardType = 3;
        GameState gameState = new GameState();
        GameState.Army[] map = new GameState.Army[42];
        for (int i = 0; i < normalCardNum; i++) {
            map[i] = gameState.new Army(-1, 0, i);
        }
        gameState.setMap(map);
        GameState.Player[] players = new GameState.Player[this.playersListView.getItems().size()];
        for (int i = 0; i < players.length; i++) {
            players[i] = gameState.new Player(this.playersListView.getItems().get(i), i, new ArrayList<GameState.Card>());
        }
        gameState.setPlayers(players);
        gameState.setGamePhase(0);
        gameState.setTurnToken(0);
        GameState.Card[] deck = new GameState.Card[42];
        for (int i = 0; i < normalCardNum; i++) {
            deck[i] = gameState.new Card(i, i % cardType);
        }
        gameState.setDeck(deck);

        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(gameState);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println(json);
        Invoker.sendGameState(json);
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
            adminPlayersListTask.cancel(true);
            Thread leaveThread = new Thread(Invoker::leave);
            leaveThread.setDaemon(true);
            leaveThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class GameSetup {
    private String map;
    private List<PlayerColorPair> players;
    private Map<String, Boolean> rules;

    GameSetup(String map, Map<String, Boolean> rules, List<PlayerColorPair> players) {
        this.map = map;
        this.rules = rules;
        this.players = players;
    }

    public Map<String, Boolean> getRules() {
        return rules;
    }

    public void setRules(Map<String, Boolean> rules) {
        this.rules = rules;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public List<PlayerColorPair> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerColorPair> players) {
        this.players = players;
    }

    static class PlayerColorPair {
        private String playerName;
        private String color;

        PlayerColorPair(String playerName, String color) {
            this.playerName = playerName;
            this.color = color;
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }
}



