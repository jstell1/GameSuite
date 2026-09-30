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
        return copyArray(this.attackVectors); 
    }

    public void setKing(boolean king) {
        this.king = king;
    }

    public void setName(String name) {
        
        this.name = name;
    }

    public String getTeam() { return this.team; }

    public void setType(String type) {
        if(this.type != null)
            throw new IllegalStateException("types cannot be reset"); 
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
            .setValidMoves(copyArray(this.validMoves))
            .setValidAttacks(copyArray(this.validJumps))
            .setAttackVectors(copyArray(this.attackVectors))
            .setMoveRules(new ArrayList<Action>(this.moveRules))
            .setAttackRules(new ArrayList<Action>(this.attackRules))
            .build();
    }

     private static int[][] copyArray(int[][] arr) {
        int[][] m = new int[arr.length][];
        for(int i = 0; i < arr.length; i++) {
            m[i] = Arrays.copyOf(arr[i], arr[i].length);
            //for(int j = 0; i < arr[i].length; j++) {
            //    m[i][j] = arr[i][j];
            //}
        }
        return m;
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
        return this.name;
    }

    public void kingPiece() {
        if(this.type.equals("C")) 
            this.type = "K";
    }

    public int[][] getValidMoves() { 
      //  if(this.type.equals("C"))
            return copyArray(this.validMoves);
        //else 
          //  return Arrays.copyOf(this.validKingMoves, this.validKingMoves.length);
    }

    public int[][] getValidJumps() {
        //if(this.type.equals("C")) 
            return copyArray(this.validJumps);
       // else 
           // return Arrays.copyOf(this.validKingJumps, this.validKingJumps.length);
    }

    public boolean isKing() { return this.type.equals("K"); }

    @Override
    public List<Action> getAttackRules() {
        return List.copyOf(this.attackRules);
    }

    @Override
    public List<Action> getMoveRules() {
        return List.copyOf(this.moveRules);
    }

    public static class Builder {
        private String name = "";
        private String type = "";
        private int val;
        //private boolean king;
        private int[][] validMoves = {{-1, -1}, {-1, 1}};
        private int[][] validJumps = {{-2, -2}, {-2, 2}};
        private int[][] attackVectors = {{-1, -1}, {-1, 1}};
        //private final int[][] validKingMoves = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        //private final int[][] validKingJumps = {{-2, -2}, {-2, 2}, {2, -2}, {2, 2}};
        private List<Action> moveRules = new ArrayList<>();
        private List<Action> attackRules = new ArrayList<>();
        private String team = "B";

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
            this.validMoves = copyArray(moves);
            return this;
        }

        public Builder setValidAttacks(int[][] attacks) {
            this.validJumps = copyArray(attacks);
            return this;
        }

        public Builder setAttackVectors(int[][] vects) {
            this.attackVectors = copyArray(vects);
            return this;
        }

        public Builder setMoveRules(List<Action> rules) {
            this.moveRules = List.copyOf(rules);
            return this;
        }

        public Builder setAttackRules(List<Action> rules) {
            this.attackRules = List.copyOf(rules);
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

    
}
