// Program to eastablish the socket connection

package gamesuite.server.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)throws Exception {
        super.afterConnectionClosed(session, status);
        System.out.println(session.getId() + " DisConnected");

        webSocketSessions.remove(session);
    }

    public void notifyPlayerJoined(String gameId, String msg) {
        Map<String, Integer> userSessions = this.gmRepo.getUserSessions(gameId);
        for (String sessionId : userSessions.keySet()) {
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
        String netWorkMsg = null;
        NetworkMessage clientMsg = null;
        String type = "";
        JsonNode payload = null;

        super.handleMessage(session, message);
      
        netWorkMsg = message.getPayload().toString();
        //System.out.println(payload);
        ObjectMapper mapper = new ObjectMapper();
        try {
            clientMsg = mapper.readValue(netWorkMsg, NetworkMessage.class);
            type = clientMsg.getMessageType();
            payload = clientMsg.getPayload();
        } catch (Exception e) {

        }
        if(type == null)
            type = "";
        switch(type) {
            case "createGameRequest":
                System.out.println("recieved");

                if(this.gmRepo.userSessions.contains(session.getId())) {
                    NetworkMessage netMsg = new NetworkMessage();
                    String msgType = "gameNotCreatedError";
                    JsonNode tmp = this.schemaRoot.get(msgType);
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "This user session is already in a game");
                    netMsg.setMessageType(msgType);
                    netMsg.setPayload(respPayload);
                    mapper = new ObjectMapper();
                    String str = mapper.writeValueAsString(netMsg);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }
                System.out.println("passed the check");
                if(payload.has("name")) {
                    String name = payload.get("name").asText();

                    if(name == null || name.equals("null")) {
                        NetworkMessage netMsg = new NetworkMessage();
                        String msgType = "missingNameError";
                        JsonNode tmp = this.schemaRoot.get(msgType);
                        ObjectNode respPayload = tmp.deepCopy();
                        respPayload.put("message", "must have a name to create game");
                        netMsg.setMessageType(msgType);
                        netMsg.setPayload(respPayload);
                        mapper = new ObjectMapper();
                        String str = mapper.writeValueAsString(netMsg);
                        TextMessage msg = new TextMessage(str);
                        session.sendMessage(msg);
                        break;
                    }

                    try {
                        Player player1 = new Player(name, 0);
                        GameBoard board = new GameBoard(8);
                        String gameId = this.gmRepo.createGame(player1, board, session.getId());
                        GameState game = this.gmRepo.getGameView(gameId);
                        //this.gmRepo.setUserNum(session.getId() , 1);
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
                         NetworkMessage netMsg = new NetworkMessage();
                        String msgType = "serverError";
                        JsonNode tmp = this.schemaRoot.get(msgType);
                        ObjectNode respPayload = tmp.deepCopy();
                        respPayload.put("message", "Error processing createGameRequest");
                        netMsg.setMessageType(msgType);
                        netMsg.setPayload(respPayload);
                        mapper = new ObjectMapper();
                        String str = mapper.writeValueAsString(netMsg);
                        TextMessage msg = new TextMessage(str);
                        session.sendMessage(msg);
                    }
                } else {
                    System.err.println("Missing name in payload");
                }
                break;
            case "joinGameRequest" :

                if(this.gmRepo.userSessions.contains(session.getId())) {
                    NetworkMessage netMsg = new NetworkMessage();
                    String msgType = "gameNotJoinedError";
                    JsonNode tmp = this.schemaRoot.get(msgType);
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "This user session is already in a game");
                    netMsg.setMessageType(msgType);
                    netMsg.setPayload(respPayload);
                    mapper = new ObjectMapper();
                    String str = mapper.writeValueAsString(netMsg);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }

                if(payload.has("name") && payload.has("gameId")) {

                    String player = payload.get("name").asText();
                    String gameId = payload.get("gameId").asText();
                    System.out.println(player + ",," + gameId);
                    if(player == null || player.equals("null") || gameId == null || gameId.equals("null")) {
                        NetworkMessage netMsg = new NetworkMessage();
                        String msgType = "gameNotJoinedError";
                        JsonNode tmp = this.schemaRoot.get(msgType);
                        ObjectNode respPayload = tmp.deepCopy();
                        respPayload.put("message", "must have a name and gameId to join a game");
                        netMsg.setMessageType(msgType);
                        netMsg.setPayload(respPayload);
                        mapper = new ObjectMapper();
                        String str = mapper.writeValueAsString(netMsg);
                        TextMessage msg = new TextMessage(str);
                        session.sendMessage(msg);
                        break;
                    }
                    if(!gmRepo.containsGame(gameId)) {
                        NetworkMessage netMsg = new NetworkMessage();
                        String msgType = "gameNotJoinedError";
                        JsonNode tmp = this.schemaRoot.get(msgType);
                        ObjectNode respPayload = tmp.deepCopy();
                        respPayload.put("message", "gameId does not exist");
                        netMsg.setMessageType(msgType);
                        netMsg.setPayload(respPayload);
                        mapper = new ObjectMapper();
                        String str = mapper.writeValueAsString(netMsg);
                        TextMessage msg = new TextMessage(str);
                        session.sendMessage(msg);
                        break;
                    }

                    try {
                        Player p2 = new Player(player, 0);
                        GameBoard board = this.gmRepo.joinGame(p2, gameId);
    
                        if(board == null) {}
                        else {
                            JsonNode boardJson = mapper.valueToTree(board.getBoard());
                            GameState game = this.gmRepo.getGM(gameId).getGameState();
                            //this.gmRepo.setUserNum(session.getId(), 2);
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
                    } catch (Exception e) {
                         NetworkMessage netMsg = new NetworkMessage();
                        String msgType = "serverError";
                        JsonNode tmp = this.schemaRoot.get(msgType);
                        ObjectNode respPayload = tmp.deepCopy();
                        respPayload.put("message", "Error processing createGameRequest");
                        netMsg.setMessageType(msgType);
                        netMsg.setPayload(respPayload);
                        mapper = new ObjectMapper();
                        String str = mapper.writeValueAsString(netMsg);
                        TextMessage msg = new TextMessage(str);
                        session.sendMessage(msg);
                    }
                }
                break;
            case "moveRequest":

                mapper = new ObjectMapper();
                //MoveRequest req = mapper.readValue(message.getPayload().toString(), MoveRequest.class);
                //String gameId = req.getGameId();
                //String sessionId = req.getSessionId();
                Move move = mapper.treeToValue(payload.get("move"), Move.class);//req.getMove();
                String gameId = payload.get("gameId").asText();
                Map<String, Integer> sessionList = this.gmRepo.getUserSessions(gameId);
        
                GameManager gm = this.gmRepo.getGM(gameId);

                int currTurn = gm.getGameState().getTurn();
                int userTurnNum = sessionList.get(session.getId());

                if(currTurn != userTurnNum) {
                     NetworkMessage netMsg = new NetworkMessage();
                    String msgType = "moveUpdateError";
                    JsonNode tmp = this.schemaRoot.get(msgType);
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "this user cannot make a move yet");
                    netMsg.setMessageType(msgType);
                    netMsg.setPayload(respPayload);
                    mapper = new ObjectMapper();
                    String str = mapper.writeValueAsString(netMsg);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }

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
        
                for(String user : sessionList.keySet())
                    this.webSocketSessions.get(user).sendMessage(msg);
                break;
            default:
                mapper = new ObjectMapper();
                netMsg = new NetworkMessage();
                msgType = "badRequestError";
                JsonNode error = schemaRoot.get(msgType);
                ObjectNode errorObj = error.deepCopy();
                errorObj.put("message", "bad request: don't recognize message type");
                netMsg = new NetworkMessage();
                netMsg.setMessageType(msgType);
                netMsg.setPayload(errorObj);
                str = mapper.writeValueAsString(netMsg);
                msg = new TextMessage(str);
                session.sendMessage(msg);
        }
    }
}
