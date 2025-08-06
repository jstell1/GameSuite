package gamesuite.model.data;

import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GameState implements GameStateView {

    private GameBoard board;
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
    private List<CoordPairView> changedPos;

    public GameState(GameBoard board, Player player1, Player player2) {
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.numPlayers = 2;
        this.turn = 1;
        this.winner = null;
        this.isDraw = false;
        this.p1Jumps = new HashSet<>();
        this.p2Jumps = new HashSet<>();
        this.furtherJumps = null;
        this.boardSize = this.board.getSideLength();
        this.turnFactor = -1;
        this.boardInit = false;
    }

    public String[] getPieceNames() { 
        return Arrays.copyOf(this.pieceNames, this.pieceNames.length); 
    }

    public boolean isBoardInit() { return this.boardInit; }

    public void setBoardInit() { this.boardInit = true; }

    public int getTurnFactor() { return this.turnFactor; }

    public void flipTurnFactor() { this.turnFactor *= -1; }

    public void addPlayerJumps(int x, int y, int playerNum) {
        if(isValidPos(x, y)) {
            if(playerNum == 1 && getBoardPos(x, y) != null)
                p1Jumps.add(getBoardPos(x, y));
            else if(playerNum == 2 && getBoardPos(x, y) != null)
                p2Jumps.add(getBoardPos(x, y));
        }
    }

    public Set<CoordPair> getJumps(int playerNum) {
        if(playerNum == 1) {
            return p1Jumps;
        } else if(playerNum == 2) {
            return p2Jumps;
        }
        return null;
    }

    public int getBoardSize() { return this.boardSize; }

    public void removePlayerJumps(int x, int y, int playerNum) {
        if(playerNum == 1) 
            this.p1Jumps.remove(getBoardPos(x, y));
        else if(playerNum == 2)
            this.p2Jumps.remove(getBoardPos(x, y));
    }

    @Override
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

    @Override
    public List<PlayerView> getPlayerViews() {
        PlayerView[] players = new PlayerView[2];
        players[0] = this.player1.getPlayerView();
        players[1] = this.player2.getPlayerView();
        return Arrays.asList(players);
    }

    @Override
    public PlayerView getPlayerView(int num) { return getPlayer(num); }

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

    @Override
    public PlayerView getWinnerView() { return this.winner; }

    public void setWinner(int playerNum) { 
        if(this.winner == null && playerNum == 1)
            this.winner = player1;
        else if(this.winner == null && playerNum == 2)
            this.winner = player2; 
    }

    @Override
    public int getPlayerPoints(int playerNum) {
        int points = -1;
        if(playerNum == 1) 
            this.player1.getPoints();
        else if(playerNum == 2) 
            this.player1.getPoints();
        return points;
    }

    public CoordPair getBoardPos(int x, int y) {
        if(isValidPos(x, y))
            return this.board.getBoardPos(x, y);
        return null;
    }

    public GameBoard getBoardCopy() { return this.board.copy(); }

    public void addPlayerPoints(int playerNum) {
        if(playerNum == 1)
            this.player1.addPoints(1);
        else if(playerNum == 2)
            this.player2.addPoints(1);
    }

    public void setBoardPos(int x, int y, GamePiece piece) {
        if(isValidPos(x, y))
            this.board.setBoardPos(x, y, piece);
    }

    @Override
    public int getNumPlayers() { return this.numPlayers; }

    
    public String getBoardStr() {
        return this.board.toString();
    }

    public boolean isValidPos(int x, int y) {
        if(this.board.isValidPos(x, y))
            return true;
        return false;
    }

    @Override
    public GameBoardView getBoardView() { 
        return this.board; 
    }

    @Override
    public List<CoordPairView> getChangedPos() {
        return this.changedPos;
    }    

    public GameStateView getGameStateView() { return this; }
}
