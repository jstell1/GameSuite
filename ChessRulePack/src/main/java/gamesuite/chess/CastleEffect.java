package gamesuite.chess;
import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Effect;


public class CastleEffect extends Effect {

    private BoardGameState gameState;
    private GameBoardImpl board;

    public CastleEffect() {
        super("castle");
    }

    public CastleEffect(String name, GameState gameState) {
        super(name, gameState);
        //TODO Auto-generated constructor stub
        this.name = "castle";
    }

    @Override
    public void setGameState(JsonNode game) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setGameState'");
    }

    @Override
    public void setBoard(JsonNode board) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setBoard'");
    }

    @Override
    public void setGameState(GameState game) {
        this.gameState = (BoardGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (GameBoardImpl) board;
    }

    @Override
    public void updateState(JsonNode move) {
        
        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();

        CoordPairImpl rookPos = null;
        CoordPairImpl currRook = null;
        if(eY < sY) {
            currRook = this.board.getBoardPos(eX, 0);
            rookPos = this.board.getBoardPos(eX, sY - 1);
        } else {
            currRook = this.board.getBoardPos(eX, 7);
            rookPos = this.board.getBoardPos(eX, sY + 1);
        }
        
        rookPos.setPiece(currRook.getPiece());
        currRook.setPiece(null);

        this.gameState.addChangedPos(rookPos);
        this.gameState.addChangedPos(currRook);

    }
    
}
