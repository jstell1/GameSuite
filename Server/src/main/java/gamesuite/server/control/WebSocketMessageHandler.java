// Program to eastablish the socket connection

package gamesuite.server.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import gamesuite.core.control.GameManagerImpl;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import gamesuite.core.network.JsonSchemaValidator;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();
    //private Map<String, 
    private ServerGameRepo gmRepo;
    private InputStream schemaStream;
    private JsonNode schemaRoot;

    @Autowired
    public WebSocketMessageHandler(ServerGameRepo gmRepo) {
        this.gmRepo = gmRepo;
        this.schemaStream = JsonSchemaValidator.class.getClassLoader().getResourceAsStream("schema.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.schemaRoot = mapper.readTree(schemaStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public WebSocketMessageHandler() {
         this.schemaStream = JsonSchemaValidator.class.getClassLoader().getResourceAsStream("schema.json");
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.schemaRoot = mapper.readTree(schemaStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    public void setGmRepo(ServerGameRepo gmRepo) {
        this.gmRepo = gmRepo;
    }

	public boolean hasSocket(String sessionId) {
        return this.webSocketSessions.containsKey(sessionId);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        synchronized(session) {

            super.afterConnectionEstablished(session);
            System.out.println(session.getId() + " Connected");
            String msgType = "sessionConnectedResponse";
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode inner = mapper.createObjectNode();
            inner.put("sessionId", session.getId());
            webSocketSessions.put(session.getId(), session);
            sendMessage(msgType, inner, session);
            System.out.println("Session connected: " + session.getId());
            System.out.println("Active Sessions: " + webSocketSessions.size());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("CloseStatus: " + status.toString());
        synchronized(session) {

            super.afterConnectionClosed(session, status);
            System.out.println(session.getId() + " DisConnected");
            String gameId = null;
            //if(status.equals(CloseStatus.NORMAL)) {
            //if(this.gmRepo.userSessions.containsKey(session.getId())) {
                gameId = this.gmRepo.getUserSessionGame(session.getId());

               // GameManager gm = this.gmRepo.getGM(gameId);

            //}

                GameState game = this.gmRepo.removePlayer(session.getId());
                webSocketSessions.remove(session.getId());
                if(gameId != null && this.gmRepo.containsGame(gameId) && game.isGameOver()) {
                    notifyGameOver(game, gameId);
                }
                
                System.out.println("Active sessions: " + webSocketSessions.size());
                System.out.println("NumGames: " + this.gmRepo.getNumGames());
            //}
        }
    }

    public void notifyPlayerJoined(String gameId, String msg) {
        Map<String, Integer> userSessions = this.gmRepo.getGameUserMap(gameId);
        for (String sessionId : userSessions.keySet()) {
            try {
                WebSocketSession s = this.webSocketSessions.get(sessionId);
                s.sendMessage(new TextMessage(msg));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void notifyGameOver(GameState game, String gameId) {
        
        GameManagerImpl gm = this.gmRepo.getGM(gameId);
        synchronized(gm) {
            ObjectMapper mapper = new ObjectMapper();
            String msgType = "stateUpdateResponse";
            ObjectNode outer = mapper.createObjectNode();
            ObjectNode inner = mapper.createObjectNode();
            inner.put("gameId", gameId);
            inner.set("gameState", mapper.valueToTree(game));
            outer = mapper.createObjectNode().set(msgType, inner);
            try {
                String str = mapper.writeValueAsString(outer);
                TextMessage msg = new TextMessage(str);
        
                for (String sessionId : this.gmRepo.getGameUsers(gameId)) {
                    
                    WebSocketSession s = this.webSocketSessions.get(sessionId);
                    
                    if(s.isOpen()) {
                        try {
                            
                            s.sendMessage(msg);
                        } catch (Exception e) {
                            System.out.println("no socket connection");
                        }
                    }
                    
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        
        synchronized(session) {
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
                inner.put("message", "don't recognize message type");
                sendMessage("badRequestError", inner, session);
                return;
            }
    
            if(!JsonSchemaValidator.isValid(netWorkMsg)) {
                ObjectNode inner = mapper.createObjectNode();
                inner.put("message", "don't recognize message type");
                sendMessage("badRequestError", inner, session);
                return;
            } 
    
            type = outer.fieldNames().next();
            payload = outer.get(type).deepCopy();

            switch(type) {
                case "createGameRequest":
                    try {
                        createGame(session, payload);
                    } catch (Exception e) {
                         mapper = new ObjectMapper();
                        String msgType = "serverError";
                        ObjectNode respPayload = mapper.createObjectNode();
                        respPayload.put("message", "Error processing createGameRequest");
                        sendMessage(msgType, respPayload, session);
                    }
                    
                    break;
                case "joinGameRequest":
                    try {
                        joinGame(session, payload);
                    } catch (Exception e) {
                        mapper = new ObjectMapper();
                        String msgType = "serverError";
                        ObjectNode respPayload = mapper.createObjectNode();
                        respPayload.put("message", "Error processing createGameRequest");
                        sendMessage(msgType, respPayload, session);
                    }
                   
                    break;
                case "moveRequest":
                    try {
                        makeMove(session, payload);
                    } catch (Exception e) {
                         mapper = new ObjectMapper();
                        String msgType = "serverError";
                        ObjectNode respPayload = mapper.createObjectNode();
                        respPayload.put("message", "Error processing createGameRequest");
                        sendMessage(msgType, respPayload, session);
                    }
                    break;
                default:
                    mapper = new ObjectMapper();
                    String msgType = "badRequestError";
                    JsonNode error = mapper.createObjectNode();
                    ObjectNode errorObj = error.deepCopy();
                    errorObj.put("message", "bad request: don't recognize message type");
                    sendMessage(msgType, errorObj, session);
            }
        }
    }

    private void createGame(WebSocketSession session, ObjectNode payload) throws Exception {
         System.out.println("recieved");
        ObjectMapper mapper = new ObjectMapper();
        if(this.gmRepo.hasUserSession(session.getId())) {
            mapper = new ObjectMapper();
            String msgType = "gameNotCreatedError";
            ObjectNode inner = mapper.createObjectNode();
            inner.put("message", "This user session is already in a game");
            sendMessage(msgType, inner, session);
            return;//break;
        }
        System.out.println("passed the check");
        String name = payload.get("name").asText();

        try {
            mapper = new ObjectMapper();
            Player player1 = new Player(name, 0);
            GameBoard board = new GameBoard(8);
            String gameId = this.gmRepo.createGame(player1, board, session.getId());
            GameState game = this.gmRepo.getGameView(gameId);
            this.gmRepo.getGM(gameId);
            String msgType = "gameCreatedResponse";
            ObjectNode respPayload = mapper.createObjectNode();
            respPayload.put("gameId", gameId);
            respPayload.set("gameState", mapper.valueToTree(game));
            sendMessage(msgType, respPayload, session);
            System.out.println("sent");
            
        } catch (Exception e) {
            mapper = new ObjectMapper();
            String msgType = "serverError";
            ObjectNode respPayload = mapper.createObjectNode();
            respPayload.put("message", "Error processing createGameRequest");
            sendMessage(msgType, respPayload, session);
        }
    }

    private void joinGame(WebSocketSession session, ObjectNode payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
         if(this.gmRepo.hasUserSession(session.getId())) {
            mapper = new ObjectMapper();
            String msgType = "gameNotJoinedError";
            JsonNode tmp = mapper.createObjectNode();
            ObjectNode respPayload = tmp.deepCopy();
            respPayload.put("message", "This user session is already in a game");
            sendMessage(msgType, respPayload, session);
            return;//break;
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
            sendMessage(msgType, respPayload, session);
            return;
        }
        GameManagerImpl gm = this.gmRepo.getGM(gameId); 
        
        synchronized(gm) {
        
            if(gm.isGameReady()) {
                mapper = new ObjectMapper();
                String msgType = "gameNotJoinedError";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("message", "game is full");
                sendMessage(msgType, respPayload, session);
                return;
            }

            try {
                
                Player p2 = new Player(player, 0);
                GameBoard board = this.gmRepo.joinGame(p2, gameId);
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
                sendMessage(msgType, respPayload, session);
                ObjectNode outer = mapper.createObjectNode().set(msgType, respPayload);
                String str = mapper.writeValueAsString(outer);
                notifyPlayerJoined(gameId, str);
            
                
            } catch (Exception e) {
                mapper = new ObjectMapper();
                String msgType = "serverError";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("message", "Error processing createGameRequest");
                sendMessage(msgType, respPayload, session);
            }
        }
    }

    private void makeMove(WebSocketSession session, ObjectNode payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Move move = null;
        move = mapper.treeToValue(payload.get("move"), Move.class);
        String gameId = payload.get("gameId").asText();

        if(!this.gmRepo.containsGame(gameId)) {
            String msgType = "badRequestError";
            JsonNode tmp = mapper.createObjectNode();
            ObjectNode respPayload = tmp.deepCopy();
            respPayload.put("message", "game does not exist");
            sendMessage(msgType, respPayload, session);
            return;
        }
        GameManagerImpl gm = this.gmRepo.getGM(gameId);
        synchronized(gm) {

            if(!this.gmRepo.rightPlayer(gameId, session.getId())) {
                String msgType = "moveUpdateError";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("message", "not in game or not your turn");
                sendMessage(msgType, respPayload, session);
                return;
            }

            if(!this.gmRepo.getGameView(gameId).isBoardInit()) {
                String msgType = "moveUpdateError";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("message", "player 2 has not joined the game");
                sendMessage(msgType, respPayload, session);
                return;
            }
            Map<String, Integer> sessionList = this.gmRepo.getGameUserMap(gameId);
    
            //gm = this.gmRepo.getGM(gameId);

            int currTurn = gm.getGameState().getTurn();
            int userTurnNum = sessionList.get(session.getId());

            if(currTurn != userTurnNum) {
                mapper = new ObjectMapper();
                String msgType = "moveUpdateError";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("message", "this user cannot make a move yet");
                sendMessage(msgType, respPayload, session);
                return;
            }

            gm.sendMove(move);
            GameState game = gm.getGameState();
            String msgType = "stateUpdateResponse";
            JsonNode tmp = mapper.createObjectNode();
            ObjectNode respPayload = tmp.deepCopy();
            respPayload.put("gameId", gameId);
            respPayload.set("gameState", mapper.valueToTree(game));
            sendMessage(msgType, respPayload, session);
            ObjectNode outer = mapper.createObjectNode().set(msgType, respPayload);


            String str = mapper.writeValueAsString(outer);
            TextMessage msg = new TextMessage(str);
    
            for(String user : sessionList.keySet())
                this.webSocketSessions.get(user).sendMessage(msg);
        }
    }

    private void sendMessage(String msgType, ObjectNode payload, WebSocketSession session) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outer = mapper.createObjectNode().set(msgType, payload);
        String str = mapper.writeValueAsString(outer);
        TextMessage msg = new TextMessage(str);
        session.sendMessage(msg);
    }
}
