package gamesuite.baserules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.ConstDependent;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Effect;

public class MakeMoveEffect extends Effect implements ConstDependent {

    private GameBoardImpl board;
    private BoardGameState gameState;
    private Constraint validAttack;
    static final String[] dependencies = {"validAttack"};

    public MakeMoveEffect() {
        super("makeMove");
    }

    public MakeMoveEffect(String name, GameState gameState) {
        super(name, gameState);
        //TODO Auto-generated constructor stub
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

        CoordPairImpl pos = this.board.getBoardPos(sX, sY);
        CoordPairImpl end = this.board.getBoardPos(eX, eY);

        BoardGamePiece piece = pos.getPiece();
        end.setPiece(piece);
        pos.setPiece(null);

        if(this.gameState.getChangedPos() == null) {
            this.gameState.resetChangedPos();
        }
        
        this.gameState.addChangedPos(pos);
        this.gameState.addChangedPos(end);
        this.gameState.setFurtherJumps(null);
    }

    @Override
    public void addDependencies(Map<String, Constraint> arg0) {
        this.validAttack = arg0.get("validAttack");
    }

    @Override
    public List<String> getDependencyList() {
        return Arrays.asList(dependencies);
    }
    
}
