package checkers.view;

import checkers.model.CheckersPlayer;
import gamesuite.core.model.Player;

public class CheckersPlayerView implements Player {

    CheckersPlayer player;

    public CheckersPlayerView(CheckersPlayer player) { this.player = player; }

    public int getPoints() { return this.player.getPoints(); }

    public String getUserId() { return this.player.getUserId(); }

    public String getName() { return this.player.getName(); }

    public CheckersPlayerView copy() { return new CheckersPlayerView(this.player.copy()); }
}