package gamesuite.boardgame.control;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.rules.Effect;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.boardgame.model.BoardGameState;
import gamesuite.boardgame.model.BoardGameMove;
import gamesuite.boardgame.model.BoardGamePlayer;

public class BoardGameManager implements GameManager {
    private MoveController moveController;
    private BoardGameState game;
    private GameBoardImpl board;
    public static final String name = "BoardGameEngine";
    private List<String> sessionPlayerMap;
    private Map<String, BoardGamePiece.Builder> pieceList;
    private int[][] attackVectors;
    private Effect turnControl;
    private String ui;

    
    public BoardGameManager(GameBoardImpl board, BoardGameState game, MoveController moveController) {
        this.board = board;
        this.game = game;
        this.moveController = moveController;
        this.sessionPlayerMap = new ArrayList<>();
    }

    public void setUI(String ui) { this.ui = ui; }
  
    @Override
    public String getUI() { return this.ui; };

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
        if(this.sessionPlayerMap.get(turn - 1).equals(playerId)) {
            return true;
        }
        return false;
    }

    public void setPieceList(Map<String, BoardGamePiece.Builder> list) {
        this.pieceList = list;
    }
    
    public BoardGamePiece buildPiece(String type, String team) {
        BoardGamePiece.Builder tmp = this.pieceList.get(type);
        tmp.setTeam(team).setName(team + type);
        return tmp.build();
    }

    public void mapSessionPlayer(String sessionId) {
        this.sessionPlayerMap.add(sessionId);
    }

    @Override
    public String getName() {
        return name;
    } 

    @Override
    public boolean sendMove(ObjectNode move, String playerId) throws Exception {

        this.game.resetChangedPos();
        this.game.setLastJumped(false);
        if(gameOver()) {
            return false;
        }

        move.put("playerId", playerId);

        

        boolean result = this.moveController.gameCheck(move); 
        
        if(result) {
            result = this.moveController.moveCheck(move);
        }

        if(result) {
            this.moveController.applyEffects(move);
        }
        this.moveController.clearEffects();

        boolean postCheck = false;
        if(result) {
            postCheck = this.moveController.postCheck(move);
        }
        
        if(postCheck) {
            this.moveController.applyEffects(move);
        }
        this.moveController.clearEffects();
        
        boolean endCheck = false;
        if(result) {
            endCheck = this.moveController.endCheck(move);
        }

        if(endCheck) {
            this.moveController.applyEffects(move);
        }
        this.moveController.clearEffects();

        if(result) {
            this.turnControl.updateState(move);
        }
            
      

        return result;
       
    }

    public boolean addPlayer(BoardGamePlayer player) {
        mapSessionPlayer(player.getUserId());
        return this.game.addPlayer(player);
    }

    public boolean addPlayer(String name) {
        BoardGamePlayer player = new BoardGamePlayer(name, getTurn());
        return this.game.addPlayer(player);
    }

    public boolean isGameReady() {
        return this.game.isBoardInit();
    }

    public String getBoardString() { return this.board.toString(); }

    public BoardGamePlayer getWinner() { 
        if(this.game.getWinner() != null) 
            return this.game.getWinner();
        return null; 
    }

    public GameBoardImpl getBoard() { return this.board; }

    public boolean gameOver() {
        if(this.game.getWinner() != null)
            return true;
        return false;
    }
    
    public boolean initBoard() { 
        if(this.game.getNumPlayers() == 2) {
             if(this.game.isBoardInit())
                return false;
            this.game.setBoardInit();
            return true;
        }
        return false;
    }

    public int getTurn() { return this.game.getTurn(); }

    @Override
    public List<String> getUserIdList() {
        return this.sessionPlayerMap;
    }

    public BoardGameState quitGame(String playerId) {

        int playerNum = this.game.getPlayerById(playerId).getTurn();
        if(this.game.getWinner() == null) {
            this.game.setWinnerNum(playerNum % 2 + 1);
        }
        playerNum = this.sessionPlayerMap.indexOf(playerId);
        this.sessionPlayerMap.remove(playerNum);
        this.game.setGameOver(true);
        return this.game;
    }

    @Override
    public JsonNode getGameStateJson() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.valueToTree(this.game);
        return json;
    }

    @Override
    public int getNumMappedPlayers() {
        return this.sessionPlayerMap.size();
    }

    @Override
    public int getNumPlayers() {
        return this.game.getNumPlayers();
    }

    @Override
    public JsonNode joinGame(String player, String playerId) {
        BoardGamePlayer p = new BoardGamePlayer(player, 0);
        p.setUserId(playerId);
        p.setTurn(2);
        if(p != null) {

            boolean added = addPlayer(p);
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
