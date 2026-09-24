package gamesuite.baserules;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Effect;
import gamesuite.core.model.rules.Result;

public class CaptureEffect extends Effect {

    private CheckersGameBoard board;
    private CheckersGameState gameState;

    public CaptureEffect() {
        super("capture");
    }

    public CaptureEffect(String name, GameState gameState) {
        super(name, gameState);
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
        this.gameState = (CheckersGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (CheckersGameBoard) board;
    }

    @Override
    public void updateState(JsonNode move) {
        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();
        int mX = (sX + eX) >> 1;
        int mY = (sY + eY) >> 1;

        CheckersCoordPair pos = this.board.getBoardPos(sX, sY);
        //CheckersCoordPair end = this.board.getBoardPos(eX, eY);
        CheckersCoordPair mid = this.board.getBoardPos(mX, mY);
        mid.setPiece(null);

        

        if(this.gameState.getChangedPos() == null) {
            this.gameState.resetChangedPos();
        }

        this.gameState.addChangedPos(mid);
    }


    
}
