package gamesuite.core.model;

import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GameState {

    private Player player1;
    private Player player2;
    private int numPlayers;
    private int turn;
    private Player winner;
    private boolean isDraw;
    private Set<CoordPair> p1Jumps;
    private Set<CoordPair> p2Jumps;
    private CoordPair furtherJumps;
    private int boardSize;
    private int turnFactor;
    private boolean boardInit;
    private final String[] pieceNames = {"B", "R"};
    private List<CoordPair> changedPos;
    private boolean gameOver;
    private Set<CoordPair> justKinged;

    public GameState(Player player1, Player player2) {
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

    public GameState(Player player1) {
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

    public void setPlayer2(Player player2) {
        if(this.player2 == null) {
            this.player2 = player2;
            this.numPlayers++;
        }
    }

    public String[] getPieceNames() { 
        return Arrays.copyOf(this.pieceNames, this.pieceNames.length); 
    }

    public void addJustKinged(CoordPair pos) {
        this.justKinged.add(pos);
    }

    public void removeJustKinged(CoordPair pos) {
        this.justKinged.remove(pos);
    }

    public boolean isJustKinged(CoordPair pos) {
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

    public void addPlayerJumps(CoordPair pos, int playerNum) {
        if(playerNum == 1 && pos != null)
            p1Jumps.add(pos);
        else if(playerNum == 2 && pos != null)
            p2Jumps.add(pos);
    }

    public Set<CoordPair> getJumps(int playerNum) {
        if(playerNum == 1) {
            return p1Jumps;
        } else if(playerNum == 2) {
            return p2Jumps;
        }
        return null;
    }

    public void removePlayerJumps(CoordPair pos, int playerNum) {
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

    public Player getPlayer(int playerNum) { 
        if(playerNum == 1) 
            return player1;
        else if(playerNum == 2) 
            return player2;
        return null;
    }

    public Player[] getPlayers() { 
        Player[] players = new Player[2];
        players[0] = this.player1;
        players[1] = this.player2;
        return players;
    }

    public void setFurtherJumps(CoordPair pos) { this.furtherJumps = pos; }

    public void removeFurtherJumps() { this.furtherJumps = null; }
    
    public CoordPair getFurtherJumps() {
        if(this.furtherJumps == null)
            return null;
        return this.furtherJumps;
    } 

    public boolean getDraw() { return this.isDraw; }

    public void setDraw() { this.isDraw = true; }
    
    public Player getWinner() { return this.winner; }

    public void setWinner(int playerNum) { 
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

    public List<CoordPair> getChangedPos() {
        return this.changedPos;
    }    

	public void setChanged(List<CoordPair> changed) {
        this.changedPos = changed;
	}

    public void clearJustKinged() {
        this.justKinged.clear();
    }

    public synchronized boolean addPlayer(Player player) {
        if(this.player2 == null) {
            this.player2 = player;
            this.numPlayers++;
            return true;
        }
        return false;
    }
}
