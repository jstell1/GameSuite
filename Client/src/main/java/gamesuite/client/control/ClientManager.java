package gamesuite.client.control;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.client.view.MainGUI;
import gamesuite.core.network.JsonSchemaValidator;
import gamesuite.core.ui.GameBoardFactory;
import gamesuite.core.ui.GameBoardUI;

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
