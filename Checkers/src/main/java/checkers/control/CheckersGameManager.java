package checkers.control;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;
import checkers.model.CheckersPlayer;
import checkers.view.CheckersGameBoardView;
import checkers.view.CheckersGameStateView;
import checkers.view.CheckersMoveView;
import checkers.view.CheckersPlayerView;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;

public class CheckersGameManager implements GameManager {
    private RulesValidator validator;
    private GameStateManager stateManager;
    private MoveController moveController;
    private CheckersGameState game;
    private CheckersGameBoard board;

    

    public CheckersGameManager(CheckersGameBoard board, CheckersPlayer player1) {
        this.board = board;
        CheckersGameState game = new CheckersGameState(player1);
        this.game = game;
        this.validator = new RulesValidator(game, board);
        this.stateManager = new GameStateManager(game, board);
        this.moveController = new MoveController(validator, stateManager);
    }

    public CheckersGameManager(CheckersGameBoard board, CheckersPlayer player1, CheckersPlayer player2) {
        this.board = board;
        CheckersGameState game = new CheckersGameState(player1, player2);
        this.game = game;
        this.validator = new RulesValidator(game, board);
        this.stateManager = new GameStateManager(game, board);
        this.moveController = new MoveController(validator, stateManager);
    }

    public void sendMove(Move move) {
        CheckersMove mv = new CheckersMove(move.getStartX(), move.getStartY(), move.getEndX(), move.getEndY());
        if(this.moveController.checkMove(mv)) {
            List<CheckersCoordPair> changed = this.moveController.makeMove(mv);
            this.moveController.updateState(changed);
        }
    }

    @Override
    public void sendMove(ObjectNode move) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            CheckersMove mv = mapper.treeToValue(move, CheckersMove.class);
            CheckersMoveView mvView = new CheckersMoveView(mv);
            sendMove(mvView);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean addPlayer(Player player) {
        CheckersPlayer pl = new CheckersPlayer(player.getName(), player.getPoints());
        return this.stateManager.addPlayer(pl);
    }

    public boolean addPlayer(String name) {
        CheckersPlayer player = new CheckersPlayer(name, getTurn());
        return this.stateManager.addPlayer(player);
    }

    public boolean isGameReady() {
        return getGameState().isBoardInit();
    }

    public String getBoardString() { return this.stateManager.getBoardString(); }

    public Player getWinner() { 
        if(this.stateManager.getWinner() != null) 
            return new CheckersPlayerView(this.stateManager.getWinner().copy());
        return null; 
    }

    public GameBoard getBoard() { return new CheckersGameBoardView(this.stateManager.getBoardCopy()); }

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
        return new CheckersGameStateView(this.game);
    }

    public GameState quitGame(int playerNum) {
        if(this.stateManager.getWinner() == null) {
            this.stateManager.setWinner(playerNum % 2 + 1);
        }
        return getGameState();
    }
}
