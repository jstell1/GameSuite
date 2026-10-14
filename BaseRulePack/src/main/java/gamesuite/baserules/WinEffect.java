package gamesuite.baserules;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Effect;

public class WinEffect extends Effect {

    private GameBoardImpl board;
    private BoardGameState gameState;

    public WinEffect() {
        super("win");
    }

    public WinEffect(String name, GameState gameState) {
        super(name, gameState);
        //TODO Auto-generated constructor stub
        this.name = "win";
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
        int winnerNum = this.gameState.getTurn();
        this.gameState.setWinnerNum(winnerNum);
        this.gameState.setGameOver(true);
    }
    
}
