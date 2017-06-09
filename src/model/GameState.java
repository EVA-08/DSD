package model;

import java.util.ArrayList;

public class GameState {
    private Army[] map; //this should be int if we are passing ints in
    private Player[] players;
    private int turnToken;
    private int gamePhase;
    private Card[] deck;

    public Army[] getMap() {
        return map;
    }

    public void setMap(Army[] map) {
        this.map = map;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public int getTurnToken() {
        return turnToken;
    }

    public void setTurnToken(int turnToken) {
        this.turnToken = turnToken;
    }

    public int getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(int gamePhase) {
        this.gamePhase = gamePhase;
    }

    public Card[] getDeck() {
        return deck;
    }

    public void setDeck(Card[] deck) {
        this.deck = deck;
    }

    public class Army {
        private int owner_id;
        private int num_armies;
        private int country_id;

        public Army() {
        }

        public Army(int owner_id, int num_armies, int country_id) {
            this.owner_id = owner_id;
            this.num_armies = num_armies;
            this.country_id = country_id;
        }

        public int getOwner_id() {
            return owner_id;
        }

        public void setOwner_id(int owner_id) {
            this.owner_id = owner_id;
        }

        public int getNum_armies() {
            return num_armies;
        }

        public void setNum_armies(int num_armies) {
            this.num_armies = num_armies;
        }

        public int getCountry_id() {
            return country_id;
        }

        public void setCountry_id(int country_id) {
            this.country_id = country_id;
        }
    }

    public class Player {
        private String name;
        private int id;
        private ArrayList<Card> hand;

        public Player(String name, int id, ArrayList<Card> hand) {
            this.name = name;
            this.id = id;
            this.hand = hand;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ArrayList<Card> getHand() {
            return hand;
        }

        public void setHand(ArrayList<Card> hand) {
            this.hand = hand;
        }
    }

    public class Card {
        private int territory; //territory
        private int type; //cannon,infantry,etc

        public Card(int territory, int type) {
            this.territory = territory;
            this.type = type;
        }

        public int getTerritory() {
            return territory;
        }

        public void setTerritory(int territory) {
            this.territory = territory;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
