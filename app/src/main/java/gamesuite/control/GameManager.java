package gamesuite.control;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import gamesuite.model.data.CoordPair;
import gamesuite.model.data.CoordPairView;
import gamesuite.model.data.GameBoard;
import gamesuite.model.data.GameBoardView;
import gamesuite.model.data.GamePiece;
import gamesuite.model.data.GameState;
import gamesuite.model.data.GameStateView;
import gamesuite.model.data.Move;
import gamesuite.model.data.Player;
import gamesuite.model.logic.GameStateManager;
import gamesuite.model.logic.MoveController;
import gamesuite.model.logic.RulesValidator;

public class GameManager {
    private GameState game;
    private RulesValidator validator;
    private GameStateManager stateManager;
    private MoveController moveController;

    public GameManager(Player player1, Player player2) {
        GameBoard board = new GameBoard(8);
        this.game = new GameState(board, player1, player2);
        this.validator = new RulesValidator(this.game);
        this.stateManager = new GameStateManager(this.game);
        this.moveController = new MoveController(validator, stateManager);
    }

    public GameStateView sendMove(Move move) {
        GameStateView gameView = null;
        if(this.moveController.checkMove(move)) {
            List<CoordPairView> changed = this.moveController.makeMove(move);
            this.moveController.updateState(changed);
            gameView = this.game.getGameStateView();
        }
        return gameView;
    }

    public Player getWinner() { 
        if(this.stateManager.getWinner() != null) 
            return this.stateManager.getWinner().copy();
        return null; 
    }

    public boolean gameOver() {
        if(this.stateManager.getWinner() != null || this.stateManager.getDraw())
            return true;
        return false;
    }
    
    public boolean initBoard() { return this.stateManager.initBoard(); }

    public GameStateView getGameStateView() { return this.stateManager.getGameStateView(); }

    public int getTurn() { return this.stateManager.getTurn(); }
}
