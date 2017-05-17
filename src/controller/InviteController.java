package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;


/**
 * Created by EVA-08 on 2017/5/15.
 */
public class InviteController {
    @FXML
    private ListView<String> availablePlayersListView;

    void setAvailablePlayersListView(List<String> list) {
        this.availablePlayersListView.getItems().setAll(list);
    }

    @FXML
    private void inviteButtonHandler() {

    }
}
