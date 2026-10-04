package gamesuite.client.control;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.hc.client5.http.impl.Operations.CompletedFuture;
import org.json.JSONException;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.client.view.MainGUI;
import gamesuite.core.control.PluginLoader;
import gamesuite.core.network.*;
import gamesuite.core.ui.GameBoardFactory;
import gamesuite.core.ui.GameBoardUI;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClientManager {
    private WebSocketClient client; //= new StandardWebSocketClient();
    private HttpClient restClient;
    private WebSocketSession session;
    private String sessionId;
    private String gameId;
    private CompletableFuture<String> sessionIdFuture = new CompletableFuture<>();
    private String baseUrl;
    private String wsUrl;
    private GUIManager guiGM;
    private InputStream schemaStream;
    private JsonNode schemaRoot;
    private String ip;
    private int port;
    private PluginLoader loader;
    private MainGUI main;
    private CompletableFuture<WebSocketSession> connectionFuture; 
    
    public ClientManager(String ip, int port) throws IOException {

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxTextMessageBufferSize(512 * 1024);
        container.setDefaultMaxBinaryMessageBufferSize(512 * 1024);
        this.client = new StandardWebSocketClient(container);
        try {
            this.connectionFuture = new CompletableFuture<>();
            this.loader = new PluginLoader("plugins/");
            this.loader.loadAll();
            //this.loader.watchForChanges();

            this.loader.setUIPluginLoader("plugins/ui");
            this.loader.loadGameBoards();
            
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
        //try {
            this.schemaRoot = mapper.readTree(schemaStream);
      // } catch (Exception e) {
           // throw new 
       // }
    }

    public void loadGame(String gameName) {

    }

    public void setMainGUI(MainGUI gui) {
        this.main = gui;
    }
    public void setGUIManager(GUIManager guiGM) {
        if(this.guiGM != null)
            throw new IllegalStateException("cannot be changed once set");
        this.guiGM = guiGM;
    }

    public void connect() {
        this.connectionFuture = new CompletableFuture<>();
        try {
            connect(this.wsUrl);
        } catch (Exception e) {
            System.out.println("no ssl");
            try {
                this.wsUrl = "ws://" + this.ip + ":" + this.port + "/ingame";
                connect(this.wsUrl);
            } catch (Exception e2) {
                e.printStackTrace();
                sendMainGuiError("could not connect to the server:\n" + e2.getMessage());
            }
        }
        
    }

    private void connect(String wsUrl) throws InterruptedException, ExecutionException {
        this.client.execute(new AbstractWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                //System.out.println("Connected to WebSocket");
                ClientManager.this.session = session;
                ClientManager.this.connectionFuture.complete(session);
                //ClientGameManager.this.sessionId = session.getId();
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
                synchronized(ClientManager.this) {

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
                            sendMainGuiError("recieved invalid Json from the server");
                            return;
                        }
                    }catch(IllegalArgumentException a) {
                        throw new IllegalArgumentException(a.getMessage());
                    } catch (JsonProcessingException e) {
                        // TODO: handle exception
                        e.printStackTrace();
                        sendMainGuiError("revcieved malformed json from the server");
                        return;
                    } catch(IOException e2) {
                        e2.printStackTrace();
                        sendMainGuiError("problem wrting message to socket");
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
                            sendMainGuiError("problem writing message to socket");
                        }
                        return;
                    }
                    
                    String type = outer.fieldNames().next();
                    payload = outer.get(type).deepCopy();
    
                    switch(type) {
                        case "sessionConnectedResponse":
                            ClientManager.this.sessionId = payload.get("sessionId").asText();
                            System.out.println("Parsed sessionId: " + sessionId);
                            break;
                        case "gameCreatedResponse":
                            ClientManager.this.gameId = payload.get("gameId").asText();
                            ClientManager.this.guiGM.setGameId(ClientManager.this.gameId);
                            JsonNode gameJson = payload.get("gameState");
                            //GameState game = mapper.treeToValue(gameJson, GameState.class);
                            //ClientManager.this.guiGM.setGameState(gameJson);
                            ClientManager.this.guiGM.setPlayerTurn(1);//setGameState(gameJson);
                            break;
                        case "gameReadyResponse":
                            JsonNode boardJson = mapper.valueToTree(payload.get("board"));
                            gameJson = mapper.valueToTree(payload.get("gameState"));
                            //CoordPair[][] board = mapper.treeToValue(boardJson, CoordPair[][].class);
                            //game = mapper.treeToValue(gameJson, GameState.class);
                            if(ClientManager.this.gameId == null) {
                                ClientManager.this.gameId = payload.get("gameId").asText();
                                ClientManager.this.guiGM.setGameId(payload.get("gameId").asText());
                            }

                            if(ClientManager.this.guiGM.getPlayerTurn() == 0) {
                                ClientManager.this.guiGM.setPlayerTurn(2);
                            }

                            GameBoardFactory fact;
                            try {
                                fact = ClientManager.this.loader.createBoardFactory("BoardGameUI");
                            } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                                    | NoSuchMethodException | SecurityException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                sendMainGuiError("issue creating board UI from plugin");
                                return; 
                            }
                            try {
                                System.out.println(boardJson);
                                System.out.println("\n\n");
                                System.out.println(gameJson);
                                GameBoardUI gbu = fact.createGameBoard(boardJson, gameJson, ClientManager.this.guiGM);
                                //ClientManager.this.guiGM.setBoard(gbu);
                                //ClientManager.this.guiGM.setGameState(gameJson);
                                ClientManager.this.guiGM.initGame(gbu);
                            } catch (JsonProcessingException e) {
                                // TODO: handle exception
                                throw new IllegalArgumentException("something is wrong with the incoming boardJson and uiJar can't be created");
                            }
                            break;
                        case "stateUpdateResponse":
                            gameJson = mapper.valueToTree(payload.get("gameState"));
                            //GameState game = mapper.treeToValue(gameJson, GameState.class);

                            //ClientManager.this.guiGM.setGameState(game);
                            ClientManager.this.guiGM.update(gameJson);
                            break;
                        case "gamesListResponse":
                            JsonNode gamesListJson = payload.get("gamesList"); 
                            try {
                                
                                Map<String, ArrayList<String>> gamesList = mapper.treeToValue(gamesListJson, new TypeReference<Map<String, ArrayList<String>>>(){});
                                ClientManager.this.main.setGamesList(gamesList);
                            } catch (JsonProcessingException e) {
                                // TODO: handle exception
                                e.printStackTrace();
                                sendMainGuiError("server error on getting gamesList");
                            }
                            break;
                        case "activeGamesResponse":
                            gamesListJson = payload.get("gamesList");
                            String[] activeGamesList;
                            try {
                                activeGamesList = mapper.treeToValue(gamesListJson, String[].class);
                                ClientManager.this.guiGM.setActiveGamesList(activeGamesList);
                            } catch (JsonProcessingException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                sendMainGuiError("server error on getting activeGamesList");
                            } 
                            break;
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
            //JsonNode tmp = mapper.createObjectNode();
            //JsonNode moveJson = mapper.valueToTree(move);
            ObjectNode payload = mapper.createObjectNode();//tmp.deepCopy();

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
                sendMainGuiError("problem wrting mesage to socket");
            }
        }
    }

    public synchronized String createGame(String game, String group, String playerName) {
        
        if(this.session != null) {
            System.out.println("Already connected");
            return "";
        }
        connect();
        //this.session = this.connectionFuture.get();

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
                sendMainGuiError("problem writing message to socket");
                return null;
            }
            System.out.println("Sent");
        }
       return null;
    }

    public synchronized String joinGame(String game, String name, String gameId) {

        
        //try {
            if(this.session != null) {
                System.out.println("Already connected");
                return "";
            }
            if(name.equals("")) {
                System.out.println("no name");
                return "";
            }
            connect();
            //this.session = this.connectionFuture.get();
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
               //try {
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
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        sendMainGuiError("problem writing message to socket");
                        return gameId;
                    }
               // } catch (Exception e) {
                    
              //  }
                System.out.println("Sent");
                return gameId;
            }
        //} catch (Exception e) {
            // TODO Auto-generated catch block
         //   e.printStackTrace();
       // }

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
            this.connectionFuture = new CompletableFuture<>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            
        }

        if(!hardQuit && this.session == null) {
            try {
                //connect();
                this.guiGM.resetGUI();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }

    public List<String> getAvailableGames() {

        

        System.out.println("getting gamesList");
        HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(this.baseUrl + "/games"))
                        .GET()
                        .build();

        CompletableFuture<HttpResponse<String>> response = this.restClient.sendAsync(request,
                                                    HttpResponse.BodyHandlers.ofString());


 
        response.thenAccept(resp -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<String, ArrayList<String>> list = mapper.readValue(
                                                    resp.body(), 
                                                    new TypeReference<Map<String, ArrayList<String>>>() {});
                this.main.setGamesList(list);
                System.out.println("Retrieved games list");
            } catch (IOException e) {
               e.printStackTrace();
               sendMainGuiError("error processing server Available games list:\n" + e.getMessage());
            }
            
        }).join();






        //  if(session != null && session.isOpen()) {



        //     ObjectMapper mapper = new ObjectMapper();
        //     String msgType = "gamesListRequest";
        //     ObjectNode payload = mapper.createObjectNode();
        //     payload.set(msgType, mapper.createObjectNode());
        //     try {
        //         String str = mapper.writeValueAsString(payload);
        //         TextMessage msg = new TextMessage(str);
        //         session.sendMessage(msg);
        //         System.out.println("Sent");
        //     } catch (Exception e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        // }
        return null;
    }

      private void sendMainGuiError(String error) {
        this.guiGM.setErrorMsg(error);
    }

    public void getActiveGames(String gameName) {
        System.out.println("getting active list");
         HttpRequest.Builder requestB = HttpRequest.newBuilder(); 
         HttpRequest request;
        //if(group != null) {

        //    request = requestB
        //                   .uri(URI.create(this.baseUrl + "/games/" + group + "/" + gameName))
        //                   .GET()
        //                   .build();
       // } else {
            request = requestB
                           .uri(URI.create(this.baseUrl + "/games/" + gameName))
                           .GET()
                           .build();
       // }

        CompletableFuture<HttpResponse<String>> response = this.restClient.sendAsync(request,
                                                    HttpResponse.BodyHandlers.ofString());

        response.thenAccept(resp -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String[] list = mapper.readValue(resp.body(), String[].class);
                this.guiGM.setActiveGamesList(list);
                System.out.println("retrieved active list");
            } catch (IOException e) {
                e.printStackTrace();
                sendMainGuiError("Error processing server Active Games List:\n" + e.getMessage());
            }
            
        }).join();

       
       
       
       
       
       
        // if(session != null && session.isOpen()) {
        //     ObjectMapper mapper = new ObjectMapper();
        //     String msgType = "activeGamesRequest";
        //     ObjectNode payload = mapper.createObjectNode();
        //     ObjectNode inner = mapper.createObjectNode();
        //     inner.put("game", gameName);
        //     payload.set(msgType, inner);
        //     try {
        //         String str = mapper.writeValueAsString(payload);
        //         TextMessage msg = new TextMessage(str);
        //         session.sendMessage(msg);
        //         System.out.println("Sent");
        //     } catch (Exception e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        //}
    }
}
