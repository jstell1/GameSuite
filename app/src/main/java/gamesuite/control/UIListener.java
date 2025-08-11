package gamesuite.control;

import gamesuite.model.data.CoordPairView;
import gamesuite.model.data.GameStateView;
import gamesuite.model.data.Move;
import gamesuite.view.CoordPairPanel;

public interface UIListener {
    public void sendChange(int x, int y);
    public boolean getIsBoardEnabled();
    public void sendYellowedPanel(CoordPairPanel pos);
}
