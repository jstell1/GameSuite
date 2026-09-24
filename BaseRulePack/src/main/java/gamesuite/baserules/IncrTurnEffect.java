package gamesuite.baserules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersGameState;
import gamesuite.boardgame.model.CheckersMove;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Effect;

public class IncrTurnEffect extends Effect {

    private CheckersGameBoard board;
    private CheckersGameState gameState;
    private int[][] vectors;
    private Set jumpsAllowed;
    private Set exactVectors;
    private boolean fullVector;
    private Constraint furtherAttacks;

    public IncrTurnEffect() {
        super("incrTurn");
        this.furtherAttacks = new FurtherAttacksConst();
    }

    public IncrTurnEffect(String name, GameState gameState) {
        super(name, gameState);
        //TODO Auto-generated constructor stub
        this.furtherAttacks = new FurtherAttacksConst();
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
    public void setExtraParams(JsonNode extras) {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode aVectorsNode = extras.get("attackVectors");
        JsonNode jumpsNode = extras.get("jumpsAllowed");
        JsonNode exactNode = extras.get("exactVector");
        this.fullVector = extras.get("fullVector").asBoolean();

        try {
            this.jumpsAllowed = mapper.treeToValue(jumpsNode, HashSet.class);
            this.exactVectors = mapper.treeToValue(exactNode, HashSet.class);
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        this.vectors = new int[aVectorsNode.size()][2];
        int i = 0;
        for(JsonNode node : aVectorsNode) {
            this.vectors[i][0] = node.get("x").asInt();
            this.vectors[i][1] = node.get("y").asInt();
            i++;
        }
    }

    @Override
    public void setBoard(GameBoard board) {
        this.board = (CheckersGameBoard) board;
    }

    @Override
    public void updateState(JsonNode move) {
       //ObjectMapper mapper = new ObjectMapper();
        updateAttacksList();
      
        this.gameState.setChangedPos(new ArrayList<CheckersCoordPair>());
        if(this.gameState.getFurtherJumps() != null) {
            return;
        }

        this.gameState.setTurn(this.gameState.getTurn() % 2 + 1);
        this.gameState.flipTurnFactor();


    }

    protected void updateAttacksList() {
        String[] teams = this.gameState.getTeamNames();
       
        //CheckersMove m = new CheckersMove();//mapper.treeToValue(move, CheckersMove.class);
        List<CheckersCoordPair> changed = this.gameState.getChangedPos();        
        
        for(CheckersCoordPair pos : changed) {

            //check the position
            handleFurtherAttacks(pos);
            
            //check each vector
            for(int[] vect : vectors) {
                
                if(this.fullVector) {
                    checkToEnd(vect, pos);
                } else {
                    checkUntilFirst(vect, pos);
                }
            }
        }
    }

    private void handleFurtherAttacks(CheckersCoordPair pos) {

        CheckersMove m = new CheckersMove();
        int x = pos.getX();
        int y = pos.getY();
        m.setStartX(x);
        m.setStartY(y);
        CheckersGamePiece piece = pos.getPiece();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode n = mapper.valueToTree(m);

        if(piece != null && this.furtherAttacks.checkMove(n)) {
            addToAttacksList(pos);
        } else {
            removeFromAttacks(pos);
        }
    }

    private void removeFromAttacks(CheckersCoordPair pos) {
        this.gameState.removePlayerJumps(pos, 1);
        this.gameState.removePlayerJumps(pos, 2);
    }

    private void addToAttacksList(CheckersCoordPair pos) {
        String[] teams = this.gameState.getTeamNames();
        CheckersGamePiece piece = pos.getPiece();
        String team = piece.getTeam();
        if(team.equals(teams[0])) {
            this.gameState.addPlayerJumps(pos, 1);
            this.gameState.removePlayerJumps(pos, 2);
        } else {
            this.gameState.addPlayerJumps(pos, 2);
            this.gameState.removePlayerJumps(pos, 1);
        }
    }

    private boolean pastFinal(int dir, int finalPos, int targPos) {

        if(dir < 0) {
            return targPos < finalPos;
        } else if(dir > 0) {
            return targPos > finalPos;
        }
        return false;
    }

    private boolean inBounds(int num) {
        return num > 0 && num < this.board.getSideLength();
    }

    protected void checkToEnd(int[] vect, CheckersCoordPair pos) {
       // CheckersGamePiece piece = pos.getPiece();

        int sX = pos.getX();
        int sY = pos.getY();
        int eX = vect[0];
        int eY = vect[1];
        int rise = eY - sY;
        int run = eX - sX;
        int finalX = sX + eX;
        int finalY = sY + eY;

        if(rise == 0) {
            if(!inBounds(finalY)) {
                return;
            }

            int dir = getDirection(run);
            
            int tmpX = sX + dir;
            
            while(!pastFinal(dir, finalX, tmpX) && inBounds(tmpX)) {
                CheckersCoordPair tmp = this.board.getBoardPos(tmpX, finalY);
                handleFurtherAttacks(tmp);
                tmpX += dir;
            }
            
            
        } else if(run == 0) {
            if(!inBounds(finalX)) {
                return;
            }

            int dir = getDirection(rise);

            int tmpY = sY + dir;

            while(!pastFinal(dir, finalY, tmpY) && inBounds(tmpY)) {
                CheckersCoordPair tmp = this.board.getBoardPos(finalX, tmpY);
                handleFurtherAttacks(tmp);
                tmpY += dir;
            }
        } else {
            int slope = rise / run;

            if(slope == 1 || slope == -1) {
                int dir = getDirection(rise);
                int dir2 = getDirection(run);
                int tmpX = sX + dir2;
                int tmpY = sY + dir;

                while(!pastFinal(dir, finalY, tmpY) && inBounds(tmpX) && inBounds(tmpY)) {
                    CheckersCoordPair tmp = this.board.getBoardPos(tmpX, tmpY);
                    handleFurtherAttacks(tmp);
                    tmpY += dir;
                    tmpX += dir2;
                }
            } else {
                if(!inBounds(finalX) || !inBounds(finalY)) {
                    return;
                }
                CheckersCoordPair tmp = this.board.getBoardPos(finalX, finalY);
                handleFurtherAttacks(tmp);
            }
        }
    }

    private int getDirection(int num) {
        if(num > 0) 
            return 1;
        else if(num < 0)
            return -1;
        return 0;
    }

    private void checkUntilFirst(int[] vect, CheckersCoordPair pos) {

    }

    private int getTurnFactor(CheckersGamePiece piece) {
        String teams[] = this.gameState.getTeamNames();
        String team = piece.getTeam();

        if(team.equals(teams[0])) {
            return -1;
        } else {
            return 1;
        }
    }
    
}
