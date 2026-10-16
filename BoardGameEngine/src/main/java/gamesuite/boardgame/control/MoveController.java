package gamesuite.boardgame.control;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.boardgame.model.CoordPairImpl;
import gamesuite.boardgame.model.GameBoardImpl;
import gamesuite.boardgame.model.BoardGamePiece;
import gamesuite.core.model.rules.Action;
import gamesuite.core.model.rules.Constraint;
import gamesuite.core.model.rules.Effect;

public class MoveController {

    private List<Constraint> gameConstraints;
    private List<Action> postChecks;
    private List<Action> endChecks;
    private List<Effect> appliedEffects;
    private List<String> negateList;
    private Constraint validMove;
    private Constraint validAttack;
    private GameBoardImpl board;
    public boolean override;

    public MoveController(GameBoardImpl board) {
        if(board == null)
            throw new IllegalArgumentException("board cannot be null");
        this.gameConstraints = new ArrayList<>();
        this.endChecks = new ArrayList<>();
        this.appliedEffects = new ArrayList<>();
        this.negateList = new ArrayList<>();
        this.postChecks = new ArrayList<>();
        this.board = board;
        this.override = false;
    }

    public void addGameConstraint(Constraint constraint) {
        if(constraint == null) 
            throw new IllegalArgumentException("gameConstraint cannot be null");
        this.gameConstraints.add(constraint);
    }

    public void setMoveConstraint(Constraint validMove) {
        if(validMove == null) {
            throw new IllegalArgumentException("validMove cannot be null");
        }
        this.validMove = validMove;
    }

    public void setAttackConstraint(Constraint validAttack) {
        if(validAttack == null) {
            throw new IllegalArgumentException("validAttack cannot be null");
        }
        this.validAttack = validAttack;
    }

    public void addAppliedEffect(Effect effect) {
        if(effect == null) {
            throw new IllegalArgumentException("effects in appliedEffects cannot be null");
        }
        this.appliedEffects.add(effect);
    }

    public void clearEffects() { this.appliedEffects = new ArrayList<>(); }

    public void addEndCheck(Action action) {
        if(action == null) {
            throw new IllegalArgumentException("endCheck actions cannot be null");
        }
        this.endChecks.add(action);
    }

    public void addPostCheck(Action action) {
        if(action == null) {
            throw new IllegalArgumentException("postCheck action cannot be null");
        }
        this.postChecks.add(action);
    }

    public void addNegation(String name) {
        if(name == null) {
            throw new IllegalArgumentException("negations cannot be null");
        }
        this.negateList.add(name);
    }

    public boolean gameCheck(JsonNode move) {

        if(move.has("promote")) {
            return true;
        }

        for(Constraint constraint : gameConstraints) {
            if(!constraint.checkMove(move)) {
                return false;
            }
        }

        return true;
    }

    public boolean moveCheck(JsonNode move) {

        if(move.has("promote")) {
            return true;
        }

        int x = move.get("startX").asInt();
        int y = move.get("startY").asInt();
        CoordPairImpl pos = this.board.getBoardPos(x, y);
        BoardGamePiece piece = pos.getPiece();
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

                if(!constraint.checkMove(move)) {
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
}
