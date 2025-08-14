package gamesuite.core.model;

import java.util.List;

import gamesuite.core.view.CoordPairView;
import gamesuite.core.view.GameBoardView;
import gamesuite.core.view.GameStateView;
import gamesuite.core.view.NetGameView;
import gamesuite.core.view.PlayerView;

public class NetGameState implements NetGameView {
    private String gameId;
    private GameStateView game;

    public NetGameState(GameStateView game, String gameId) {
        this.game = game;
        this.gameId = gameId;
    } 

    public void setGameId(String gameId) {
        if(this.gameId == null)
            this.gameId = gameId;
    }

    @Override
    public String getGameId() { return this.gameId; }

    @Override
    public GameBoardView getBoardView() { return this.game.getBoardView(); }

    @Override
    public List<PlayerView> getPlayerViews() { return this.game.getPlayerViews(); }

    @Override
    public PlayerView getPlayerView(int num) { 
        int tmp = this.game.getNumPlayers();
        if(num < tmp)
            return this.game.getPlayerView(num); 
        return null;
    }

    @Override
    public int getTurn() { return this.game.getTurn(); }

    @Override
    public int getPlayerPoints(int num) { return this.game.getPlayerPoints(num); }

    @Override
    public int getNumPlayers() { return this.game.getNumPlayers(); }

    @Override
    public List<CoordPairView> getChangedPos() { return this.game.getChangedPos(); }

    @Override
    public PlayerView getWinnerView() { return this.game.getWinnerView(); }

    @Override
    public boolean isGameOver() { return this.game.isGameOver(); }

    @Override
    public boolean getDraw() { return this.game.getDraw(); }

    



}
