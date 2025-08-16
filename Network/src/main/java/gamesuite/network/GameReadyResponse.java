package gamesuite.network;

import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameState;

public class GameReadyResponse {
    private CoordPair[][] board;
    private String gameId;
    private GameState game;

    public GameReadyResponse(CoordPair[][] board, String gameId, GameState game) {
        this.board = board;
        this.gameId = gameId;
        this.game = game;
    }

    public CoordPair[][] getBoard() { return this.board; }

    public String getGameId() { return this.gameId; }

    public GameState getGameState() { return this.game; }
}
