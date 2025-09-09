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
import gamesuite.core.network.JsonSchemaValidator;

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
        String msgType = "sessionConnectedResponse";
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode inner = mapper.createObjectNode();
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
        
        netWorkMsg = message.getPayload().toString();
        try {
            outer = (ObjectNode) mapper.readTree(netWorkMsg);
        } catch (Exception e) {
            ObjectNode inner = mapper.createObjectNode();
            ObjectNode err = mapper.createObjectNode();
            inner.put("message", "don't recognize message type");
            err.set("badRequestError", inner);
            String str = mapper.writeValueAsString(err);
            session.sendMessage(new TextMessage(str));
            return;
        }

        if(!JsonSchemaValidator.isValid(netWorkMsg)) {
            ObjectNode inner = mapper.createObjectNode();
            ObjectNode err = mapper.createObjectNode();
            inner.put("message", "don't recognize message type");
            err.set("badRequestError", inner);
            String str = mapper.writeValueAsString(err);
            session.sendMessage(new TextMessage(str));
            return;
        } 

        type = outer.fieldNames().next();
        payload = outer.get(type).deepCopy();
        switch(type) {
            case "createGameRequest":
                System.out.println("recieved");

                if(this.gmRepo.userSessions.contains(session.getId())) {
                    mapper = new ObjectMapper();
                    String msgType = "gameNotCreatedError";
                    ObjectNode inner = mapper.createObjectNode();
                    inner.put("message", "This user session is already in a game");
                    ObjectNode respPayload = mapper.createObjectNode().set(msgType, inner);
                    String str = mapper.writeValueAsString(respPayload);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }
                System.out.println("passed the check");
                String name = payload.get("name").asText();

                try {
                    mapper = new ObjectMapper();
                    Player player1 = new Player(name, 0);
                    GameBoard board = new GameBoard(8);
                    String gameId = this.gmRepo.createGame(player1, board, session.getId());
                    GameState game = this.gmRepo.getGameView(gameId);
                    GameManager gm = new GameManager(board, player1);
                    this.gmRepo.setGame(gameId, gm);
                    String msgType = "gameCreatedResponse";
                    ObjectNode respPayload = mapper.createObjectNode();
                    outer = mapper.createObjectNode();
                    respPayload.put("gameId", gameId);
                    respPayload.set("gameState", mapper.valueToTree(game));
                    outer.set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    System.out.println("sent");
                    
                } catch (Exception e) {
                    mapper = new ObjectMapper();
                    String msgType = "serverError";
                    ObjectNode respPayload = mapper.createObjectNode();
                    respPayload.put("message", "Error processing createGameRequest");
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                }
                break;
            case "joinGameRequest" :

                if(this.gmRepo.userSessions.contains(session.getId())) {
                    mapper = new ObjectMapper();
                    String msgType = "gameNotJoinedError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "This user session is already in a game");
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }

                String player = payload.get("name").asText();
                String gameId = payload.get("gameId").asText();
                System.out.println(player + ",," + gameId);

                if(!gmRepo.containsGame(gameId)) {
                    mapper = new ObjectMapper();
                    String msgType = "gameNotJoinedError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "gameId does not exist");
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }

                if(this.gmRepo.games.get(gameId).getGameState().isBoardInit()) {
                    mapper = new ObjectMapper();
                    String msgType = "gameNotJoinedError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "game is full");
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
                        this.gmRepo.addWebSocketToGame(gameId, session.getId());

                        String msgType = "gameReadyResponse";
                        JsonNode tmp = mapper.createObjectNode();
                        ObjectNode respPayload = tmp.deepCopy();
                        respPayload.set("board", boardJson);
                        respPayload.put("gameId", gameId);
                        respPayload.set("gameState", mapper.valueToTree(game));

                        outer = mapper.createObjectNode().set(msgType, respPayload);
                        String str = mapper.writeValueAsString(outer);
                        notifyPlayerJoined(gameId, str);
                    }
                } catch (Exception e) {
                    mapper = new ObjectMapper();
                    String msgType = "serverError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "Error processing createGameRequest");
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                }
            
                break;
            case "moveRequest":

                mapper = new ObjectMapper();
                Move move = null;
                move = mapper.treeToValue(payload.get("move"), Move.class);//req.getMove();
                gameId = payload.get("gameId").asText();


                if(!this.gmRepo.rightPlayer(gameId, session.getId())) {
                    String msgType = "moveUpdateError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "not in game or not your turn");
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }

                if(!this.gmRepo.games.containsKey(gameId)) {
                    String msgType = "badRequestError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "game does not exist");
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }

                if(!this.gmRepo.getGameView(gameId).isBoardInit()) {
                    String msgType = "moveUpdateError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "player 2 has not joined the game");
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }
                Map<String, Integer> sessionList = this.gmRepo.getUserSessions(gameId);
        
                GameManager gm = this.gmRepo.getGM(gameId);

                int currTurn = gm.getGameState().getTurn();
                int userTurnNum = sessionList.get(session.getId());

                if(currTurn != userTurnNum) {
                    mapper = new ObjectMapper();
                    String msgType = "moveUpdateError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "this user cannot make a move yet");
                    outer = mapper.createObjectNode().set(msgType, respPayload);
                    String str = mapper.writeValueAsString(outer);
                    TextMessage msg = new TextMessage(str);
                    session.sendMessage(msg);
                    break;
                }

                gm.sendMove(move);
                GameState game = gm.getGameState();
                String msgType = "stateUpdateResponse";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("gameId", gameId);
                respPayload.set("gameState", mapper.valueToTree(game));
                outer = mapper.createObjectNode().set(msgType, respPayload);


                String str = mapper.writeValueAsString(outer);
                TextMessage msg = new TextMessage(str);
        
                for(String user : sessionList.keySet())
                    this.webSocketSessions.get(user).sendMessage(msg);
                break;
            default:
                mapper = new ObjectMapper();
                msgType = "badRequestError";
                JsonNode error = mapper.createObjectNode();
                ObjectNode errorObj = error.deepCopy();
                errorObj.put("message", "bad request: don't recognize message type");
                outer = mapper.createObjectNode().set(msgType, errorObj);
                str = mapper.writeValueAsString(outer);
                msg = new TextMessage(str);
                session.sendMessage(msg);
        }
    }
}
