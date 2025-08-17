package gamesuite.network;

import gamesuite.core.model.GameState;

public class GameCreatedResponse {

    private String gameId;
    private GameState game;

    public GameCreatedResponse(String gameId, GameState game) {
        this.gameId = gameId;
        this.game = game;
    }

    public String getGameId() { return this.gameId; }

    public GameState getGameState() { return this.game; }
}
