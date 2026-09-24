package gamesuite.boardgame.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.rules.Effect;
import gamesuite.core.model.rules.Result;
import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.boardgame.model.CheckersMove;
import gamesuite.boardgame.model.CheckersPlayer;

public class BoardGameManager implements GameManager {
    private RulesValidator validator;
    private GameStateManager stateManager;
    private MoveController moveController;
    private CheckersGameState game;
    private CheckersGameBoard board;
    public static final String name = "BoardGameEngine";
    private List<String> sessionPlayerMap;
    private Map<String, CheckersGamePiece.Builder> pieceList;
    private int[][] attackVectors;
    private Effect turnControl;

    

    public BoardGameManager(CheckersGameBoard board, CheckersPlayer player1) {
        this.board = board;
        CheckersGameState game = new CheckersGameState(player1);
        this.game = game;
        this.validator = new RulesValidator(game, board);
        this.stateManager = new GameStateManager(game, board);
        this.moveController = new MoveController(validator, stateManager, this.board);
        this.sessionPlayerMap = new ArrayList<>();
        //this.runList = new ArrayList<>();
    }

    public BoardGameManager(CheckersGameBoard board, RulesValidator validator, CheckersGameState game, MoveController moveController, GameStateManager mang) {
        this.board = board;
        this.game = game;
        this.stateManager = mang;
        //this.stateManager = new GameStateManager(game, board);
        this.validator = validator;
        this.moveController = moveController;
        this.sessionPlayerMap = new ArrayList<>();
    }

    public BoardGameManager(CheckersGameBoard board, RulesValidator validator) {
        this.board = board;
        this.validator = validator;
        //this.runList = new ArrayList<>();
        this.sessionPlayerMap = new ArrayList<>();
        
    }

    public BoardGameManager(CheckersGameBoard board, CheckersPlayer player1, CheckersPlayer player2) {
        this.board = board;
        CheckersGameState game = new CheckersGameState(player1, player2);
        this.game = game;
        this.validator = new RulesValidator(game, board);
        this.stateManager = new GameStateManager(game, board);
        this.moveController = new MoveController(validator, stateManager, this.board);
        //this.runList = new ArrayList<>();
        this.sessionPlayerMap = new ArrayList<>();
    }

    public void setAttackVectors(JsonNode vects) {
        int[][] vectArr = new int[vects.size()][2];
        int i = 0;
        for(JsonNode node : vects) {
            int x = node.get("x").asInt();
            int y = node.get("y").asInt();
            vectArr[i][0] = x;
            vectArr[i][1] = y;
            i++;
        }
    }

    public void setTurnControl(Effect turnControl) {
        this.turnControl = turnControl;
    }

    @Override 
    public boolean checkPlayerSession(String playerId) {

         int turn = this.game.getTurn();
        if(this.sessionPlayerMap.get(turn).equals(playerId)) {
            return true;
        }
        return false;
    }

    public void setPieceList(Map<String, CheckersGamePiece.Builder> list) {
        this.pieceList = list;
    }
    
    public CheckersGamePiece buildPiece(String type, String name) {
        CheckersGamePiece.Builder tmp = this.pieceList.get(type);
        tmp.setTeam(name).setName(name + type);
        return tmp.build();
    }

    public void sendMove(CheckersMove move) {

        // boolean endCheck = this.moveController.endCheck();

        // if(endCheck) {
        //     this.moveController.applyEffects(null);
        // }
        // this.moveController.clearEffects();

        // if(!endCheck) {

        //     boolean result = this.moveController.gameCheck(); 
           
        //     if(result) {
        //         result = this.moveController.pieceCheck(move);
        //     }
    
        //     if(result) {
        //         this.moveController.applyEffects(move);
        //     }
    
        //     this.moveController.clearEffects();
    
        //     if(result) {
        //         result = this.moveController.postCheck(move);
        //     }
    
        //     if(result) {
        //         this.moveController.applyEffects(move);
        //     }
    
        //     this.moveController.clearEffects();
        // }

       
       /*
        CheckersMove mv = new CheckersMove(move.getStartX(), move.getStartY(), move.getEndX(), move.getEndY());
        if(this.moveController.checkMove(mv)) {
            List<CheckersCoordPair> changed = this.moveController.makeMove(mv);
            this.moveController.updateState(changed);
        }
        */
    }

    public void mapSessionPlayer(String sessionId) {
        this.sessionPlayerMap.add(sessionId);
    }

    @Override
    public String getName() {
        return name;
    } 

    @Override
    public void sendMove(ObjectNode move, String playerId) {

        if(gameOver()) {
            return;
        }

        move.put("playerId", playerId);

        boolean endCheck = this.moveController.endCheck(move);
        if(endCheck) {

            this.moveController.applyEffects(move);
        }
        this.moveController.clearEffects();

        if(!endCheck) {

            boolean result = this.moveController.gameCheck(move); 
           
            if(result) {
                result = this.moveController.moveCheck(move);
            }
    
            boolean moveSuccess = false;
            if(result) {
                this.moveController.applyEffects(move);
                moveSuccess = true;
            }
    
            this.moveController.clearEffects();
    
            boolean postCheck = false;
            if(moveSuccess) {
                postCheck = this.moveController.postCheck(move);
            }
    
            if(postCheck) {
                this.moveController.applyEffects(move);
            }
    
            this.moveController.clearEffects();

            if(moveSuccess) {
                this.turnControl.updateState(move);
            }
            
        }
        // ObjectMapper mapper = new ObjectMapper();
        // try {
        //     CheckersMove mv = mapper.treeToValue(move, CheckersMove.class);
        //     mv.setPlayerId(playerId);
        //     //CheckersMoveView mvView = new CheckersMoveView(mv);
        //     sendMove(mv);
        // } catch (JsonProcessingException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // } catch (IllegalArgumentException e) {
        //     // TODO Auto-generated catch block
        //     e.printStackTrace();
        // }
    }

    public boolean addPlayer(CheckersPlayer player) {
        //CheckersPlayer pl = new CheckersPlayer(player.getName(), player.getPoints());
        mapSessionPlayer(player.getUserId());
        return this.stateManager.addPlayer(player);
    }

    public boolean addPlayer(String name) {
        CheckersPlayer player = new CheckersPlayer(name, getTurn());
        return this.stateManager.addPlayer(player);
    }

    public boolean isGameReady() {
        return this.game.isBoardInit();
    }

    public String getBoardString() { return this.stateManager.getBoardString(); }

    public CheckersPlayer getWinner() { 
        if(this.stateManager.getWinner() != null) 
            return this.stateManager.getWinner().copy();
        return null; 
    }

    public CheckersGameBoard getBoard() { return this.board; }

    public boolean gameOver() {
        if(this.stateManager.getWinner() != null)//|| this.stateManager.getDraw())
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

    //public GameState getGameState() {
    //    return new CheckersGameStateView(this.game);
    //}

    @Override
    public List<String> getUserIdList() {
        //for(String id : this.sessionPlayerMap) {

       // }
        return this.sessionPlayerMap;
    }

    public CheckersGameState quitGame(String playerId) {

        int playerNum = this.sessionPlayerMap.indexOf(playerId) + 1;
        if(this.stateManager.getWinner() == null) {
            this.stateManager.setWinner(playerNum % 2 + 1);
        }
        return this.game;
    }

    @Override
    public JsonNode getGameStateJson() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.valueToTree(this.game);
        return json;
    }

    @Override
    public int getNumPlayers() {
        return this.game.getNumPlayers();
    }

    @Override
    public JsonNode joinGame(String player, String playerId) {
        CheckersPlayer p = new CheckersPlayer(player, 0);
        p.setUserId(playerId);
        mapSessionPlayer(playerId);
        if(p != null) {

            boolean added = addPlayer(player);
            if(added) {
                initBoard();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode b = mapper.valueToTree(this.board.getBoard());
                return b;
            }
        }
        return null;
    }

    @Override
    public void sendMove(Move move) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendMove'");
    }

    @Override
    public GameState getGameState() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGameState'");
    }
}
