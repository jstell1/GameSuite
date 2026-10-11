
package gamesuite.server.control;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
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
import gamesuite.core.control.GameManager;
import gamesuite.core.network.JsonSchemaValidator;
import gamesuite.server.model.ServerGameRepo;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();
    //private Map<String, 
    private ServerGameRepo gmRepo;
    //private InputStream schemaStream;
    //private JsonNode schemaRoot;

    @Autowired
    public WebSocketMessageHandler(ServerGameRepo gmRepo) {
        this.gmRepo = gmRepo;
        //this.schemaStream = JsonSchemaValidator.class.getClassLoader().getResourceAsStream("schema.json");
        // ObjectMapper mapper = new ObjectMapper();
        // try {
        //     this.schemaRoot = mapper.readTree(schemaStream);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

    }

    public WebSocketMessageHandler() {
        //  this.schemaStream = JsonSchemaValidator.class.getClassLoader().getResourceAsStream("schema.json");
        // ObjectMapper mapper = new ObjectMapper();
        // try {
        //     this.schemaRoot = mapper.readTree(schemaStream);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
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

               GameManager gm = this.gmRepo.getGM(gameId);

            //}
            synchronized(gm) {

                if(gm.getNumMappedPlayers() > 1) {
                    GameManager game = this.gmRepo.removePlayer(session.getId());
                    webSocketSessions.remove(session.getId());
                    if(gm.gameOver()) {
                        notifyGameOver(gm.getGameStateJson(), gameId);
                    }
                } else {
                    webSocketSessions.remove(session.getId());
                    this.gmRepo.removePlayer(session.getId());
                    this.gmRepo.closeGame(gameId);
                }




                
            }

                System.out.println("Active sessions: " + webSocketSessions.size());
                System.out.println("NumGames: " + this.gmRepo.getNumGames());
                //System.out.println("mappedGames: " + this.gmRepo.get)
            //}
        }
    }

    //private 

    public void notifyPlayerJoined(String gameId, String msg) throws IOException {
        //Map<String, Integer> userSessions = this.gmRepo.getGameUserMap(gameId);
        List<String> userSessions = this.gmRepo.getGameSessions(gameId);//getGameUserMap(gameId);
        System.out.println(msg.length());
        for (String sessionId : userSessions) {
           // try {
                WebSocketSession s = this.webSocketSessions.get(sessionId);
                s.sendMessage(new TextMessage(msg));
           // } catch (IOException e) {
          //      e.printStackTrace();
          //  }
        }
    }

    public void notifyGameOver(JsonNode game, String gameId) {
        
        GameManager gm = this.gmRepo.getGM(gameId);
        synchronized(gm) {
            ObjectMapper mapper = new ObjectMapper();
            String msgType = "stateUpdateResponse";
            ObjectNode outer = mapper.createObjectNode();
            ObjectNode inner = mapper.createObjectNode();
            inner.put("gameId", gameId);
            inner.set("gameState", game);
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
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {

        
        synchronized(session) {
        
            try {
                super.handleMessage(session, message);
            } catch (Exception e) {
                // TODO: handle exception
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode inner = mapper.createObjectNode();
                inner.put("message", "issue with socket");
                sendMessage("badRequestError", inner, session);
                return;

            }
            
            ObjectMapper mapper = new ObjectMapper();
            String netWorkMsg = null;
            ObjectNode outer = null;
            ObjectNode payload = null;
            String type = "";
            
            netWorkMsg = message.getPayload().toString();
  
            try {
                outer = (ObjectNode) mapper.readTree(netWorkMsg);
                if(!JsonSchemaValidator.isValid(netWorkMsg)) {
                    ObjectNode inner = mapper.createObjectNode();
                    inner.put("message", "message is not correctly formed");
                    sendMessage("badRequestError", inner, session);
                    return;
                } 
            } catch (JsonProcessingException e) {
            // TODO: handle exception
                mapper = new ObjectMapper();
                ObjectNode inner = mapper.createObjectNode();
                inner.put("message", "json processing failed: invalid json");
                sendMessage("badRequestError", inner, session);
                return;
            }
    
            type = outer.fieldNames().next();
            payload = outer.get(type).deepCopy();

            switch(type) {
                case "createGameRequest":
                    createGame(session, payload);
                    break;
                case "joinGameRequest":
                    joinGame(session, payload);
                    break;
                case "moveRequest":
                    makeMove(session, payload);
                    break;
                case "gamesListRequest":
                    sendGamesList(session, payload);
                    break;
                case "activeGamesRequest":
                    sendActiveGamesList(session, payload);
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

    
    private void sendActiveGamesList(WebSocketSession session, ObjectNode payload) {
        ObjectMapper mapper = new ObjectMapper();
       // try {
            String game = payload.get("game").asText();

            if(!this.gmRepo.validGame(game, game)) {
                 mapper = new ObjectMapper();
                String msgType = "badRequestError";
                ObjectNode respPayload = mapper.createObjectNode();
                respPayload.put("message", "game does not exist");
                sendMessage(msgType, respPayload, session);
                return;
            }

            String[] list = this.gmRepo.getJoinableGames(game);
            String msgType = "activeGamesResponse";
            ObjectNode respPayload = mapper.createObjectNode();
            respPayload.set("gamesList", mapper.valueToTree(list));
            sendMessage(msgType, respPayload, session);
        // } catch (Exception e) {
        //     mapper = new ObjectMapper();
        //     String msgType = "serverError";
        //     ObjectNode respPayload = mapper.createObjectNode();
        //     respPayload.put("message", "Error processing createGameRequest");
        //     sendMessage(msgType, respPayload, session);
        // }
    }
    
    private void sendGamesList(WebSocketSession session, ObjectNode payload) {
        Map<String, ArrayList<String>> list = this.gmRepo.getGamesList();
        ObjectMapper mapper = new ObjectMapper();
       // try {
            String msgType = "gamesListResponse";
            ObjectNode respPayload = mapper.createObjectNode();
            respPayload.set("games", mapper.valueToTree(list));
            sendMessage(msgType, respPayload, session);
        // } catch(Exception e) {
        //     mapper = new ObjectMapper();
        //     String msgType = "serverError";
        //     ObjectNode respPayload = mapper.createObjectNode();
        //     respPayload.put("message", "Error processing createGameRequest");
        //     sendMessage(msgType, respPayload, session);
        // }
    }

    private void createGame(WebSocketSession session, ObjectNode payload) {
         System.out.println("recieved");
        ObjectMapper mapper = new ObjectMapper();
        if(this.gmRepo.hasUserSession(session.getId())) {
            mapper = new ObjectMapper();
            String msgType = "gameNotCreatedError";
            ObjectNode inner = mapper.createObjectNode();
            inner.put("message", "This user session is already in a game");
            sendMessage(msgType, inner, session);
            return;
        }

        
        System.out.println("passed the check");
        String group = payload.get("game").asText();
        String gameNm = payload.get("subGame").asText();
        String name = payload.get("name").asText();

        if(!this.gmRepo.validGame(group, gameNm)) {
             mapper = new ObjectMapper();
            String msgType = "gameNotCreatedError";
            ObjectNode respPayload = mapper.createObjectNode();
            respPayload.put("message", "invalid game request");
            sendMessage(msgType, respPayload, session);
            return;
        }
        try {
            mapper = new ObjectMapper();
            //Player player1 = new Player(name, 0);
            //GameBoard board = new GameBoard(8);
            String gameId = this.gmRepo.createGame(gameNm, group, name, session.getId());
                JsonNode game = this.gmRepo.getGameView(gameId);
            this.gmRepo.getGM(gameId);
            String msgType = "gameCreatedResponse";
            ObjectNode respPayload = mapper.createObjectNode();
            respPayload.put("gameId", gameId);
            respPayload.set("gameState", game);
            sendMessage(msgType, respPayload, session);
            System.out.println("sent");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            mapper = new ObjectMapper();
            String msgType = "serverError";
            ObjectNode respPayload = mapper.createObjectNode();
            respPayload.put("message", "Error processing createGameRequest");
            sendMessage(msgType, respPayload, session);
        }
    }

    private void joinGame(WebSocketSession session, ObjectNode payload) {
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

        
        GameManager gm = this.gmRepo.getGM(gameId); 
        
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

            JsonNode boardJson = null;
            try {
                
                //Player p2 = new Player(player, 0);
                boardJson = this.gmRepo.joinGame(player, session.getId(), gameId);
                
                
            } catch (Exception e) {
                mapper = new ObjectMapper();
                String msgType = "serverError";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("message", "Error processing joinGameRequest");
                sendMessage(msgType, respPayload, session);
                return;
            }
            mapper = new ObjectMapper();
            JsonNode game = this.gmRepo.getGM(gameId).getGameStateJson();
            this.gmRepo.addWebSocketToGame(gameId, session.getId());

            String msgType = "gameReadyResponse";
            JsonNode tmp = mapper.createObjectNode();
            ObjectNode respPayload = tmp.deepCopy();
            respPayload.set("board", boardJson);
            respPayload.put("gameId", gameId);
            respPayload.set("gameState", game);
            //sendMessage(msgType, respPayload, session);
            ObjectNode outer = mapper.createObjectNode().set(msgType, respPayload);
            String str;
            try {
                str = mapper.writeValueAsString(outer);
                notifyPlayerJoined(gameId, str);
            } catch (Exception e) {
               //  TODO: handle exception
                e.printStackTrace();
               
            }
        }
    }

    private void makeMove(WebSocketSession session, ObjectNode payload) {
        ObjectMapper mapper = new ObjectMapper();
        //Move move = null;
       // move = mapper.treeToValue(payload.get("move"), Move.class);
        ObjectNode move = (ObjectNode) payload.get("move");
        String gameId = payload.get("gameId").asText();

        if(!this.gmRepo.containsGame(gameId)) {
            String msgType = "badRequestError";
            JsonNode tmp = mapper.createObjectNode();
            ObjectNode respPayload = tmp.deepCopy();
            respPayload.put("message", "game does not exist");
            sendMessage(msgType, respPayload, session);
            return;
        }
        GameManager gm = this.gmRepo.getGM(gameId);
        synchronized(gm) {

            
            try {
                
                if(!this.gmRepo.rightPlayer(gameId, session.getId())) {
                    String msgType = "moveUpdateError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "not in game or not your turn");
                    sendMessage(msgType, respPayload, session);
                    return;
                }
    
                if(!this.gmRepo.getGM(gameId).isGameReady()) {
                    String msgType = "moveUpdateError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "player 2 has not joined the game");
                    sendMessage(msgType, respPayload, session);
                    return;
                }
               // Map<String, Integer> sessionList = this.gmRepo.getGameUserMap(gameId);
               List<String> sessionList = this.gmRepo.getGameSessions(gameId);//getGameUserMap(gameId);
        
                //gm = this.gmRepo.getGM(gameId);
    
                //this is game engine responsibility!!! not supposed to be here!
                //int currTurn = gm.getTurn();
                //int userTurnNum = sessionList.get(session.getId());
    
                /*
                if(currTurn != userTurnNum) {
                    mapper = new ObjectMapper();
                    String msgType = "moveUpdateError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "this user cannot make a move yet");
                    sendMessage(msgType, respPayload, session);
                    return;
                }
                    */
                   boolean success = gm.sendMove(move, session.getId());
       
                   //if(success) {
       
                       JsonNode game = gm.getGameStateJson();
                       String msgType = "stateUpdateResponse";
                       JsonNode tmp = mapper.createObjectNode();
                       ObjectNode respPayload = tmp.deepCopy();
                       respPayload.put("gameId", gameId);
                       respPayload.set("gameState", game);
                       sendMessage(msgType, respPayload, session);
                       ObjectNode outer = mapper.createObjectNode().set(msgType, respPayload);
           
                      
                           
                        String str = mapper.writeValueAsString(outer);
                        TextMessage msg = new TextMessage(str);
                
                        for(String user : sessionList)
                            this.webSocketSessions.get(user).sendMessage(msg);
        
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                         mapper = new ObjectMapper();
                        String msgType = "moveUpdateError";
                        JsonNode tmp = mapper.createObjectNode();
                        ObjectNode respPayload = tmp.deepCopy();
                        respPayload.put("message", "problem processing player move");
                        sendMessage(msgType, respPayload, session);
                    }
                    /* 
                } else {
                     mapper = new ObjectMapper();
                    String msgType = "moveUpdateError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "move failed validation");
                    sendMessage(msgType, respPayload, session);
                    return;
                }
                    */
        }
    }

    private void sendMessage(String msgType, ObjectNode payload, WebSocketSession session) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode outer = mapper.createObjectNode().set(msgType, payload);
        try {
            String str = mapper.writeValueAsString(outer);
            TextMessage msg = new TextMessage(str);
            session.sendMessage(msg);
        } catch (JsonProcessingException e) {
            // TODO: handle exception
            try {
                
                session.sendMessage(new TextMessage("Server failed to build response message"));
            } catch (Exception e2) {
                // TODO: handle exception
                System.out.println("Server is having issues with jsonParsing");
            }

        } catch(IOException e) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
                
            } catch (Exception e1) {
                // TODO: handle exception
                System.out.println("Server failure, could not close socket: " + e1.getMessage());
            }
        }
    }
}
