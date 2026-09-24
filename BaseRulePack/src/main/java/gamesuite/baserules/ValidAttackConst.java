package gamesuite.baserules;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.boardgame.model.CheckersMove;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Action;
import gamesuite.core.model.rules.Constraint;

public class ValidAttackConst extends Constraint {

    private CheckersGameState gameState;
    private CheckersGameBoard board;

    public ValidAttackConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "validAttack";
    }

    public ValidAttackConst() {
        super("validAttack");
    }

    @Override
    public boolean checkMove(JsonNode move) {
        int sX = move.get("startX").asInt();
        int sY = move.get("startY").asInt();
        int eX = move.get("endX").asInt();
        int eY = move.get("endY").asInt();
        
        if(!this.board.isValidPos(sX, sY) || !this.board.isValidPos(eX, eY)) {
            return false;
        }
        
        int turnFactor = this.gameState.getTurnFactor();
        String[] teamNames = this.gameState.getTeamNames();
        int turn = this.gameState.getTurn();
        CheckersCoordPair start = this.board.getBoardPos(sX, sY);
        CheckersCoordPair end = this.board.getBoardPos(eX, eY);
        if(start == null || end == null)
            return false;


        CheckersGamePiece piece = start.getPiece();
         String team = piece.getTeam();
        //List<Action> actions = piece.getAttackRules();

        if(!teamNames[turn - 1].equals(team)) {
            return false;
        }

        int[][] attackVectors = piece.getAttackVectors();
        if(attackVectors != null) {
            
            
            for(int[] pair : attackVectors) {
                int x = start.getX() + pair[0] * turnFactor;
                int y = start.getY() + pair[1] * turnFactor;

                CheckersCoordPair attackCoord = this.board.getBoardPos(x, y);
                CheckersGamePiece p = attackCoord.getPiece();
                if(p == null || teamNames[turn - 1].equals(team)) {
                    return false;
                }
            }
        }
       

        
        //int turnFactor = -1;
        //if(name.equals(pieceNames[1]))
         //   turnFactor = 1;

        int[][] validJumps = piece.getValidJumps();
        for(int[] pair : validJumps) {
            int x = start.getX() + pair[0] * turnFactor;
            int y = start.getY() + pair[1] * turnFactor;

            if(end.getX() == x && end.getY() == y)
                return true;
        }
        return false;
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
        this.gameState = (CheckersGameState) game;
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (CheckersGameBoard) board;
    }

}
