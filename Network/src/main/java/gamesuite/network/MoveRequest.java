package gamesuite.network;

import gamesuite.core.model.Move;

public class MoveRequest {
    private String gameId;
    private String sessionId;
    private Move move;

    public MoveRequest() {}

    public MoveRequest(String userId, String gameId, Move move) {
        this.gameId = gameId;
        this.move = move;
    }

    public void setSessionId(String sessionId) { 
        if(this.sessionId == null)
            this.sessionId = sessionId;
    }

    public void setGameId(String gameId) {
        if(this.gameId == null)
            this.gameId = gameId;
    }

    public void setMove(Move move) {
        if(this.move == null)
            this.move = move;
    }

    public String getGameId() { return this.gameId; }

    public Move getMove() { return this.move; }

    public String getSessionId() { return this.sessionId; }
}
