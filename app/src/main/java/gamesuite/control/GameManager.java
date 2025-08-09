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
import gamesuite.model.data.PlayerView;
import gamesuite.model.logic.GameStateManager;
import gamesuite.model.logic.MoveController;
import gamesuite.model.logic.RulesValidator;
import gamesuite.view.GameUI;

public class GameManager implements MoveListener {
    private RulesValidator validator;
    private GameStateManager stateManager;
    private MoveController moveController;
    private GameUI ui;

    public GameManager(Player player1, Player player2) {
        GameBoard board = new GameBoard(8);
        GameState game = new GameState(board, player1, player2);
        this.validator = new RulesValidator(game);
        this.stateManager = new GameStateManager(game);
        this.moveController = new MoveController(validator, stateManager);
    }

    public void setUI(GameUI ui) { 
        if(ui != null)
            this.ui = ui;
    }

    @Override
    public void sendMove(Move move) {
        if(ui == null)
            return;
        GameStateView gameView = null;
        if(this.moveController.checkMove(move)) {
            List<CoordPairView> changed = this.moveController.makeMove(move);
            this.moveController.updateState(changed);
            gameView = this.stateManager.getGameStateView();
        }
        this.ui.sendChanges(gameView);
    }

    public PlayerView getWinner() { 
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
