// Program to eastablish the socket connection

package gamesuite.server.control;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import gamesuite.core.network.JsonSchemaValidator;
import gamesuite.core.network.NetworkMessage;

@Component
public class SocketConnectionHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();
    private ServerGameRepo gmRepo;
    private InputStream schemaStream;
    private JsonNode schemaRoot;

    public SocketConnectionHandler(ServerGameRepo gmRepo) {
        this.gmRepo = gmRepo;
        this.schemaStream = JsonSchemaValidator.class.getClassLoader().getResourceAsStream("schema.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.schemaRoot = mapper.readTree(schemaStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean hasSocket(String sessionId) {
        return this.webSocketSessions.containsKey(sessionId);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println(session.getId() + " Connected");
        //NetworkMessage msg = new NetworkMessage();
        //JsonNode tmp = schemaRoot.get(msgType);
        //msg.setMessageType(msgType);
        //msg.setPayload(payload);
        String msgType = "sessionConnectedResponse";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode inner = mapper.createObjectNode();//this.schemaRoot.get(msgType).deepCopy();
        inner.put("sessionId", session.getId());
        ObjectNode payload = mapper.createObjectNode().set(msgType, inner);
        String str = mapper.writeValueAsString(payload);
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

        super.handleMessage(session, message);

        ObjectMapper mapper = new ObjectMapper();
        String netWorkMsg = null;
        ObjectNode outer = null;
        ObjectNode payload = null;
        String type = "";
        //JsonNode payload = null;
        
        netWorkMsg = message.getPayload().toString();
        try {
            outer = (ObjectNode) mapper.readTree(netWorkMsg);
        } catch (Exception e) {
            ObjectNode inner = mapper.createObjectNode();//SocketConnectionHandler.this.schemaRoot.get("badRequestError").deepCopy();
            ObjectNode err = mapper.createObjectNode();
            inner.put("message", "don't recognize message type");
            err.set("badRequestError", inner);
            String str = mapper.writeValueAsString(err);
            session.sendMessage(new TextMessage(str));
            return;
        }
      
        //System.out.println(payload);
        if(!JsonSchemaValidator.isValid(netWorkMsg)) {
            ObjectNode inner = mapper.createObjectNode();//SocketConnectionHandler.this.schemaRoot.get("badRequestError").deepCopy();
            ObjectNode err = mapper.createObjectNode();
            inner.put("message", "don't recognize message type");
            err.set("badRequestError", inner);
            String str = mapper.writeValueAsString(err);
            session.sendMessage(new TextMessage(str));
            return;
        } 

        type = outer.fieldNames().next();
        payload = outer.get(type).deepCopy();
        //if(type == null)
         //   type = "";
        switch(type) {
            case "createGameRequest":
                System.out.println("recieved");

                if(this.gmRepo.userSessions.contains(session.getId())) {
                    //NetworkMessage netMsg = new NetworkMessage();
                    mapper = new ObjectMapper();
                    String msgType = "gameNotCreatedError";
                    ObjectNode inner = mapper.createObjectNode();//this.schemaRoot.get(msgType).deepCopy();
                    inner.put("message", "This user session is already in a game");
                    ObjectNode respPayload = mapper.createObjectNode().set(msgType, inner);
                    //netMsg.setMessageType(msgType);
                    //netMsg.setPayload(respPayload);
                    String str = mapper.writeValueAsString(respPayload);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }
                System.out.println("passed the check");
                String name = payload.get("name").asText();
                /*
                 * 
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
                 */

                try {
                    mapper = new ObjectMapper();
                    Player player1 = new Player(name, 0);
                    GameBoard board = new GameBoard(8);
                    String gameId = this.gmRepo.createGame(player1, board, session.getId());
                    GameState game = this.gmRepo.getGameView(gameId);
                    //this.gmRepo.setUserNum(session.getId() , 1);
                    //this.gmRepo.addWebSocketToGame(gameId, session.getId());
                    GameManager gm = new GameManager(board, player1);
                    this.gmRepo.setGame(gameId, gm);
                    //NetworkMessage netMsg = new NetworkMessage();
                    String msgType = "gameCreatedResponse";
                    ObjectNode respPayload = mapper.createObjectNode();//this.schemaRoot.get(msgType).deepCopy();
                    outer = mapper.createObjectNode();
                    respPayload.put("gameId", gameId);
                    respPayload.set("gameState", mapper.valueToTree(game));
                    //netMsg.setMessageType(msgType);
                    //netMsg.setPayload(respPayload);
                    outer.set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    System.out.println("sent");
                    
                } catch (Exception e) {
                        //NetworkMessage netMsg = new NetworkMessage();
                    mapper = new ObjectMapper();
                    String msgType = "serverError";
                    ObjectNode respPayload = mapper.createObjectNode();//this.schemaRoot.get(msgType).deepCopy();
                    respPayload.put("message", "Error processing createGameRequest");
                    //netMsg.setMessageType(msgType);
                    //netMsg.setPayload(respPayload);
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                }
                break;
            case "joinGameRequest" :

                if(this.gmRepo.userSessions.contains(session.getId())) {
                    //NetworkMessage netMsg = new NetworkMessage();
                    mapper = new ObjectMapper();
                    String msgType = "gameNotJoinedError";
                    JsonNode tmp = mapper.createObjectNode();//this.schemaRoot.get(msgType);
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "This user session is already in a game");
                    //netMsg.setMessageType(msgType);
                    //netMsg.setPayload(respPayload);
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }

                String player = payload.get("name").asText();
                String gameId = payload.get("gameId").asText();
                System.out.println(player + ",," + gameId);

                /*
                 * 
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
                 */

                if(!gmRepo.containsGame(gameId)) {
                    //NetworkMessage netMsg = new NetworkMessage();
                    mapper = new ObjectMapper();
                    String msgType = "gameNotJoinedError";
                    JsonNode tmp = mapper.createObjectNode();//this.schemaRoot.get(msgType);
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "gameId does not exist");
                    //netMsg.setMessageType(msgType);
                    //netMsg.setPayload(respPayload);
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }

                try {
                    Player p2 = new Player(player, 0);
                    GameBoard board = this.gmRepo.joinGame(p2, gameId);

                    if(board == null) {}
                    else {
                        mapper = new ObjectMapper();
                        JsonNode boardJson = mapper.valueToTree(board.getBoard());
                        GameState game = this.gmRepo.getGM(gameId).getGameState();
                        //this.gmRepo.setUserNum(session.getId(), 2);
                        this.gmRepo.addWebSocketToGame(gameId, session.getId());

                        //NetworkMessage netMsg = new NetworkMessage();
                        String msgType = "gameReadyResponse";
                        JsonNode tmp = mapper.createObjectNode();//this.schemaRoot.get(msgType);
                        ObjectNode respPayload = tmp.deepCopy();
                        respPayload.set("board", boardJson);
                        respPayload.put("gameId", gameId);
                        respPayload.set("gameState", mapper.valueToTree(game));
                        //netMsg.setMessageType(msgType);
                        //netMsg.setPayload(respPayload);


                        //GameReadyResponse resp2 = new GameReadyResponse(board.getBoard(), gameId, game);
                        //WebSockServerMessage servMsg = new WebSockServerMessage(resp2, null, session.getId());


                        outer = mapper.createObjectNode().set(msgType, respPayload);
                        String str = mapper.writeValueAsString(outer);
                        //TextMessage msg = new TextMessage(str);
                        notifyPlayerJoined(gameId, str);
                    }
                } catch (Exception e) {
                        //NetworkMessage netMsg = new NetworkMessage();
                    mapper = new ObjectMapper();
                    String msgType = "serverError";
                    JsonNode tmp = mapper.createObjectNode();//this.schemaRoot.get(msgType);
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "Error processing createGameRequest");
                    //netMsg.setMessageType(msgType);
                    //netMsg.setPayload(respPayload);
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                }
            
                break;
            case "moveRequest":

            /*
             * 
             if(!payload.has("gameId") || !payload.has("move")) {
                 NetworkMessage netMsg = new NetworkMessage();
                 String msgType = "moveUpdateError";
                 JsonNode tmp = this.schemaRoot.get(msgType);
                 ObjectNode respPayload = tmp.deepCopy();
                 respPayload.put("message", "Missing or invalid move parameters");
                 netMsg.setMessageType(msgType);
                 netMsg.setPayload(respPayload);
                 mapper = new ObjectMapper();
                 String str = mapper.writeValueAsString(netMsg);
                 TextMessage msg = new TextMessage(str);
                 session.sendMessage(msg);
                 break;
             }
             */
                mapper = new ObjectMapper();
                //MoveRequest req = mapper.readValue(message.getPayload().toString(), MoveRequest.class);
                //String gameId = req.getGameId();
                //String sessionId = req.getSessionId();
                Move move = null;
                move = mapper.treeToValue(payload.get("move"), Move.class);//req.getMove();
                gameId = payload.get("gameId").asText();
                /*
                 * 
                 if(gameId == null || gameId.equals("null") || !this.gmRepo.containsGame(gameId) || !this.gmRepo.gameUserMap.get(gameId).containsKey(session.getId()) || move == null) {
                     
                     NetworkMessage netMsg = new NetworkMessage();
                     String msgType = "moveUpdateError";
                     JsonNode tmp = this.schemaRoot.get(msgType);
                     ObjectNode respPayload = tmp.deepCopy();
                     respPayload.put("message", "Missing or invalid move parameters");
                     netMsg.setMessageType(msgType);
                     netMsg.setPayload(respPayload);
                     mapper = new ObjectMapper();
                     String str = mapper.writeValueAsString(netMsg);
                     TextMessage msg = new TextMessage(str);
                     session.sendMessage(msg);
                     break;
                 }
                 */
                Map<String, Integer> sessionList = this.gmRepo.getUserSessions(gameId);
        
                GameManager gm = this.gmRepo.getGM(gameId);

                int currTurn = gm.getGameState().getTurn();
                int userTurnNum = sessionList.get(session.getId());

                if(currTurn != userTurnNum) {
                     //NetworkMessage netMsg = new NetworkMessage();
                    mapper = new ObjectMapper();
                    String msgType = "moveUpdateError";
                    JsonNode tmp = mapper.createObjectNode();//this.schemaRoot.get(msgType);
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "this user cannot make a move yet");
                    //netMsg.setMessageType(msgType);
                    //netMsg.setPayload(respPayload);
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }

                gm.sendMove(move);
                GameState game = gm.getGameState();

                //NetworkMessage netMsg = new NetworkMessage();
                String msgType = "stateUpdateResponse";
                JsonNode tmp = mapper.createObjectNode();//this.schemaRoot.get(msgType);
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("gameId", gameId);
                respPayload.set("gameState", mapper.valueToTree(game));
                //netMsg.setMessageType(msgType);
                //netMsg.setPayload(respPayload);
                outer = mapper.createObjectNode().set(msgType, respPayload);

                //GameCreatedResponse resp = new GameCreatedResponse(gameId, game);
                //WebSockServerMessage srvMsg = new WebSockServerMessage(null, resp, null);


                String str = mapper.writeValueAsString(outer);
                TextMessage msg = new TextMessage(str);
        
                for(String user : sessionList.keySet())
                    this.webSocketSessions.get(user).sendMessage(msg);
                break;
            default:
                mapper = new ObjectMapper();
                //netMsg = new NetworkMessage();
                msgType = "badRequestError";
                JsonNode error = mapper.createObjectNode();//schemaRoot.get(msgType);
                ObjectNode errorObj = error.deepCopy();
                errorObj.put("message", "bad request: don't recognize message type");
                outer = mapper.createObjectNode().set(msgType, errorObj);
                //netMsg = new NetworkMessage();
                //netMsg.setMessageType(msgType);
                //netMsg.setPayload(errorObj);
                str = mapper.writeValueAsString(outer);
                msg = new TextMessage(str);
                session.sendMessage(msg);
        }
    }
}
