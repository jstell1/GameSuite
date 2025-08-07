package gamesuite.model.data;

import java.util.List;

public interface GameStateView {

    public GameBoardView getBoardView();
    public List<PlayerView> getPlayerViews();
    public PlayerView getPlayerView(int num);
    public int getTurn();
    public int getPlayerPoints(int num);
    public int getNumPlayers();
    public List<CoordPairView> getChangedPos();
    public PlayerView getWinnerView();
    public boolean isGameOver();
    public boolean getDraw();
}
