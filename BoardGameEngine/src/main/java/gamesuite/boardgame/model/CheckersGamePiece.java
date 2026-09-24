package gamesuite.boardgame.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import gamesuite.core.model.GamePiece;
import gamesuite.core.model.rules.Action;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckersGamePiece implements GamePiece {
    private String name;
    private String team;
    private String type;
    private int val;
    private boolean king;
    private int[][] validMoves; //= {{-1, -1}, {-1, 1}};
    private int[][] validJumps; //= {{-2, -2}, {-2, 2}};
    private int[][] validAttacks; //= {{-1, -1}, -1, 1};
    private int[][] attackVectors;
    private final int[][] validKingMoves = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    private final int[][] validKingJumps = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
    private List<Action> moveRules;
    private List<Action> attackRules;

    public CheckersGamePiece() {}

    public CheckersGamePiece(String name, String type, int val) {
        this.name = name;
        this.type = type;
        this.val = val;
    }

    private CheckersGamePiece(
         String name, String type, int val, 
        int[][] validMoves, int[][] validJumps,
        List<Action> moveRules, List<Action> attackRules,
        int[][] attackVectors, String team
    ) {
        this.name = name; this.type = type;
        this.validMoves = validMoves; this.validJumps = validJumps;
        this.moveRules = moveRules; this.attackRules = attackRules;
        this.attackVectors = attackVectors; this.team = team;
    }

    public int[][] getAttackVectors() { 
        return Arrays.copyOf(this.attackVectors, this.attackVectors.length); 
    }

    public void setKing(boolean king) {
        this.king = king;
    }

    public void setName(String name) {
        if(this.name == null)
            this.name = name;
    }

    public String getTeam() { return this.team; }

    public void setType(String type) {
        if(this.type == null) 
            this.type = type;
    }

    public void setVal(int val) {
        this.val = val;
    }

    public CheckersGamePiece copy() { 
        return new Builder()
            .setName(this.name)
            .setType(this.type)
            .setVal(this.val)
            .setValidMoves(deepCopy(this.validMoves))
            .setValidAttacks(deepCopy(this.validJumps))
            .setAttackVectors(deepCopy(this.attackVectors))
            .setMoveRules(new ArrayList<Action>(this.moveRules))
            .setAttackRules(new ArrayList<Action>(this.attackRules))
            .build();
    }

    private static int[][] deepCopy(int[][] original) {
        if (original == null) {
            return null;
        }

        int[][] copy = new int[original.length][];

        for (int i = 0; i < original.length; i++) {
            copy[i] = Arrays.copyOf(original[i], original[i].length);
        }

        return copy;
    }

    public String getName() { return this.name; }

    public String getType() { return this.type; }

    public int getVal() { return this.val; }
    
    public String toString() {
        return this.name + this.type;
    }

    public void kingPiece() {
        if(this.type.equals("C")) 
            this.type = "K";
    }

    public int[][] getValidMoves() { 
      //  if(this.type.equals("C"))
            return Arrays.copyOf(this.validMoves, this.validMoves.length);
        //else 
          //  return Arrays.copyOf(this.validKingMoves, this.validKingMoves.length);
    }

    public int[][] getValidJumps() {
        //if(this.type.equals("C")) 
            return Arrays.copyOf(this.validJumps, this.validJumps.length);
       // else 
           // return Arrays.copyOf(this.validKingJumps, this.validKingJumps.length);
    }

    public boolean isKing() { return this.type.equals("K"); }

    public static class Builder {
        private String name;
        private String type;
        private int val;
        //private boolean king;
        private int[][] validMoves; //= {{-1, -1}, {-1, 1}};
        private int[][] validJumps; //= {{-2, -2}, {-2, 2}};
        private int[][] attackVectors;
        //private final int[][] validKingMoves = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        //private final int[][] validKingJumps = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        private List<Action> moveRules;
        private List<Action> attackRules;
        private String team;

        public Builder setName(String name) { 
            this.name = name; 
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setTeam(String team) {
            this.team = team;
            return this;
        }

        public Builder setVal(int val) {
            this.val = val;
            return this;
        }

        public Builder setValidMoves(int[][] moves) {
            this.validMoves = moves;
            return this;
        }

        public Builder setValidAttacks(int[][] attacks) {
            this.validJumps = attacks;
            return this;
        }

        public Builder setAttackVectors(int[][] vects) {
            this.attackVectors = vects;
            return this;
        }

        public Builder setMoveRules(List<Action> rules) {
            this.moveRules = rules;
            return this;
        }

        public Builder setAttackRules(List<Action> rules) {
            this.attackRules = rules;
            return this;
        }

        public CheckersGamePiece build() {
            return new CheckersGamePiece(
                this.name, this.type, this.val, 
                this.validMoves, this.validJumps, 
                this.moveRules, this.attackRules,
                this.attackVectors, this.team
            );
        }


    }

    @Override
    public List<Action> getAttackRules() {
        return this.attackRules;
    }

    @Override
    public List<Action> getMoveRules() {
        return this.moveRules;
    }
}
