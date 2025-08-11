package gamesuite.Client.Control;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gamesuite.Client.View.GameUI;
import gamesuite.Core.Control.GameStateManager;
import gamesuite.Core.Control.MoveController;
import gamesuite.Core.Control.RulesValidator;
import gamesuite.Core.Model.CoordPair;
import gamesuite.Core.Model.GameBoard;
import gamesuite.Core.Model.GamePiece;
import gamesuite.Core.Model.GameState;
import gamesuite.Core.Model.Move;
import gamesuite.Core.Model.Player;
import gamesuite.Core.View.CoordPairView;
import gamesuite.Core.View.GameBoardView;
import gamesuite.Core.View.GameStateView;
import gamesuite.Core.View.PlayerView;

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
