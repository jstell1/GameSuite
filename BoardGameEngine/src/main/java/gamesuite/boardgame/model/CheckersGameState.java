
package gamesuite.boardgame.model;

import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.core.model.GameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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
        boolean gameOver,
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
    }

    public void setPlayer2(CheckersPlayer player2) {
        if(this.player2 == null) {
            this.player2 = player2;
            this.numPlayers++;
        }
    }

    public String[] getTeamNames() { 
        return Arrays.copyOf(this.teamNames, this.teamNames.length); 
    }

    // public JsonNode getGameStateJson() {
    //     ObjectMapper mapper = new ObjectMapper();
    //     JsonNode json = mapper.valueToTree(this);
    //     return json;
    // } 

    public void addJustKinged(CheckersCoordPair pos) {
        this.justKinged.add(pos);
    }

    public void removeJustKinged(CheckersCoordPair pos) {
        this.justKinged.remove(pos);
    }

    public boolean isJustKinged(CheckersCoordPair pos) {
        return this.justKinged.contains(pos);
    }

    public boolean isGameOver() { return this.gameOver; }

    public void setGameOver(boolean gameOver) { 
        if(this.gameOver == false)
            this.gameOver = gameOver; 
    }

    public boolean isBoardInit() { return this.boardInit; }

    public void setBoardInit() { this.boardInit = true; }

    public int getTurnFactor() { return this.turnFactor; }

    public void flipTurnFactor() { this.turnFactor *= -1; }

    public void addPlayerJumps(CheckersCoordPair pos, int playerNum) {
        if(playerNum == 1 && pos != null)
            p1Jumps.add(pos);
        else if(playerNum == 2 && pos != null)
            p2Jumps.add(pos);
    }

    public Set<CheckersCoordPair> getJumps(int playerNum) {
        if(playerNum == 1) {
            return p1Jumps;
        } else if(playerNum == 2) {
            return p2Jumps;
        }
        return null;
    }

    public void removePlayerJumps(CheckersCoordPair pos, int playerNum) {
        if(playerNum == 1) 
            this.p1Jumps.remove(pos);
        else if(playerNum == 2)
            this.p2Jumps.remove(pos);
    }

    public int getTurn() { return this.turn; }

    public int setTurn(int num) {
        if(num > 0 && num <= this.numPlayers)
            this.turn = num;
        return this.turn;
    }

    public CheckersPlayer getPlayer(int playerNum) { 
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

    public void setFurtherJumps(CheckersCoordPair pos) { this.furtherJumps = pos; }

    public void removeFurtherJumps() { this.furtherJumps = null; }
    
    public CheckersCoordPair getFurtherJumps() {
        if(this.furtherJumps == null)
            return null;
        return this.furtherJumps;
    } 

    public void addChangedPos(CheckersCoordPair pos) {
        this.changedPos.add(pos);
    }

    public void resetChangedPos() { this.changedPos = new ArrayList<>(); }

    public boolean getDraw() { return this.isDraw; }

    public void setDraw() { this.isDraw = true; }
    
    public CheckersPlayer getWinner() { return this.winner; }

    public void setWinner(CheckersPlayer winner) {
        this.winner = winner;
    }

    public void setWinnerNum(int playerNum) { 
        if(this.winner == null && playerNum == 1)
            this.winner = player1;
        else if(this.winner == null && playerNum == 2)
            this.winner = player2; 
    }

    public int getPlayerPoints(int playerNum) {
        int points = -1;
        if(playerNum == 1) 
            this.player1.getPoints();
        else if(playerNum == 2 && this.player2 != null) 
            this.player2.getPoints();
        return points;
    }

    public void addPlayerPoints(int playerNum) {
        if(playerNum == 1)
            this.player1.addPoints(1);
        else if(playerNum == 2)
            this.player2.addPoints(1);
    }

    public int getNumPlayers() { return this.numPlayers; }

    public List<CheckersCoordPair> getChangedPos() {
        return this.changedPos;
    }    

	public void setChangedPos(List<CheckersCoordPair> changed) {
        this.changedPos = changed;
	}

    public void clearJustKinged() {
        this.justKinged.clear();
    }

    public boolean addPlayer(CheckersPlayer player) {
        if(this.player2 == null) {
            this.player2 = player;
            this.numPlayers++;
            return true;
        }
        return false;
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
        private int turn;
        private CheckersPlayer winner;
        private boolean isDraw;
        private Set<CheckersCoordPair> p1Attacks;
        private Set<CheckersCoordPair> p2Attacks;
        private Set<CheckersCoordPair> p1Moves;
        private Set<CheckersCoordPair> p2Moves;
        private CheckersCoordPair furtherAttacks;
        private int boardSize;
        private int turnFactor;
        private boolean boardInit;
        private String[] teamNames;
        private List<CheckersCoordPair> changedPos;
        private boolean gameOver;
        private Set<CheckersCoordPair> justPromoted;

        public Builder setPlayer1(CheckersPlayer player1) {
            this.player1 = player1;
            return this;
        }

        public Builder setPlayer2(CheckersPlayer player2) {
            this.player2 = player2;
            return this;
        }

        public Builder setNumPlayers(int numPlayers) {
            this.numPlayers = numPlayers;
            return this;
        }

        public Builder setTurn(int turn) {
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

        public Builder setP1Attacks(Set<CheckersCoordPair> p1Attacks) {
            this.p1Attacks = p1Attacks;
            return this;
        }

        public Builder setP2Attacks(Set<CheckersCoordPair> p2Attacks) {
            this.p2Attacks = p2Attacks;
            return this;
        }

        public Builder setP1Moves(Set<CheckersCoordPair> p1Moves) {
            this.p1Moves = p1Moves;
            return this;
        }

        public Builder setP2Moves(Set<CheckersCoordPair> p2Moves) {
            this.p2Moves = p2Moves;
            return this;
        }

        public Builder setFurtherAttacks(CheckersCoordPair furtherAttacks) {
            this.furtherAttacks = furtherAttacks;
            return this;
        }
        
        public Builder setBoardSize(int boardSize) {
            this.boardSize = boardSize;
            return this;
        }

        public Builder setTurnFactor(int turnFactor) {
            this.turnFactor = turnFactor;
            return this;
        }

        public Builder setBoardInit(boolean boardInit) {
            this.boardInit = boardInit;
            return this;
        }

        public Builder setTeamNames(String[] pieceNames) {
            this.teamNames = pieceNames;
            return this;
        }

        public Builder setChangedPos(List<CheckersCoordPair> changedPos) {
            this.changedPos = changedPos;
            return this;
        }

        public Builder setGameOver(boolean gameOver) {
            this.gameOver = gameOver;
            return this;
        }

        public Builder setJustPromoted(Set<CheckersCoordPair> justPromoted) {
            this.justPromoted = justPromoted;
            return this;
        }

        public CheckersGameState build() {
            return new CheckersGameState(
                this.player1, this.player2,
                this.numPlayers, this.turn,
                this.winner, this.isDraw,
                this.p1Attacks, this.p2Attacks,
                this.p1Moves, this.p2Moves,
                this.furtherAttacks, this.boardSize,
                this.turnFactor, this.boardInit,
                this.teamNames, this.changedPos,
                this.gameOver, this.justPromoted
            );
        }
    }

  
 }
