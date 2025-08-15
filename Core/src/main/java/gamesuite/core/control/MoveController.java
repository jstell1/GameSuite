package gamesuite.core.control;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import gamesuite.core.model.CoordPair;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;

public class MoveController {

    private RulesValidator validator;
    private GameStateManager stateManager;

    public MoveController(RulesValidator validator, GameStateManager stateManager) {
        this.validator = validator;
        this.stateManager = stateManager;
    }

    public boolean checkMove(Move move) {
        if(move == null)
            return false;
        
        CoordPair start = new CoordPair(move.getStartX(), move.getStartY());
        CoordPair end = new CoordPair(move.getEndX(), move.getEndY());
        start = this.stateManager.getBoardPos(start.getX(), start.getY());
        end = this.stateManager.getBoardPos(end.getX(), end.getY());
        
        boolean check = this.stateManager.getFurtherJumps() != null;
        check = check && this.stateManager.getFurtherJumps().equals(start);

        boolean check2 = this.validator.isValidPos(start);
        check2 = check2 && this.validator.isValidPos(end); 
        check2 = check2 && this.validator.isTurnPiece(start.getPiece());
        
        boolean check3 = this.validator.isValidMove(move) && this.stateManager.getFurtherJumps() == null;
        boolean check4 = this.validator.hasJumps(this.stateManager.getTurn());
        if(check4 && check2 && this.stateManager.getFurtherJumps() == null) {
            check4 = check4 && this.validator.isValidJump(move); 
            return check4 && this.validator.isPlayerJump(this.stateManager.getTurn(), start);
        } else if(check4 && check2 && check) {
            check4 = check4 && this.validator.isValidJump(move); 
            return check4 && this.validator.isPlayerJump(this.stateManager.getTurn(), start);
        }

        if(!check3 && this.stateManager.getFurtherJumps() == null)
            check3 = this.validator.isValidJump(move);
        else if(!check3 && check)
            check3 = this.validator.isValidJump(move) && check; 
        check3 = check3 && check2; 
        return check3;
    }

    public List<CoordPair> makeMove(Move move) {
        if(move == null)
            return null;
        int x = move.getStartX();
        int y = move.getStartY();
        int eX = move.getEndX();
        int eY = move.getEndY();
        CoordPair start = this.stateManager.getBoardPos(x, y);
        CoordPair end = this.stateManager.getBoardPos(eX, eY);
        //move = new Move(start, end);
        CoordPair[] changed = null;
        if(this.validator.isValidMove(move) && this.stateManager.getFurtherJumps() == null) {
            this.stateManager.updateBoard(move);
            changed = new CoordPair[2];
            changed[0] = start;
            changed[1] = end;
        } else if(this.validator.isValidJump(move)) {
            this.stateManager.updateBoard(move);
            int jumpedX = (start.getX() + end.getX()) >> 1;
            int jumpedY = (start.getY() + end.getY()) >> 1;
            CoordPair jumped = this.stateManager.getBoardPos(jumpedX, jumpedY);
            this.stateManager.removeJumped(jumped);
            this.stateManager.incrPoints();
            changed = new CoordPair[3];
            changed[0] = start;
            changed[1] = jumped;
            changed[2] = end;
        }
        this.stateManager.setChanged(Arrays.asList(changed));
        if(changed != null)
            return Arrays.asList(changed);
        return null;
    }

    public void updateState(List<CoordPair> changed) {
        if(changed != null) {
            int size = changed.size();
            for(CoordPair pos : changed) {
                int x = pos.getX();
                int y = pos.getY();
                CoordPair[] checks = { 
                    this.stateManager.getBoardPos(x - 1, y - 1),
                    this.stateManager.getBoardPos(x - 1, y + 1),
                    this.stateManager.getBoardPos(x + 1, y - 1),
                    this.stateManager.getBoardPos(x + 1, y + 1),
                    this.stateManager.getBoardPos(x - 2, y - 2),
                    this.stateManager.getBoardPos(x - 2, y + 2),
                    this.stateManager.getBoardPos(x + 2, y - 2),
                    this.stateManager.getBoardPos(x + 2, y + 2)
                };
                for(CoordPair currPos : checks) {
                    boolean hasFurtherJumps = this.validator.hasFurtherJumps(currPos);
                    if(!hasFurtherJumps)
                        this.stateManager.removeFromJumps(currPos, hasFurtherJumps);
                    else if(currPos.getPiece() != null && currPos.getPiece().getName() == "B")
                        this.stateManager.addPlayerJumps(currPos, 1);
                    else if(currPos.getPiece() != null && currPos.getPiece().getName() == "R")
                        this.stateManager.addPlayerJumps(currPos, 2);
                }
            }

            int x = changed.get(size - 1).getX();
            int y = changed.get(size - 1).getY();
            int sX = changed.get(0).getX();
            int sY = changed.get(0).getY();
            CoordPair end = this.stateManager.getBoardPos(x, y);
            CoordPair start = this.stateManager.getBoardPos(sX, sY);
            boolean isKingable = this.validator.isKingable(end); 
            if(isKingable) {
                this.stateManager.kingPiece(end);
                this.stateManager.addJustKinged(end);
            }

            if(this.stateManager.getFurtherJumps() != null && this.stateManager.getFurtherJumps().equals(start))
                this.stateManager.setFurtherJumps(null);   

            if(changed.size() == 3 && this.stateManager.getFurtherJumps() == null && this.validator.hasFurtherJumps(end) && !this.stateManager.isJustKinged(end))
                this.stateManager.setFurtherJumps(end);
            else if(changed.size() == 3 && this.stateManager.getFurtherJumps() == null && this.validator.hasFurtherJumps(end)) {
                this.stateManager.addPlayerJumps(end, this.stateManager.getTurn());
            }
            this.stateManager.clearJustKinged();
        }

        if(this.stateManager.getFurtherJumps() == null) {
        boolean check = this.validator.hasValidMoves("B");
        boolean check2 = this.validator.hasValidMoves("R");
  
        this.stateManager.incrTurn(check, check2);
        }
        updateJumpsList();
    }

      private void updateJumpsList() {
        Set<CoordPair> p1Jumps = this.stateManager.getJumps(1);
        Set<CoordPair> p2Jumps = this.stateManager.getJumps(2);

        for(CoordPair pos: p1Jumps) {
            boolean hasFurther = this.validator.hasFurtherJumps(pos);
            if(!hasFurther) 
                this.stateManager.removeFromJumps(pos, hasFurther);
        }
        for(CoordPair pos: p2Jumps) {
            boolean hasFurther = this.validator.hasFurtherJumps(pos);
            if(!hasFurther)
                this.stateManager.removeFromJumps(pos, hasFurther);
        }
    }
    
}
