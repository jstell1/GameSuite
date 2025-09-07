// Program to eastablish the socket connection

package gamesuite.server.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import gamesuite.core.network.GameCreatedResponse;
import gamesuite.core.network.GameReadyResponse;
import gamesuite.core.network.MoveRequest;
import gamesuite.core.network.WebSockServerMessage;
import gamesuite.core.network.NetworkMessage;

@Component
public class SocketConnectionHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();
    private ServerGameRepo gmRepo;
    private InputStream schemaStream;
    private JsonNode schemaRoot;

    public SocketConnectionHandler(ServerGameRepo gmRepo) {
        this.gmRepo = gmRepo;
        this.schemaStream = getClass().getResourceAsStream("/message_schema.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.schemaRoot = mapper.readTree(schemaStream);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public boolean hasSocket(String sessionId) {
        return this.webSocketSessions.containsKey(sessionId);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println(session.getId() + " Connected");
        NetworkMessage msg = new NetworkMessage();
        String msgType = "sessionConnectedResponse";
        JsonNode tmp = schemaRoot.get(msgType);
        ObjectNode payload = tmp.deepCopy();
        payload.put("sessionId", session.getId());
        msg.setMessageType(msgType);
        msg.setPayload(payload);
        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(msg);
        TextMessage txtMsg = new TextMessage(str);
        webSocketSessions.put(session.getId(), session);
        session.sendMessage(txtMsg);
        /* 
        WebSockServerMessage msg1 = new WebSockServerMessage(null, null, session.getId());
        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(msg1);
        TextMessage msg = new TextMessage(str);
        webSocketSessions.put(session.getId(), session);
        session.sendMessage(msg);
        */
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)throws Exception {
        super.afterConnectionClosed(session, status);
        System.out.println(session.getId() + " DisConnected");

        webSocketSessions.remove(session);
    }

    public void notifyPlayerJoined(String gameId, String msg) {
        List<String> userSessions = this.gmRepo.getUserSessions(gameId);
        for (String sessionId : userSessions) {
            try {
                WebSocketSession s = this.webSocketSessions.get(sessionId);
                s.sendMessage(new TextMessage(msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        super.handleMessage(session, message);
        
        String netWorkMsg = message.getPayload().toString();
        //System.out.println(payload);
        ObjectMapper mapper = new ObjectMapper();
        NetworkMessage clientMsg = mapper.readValue(netWorkMsg, NetworkMessage.class);
        String type = clientMsg.getMessageType();
        JsonNode payload = clientMsg.getPayload();

        switch(type) {
            case "createGameRequest":
                System.out.println("recieved");
                
                if(payload.has("name")) {
                    String name = payload.get("name").asText();

                    if(name == null) {

                    }

                    try {
                        Player player1 = new Player(name, 0);
                        GameBoard board = new GameBoard(8);
                        String gameId = this.gmRepo.createGame(player1, board);
                        GameState game = this.gmRepo.getGameView(gameId);
                        this.gmRepo.setUserNum(session.getId() , 1);
                        this.gmRepo.addWebSocketToGame(gameId, session.getId());
                        GameManager gm = new GameManager(board, player1);
                        this.gmRepo.setGame(gameId, gm);
                        NetworkMessage netMsg = new NetworkMessage();
                        String msgType = "gameCreatedResponse";
                        JsonNode tmp = this.schemaRoot.get(msgType);
                        ObjectNode respPayload = tmp.deepCopy();
                        respPayload.put("gameId", gameId);
                        respPayload.set("gameState", mapper.valueToTree(game));
                        netMsg.setMessageType(msgType);
                        netMsg.setPayload(respPayload);
                        mapper = new ObjectMapper();
                        String str = mapper.writeValueAsString(netMsg);
                        TextMessage msg = new TextMessage(str);
                        session.sendMessage(msg);
                        System.out.println("sent");
                        
                    } catch (Exception e) {

                    }
                } else {
                    System.err.println("Missing name in payload");
                }
                break;
            case "joinGameRequest" :

                if(payload.has("name") && payload.has("gameId")) {

                    String player = payload.get("name").asText();
                    String gameId = payload.get("gameId").asText();
                    System.out.println(player + ",," + gameId);
                    if(player == null || gameId == null) {

                    }
                    if(!gmRepo.containsGame(gameId)) {

                    }

                    try {
                        Player p2 = new Player(player, 0);
                        GameBoard board = this.gmRepo.joinGame(p2, gameId);
    
                        if(board == null) {}
                        else {
                            JsonNode boardJson = mapper.valueToTree(board.getBoard());
                            GameState game = this.gmRepo.getGM(gameId).getGameState();
                            this.gmRepo.setUserNum(session.getId(), 2);
                            this.gmRepo.addWebSocketToGame(gameId, session.getId());

                            NetworkMessage netMsg = new NetworkMessage();
                            String msgType = "gameReadyResponse";
                            JsonNode tmp = this.schemaRoot.get(msgType);
                            ObjectNode respPayload = tmp.deepCopy();
                            respPayload.set("board", boardJson);
                            respPayload.put("gameId", gameId);
                            respPayload.set("gameState", mapper.valueToTree(game));
                            netMsg.setMessageType(msgType);
                            netMsg.setPayload(respPayload);


                            //GameReadyResponse resp2 = new GameReadyResponse(board.getBoard(), gameId, game);
                            //WebSockServerMessage servMsg = new WebSockServerMessage(resp2, null, session.getId());


                            mapper = new ObjectMapper();
                            String str = mapper.writeValueAsString(netMsg);
                            //TextMessage msg = new TextMessage(str);
                            notifyPlayerJoined(gameId, str);
                        }
                    } catch (Exception e) {}
                }
                break;
            default:


                mapper = new ObjectMapper();
                //MoveRequest req = mapper.readValue(message.getPayload().toString(), MoveRequest.class);
                //String gameId = req.getGameId();
                //String sessionId = req.getSessionId();
                Move move = mapper.treeToValue(payload.get("move"), Move.class);//req.getMove();
                String gameId = payload.get("gameId").asText();
                List<String> sessionList = this.gmRepo.getUserSessions(gameId);
        
                GameManager gm = this.gmRepo.getGM(gameId);
                gm.sendMove(move);
                GameState game = gm.getGameState();

                NetworkMessage netMsg = new NetworkMessage();
                String msgType = "stateUpdateResponse";
                JsonNode tmp = this.schemaRoot.get(msgType);
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("gameId", gameId);
                respPayload.set("gameState", mapper.valueToTree(game));
                netMsg.setMessageType(msgType);
                netMsg.setPayload(respPayload);


                //GameCreatedResponse resp = new GameCreatedResponse(gameId, game);
                //WebSockServerMessage srvMsg = new WebSockServerMessage(null, resp, null);


                String str = mapper.writeValueAsString(netMsg);
                TextMessage msg = new TextMessage(str);
        
                for(String user : sessionList)
                    this.webSocketSessions.get(user).sendMessage(msg);
                break;
        }

        /*
         * 
        
         * 
         */
    }
}
