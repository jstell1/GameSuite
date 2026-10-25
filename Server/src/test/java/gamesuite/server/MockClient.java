package gamesuite.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.fasterxml.jackson.databind.node.ArrayNode;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameState;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.core.utils.PluginLoader;
import gamesuite.core.utils.*;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MockClient {
    WebSocketClient client;
    HttpClient restClient;
    WebSocketSession session;
    String sessionId;
    String gameId;
    CompletableFuture<String> sessionIdFuture = new CompletableFuture<>();
    String baseUrl;
    String wsUrl;
    InputStream schemaStream;
    JsonNode schemaRoot;
    String ip;
    int port;
    PluginLoader loader;
    ArrayNode board;
    //private GameState game;
    ObjectNode gameState;
    TestApp main;

    public MockClient(String ip, int port, TestApp main) throws IOException {
        this.main = main;
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(512 * 1024);
        container.setDefaultMaxBinaryMessageBufferSize(512 * 1024);
        this.client = new StandardWebSocketClient(container);
        try {
           // this.loader = new PluginLoader("../plugins/");
          //  this.loader.loadAll();
            //this.loader.watchForChanges();

         //   this.loader.setUIPluginLoader("../plugins/ui");
         //   this.loader.loadGameBoards();

        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException("issue loading plugins");
        }
        this.ip = ip;
        this.port = port;
        this.baseUrl = "http://" + ip + ":" + port;
        this.wsUrl = "wss://" + ip + ":" + port + "/ingame";
        this.restClient = HttpClient.newHttpClient();
        this.schemaStream = JsonSchemaValidator.class.getClassLoader().getResourceAsStream("schema.json");
        ObjectMapper mapper = new ObjectMapper();

        this.schemaRoot = mapper.readTree(schemaStream);

        System.out.println("cwd: " + System.getProperty("user.dir"));
        System.out.println("ui dir: " + new File("../plugins/ui").getCanonicalPath());
        System.out.println("core: " + PluginLoader.class.getProtectionDomain().getCodeSource().getLocation());

    }

    public void connect() {
        try {
            connect(this.wsUrl);
        } catch (Exception e) {
            System.out.println("no ssl");
            try {
                this.wsUrl = "ws://" + this.ip + ":" + this.port + "/ingame";
                connect(this.wsUrl);
            } catch (Exception e2) {
                e.printStackTrace();

            }
        }

    }

    private void connect(String wsUrl) throws InterruptedException, ExecutionException {
        this.session = this.client.execute(new AbstractWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                MockClient.this.session = session;
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException, InterruptedException {
                synchronized(MockClient.this) {

                    ObjectMapper mapper = new ObjectMapper();
                    String netWorkMsg = message.getPayload().toString();

                    try {

                        if(!JsonSchemaValidator.isValid(netWorkMsg)) {
                            ObjectNode inner = mapper.createObjectNode();
                            ObjectNode err = mapper.createObjectNode();
                            inner.put("message", "don't recognize message type");
                            err.set("badRequestError", inner);
                            String str = null;
                            try {
                                str = mapper.writeValueAsString(err);

                            } catch (JsonProcessingException e) {
                                // TODO: handle exception
                                throw new IllegalArgumentException("set up error messages incorrectly");
                            }
                            session.sendMessage(new TextMessage(str));

                            return;
                        }
                    }catch(IllegalArgumentException a) {
                        throw new IllegalArgumentException(a.getMessage());
                    } catch (JsonProcessingException e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        return;
                    } catch(IOException e2) {
                        e2.printStackTrace();

                        return;
                    }

                    ObjectNode outer = null;
                    ObjectNode payload = null;

                    try {
                        outer = (ObjectNode) mapper.readTree(netWorkMsg);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        ObjectNode inner = mapper.createObjectNode();
                        ObjectNode err = mapper.createObjectNode();
                        inner.put("message", "don't recognize message type");
                        err.set("badRequestError", inner);

                        String str = null;
                        try {

                            str = mapper.writeValueAsString(err);
                        } catch (JsonProcessingException e2) {
                            // TODO: handle exception
                            throw new IllegalArgumentException("set up error message incorrectly");
                        }

                        try {
                            session.sendMessage(new TextMessage(str));
                        } catch (IOException e3) {
                            // TODO: handle exception
                            e3.printStackTrace();

                        }
                        e.printStackTrace();
                        return;
                    }

                    String type = outer.fieldNames().next();
                    payload = outer.get(type).deepCopy();

                    switch(type) {
                        case "sessionConnectedResponse":
                            MockClient.this.sessionId = payload.get("sessionId").asText();
                            System.out.println("Parsed sessionId: " + sessionId);
                            break;
                        case "gameCreatedResponse":
                            MockClient.this.gameId = payload.get("gameId").asText();

                            MockClient.this.gameState = (ObjectNode)payload.get("gameState");
                            MockClient.this.main.setGameId(MockClient.this.gameId);
                            break;
                        case "gameReadyResponse":
                            MockClient.this.board = mapper.valueToTree(payload.get("board"));
                            MockClient.this.gameState = mapper.valueToTree(payload.get("gameState"));
                            String ui = mapper.valueToTree(payload.get("ui")).asText();

                            if(MockClient.this.gameId == null) {
                                MockClient.this.gameId = payload.get("gameId").asText();
                                //MockClient.this.main.setGameId(MockClient.this.gameId);
                            }

                            System.out.println("");
                            break;
                        case "stateUpdateResponse":
                             JsonNode game = payload.get("gameState");
                            ArrayNode changed = (ArrayNode) game.get("changedPos");

                            for(JsonNode node : changed) {
                                ArrayNode row = (ArrayNode)MockClient.this.board.get(node.get("x").asInt());
                                JsonNode pos = row.get(node.get("y").asInt());
                                JsonNode changePiece = node.get("piece");
                                ((ObjectNode)pos).set("piece", changePiece);
                            }

                            break;
                        case "gamesListResponse":
                            JsonNode gamesListJson = payload.get("gamesList");

                            break;
                        case "activeGamesResponse":
                            gamesListJson = payload.get("gamesList");
                            String[] activeGamesList;

                            break;
                        case "requestPromoteChoice":
                            System.out.println("recieved choices");
                            JsonNode choices = payload.get("choices");

                            break;
                        case "moveUpdateError":

                        default: break;
                    }
                }
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
                System.out.println("WebSocket closed");
            }
        }, this.wsUrl).get();
    }

    public String awaitSessionId() throws InterruptedException, ExecutionException {
        return sessionIdFuture.get();
    }

    public synchronized void sendMove(JsonNode move) {
        if (session != null && session.isOpen()) {

            ObjectMapper mapper = new ObjectMapper();
            String msgType = "moveRequest";
            ObjectNode payload = mapper.createObjectNode();

            payload.put("gameId", this.gameId);
            payload.set("move", move);
            ObjectNode outer = mapper.createObjectNode().set(msgType, payload);
            String str = null;
            try {
                str = mapper.writeValueAsString(outer);


            } catch (JsonProcessingException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("problem writing move message");

            }
            TextMessage msg = new TextMessage(str);
            try {
                session.sendMessage(msg);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
    }

    public synchronized String createGame(String game, String group, String playerName) {

        if(this.session != null) {
            System.out.println("Already connected");
            return "";
        }
        connect();

        if(this.session != null && this.session.isOpen()) {
            ObjectMapper mapper = new ObjectMapper();
            String msgType = "createGameRequest";
            ObjectNode inner = mapper.createObjectNode();
            inner.put("game", group);
            inner.put("subGame", game);
            inner.put("name", playerName);
            ObjectNode payload = mapper.createObjectNode();
            payload.set(msgType, inner);
            String str;
            try {
                str = mapper.writeValueAsString(payload);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new IllegalArgumentException("error creating createGame Message");
            }
            TextMessage msg = new TextMessage(str);
            try {
                this.session.sendMessage(msg);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
            System.out.println("Sent");
        }
        return null;
    }

    public synchronized String joinGame(String name, String gameId) {
        //connect();

        if(this.session != null) {
            System.out.println("Already connected");
            return "";
        }
        if(name.equals("")) {
            System.out.println("no name");
            return "";
        }
        connect();
        System.out.println(this.session != null ? this.session.getId(): "no session");
        System.out.println(this.session == null);
        System.out.println(this.session.isOpen());
        if(this.session != null && this.session.isOpen()) {
            System.out.println("past check");
            ObjectMapper mapper = new ObjectMapper();
            String msgType = "joinGameRequest";
            ObjectNode payload = mapper.createObjectNode();
            payload.put("name", name);
            payload.put("gameId", gameId);
            ObjectNode outer = mapper.createObjectNode().set(msgType, payload);
            System.out.println("made payload");

            String str;
            try {
                str = mapper.writeValueAsString(outer);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new IllegalArgumentException();
            }
            TextMessage msg = new TextMessage(str);
            System.out.println(str);
            try {
                this.session.sendMessage(msg);
                //this.session.close();
               // System.out.println("closed");
                //throw new RuntimeException();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                //throw new RuntimeException();
                return gameId;
            }

           return gameId;
        }


        return null;
    }

    public synchronized void quitGame(boolean hardQuit) {

        try {
            if(this.session != null) {
                this.session.close();
            }
            System.out.println("Sent");
            this.session = null;
            this.gameId = null;
            this.sessionId = null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        if(!hardQuit && this.session == null) {

        }

    }

    public List<String> getAvailableGames() {




        return null;
    }

    public void getActiveGames(String gameName) {



    }

    public void sendPromoteChoice(String choice) {

    }
}

