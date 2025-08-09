package gamesuite.view;

import gamesuite.model.data.CoordPairView;

public interface CoordPairPanelObserver {
    public CoordPairView sendChange(int x, int y);
    public boolean getIsBoardEnabled();
    public void sendYellowedPanel(CoordPairPanel pos);
}
