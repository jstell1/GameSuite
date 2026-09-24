package gamesuite.boardgame.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CheckersCoordPair;
import gamesuite.boardgame.model.CheckersGameBoard;
import gamesuite.boardgame.model.CheckersGamePiece;
import gamesuite.boardgame.model.CheckersMove;
import gamesuite.core.model.Move;
import gamesuite.core.model.rules.Action;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Effect;
import gamesuite.core.model.rules.Result;

public class MoveController {

    private RulesValidator validator;
    private GameStateManager stateManager;
    private List<Constraint> gameConstraints;
    private List<Action> postChecks;
    private List<Action> endChecks;
    private List<Effect> appliedEffects;
    private List<String> negateList;
    private Constraint validMove;
    private Constraint validAttack;
    private CheckersGameBoard board;

    public MoveController(RulesValidator validator, GameStateManager stateManager, CheckersGameBoard board) {
        this.validator = validator;
        this.stateManager = stateManager;
        this.gameConstraints = new ArrayList<>();
        this.endChecks = new ArrayList<>();
        this.appliedEffects = new ArrayList<>();
        this.negateList = new ArrayList<>();
        this.postChecks = new ArrayList<>();
        this.board = board;
    }

    public void addGameConstraint(Constraint constraint) {
        this.gameConstraints.add(constraint);
    }

    public void setMoveConstraint(Constraint validMove) {
        this.validMove = validMove;
    }

    public void setAttackConstraint(Constraint validAttack) {
        this.validAttack = validAttack;
    }

    public void addAppliedEffect(Effect effect) {
        this.appliedEffects.add(effect);
    }

    public void clearEffects() { this.appliedEffects = new ArrayList<>(); }

    public void addEndCheck(Action action) {
        this.endChecks.add(action);
    }

    public void addPostCheck(Action action) {
        this.postChecks.add(action);
    }

    public void addNegation(String name) {
        this.negateList.add(name);
    }

    public boolean gameCheck(JsonNode move) {

        for(Constraint constraint : gameConstraints) {
            if(!constraint.checkMove(move)) {
                return false;
            }
        }

        return true;
    }

    public boolean moveCheck(JsonNode move) {
        int x = move.get("startX").asInt();
        int y = move.get("startY").asInt();
        CheckersCoordPair pos = this.board.getBoardPos(x, y);
        CheckersGamePiece piece = pos.getPiece();
        List<Action> attackRules = piece.getAttackRules();
        List<Action> moveRules = piece.getMoveRules();
        boolean success1 = false;

        if(this.validAttack.checkMove(move)) {
            
            for(Action act : attackRules) {
                List<Constraint> constraints = act.getConstraints();
                List<Effect> effects = act.getEffects();
                boolean success = true;
                for(Constraint constraint : constraints) {
                    if(!constraint.checkMove(move)) {
                        success = false;
                        break;
                    }
                }

                if(success) {
                    success1 = true;
                    for(Effect effect : effects) {
                        this.appliedEffects.add(effect);
                    }
                }
                
            }
            return success1;
        } 

        if(this.validMove.checkMove(move)) {
             for(Action act : moveRules) {
                List<Constraint> constraints = act.getConstraints();
                List<Effect> effects = act.getEffects();
                boolean success = true;
                for(Constraint constraint : constraints) {
                    if(!constraint.checkMove(move)) {
                        success = false;
                        break;
                    }
                }

                if(success) {
                    success1 = true;
                    for(Effect effect : effects) {
                        this.appliedEffects.add(effect);
                    }
                }
                
            }
            return success1;
        }

        return false;
    }

    public boolean postCheck(JsonNode move) {
        boolean success1 = false;
        for(Action act : postChecks) {
            List<Constraint> constraints = act.getConstraints();
            List<Effect> effects = act.getEffects();
            boolean success = true;
            for(Constraint constraint : constraints) {
                if(!constraint.checkMove(move)) {
                    success = false;
                    break;
                }
            }

            if(success) {
                success1 = true;
                for(Effect effect : effects) {
                    this.appliedEffects.add(effect);
                }
            }
            
        }

        return success1;
    }

    public void applyEffects(JsonNode move) {
        for(Effect effect : this.appliedEffects) {
            effect.updateState(move);
        }
    }

    public boolean endCheck(JsonNode move) {
        boolean success = true;
        for(Action act : endChecks) {
            List<Constraint> constraints = act.getConstraints();
            List<Effect> effects = act.getEffects();

            
            for(Constraint constraint : constraints) {

                if(constraint.checkMove(move)) {
                    success = false;
                    break;
                }
            }

            if(success) {
                for(Effect effect : effects) {
                    this.appliedEffects.add(effect);
                }
                break;
            }

        }
        return success;
    }

    protected boolean checkIsAttack(JsonNode move) {

        return false;
    }










    public boolean checkMove(CheckersMove move) {
        if(move == null)
            return false;
        
        CheckersCoordPair start = new CheckersCoordPair(move.getStartX(), move.getStartY());
        CheckersCoordPair end = new CheckersCoordPair(move.getEndX(), move.getEndY());
        //start = this.stateManager.getBoardPos(start.getX(), start.getY());
        //end = this.stateManager.getBoardPos(end.getX(), end.getY());
        
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

    public List<CheckersCoordPair> makeMove(CheckersMove move) {
        if(move == null)
            return null;
        int x = move.getStartX();
        int y = move.getStartY();
        int eX = move.getEndX();
        int eY = move.getEndY();
        CheckersCoordPair start = this.stateManager.getBoardPos(x, y);
        CheckersCoordPair end = this.stateManager.getBoardPos(eX, eY);
        //move = new Move(start, end);
        CheckersCoordPair[] changed = null;
        if(this.validator.isValidMove(move) && this.stateManager.getFurtherJumps() == null) {
            this.stateManager.updateBoard(move);
            changed = new CheckersCoordPair[2];
            changed[0] = start;
            changed[1] = end;
        } else if(this.validator.isValidJump(move)) {
            this.stateManager.updateBoard(move);
            int jumpedX = (start.getX() + end.getX()) >> 1;
            int jumpedY = (start.getY() + end.getY()) >> 1;
            CheckersCoordPair jumped = this.stateManager.getBoardPos(jumpedX, jumpedY);
            this.stateManager.removeJumped(jumped);
            this.stateManager.incrPoints();
            changed = new CheckersCoordPair[3];
            changed[0] = start;
            changed[1] = jumped;
            changed[2] = end;
        }
        this.stateManager.setChanged(Arrays.asList(changed));
        if(changed != null)
            return Arrays.asList(changed);
        return null;
    }

    public void updateState(List<CheckersCoordPair> changed) {
        if(changed != null) {
            int size = changed.size();
            for(CheckersCoordPair pos : changed) {
                int x = pos.getX();
                int y = pos.getY();
                CheckersCoordPair[] checks = { 
                    this.stateManager.getBoardPos(x - 1, y - 1),
                    this.stateManager.getBoardPos(x - 1, y + 1),
                    this.stateManager.getBoardPos(x + 1, y - 1),
                    this.stateManager.getBoardPos(x + 1, y + 1),
                    this.stateManager.getBoardPos(x - 2, y - 2),
                    this.stateManager.getBoardPos(x - 2, y + 2),
                    this.stateManager.getBoardPos(x + 2, y - 2),
                    this.stateManager.getBoardPos(x + 2, y + 2)
                };
                for(CheckersCoordPair currPos : checks) {
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
            CheckersCoordPair end = this.stateManager.getBoardPos(x, y);
            CheckersCoordPair start = this.stateManager.getBoardPos(sX, sY);
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
        Set<CheckersCoordPair> p1Jumps = this.stateManager.getJumps(1);
        Set<CheckersCoordPair> p2Jumps = this.stateManager.getJumps(2);

        for(CheckersCoordPair pos: p1Jumps) {
            boolean hasFurther = this.validator.hasFurtherJumps(pos);
            if(!hasFurther) 
                this.stateManager.removeFromJumps(pos, hasFurther);
        }
        for(CheckersCoordPair pos: p2Jumps) {
            boolean hasFurther = this.validator.hasFurtherJumps(pos);
            if(!hasFurther)
                this.stateManager.removeFromJumps(pos, hasFurther);
        }
    }
    
}
