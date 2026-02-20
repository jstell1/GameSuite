package gamesuite.core.ui;

import com.fasterxml.jackson.databind.JsonNode;

public interface UIListener {
    public boolean getIsBoardEnabled();
    public void disabledBoard();
    public void sendMove(JsonNode move);
    public void sendYellowedPanel(JsonNode pos);
    public void createGame(String name);
    public void joinGame(String name, String gameId);
    public void quitGame(boolean hardQuit);
    public boolean isPlayerTurn();
    public void enableBoard();
}
