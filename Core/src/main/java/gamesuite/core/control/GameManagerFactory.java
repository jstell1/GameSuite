package gamesuite.core.control;

public abstract class GameManagerFactory {
    public abstract GameManager createGame(String playerName);
    public abstract boolean isMultiGame();
}
