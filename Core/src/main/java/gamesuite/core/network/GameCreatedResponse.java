package gamesuite.core.network;

import gamesuite.core.model.GameState;

public class GameCreatedResponse {

    private String gameId;
    private GameState game;

    public GameCreatedResponse() {}

    public GameCreatedResponse(String gameId, GameState game) {
        this.gameId = gameId;
        this.game = game;
    }

    public void setGameId(String gameId) {
        if(this.gameId == null)
            this.gameId = gameId;
    }

    public void setGame(GameState game) {
        if(this.game == null)
            this.game = game;
    }
    
    public String getGameId() { return this.gameId; }

    public GameState getGame() { return this.game; }
}
