package gamesuite.client.control;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.network.*;
import jakarta.websocket.Decoder.Text;

public class ClientManager {
    private final WebSocketClient client = new StandardWebSocketClient();
    private RestTemplate restTemplate = new RestTemplate();
    private WebSocketSession session;
    private String sessionId;
    private String gameId;
    private CompletableFuture<String> sessionIdFuture = new CompletableFuture<>();
    private String baseUrl;
    private String wsUrl;
    private GUIManager guiGM;
    private InputStream schemaStream;
    private JsonNode schemaRoot;
    
    public ClientManager(String ip, int port) {
        this.baseUrl = "http://" + ip + ":" + port;
        this.wsUrl = "ws://" + ip + ":" + port + "/ingame";
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        this.schemaStream = JsonSchemaValidator.class.getClassLoader().getResourceAsStream("schema.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.schemaRoot = mapper.readTree(schemaStream);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void setGUIManager(GUIManager guiGM) {
        if(this.guiGM == null)
            this.guiGM = guiGM;
    }

    public void connect() throws Exception {
        this.client.execute(new AbstractWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                //System.out.println("Connected to WebSocket");
                ClientManager.this.session = session;
                //ClientGameManager.this.sessionId = session.getId();
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                ObjectMapper mapper = new ObjectMapper();
                String netWorkMsg = message.getPayload().toString();
                //System.out.println(payload)

                if(!JsonSchemaValidator.isValid(netWorkMsg)) {
                    ObjectNode inner = mapper.createObjectNode();//ClientManager.this.schemaRoot.get("badRequestError").deepCopy();
                    ObjectNode err = mapper.createObjectNode();
                    inner.put("message", "don't recognize message type");
                    err.set("badRequestError", inner);
                    String str = mapper.writeValueAsString(err);
                    session.sendMessage(new TextMessage(str));
                    return;
                }

                //NetworkMessage servMsg = mapper.readValue(netWorkMsg, NetworkMessage.class);
                //String type = servMsg.getMessageType();
                //JsonNode payload = servMsg.getPayload();
                ObjectNode outer = null;
                ObjectNode payload =null; 

                try {
                    outer = (ObjectNode) mapper.readTree(netWorkMsg);
                } catch (Exception e) {
                    ObjectNode inner = mapper.createObjectNode();//ClientManager.this.schemaRoot.get("badRequestError").deepCopy();
                    ObjectNode err = mapper.createObjectNode();
                    inner.put("message", "don't recognize message type");
                    err.set("badRequestError", inner);
                    String str = mapper.writeValueAsString(err);
                    session.sendMessage(new TextMessage(str));
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
                        GameState game = mapper.treeToValue(gameJson, GameState.class);
                        ClientManager.this.guiGM.setGameState(game);
                        break;
                    case "gameReadyResponse":
                        JsonNode boardJson = mapper.valueToTree(payload.get("board"));
                        gameJson = mapper.valueToTree(payload.get("gameState"));
                        CoordPair[][] board = mapper.treeToValue(boardJson, CoordPair[][].class);
                        game = mapper.treeToValue(gameJson, GameState.class);
                        if(ClientManager.this.gameId == null)
                            ClientManager.this.gameId = payload.get("gameId").asText();
                        ClientManager.this.guiGM.setGameState(game);
                        //ClientManager.this.guiGM.setGameId(type);
                        ClientManager.this.guiGM.initGame(board, game);
                        break;
                    case "stateUpdateResponse":
                        gameJson = mapper.valueToTree(payload.get("gameState"));
                        game = mapper.treeToValue(gameJson, GameState.class);

                        //ameCreatedResponse msg = servMsg.getResp2();
                        ClientManager.this.guiGM.setGameState(game);
                        ClientManager.this.guiGM.update();
                        break;
                    default: break;
                }
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
                //System.out.println("WebSocket closed");
            }
        }, this.wsUrl).get();
    }

    public String awaitSessionId() throws Exception {
        return sessionIdFuture.get();
    }

    public void sendMove(Move move) {
        if (session != null && session.isOpen()) {

            //MoveRequest request = new MoveRequest(this.sessionId, this.gameId, move);
            ObjectMapper mapper = new ObjectMapper();
            //NetworkMessage netMsg = new NetworkMessage();
            String msgType = "moveRequest";
            JsonNode tmp = mapper.createObjectNode();//this.schemaRoot.get(msgType);
            JsonNode moveJson = mapper.valueToTree(move);
            ObjectNode payload = tmp.deepCopy();

            //netMsg.setMessageType(msgTpye);
            payload.put("gameId", this.gameId);
            payload.set("move", moveJson);
            //netMsg.setPayload(payload);
            ObjectNode outer = mapper.createObjectNode().set(msgType, payload);
            
            try {
                String str = mapper.writeValueAsString(outer);
                TextMessage msg = new TextMessage(str);
                session.sendMessage(msg);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String createGame(String playerName) throws Exception {
        
        if(session != null && session.isOpen()) {
            ObjectMapper mapper = new ObjectMapper();
            //NetworkMessage netMsg = new NetworkMessage();
            String msgType = "createGameRequest";
            ObjectNode inner = mapper.createObjectNode();//this.schemaRoot.get(msgType).deepCopy();
            inner.put("name", playerName);
            ObjectNode payload = mapper.createObjectNode();
            payload.set(msgType, inner);
            //netMsg.setMessageType(msgType);
            //netMsg.setPayload(payload);
            String str = mapper.writeValueAsString(payload);
            TextMessage msg = new TextMessage(str);
            session.sendMessage(msg);
            System.out.println("Sent");
        }
       return null;
    }

    public String joinGame(String name, String gameId) {
        if(session != null && session.isOpen()) {
            ObjectMapper mapper = new ObjectMapper();
            //NetworkMessage netMsg = new NetworkMessage();
            String msgType = "joinGameRequest";
            ObjectNode payload = mapper.createObjectNode();//this.schemaRoot.get(msgType).deepCopy();
            payload.put("name", name);
            payload.put("gameId", gameId);
            ObjectNode outer = mapper.createObjectNode().set(msgType, payload);
            //netMsg.setMessageType(msgType);
            //netMsg.setPayload(payload);
            try {
                String str = mapper.writeValueAsString(outer);
                TextMessage msg = new TextMessage(str);
                System.out.println(str);
                session.sendMessage(msg);
            } catch (Exception e) {
                // TODO: handle exception
            }
            System.out.println("Sent");
            return gameId;
        }
       return null;


        /*
         * 
         String url = baseUrl + "/games/players";
 
         JoinGameRequest request = new JoinGameRequest();
         request.setPlayer(name);
         request.setGameId(gameId);
         request.setSessionId(this.sessionId);
 
         try {
             GameCreatedResponse response = restTemplate.patchForObject(url, request, GameCreatedResponse.class);
             this.gameId = response.getGameId();
             return response.getGameId();
         } catch (Exception e) {}
         return null;
         * 
         */
    }
}
