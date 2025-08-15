package gamesuite.network;

import java.util.List;
import java.util.Set;

import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.Player;

public class NetGameState {
    private String gameId;
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
    private List<CoordPair> changedPos;
    private boolean gameOver;
    private Set<CoordPair> justKinged;

    public NetGameState(String gameId) {
        this.gameId = gameId;
    } 

    
}
