package model;

import java.util.List;

public class Invoker {
    static public int validateLogin(String username, String password)
            throws InterruptedException {
        return Mock.loginAccount(username, password);
    }

    static public int createNewAccount(String username, String password, String email)
            throws InterruptedException {
        return Mock.createNewAccount(username, password, email);
    }

    static public int changeOptions(String map, String rule, String card)
            throws InterruptedException {
        return Mock.changeOptions(map, rule, card);
    }

    static public List<String> getPlayersList() throws InterruptedException {
        return Mock.getPlayersList();
    }

    static public List<String> getAvailablePlayersList() throws InterruptedException {
        return Mock.getAvailablePlayersList();
    }

    static public void invite(String username) {
        Mock.invite(username);
    }

    static public String getInvatation() throws InterruptedException {
        return Mock.getInvivation();
    }

    static public int joinSession(String hostname, String sessionID) throws InterruptedException {
        return Mock.joinSession(hostname, sessionID);
    }

    static public List<String> getOptions() throws InterruptedException {
        return Mock.getOptions();
    }

    static public void leave() {
        Mock.leave();
    }
}
