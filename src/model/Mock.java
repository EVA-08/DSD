package model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class Mock {
    static private int times = 0;

    static int loginAccount(String username, String password)
            throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        return 1;
    }

    static int createNewAccount(String username, String password, String email)
            throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        return 1;
    }

    static int changeOptions(String map, String rule, String card)
            throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        return 1;
    }

    static List<String> getPlayersList() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        List<String> list = new ArrayList<>();
        list.add(String.valueOf(times));
        list.add(String.valueOf(times + 1));
        list.add(String.valueOf(times + 2));
        times++;
        return list;
    }

    static List<String> getAvailablePlayersList() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        List<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        return list;
    }

    static void invite(String username) {
    }

    static String getInvivation() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        times++;
        return String.valueOf(times) + " " + String.valueOf(times + 10);
    }

    static int joinSession(String hostname, String sessionID) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        return 1;
    }

    static List<String> getOptions() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        List<String> list = new ArrayList<>();
        list.add("map1");
        list.add("rule1");
        list.add("card1");
        return list;
    }
}
