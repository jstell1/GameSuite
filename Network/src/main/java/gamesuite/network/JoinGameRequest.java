package gamesuite.network;

import gamesuite.core.model.Player;

public class JoinGameRequest {

    private Player player;
    private String gameId;
    public JoinGameRequest() {}

    public void setPlayer(Player player) { this.player = player; }

    public void setGameId(String gameId) { this.gameId = gameId; }

    public Player getPlayer() { return this.player; }

    public String getGameId() { return this.gameId; }
}
