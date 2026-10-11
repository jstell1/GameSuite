
package gamesuite.boardgame.model;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.core.model.GameState;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(value = {"lastJumped"},ignoreUnknown = true)
public class CheckersGameState implements GameState {

    private CheckersPlayer player1;
    private CheckersPlayer player2;
    private int numPlayers;
    private int turn;
    private CheckersPlayer winner;
    private boolean isDraw;
    private Set<CheckersCoordPair> p1Jumps;
    private Set<CheckersCoordPair> p2Jumps;
    private Set<CheckersCoordPair> p1Moves;
    private Set<CheckersCoordPair> p2Moves;
    private CheckersCoordPair furtherJumps;
    private int boardSize;
    private int turnFactor;
    private boolean boardInit;
    private String[] teamNames;// = {"B", "R"};
    private List<CheckersCoordPair> changedPos;
    private boolean gameOver;
    private Set<CheckersCoordPair> justKinged;
    private boolean lastJumped;

    public CheckersGameState() {}

    public CheckersGameState(CheckersPlayer player1, CheckersPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.numPlayers = 2;
        this.turn = 1;
        this.winner = null;
        this.isDraw = false;
        this.p1Jumps = new HashSet<>();
        this.p2Jumps = new HashSet<>();
        this.furtherJumps = null;
        this.turnFactor = -1;
        this.boardInit = false;
        this.gameOver = false;
        this.justKinged = new HashSet<>();
        this.lastJumped = false;
    }


    public CheckersGameState(CheckersPlayer player1) {
        this.player1 = player1;
        this.numPlayers = 1;
        this.turn = 1;
        this.winner = null;
        this.isDraw = false;
        this.p1Jumps = new HashSet<>();
        this.p2Jumps = new HashSet<>();
        this.furtherJumps = null;
        this.turnFactor = -1;
        this.boardInit = false;
        this.gameOver = false;
        this.justKinged = new HashSet<>();
        this.lastJumped = false;
    }

    private CheckersGameState(
        CheckersPlayer player1, CheckersPlayer player2,
        int numPlayers, int turn,
        CheckersPlayer winner, boolean isDraw,
        Set<CheckersCoordPair> p1Attacks,
        Set<CheckersCoordPair> p2Attacks,
        Set<CheckersCoordPair> p1Moves,
        Set<CheckersCoordPair> p2Moves,
        CheckersCoordPair furtherAttacks,
        int boardSize, int turnFactor,
        boolean boardInit, String[] teamNames,
        List<CheckersCoordPair> changedPos,
        boolean gameOver, boolean lastJumped,
        Set<CheckersCoordPair> justPromoted
    ) {
        this.player1 = player1; this.player2 = player2;
        this.numPlayers = numPlayers; this.turn = turn;
        this.winner = winner; this.isDraw = isDraw; 
        this.p1Jumps = p1Attacks; this.p2Jumps = p2Attacks; 
        this.p1Moves = p1Moves; this.p2Moves = p2Moves; 
        this.furtherJumps = furtherAttacks;
        this.boardSize = boardSize; this.turnFactor = turnFactor;
        this.boardInit = boardInit; this.teamNames = teamNames;
        this.changedPos = changedPos; this.gameOver = gameOver;
        this.justKinged = justPromoted;
        this.lastJumped = lastJumped;
    }

    public void setPlayer2(CheckersPlayer player2) {

        if(this.player2 != null) {
            throw new IllegalStateException("once player2 is set it cannot be changed");
        }
        this.player2 = player2;
        this.numPlayers++;
        
    }

    public String[] getTeamNames() { 
        return Arrays.copyOf(this.teamNames, this.teamNames.length); 
    }

    public void setTeamNames(String[] teamNames) {
        this.teamNames = teamNames;
    }

    public boolean getLastJumped() { return this.lastJumped; }
    public void setLastJumped(boolean lastJumped) { this.lastJumped = lastJumped; }

    // public JsonNode getGameStateJson() {
    //     ObjectMapper mapper = new ObjectMapper();
    //     JsonNode json = mapper.valueToTree(this);
    //     return json;
    // } 

    public void addJustKinged(CheckersCoordPair pos) {
        Objects.requireNonNull(pos, "pos");
        this.justKinged.add(pos);
    }

    public void removeJustKinged(CheckersCoordPair pos) {
        Objects.requireNonNull(pos, "pos");
        this.justKinged.remove(pos);
    }

    public boolean isJustKinged(CheckersCoordPair pos) {
        Objects.requireNonNull(pos, "pos");
        return this.justKinged.contains(pos);
    }

    public boolean isGameOver() { return this.gameOver; }

    public void setGameOver(boolean gameOver) { 
        //if(this.gameOver)
        //    throw new IllegalStateException("gameOver is already set to true");
            this.gameOver = gameOver; 
    }

    public boolean isBoardInit() { return this.boardInit; }

    public void setBoardInit() { 
        if(this.boardInit) {
            throw new IllegalStateException("game already initialized");
        }
        this.boardInit = true; 
    }

    public int getTurnFactor() { return this.turnFactor; }

    public void flipTurnFactor() { this.turnFactor *= -1; }

    private static void checkPlayerTurnRange(int num) {
        if(num < 1 || num > 2) {
            throw new IllegalArgumentException("urnNums must be in range [1,2]");
        }
    }

    public void addPlayerJumps(CheckersCoordPair pos, int playerNum) {
        checkPlayerTurnRange(playerNum);
        Objects.requireNonNull(pos, "pos");

        if(playerNum == 1)
            p1Jumps.add(pos);
        else if(playerNum == 2)
            p2Jumps.add(pos);
    }

    public Set<CheckersCoordPair> getJumps(int playerNum) {
        checkPlayerTurnRange(playerNum);
        if(playerNum == 1) {
            return Set.copyOf(p1Jumps);
        } else if(playerNum == 2) {
            return Set.copyOf(p2Jumps);
        }
        return null;
    }

    public void removePlayerJumps(CheckersCoordPair pos, int playerNum) {
        checkPlayerTurnRange(playerNum);
        Objects.requireNonNull(pos, "pos");

        if(playerNum == 1) 
            this.p1Jumps.remove(pos);
        else if(playerNum == 2)
            this.p2Jumps.remove(pos);
    }

    public int getTurn() { return this.turn; }

    public int setTurn(int num) {
        checkPlayerTurnRange(num);
            this.turn = num;
        return this.turn;
    }

    public CheckersPlayer getPlayer(int playerNum) { 

        //checkPlayerTurnRange(playerNum);
        if(playerNum == 1) 
            return player1;
        else if(playerNum == 2) 
            return player2;
        return null;
    }

    public CheckersPlayer getPlayerById(String id) {
        
        if(this.player1.getUserId().equals(id))
            return this.player1;
        else if(this.player2.getUserId().equals(id))
            return this.player2;
        else 
            return null;
    }

    public boolean playerIdExists(String id) {
        if(this.player1.getUserId().equals(id) || this.player2.getUserId().equals(id)) {
            return true;
        }
       return false;    
    }

    public CheckersPlayer[] getPlayers() { 
        CheckersPlayer[] players = new CheckersPlayer[2];
        players[0] = this.player1;
        players[1] = this.player2;
        return players;
    }

    public void setFurtherJumps(CheckersCoordPair pos) { 
        this.furtherJumps = pos; 
    }

    public void removeFurtherJumps() { this.furtherJumps = null; }
    
    public CheckersCoordPair getFurtherJumps() {
        return this.furtherJumps;
    } 

    public void addChangedPos(CheckersCoordPair pos) {
        Objects.requireNonNull(pos, "pos");
        this.changedPos.add(pos);
    }

    public void resetChangedPos() { this.changedPos = new ArrayList<>(); }

    public boolean getDraw() { return this.isDraw; }

    public void setDraw() { this.isDraw = true; }
    
    public CheckersPlayer getWinner() { return this.winner; }

    //public void setWinner(CheckersPlayer winner) {
    //    this.winner = winner;
    //}

    public void setWinnerNum(int playerNum) { 
        checkPlayerTurnRange(playerNum);
        if(playerNum == 1)
            this.winner = player1;
        else if(playerNum == 2)
            this.winner = player2; 
    }

    public int getPlayerPoints(int playerNum) {
         checkPlayerTurnRange(playerNum);
        int points = -1;
        if(playerNum == 1) 
            this.player1.getPoints();
        else if(playerNum == 2 && this.player2 != null) 
            this.player2.getPoints();
        return points;
    }

    public void addPlayerPoints(int playerNum) {
        checkPlayerTurnRange(playerNum);
        if(playerNum == 1)
            this.player1.addPoints(1);
        else if(playerNum == 2)
            this.player2.addPoints(1);
    }

    public int getNumPlayers() { return this.numPlayers; }

    public List<CheckersCoordPair> getChangedPos() {
        return List.copyOf(this.changedPos);
    }    


    //this will be removed eventually
	public void setChangedPos(List<CheckersCoordPair> changed) {
        
        this.changedPos = changed;
	}

    public void clearJustKinged() {
        this.justKinged.clear();
    }

    public boolean addPlayer(CheckersPlayer player) {
        Objects.requireNonNull(player, "player");
        if(this.player1 == null) {
            this.player1 = player;
            this.numPlayers++;
            return true;
        }
        if(this.player2 != null) {
            throw new IllegalStateException("once player 2 is set they cannot be changed");
        }
        this.player2 = player;
        this.numPlayers++;
        return true;
    }

    // @Override
    // public boolean isJustKinged(CoordPair pos) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'isJustKinged'");
    // }

//     @Override
//     public JsonNode getJsonNode() {
//         // TODO Auto-generated method stub
//         throw new UnsupportedOperationException("Unimplemented method 'getJsonNode'");
//     }

    public static class Builder {
        private CheckersPlayer player1;
        private CheckersPlayer player2;
        private int numPlayers;
        private int turn = 1;
        private CheckersPlayer winner;
        private boolean isDraw;
        private Set<CheckersCoordPair> p1Attacks = new HashSet<>();
        private Set<CheckersCoordPair> p2Attacks = new HashSet<>();
        private Set<CheckersCoordPair> p1Moves = new HashSet<>();
        private Set<CheckersCoordPair> p2Moves = new HashSet<>();
        private CheckersCoordPair furtherAttacks;
        private int boardSize = 1;
        private int turnFactor = 1;
        private boolean boardInit;
        private String[] teamNames = {"B", "R"};
        private List<CheckersCoordPair> changedPos = new ArrayList<>();
        private boolean gameOver;
        private Set<CheckersCoordPair> justPromoted = new HashSet<>();
        private boolean lastJumped = false;
        private List<Field> failedFields = new ArrayList<>(); 
        private Field[] allFields = this.getClass().getDeclaredFields();

        public Builder setPlayer1(CheckersPlayer player1) {
            this.player1 = player1;
            return this;
        }

        public Builder setPlayer2(CheckersPlayer player2) {
            this.player2 = player2;
            return this;
        }

        public Builder setLastJumped(boolean lastJumped) {
            this.lastJumped = lastJumped;
            return this;
        }

        public Builder setNumPlayers(int numPlayers) {
            
            if(numPlayers < 0 || numPlayers > 2) {
                this.failedFields.add(this.allFields[2]);//throw new IllegalArgumentException("numPlayers must be in range [0,2]");
            }
            this.numPlayers = numPlayers;
            return this;
        }

        public Builder setTurn(int turn) {

            if(turn < 1 || turn > 2) {
                this.failedFields.add(this.allFields[3]);//throw new IllegalArgumentException("turnNum must be in range [1,2]");
            }
            this.turn = turn;
            return this;
        }

        public Builder setWinner(CheckersPlayer winner) {
            this.winner = winner;
            return this;
        }

        public Builder setIsDraw(boolean isDraw) {
            this.isDraw = isDraw;
            return this;
        }

        private static Set<CheckersCoordPair> copySet(Set<CheckersCoordPair> set) {
            Set<CheckersCoordPair> s = new HashSet<>();
            for(CheckersCoordPair pos : set) {
                //CheckersCoordPair p = pos;
                s.add(pos);
            }
            return s;
        }

        public Builder setP1Attacks(Set<CheckersCoordPair> p1Attacks) {
            this.p1Attacks = copySet(p1Attacks);
            return this;
        }

        public Builder setP2Attacks(Set<CheckersCoordPair> p2Attacks) {
            this.p2Attacks = copySet(p2Attacks);
            return this;
        }

        public Builder setP1Moves(Set<CheckersCoordPair> p1Moves) {
            this.p1Moves = copySet(p1Moves);
            return this;
        }

        public Builder setP2Moves(Set<CheckersCoordPair> p2Moves) {
            this.p2Moves = copySet(p2Moves);
            return this;
        }

        public Builder setFurtherAttacks(CheckersCoordPair furtherAttacks) {
           // if(furtherAttacks != null) {
            //    CheckersCoordPair pos = furtherAttacks.copy();
            //    this.furtherAttacks = pos;
          //  }

            this.furtherAttacks = furtherAttacks;
            return this;
        }
        
        public Builder setBoardSize(int boardSize) {
            if(boardSize < 1) {
                this.failedFields.add(this.allFields[11]);//throw new IllegalArgumentException("board size cannot be less than 1");
            }
            this.boardSize = boardSize;
            return this;
        }

        public Builder setTurnFactor(int turnFactor) {
            if(turnFactor != 1 && turnFactor != -1) {
                this.failedFields.add(this.allFields[12]);//throw new IllegalArgumentException("turnFactor can only be 1 or -1");
            }
            this.turnFactor = turnFactor;
            return this;
        }

        public Builder setBoardInit(boolean boardInit) {
            this.boardInit = boardInit;
            return this;
        }

        public Builder setTeamNames(String[] pieceNames) {
            if(pieceNames == null || pieceNames.length != 2) {
                this.failedFields.add(this.allFields[14]);//throw new IllegalArgumentException("there must be 2 teamNames for each player");
            }
            this.teamNames = Arrays.copyOf(pieceNames, pieceNames.length);
            return this;
        }

        private static List<CheckersCoordPair> copyList(List<CheckersCoordPair> list) {
            List<CheckersCoordPair> l = new ArrayList<>();
            for(CheckersCoordPair pos : list) {
                //CheckersCoordPair p = pos.copy();
                l.add(pos);
            }
            return l;
        }

        public Builder setChangedPos(List<CheckersCoordPair> changedPos) {
            this.changedPos = copyList(changedPos);
            return this;
        }

        public Builder setGameOver(boolean gameOver) {
            this.gameOver = gameOver;
            return this;
        }

        public Builder setJustPromoted(Set<CheckersCoordPair> justPromoted) {
            this.justPromoted = copySet(justPromoted);
            return this;
        }

        public CheckersGameState build() {

            if(this.failedFields.size() != 0) {
                int len = this.failedFields.size();
                String fields = "[\n";

                for(int i = 0; i < len - 1; i++) {
                    fields += this.failedFields.get(i).getName() + ",\n";
                }
                fields += this.failedFields.get(len - 1) + "\n]\n";

                throw new IllegalArgumentException("Build failed due to invalid inputs set to CheckersGameState.Buidler\nFailed Fields:\n"  + fields);
            }

            return new CheckersGameState(
                this.player1, this.player2,
                this.numPlayers, this.turn,
                this.winner, this.isDraw,
                this.p1Attacks, this.p2Attacks,
                this.p1Moves, this.p2Moves,
                this.furtherAttacks, this.boardSize,
                this.turnFactor, this.boardInit,
                this.teamNames, this.changedPos,
                this.gameOver, this.lastJumped,
                this.justPromoted
            );
        }
    }

  
 }
