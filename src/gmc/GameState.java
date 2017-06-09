package gmc;

import java.util.Map;

public class GameState {

    private Map<Integer, Army> army_distribution; //this should be int if we are passing ints in
    private Player[] players;
    private long game_id;
    private int player_turn;
    private int game_phase;
    private int winner;
    private Card[] deck;

    public class Army {
        public int owner_id;
        public int num_armies;
    }
}