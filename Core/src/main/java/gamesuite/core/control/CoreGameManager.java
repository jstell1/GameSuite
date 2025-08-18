package gamesuite.core.control;

import java.util.List;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import gamesuite.core.network.GameCreatedResponse;
import gamesuite.core.model.CoordPair;

public class CoreGameManager extends AbstractGameManager {
    private RulesValidator validator;
    private GameStateManager stateManager;
    private MoveController moveController;
    private GameState game;
    GameBoard board;

    public CoreGameManager(GameBoard board, Player player1) {
        this.board = board;
        GameState game = new GameState(player1);
        this.game = game;
        this.validator = new RulesValidator(game, board);
        this.stateManager = new GameStateManager(game, board);
        this.moveController = new MoveController(validator, stateManager);
    }

    public CoreGameManager(GameBoard board, Player player1, Player player2) {
        this.board = board;
        GameState game = new GameState(player1, player2);
        this.game = game;
        this.validator = new RulesValidator(game, board);
        this.stateManager = new GameStateManager(game, board);
        this.moveController = new MoveController(validator, stateManager);
    }

    @Override
    public void sendMove(Move move) {
        if(this.moveController.checkMove(move)) {
            List<CoordPair> changed = this.moveController.makeMove(move);
            this.moveController.updateState(changed);
        }
    }

    public boolean addPlayer(Player player) {
        return this.stateManager.addPlayer(player);
    }

    public String getBoardString() { return this.stateManager.getBoardString(); }

    public Player getWinner() { 
        if(this.stateManager.getWinner() != null) 
            return this.stateManager.getWinner().copy();
        return null; 
    }

    public GameBoard getBoard() { return this.stateManager.getBoardCopy(); }

    public boolean gameOver() {
        if(this.stateManager.getWinner() != null || this.stateManager.getDraw())
            return true;
        return false;
    }
    
    public boolean initBoard() { 
        if(this.validator.playersReady()) {
            this.stateManager.initBoard(); 
            return true;
        }
        return false;
    }

    public int getTurn() { return this.stateManager.getTurn(); }

    public GameState getGameState() {
        return this.game;
    }
}
