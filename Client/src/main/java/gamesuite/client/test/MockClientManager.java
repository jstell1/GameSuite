package gamesuite.client.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.client.control.ClientManager;
import gamesuite.client.control.ClientManagerImpl;
import gamesuite.client.control.GUIManager;
import gamesuite.client.view.MainGUI;
import gamesuite.core.control.PluginLoader;
import gamesuite.core.ui.GameBoardFactory;
import gamesuite.core.ui.GameBoardUI;

public class MockClientManager implements ClientManager {

    GUIManager guiGm;
    MainGUI main;
    String p1GameId;
    String p2GameId;
    JsonNode gameState;
    JsonNode board;
    JsonNode changes;
    
    PluginLoader loader;
    String path = "../plugins";

    public MockClientManager() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root;
            InputStream in = getClass().getClassLoader().getResourceAsStream("test.json");
            this.loader = new PluginLoader(path);
            this.loader.setUIPluginLoader(path + "/ui");
            this.loader.loadGameBoards();
            root = mapper.readTree(in);
            this.gameState = root.get("gameState");
              this.board = root.get("board");
              this.changes = root.get("changes");

              System.out.println("successfully setup the mockclientmang");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void setMainGUI(MainGUI gui) {
        this.main = gui;
    }

    @Override
    public void setGUIManager(GUIManager guiGM) {
        this.guiGm = guiGM;
    }

    @Override
    public void connect() {
        
    }

    @Override
    public String awaitSessionId() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public void sendMove(JsonNode move) {
        ((ObjectNode) this.gameState).set("changedPos", changes);
        this.guiGm.update(this.gameState);

    }

    @Override
    public String createGame(String game, String group, String playerName) {
        System.out.println("creating game");
        this.guiGm.setGameId("gameId");
        this.guiGm.setPlayerTurn(1);
        p1GameId = "gameId";
        getGameReady();
        return null;
    }

    private void getGameReady() 
    {
        try {
            System.out.println("readying game");
            GameBoardFactory fact = loader.createBoardFactory("BoardGameUI");
            ObjectMapper mapper = new ObjectMapper();
            
          

            GameBoardUI gameUI = fact.createGameBoard(this.board, this.gameState, this.guiGm);
            this.guiGm.initGame(gameUI);
            System.out.println("game initialized???");
           
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | SecurityException | JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        

    }

    @Override
    public String joinGame(String game, String name, String gameId) {
         this.guiGm.setGameId("gameId");
        this.guiGm.setPlayerTurn(2);
        p2GameId = "gameId";
        getGameReady();
        return null;
    }

    @Override
    public void quitGame(boolean hardQuit) {
        this.guiGm.resetGUI();
    }

    @Override
    public List<String> getAvailableGames() {
        System.out.println("getting games");
        
        Map<String, ArrayList<String>> m = new HashMap<>();
        ArrayList<String> l = new ArrayList<>();
        l.add("game1");
        l.add("game2");
        m.put("games", l);
        m.put("game3", null);

        this.main.setGamesList(m);
        return null;
    }

    @Override
    public void getActiveGames(String gameName) {
        System.out.println("Getting active games");
       String[] joinable = {"game1", "game2", "game3"};
        this.guiGm.setActiveGamesList(joinable);
    }


    
}
