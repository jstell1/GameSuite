package gamesuite.client.control;

import gamesuite.client.view.CoordPairPanel;

public interface UIListener {
    public void sendChange(int x, int y);
    public boolean getIsBoardEnabled();
    public void sendYellowedPanel(CoordPairPanel pos);
    public void createGame(String name);
    public void joinGame(String name, String gameId);
    public boolean isPlayerTurn();
}
