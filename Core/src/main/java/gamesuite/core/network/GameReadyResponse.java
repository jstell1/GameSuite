package gamesuite.core.network;

import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameState;

public class GameReadyResponse {
    private CoordPair[][] board;
    private String gameId;
    private GameState game;

    public GameReadyResponse() {}

    public GameReadyResponse(CoordPair[][] board, String gameId, GameState game) {
        this.board = board;
        this.gameId = gameId;
        this.game = game;
    }

    public void setBoard(CoordPair[][] board) {
        if(this.board == null)
            this.board = board;
    }

    public void setGameId(String gameId) {
        if(this.gameId == null)
            this.gameId = gameId;
    }

    public void setGame(GameState game) {
        if(this.game == null)
             this.game = game;
    }

    public CoordPair[][] getBoard() { return this.board; }

    public String getGameId() { return this.gameId; }

    public GameState getGameState() { return this.game; }
}
