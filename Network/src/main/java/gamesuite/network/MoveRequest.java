package gamesuite.network;

import gamesuite.core.model.Move;

public class MoveRequest {
    private String userId;
    private String gameId;
    private Move move;

    public MoveRequest() {}

    public MoveRequest(String userId, String gameId, Move move) {
        this.userId = userId;
        this.gameId = gameId;
        this.move = move;
    }

    public void setUserId(String userId) {
        if(this.userId == null)
            this.userId = userId;
    }

    public void setGameId(String gameId) {
        if(this.gameId == null)
            this.gameId = gameId;
    }

    public void setMove(Move move) {
        if(this.move == null)
            this.move = move;
    }

    public String getUserId() { return this.userId; }

    public String getGameId() { return this.gameId; }

    public Move getMove() { return this.move; }
}
