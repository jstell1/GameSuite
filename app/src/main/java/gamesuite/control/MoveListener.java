package gamesuite.control;

import gamesuite.model.data.GameStateView;
import gamesuite.model.data.Move;

public interface MoveListener {
    public void sendMove(Move move);
}
