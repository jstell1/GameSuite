package gamesuite.core.network;

import gamesuite.core.model.Player;

public class JoinGameRequest {

    private String player;
    private String gameId;
    private String sessionId;

    public JoinGameRequest() {}

    public void setPlayer(String player) { this.player = player; }

    public void setGameId(String gameId) { this.gameId = gameId; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getPlayer() { return this.player; }

    public String getGameId() { return this.gameId; }

    public String getSessionId() { return this.sessionId; }
}
