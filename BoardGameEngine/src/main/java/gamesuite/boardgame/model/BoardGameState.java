
package gamesuite.boardgame.model;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import gamesuite.core.model.GameState;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(value = {"lastJumped"},ignoreUnknown = true)
public class BoardGameState implements GameState {

    private BoardGamePlayer player1;
    private BoardGamePlayer player2;
    private int numPlayers;
    private int turn;
    private BoardGamePlayer winner;
    private boolean isDraw;
    private Set<CoordPairImpl> p1Jumps;
    private Set<CoordPairImpl> p2Jumps;
    private Set<CoordPairImpl> p1Moves;
    private Set<CoordPairImpl> p2Moves;
    private CoordPairImpl furtherJumps;
    private int boardSize;
    private int turnFactor;
    private boolean boardInit;
    private String[] teamNames;// = {"B", "R"};
    private List<CoordPairImpl> changedPos;
    private boolean gameOver;
    private Set<CoordPairImpl> justKinged;
    private boolean lastJumped;

    public BoardGameState() {}

    public BoardGameState(BoardGamePlayer player1, BoardGamePlayer player2) {
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


    public BoardGameState(BoardGamePlayer player1) {
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

    private BoardGameState(
        BoardGamePlayer player1, BoardGamePlayer player2,
        int numPlayers, int turn,
        BoardGamePlayer winner, boolean isDraw,
        Set<CoordPairImpl> p1Attacks,
        Set<CoordPairImpl> p2Attacks,
        Set<CoordPairImpl> p1Moves,
        Set<CoordPairImpl> p2Moves,
        CoordPairImpl furtherAttacks,
        int boardSize, int turnFactor,
        boolean boardInit, String[] teamNames,
        List<CoordPairImpl> changedPos,
        boolean gameOver, boolean lastJumped,
        Set<CoordPairImpl> justPromoted
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

    public void setPlayer2(BoardGamePlayer player2) {

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

    

    public void addJustKinged(CoordPairImpl pos) {
        Objects.requireNonNull(pos, "pos");
        this.justKinged.add(pos);
    }

    public void removeJustKinged(CoordPairImpl pos) {
        Objects.requireNonNull(pos, "pos");
        this.justKinged.remove(pos);
    }

    public boolean isJustKinged(CoordPairImpl pos) {
        Objects.requireNonNull(pos, "pos");
        return this.justKinged.contains(pos);
    }

    public boolean isGameOver() { return this.gameOver; }

    public void setGameOver(boolean gameOver) { 
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

    public void addPlayerJumps(CoordPairImpl pos, int playerNum) {
        checkPlayerTurnRange(playerNum);
        Objects.requireNonNull(pos, "pos");

        if(playerNum == 1)
            p1Jumps.add(pos);
        else if(playerNum == 2)
            p2Jumps.add(pos);
    }

    public Set<CoordPairImpl> getJumps(int playerNum) {
        checkPlayerTurnRange(playerNum);
        if(playerNum == 1) {
            return Set.copyOf(p1Jumps);
        } else if(playerNum == 2) {
            return Set.copyOf(p2Jumps);
        }
        return null;
    }

    public void removePlayerJumps(CoordPairImpl pos, int playerNum) {
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

    public BoardGamePlayer getPlayer(int playerNum) { 

        if(playerNum == 1) 
            return player1;
        else if(playerNum == 2) 
            return player2;
        return null;
    }

    public BoardGamePlayer getPlayerById(String id) {
        
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

    public BoardGamePlayer[] getPlayers() { 
        BoardGamePlayer[] players = new BoardGamePlayer[2];
        players[0] = this.player1;
        players[1] = this.player2;
        return players;
    }

    public void setFurtherJumps(CoordPairImpl pos) { 
        this.furtherJumps = pos; 
    }

    public void removeFurtherJumps() { this.furtherJumps = null; }
    
    public CoordPairImpl getFurtherJumps() {
        return this.furtherJumps;
    } 

    public void addChangedPos(CoordPairImpl pos) {
        Objects.requireNonNull(pos, "pos");
        this.changedPos.add(pos);
    }

    public void resetChangedPos() { this.changedPos = new ArrayList<>(); }

    public boolean getDraw() { return this.isDraw; }

    public void setDraw() { this.isDraw = true; }
    
    public BoardGamePlayer getWinner() { return this.winner; }


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

    public List<CoordPairImpl> getChangedPos() {
        return List.copyOf(this.changedPos);
    }    


	public void setChangedPos(List<CoordPairImpl> changed) {
        
        this.changedPos = changed;
	}

    public void clearJustKinged() {
        this.justKinged.clear();
    }

    public boolean addPlayer(BoardGamePlayer player) {
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


    public static class Builder {
        private BoardGamePlayer player1;
        private BoardGamePlayer player2;
        private int numPlayers;
        private int turn = 1;
        private BoardGamePlayer winner;
        private boolean isDraw;
        private Set<CoordPairImpl> p1Attacks = new HashSet<>();
        private Set<CoordPairImpl> p2Attacks = new HashSet<>();
        private Set<CoordPairImpl> p1Moves = new HashSet<>();
        private Set<CoordPairImpl> p2Moves = new HashSet<>();
        private CoordPairImpl furtherAttacks;
        private int boardSize = 1;
        private int turnFactor = 1;
        private boolean boardInit;
        private String[] teamNames = {"B", "R"};
        private List<CoordPairImpl> changedPos = new ArrayList<>();
        private boolean gameOver;
        private Set<CoordPairImpl> justPromoted = new HashSet<>();
        private boolean lastJumped = false;
        private List<Field> failedFields = new ArrayList<>(); 
        private Field[] allFields = this.getClass().getDeclaredFields();

        public Builder setPlayer1(BoardGamePlayer player1) {
            this.player1 = player1;
            return this;
        }

        public Builder setPlayer2(BoardGamePlayer player2) {
            this.player2 = player2;
            return this;
        }

        public Builder setLastJumped(boolean lastJumped) {
            this.lastJumped = lastJumped;
            return this;
        }

        public Builder setNumPlayers(int numPlayers) {
            
            if(numPlayers < 0 || numPlayers > 2) {
                this.failedFields.add(this.allFields[2]);
            }
            this.numPlayers = numPlayers;
            return this;
        }

        public Builder setTurn(int turn) {

            if(turn < 1 || turn > 2) {
                this.failedFields.add(this.allFields[3]);
            }
            this.turn = turn;
            return this;
        }

        public Builder setWinner(BoardGamePlayer winner) {
            this.winner = winner;
            return this;
        }

        public Builder setIsDraw(boolean isDraw) {
            this.isDraw = isDraw;
            return this;
        }

        private static Set<CoordPairImpl> copySet(Set<CoordPairImpl> set) {
            Set<CoordPairImpl> s = new HashSet<>();
            for(CoordPairImpl pos : set) {
                s.add(pos);
            }
            return s;
        }

        public Builder setP1Attacks(Set<CoordPairImpl> p1Attacks) {
            this.p1Attacks = copySet(p1Attacks);
            return this;
        }

        public Builder setP2Attacks(Set<CoordPairImpl> p2Attacks) {
            this.p2Attacks = copySet(p2Attacks);
            return this;
        }

        public Builder setP1Moves(Set<CoordPairImpl> p1Moves) {
            this.p1Moves = copySet(p1Moves);
            return this;
        }

        public Builder setP2Moves(Set<CoordPairImpl> p2Moves) {
            this.p2Moves = copySet(p2Moves);
            return this;
        }

        public Builder setFurtherAttacks(CoordPairImpl furtherAttacks) {
          

            this.furtherAttacks = furtherAttacks;
            return this;
        }
        
        public Builder setBoardSize(int boardSize) {
            if(boardSize < 1) {
                this.failedFields.add(this.allFields[11]);
            }
            this.boardSize = boardSize;
            return this;
        }

        public Builder setTurnFactor(int turnFactor) {
            if(turnFactor != 1 && turnFactor != -1) {
                this.failedFields.add(this.allFields[12]);
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
                this.failedFields.add(this.allFields[14]);
            }
            this.teamNames = Arrays.copyOf(pieceNames, pieceNames.length);
            return this;
        }

        private static List<CoordPairImpl> copyList(List<CoordPairImpl> list) {
            List<CoordPairImpl> l = new ArrayList<>();
            for(CoordPairImpl pos : list) {
                l.add(pos);
            }
            return l;
        }

        public Builder setChangedPos(List<CoordPairImpl> changedPos) {
            this.changedPos = copyList(changedPos);
            return this;
        }

        public Builder setGameOver(boolean gameOver) {
            this.gameOver = gameOver;
            return this;
        }

        public Builder setJustPromoted(Set<CoordPairImpl> justPromoted) {
            this.justPromoted = copySet(justPromoted);
            return this;
        }

        public BoardGameState build() {

            if(this.failedFields.size() != 0) {
                int len = this.failedFields.size();
                String fields = "[\n";

                for(int i = 0; i < len - 1; i++) {
                    fields += this.failedFields.get(i).getName() + ",\n";
                }
                fields += this.failedFields.get(len - 1) + "\n]\n";

                throw new IllegalArgumentException("Build failed due to invalid inputs set to CheckersGameState.Buidler\nFailed Fields:\n"  + fields);
            }

            return new BoardGameState(
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
