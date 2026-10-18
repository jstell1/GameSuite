package gamesuite.chess;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.control.BoardGameManager;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Effect;
import gamesuite.core.model.rules.Updatable;
import gamesuite.core.model.rules.Updater;

public class ChessPromoteEffect extends Effect {

    private GameBoardImpl board;
    private BoardGameState gameState;
    private BoardGameManager gm;

    public ChessPromoteEffect() {
        super("chessPromote");
    }

    public ChessPromoteEffect(String name, GameState gameState) {

        super(name, gameState);
        //TODO Auto-generated constructor stub
        this.name = "chessPromote";
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
    public void setGameManager(GameManager gm) {
        this.gm = (BoardGameManager) gm;
    } 

    @Override
    public void updateState(JsonNode move) {
        String type = move.get("promote").asText();
        int x = move.get("endX").asInt();
        int y = move.get("endY").asInt();
        int oX = move.get("startX").asInt();
        int oY = move.get("startY").asInt();
        CoordPairImpl og = this.board.getBoardPos(oX,oY);
        CoordPairImpl pos = this.board.getBoardPos(x, y);
        String team = pos.getPiece().getTeam();


        BoardGamePiece piece = this.gm.buildPiece(type, team);
        pos.setPiece(piece);
        this.gameState.addChangedPos(og);
        this.gameState.addChangedPos(pos);
    }

    
    
}
