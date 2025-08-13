package gamesuite.client.control;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gamesuite.core.control.GameStateManager;
import gamesuite.core.control.MoveController;
import gamesuite.core.control.RulesValidator;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GamePiece;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import gamesuite.core.view.CoordPairView;
import gamesuite.core.view.GameBoardView;
import gamesuite.core.view.GameStateView;
import gamesuite.core.view.PlayerView;

public class GameManager {
    private RulesValidator validator;
    private GameStateManager stateManager;
    private MoveController moveController;
    //private GameUI ui;

    public GameManager(Player player1, Player player2) {
        GameBoard board = new GameBoard(8);
        GameState game = new GameState(board, player1, player2);
        this.validator = new RulesValidator(game);
        this.stateManager = new GameStateManager(game);
        this.moveController = new MoveController(validator, stateManager);
    }
/*
    public void setUI(GameUI ui) { 
        if(ui != null)
            this.ui = ui;
    }
*/
    public void sendMove(Move move) {
        //if(ui == null)
          //  return null;
        GameStateView gameView = null;
        if(this.moveController.checkMove(move)) {
            List<CoordPairView> changed = this.moveController.makeMove(move);
            this.moveController.updateState(changed);
            gameView = this.stateManager.getGameStateView();
        }
        //System.out.println(getBoardString());
        //this.ui.sendChanges(gameView);
    }

    public String getBoardString() { return this.stateManager.getBoardString(); }

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
