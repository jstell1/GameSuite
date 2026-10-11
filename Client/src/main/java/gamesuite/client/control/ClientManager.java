package gamesuite.client.control;
import java.util.List;
import java.util.concurrent.ExecutionException;
import com.fasterxml.jackson.databind.JsonNode;

import gamesuite.client.view.MainGUI;

public interface ClientManager {

    public void setMainGUI(MainGUI gui);
    public void setGUIManager(GUIManager guiGM);

    public void connect();


    public String awaitSessionId() throws InterruptedException, ExecutionException;

    public void sendMove(JsonNode move);

    public String createGame(String game, String group, String playerName);

    public String joinGame(String game, String name, String gameId);

    public void quitGame(boolean hardQuit);

    public List<String> getAvailableGames();


    public void getActiveGames(String gameName);
}
