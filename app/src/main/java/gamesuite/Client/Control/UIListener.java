package gamesuite.Client.Control;

import gamesuite.Client.View.CoordPairPanel;
import gamesuite.Core.Model.Move;
import gamesuite.Core.View.CoordPairView;
import gamesuite.Core.View.GameStateView;

public interface UIListener {
    public void sendChange(int x, int y);
    public boolean getIsBoardEnabled();
    public void sendYellowedPanel(CoordPairPanel pos);
}
