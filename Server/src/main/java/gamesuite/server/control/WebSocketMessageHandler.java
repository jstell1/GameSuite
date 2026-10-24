
package gamesuite.server.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
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
import gamesuite.core.model.PromoteRequester;
import gamesuite.core.utils.JsonSchemaValidator;
import gamesuite.server.model.ServerGameRepo;

@Component
public class WebSocketMessageHandler extends TextWebSocketHandler implements PromoteRequester {

    private Map<String, WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();
    private ServerGameRepo gmRepo;
    private static final Logger log = LoggerFactory.getLogger(WebSocketMessageHandler.class);

    @Autowired
    public WebSocketMessageHandler(ServerGameRepo gmRepo) {
        this.gmRepo = gmRepo;
        this.gmRepo.setPromoteRequester(this);

    }

    public WebSocketMessageHandler() {
	}

    public void setGmRepo(ServerGameRepo gmRepo) {
        this.gmRepo = gmRepo;
        this.gmRepo.setPromoteRequester(this);
    }

	public boolean hasSocket(String sessionId) {
        return this.webSocketSessions.containsKey(sessionId);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        synchronized(session) {

            super.afterConnectionEstablished(session);

            String msgType = "sessionConnectedResponse";
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode inner = mapper.createObjectNode();
            inner.put("sessionId", session.getId());
            webSocketSessions.put(session.getId(), session);
            sendMessage(msgType, inner, session);
            log.info("Websocket connected: session={} activeSessions={}", session.getId(), webSocketSessions.size());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        synchronized(session) {
            String id = session.getId();
            super.afterConnectionClosed(session, status);
            try {

                String gameId = null;
                    gameId = this.gmRepo.getUserSessionGame(id);
                    MDC.put("gameId", gameId);
                    MDC.put("sessionId", session.getId());
                   GameManager gm = this.gmRepo.getGM(gameId);

                synchronized(gm) {

                    if(gm.getNumMappedPlayers() > 1) {
                        GameManager game = this.gmRepo.removePlayer(id);
                        this.webSocketSessions.remove(id);
                        if(gm.gameOver()) {
                            notifyGameOver(gm.getGameStateJson(), gameId);
                        }
                    } else {
                        this.webSocketSessions.remove(id);
                        this.gmRepo.removePlayer(id);
                        this.gmRepo.closeGame(gameId);
                    }
                }

                log.info("Websocket session closed: activeSessions={} closeStatus={}", this.webSocketSessions.size(), status.toString());

            } finally {
                MDC.clear();
            }
        }
    }


    public void notifyPlayerJoined(String gameId, String msg) throws IOException {
        List<String> userSessions = this.gmRepo.getGameSessions(gameId);
        log.debug("Notifying of player joined: msgLength={}", msg.length());
        for (String sessionId : userSessions) {
        
                WebSocketSession s = this.webSocketSessions.get(sessionId);
                synchronized (s) {
                    s.sendMessage(new TextMessage(msg));
                }
        }
    }

    public void notifyGameOver(JsonNode game, String gameId) throws IOException {
        
        GameManager gm = this.gmRepo.getGM(gameId);
        synchronized(gm) {
            ObjectMapper mapper = new ObjectMapper();
            String msgType = "stateUpdateResponse";
            ObjectNode outer = mapper.createObjectNode();
            ObjectNode inner = mapper.createObjectNode();
            inner.put("gameId", gameId);
            inner.set("gameState", game);
            outer = mapper.createObjectNode().set(msgType, inner);
          //  try {
                String str = mapper.writeValueAsString(outer);
                TextMessage msg = new TextMessage(str);
        
                for (String sessionId : this.gmRepo.getGameUsers(gameId)) {
                    
                    WebSocketSession s = this.webSocketSessions.get(sessionId);
                    
                 //   if(s.isOpen()) {
                       // try {
                            synchronized (s) {
                                s.sendMessage(msg);
                            }
                    //    } catch (Exception e) {
                           // System.out.println("no socket connection");
                    //    }
                //    }
                    
                }
       //     } catch (Exception e) {
                // TODO Auto-generated catch block
         //       e.printStackTrace();
         //   }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {

        
            try {
                MDC.put("sessionId", session.getId());
                synchronized(session) {
                    try {
                        super.handleMessage(session, message);
                    } catch (Exception e) {
                        // TODO: handle exception

                        ObjectMapper mapper = new ObjectMapper();
                        ObjectNode inner = mapper.createObjectNode();
                        inner.put("message", "issue with socket");
                        sendMessage("badRequestError", inner, session);
                        log.error("Issue with socket", e);
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
                            log.warn("Invalid json sent: networkMsg={}", netWorkMsg);
                            return;
                        }
                    } catch (JsonProcessingException e) {
                    // TODO: handle exception
                        mapper = new ObjectMapper();
                        ObjectNode inner = mapper.createObjectNode();
                        inner.put("message", "json processing failed: invalid json");
                        sendMessage("badRequestError", inner, session);
                        log.error("error processing json message", e);
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
                        case "promoteChoice":
                            sendPromotion(session, payload);
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
            } finally {
                MDC.clear();
            }
    }

    private void sendPromotion(WebSocketSession session, ObjectNode payload) {
        String type = payload.get("choice").asText();
        GameManager gm = this.gmRepo.getGM(payload.get("gameId").asText());
        String id = session.getId();
        synchronized(gm) {
            try {
                gm.sendPromotionChoice(type);
            } catch (JsonProcessingException | IllegalArgumentException e) {
                // TODO Auto-generated catch block
             //   e.printStackTrace();
                ObjectMapper mapper = new ObjectMapper();
                  mapper = new ObjectMapper();
                String msgType = "serverError";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("message", "Error processing promotion choice");
                sendMessage(msgType, respPayload, session);
                log.error("Error processing promotion choice",  e);
            } catch (Exception e) {
                // TODO Auto-generated catch block
             //   e.printStackTrace();
                ObjectMapper mapper = new ObjectMapper();
                  mapper = new ObjectMapper();
                String msgType = "serverError";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("message", "Error processing promotion choice");
                sendMessage(msgType, respPayload, session);
                log.error("Error processing promotion choice",  e);
            }
        }
    } 

    private void sendActiveGamesList(WebSocketSession session, ObjectNode payload) {
        ObjectMapper mapper = new ObjectMapper();
        String id = session.getId();
            String game = payload.get("game").asText();

            if(!this.gmRepo.validGame(game, game)) {
                 mapper = new ObjectMapper();
                String msgType = "badRequestError";
                ObjectNode respPayload = mapper.createObjectNode();
                respPayload.put("message", "game does not exist");
                sendMessage(msgType, respPayload, session);
                log.warn("requesting join list for non-existing game: gameChoice={}", game);
                return;
            }

            String[] list = this.gmRepo.getJoinableGames(game);
            String msgType = "activeGamesResponse";
            ObjectNode respPayload = mapper.createObjectNode();
            respPayload.set("gamesList", mapper.valueToTree(list));
            sendMessage(msgType, respPayload, session);
            log.info("Sent joinable games list: gameChoice={}", game);
    }
    
    private void sendGamesList(WebSocketSession session, ObjectNode payload) {
        Map<String, ArrayList<String>> list = this.gmRepo.getGamesList();
        ObjectMapper mapper = new ObjectMapper();
      
            String msgType = "gamesListResponse";
            ObjectNode respPayload = mapper.createObjectNode();
            respPayload.set("games", mapper.valueToTree(list));
            sendMessage(msgType, respPayload, session);
            log.info("Sent games list");
    }

    private void createGame(WebSocketSession session, ObjectNode payload) {
         //System.out.println("recieved");
        String id = session.getId();
        ObjectMapper mapper = new ObjectMapper();
        if(this.gmRepo.hasUserSession(session.getId())) {
            mapper = new ObjectMapper();
            String msgType = "gameNotCreatedError";
            ObjectNode inner = mapper.createObjectNode();
            inner.put("message", "This user session is already in a game");
            sendMessage(msgType, inner, session);
            log.warn("Player already created game");
            return;
        }


        String group = payload.get("game").asText();
        String gameNm = payload.get("subGame").asText();
        String name = payload.get("name").asText();

        if(!this.gmRepo.validGame(group, gameNm)) {
             mapper = new ObjectMapper();
            String msgType = "gameNotCreatedError";
            ObjectNode respPayload = mapper.createObjectNode();
            respPayload.put("message", "invalid game request");
            sendMessage(msgType, respPayload, session);
            log.warn("Invalid game creation choice: chosenGroup={} chosenGame={}", group, gameNm);
            return;
        }
        try {

            try {
                mapper = new ObjectMapper();
                String gameId = this.gmRepo.createGame(gameNm, group, name, session.getId());
                MDC.put("gameId", gameId);
                    JsonNode game = this.gmRepo.getGameView(gameId);
                //String ui = this.gmRepo.getGM(gameId).getUI();
                String msgType = "gameCreatedResponse";
                ObjectNode respPayload = mapper.createObjectNode();
                respPayload.put("gameId", gameId);
                //respPayload.put("ui", ui);
                respPayload.set("gameState", game);

                sendMessage(msgType, respPayload, session);
                log.info("Game created: chosenGroup={} chosenGame={} name={}", group, gameNm, name);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
                mapper = new ObjectMapper();
                String msgType = "serverError";
                ObjectNode respPayload = mapper.createObjectNode();
                respPayload.put("message", "Error processing createGameRequest");
                sendMessage(msgType, respPayload, session);
                log.error("Game creation error: chosenGroup={} chosenGame={} name={}", group, gameNm, name, e);
            }
        } finally {
            MDC.clear();
        }
    }

    private void joinGame(WebSocketSession session, ObjectNode payload) {
        ObjectMapper mapper = new ObjectMapper();
        String id = session.getId();
         if(this.gmRepo.hasUserSession(id)) {
            mapper = new ObjectMapper();
            String msgType = "gameNotJoinedError";
            JsonNode tmp = mapper.createObjectNode();
            ObjectNode respPayload = tmp.deepCopy();
            respPayload.put("message", "This user session is already in a game");
            sendMessage(msgType, respPayload, session);
            log.warn("Session already joined game");
            return;
        }

        String player = payload.get("name").asText();
        String gameId = payload.get("gameId").asText();

        try {
            MDC.put("gameId", gameId);
            if(!gmRepo.containsGame(gameId)) {
                mapper = new ObjectMapper();
                String msgType = "gameNotJoinedError";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("message", "gameId does not exist");
                sendMessage(msgType, respPayload, session);
                log.warn("Game does not exist");
                return;
            }


            GameManager gm = this.gmRepo.getGM(gameId);
            String ui = gm.getUI();

            synchronized(gm) {

                if(gm.isGameReady()) {
                    mapper = new ObjectMapper();
                    String msgType = "gameNotJoinedError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "game is full");
                    sendMessage(msgType, respPayload, session);
                    log.warn("Game is already full");
                    return;
                }

                JsonNode boardJson = null;
              //  try {

                    boardJson = this.gmRepo.joinGame(player, session.getId(), gameId);


              //  } catch (Exception e) {
               //     mapper = new ObjectMapper();
             //       String msgType = "serverError";
             //       JsonNode tmp = mapper.createObjectNode();
            //        ObjectNode respPayload = tmp.deepCopy();
             //       respPayload.put("message", "Error processing joinGameRequest");
             //       sendMessage(msgType, respPayload, session);
             //       return;
             //   }
                mapper = new ObjectMapper();
                JsonNode game = this.gmRepo.getGM(gameId).getGameStateJson();
                this.gmRepo.addWebSocketToGame(gameId, session.getId());

                String msgType = "gameReadyResponse";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.set("board", boardJson);
                respPayload.put("gameId", gameId);
                respPayload.put("ui", ui);
                respPayload.set("gameState", game);
                ObjectNode outer = mapper.createObjectNode().set(msgType, respPayload);
                String str;
                try {
                    str = mapper.writeValueAsString(outer);
                    notifyPlayerJoined(gameId, str);
                    log.info("Game joined");
                } catch (Exception e) {
                   //  TODO: handle exception
                   // e.printStackTrace();
                    mapper = new ObjectMapper();
                   msgType = "serverError";
                   tmp = mapper.createObjectNode();
                   respPayload = tmp.deepCopy();
                   respPayload.put("message", "Error processing json");
                   sendMessage(msgType, respPayload, session);
                   log.error("Json processing error after joining game: faultJson={}", outer, e);
                }
            }
        } finally {
            MDC.clear();
        }
    }

    private void makeMove(WebSocketSession session, ObjectNode payload) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode move = (ObjectNode) payload.get("move");
        String gameId = payload.get("gameId").asText();
        String id = session.getId();
        try {
            MDC.put("gameId", gameId);
            if(!this.gmRepo.containsGame(gameId)) {
                String msgType = "badRequestError";
                JsonNode tmp = mapper.createObjectNode();
                ObjectNode respPayload = tmp.deepCopy();
                respPayload.put("message", "game does not exist");
                sendMessage(msgType, respPayload, session);
                log.warn("Game does not exist");
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
                        log.warn("invalid move attempt. Not in game");
                        return;
                    }

                    if(!this.gmRepo.getGM(gameId).isGameReady()) {
                        String msgType = "moveUpdateError";
                        JsonNode tmp = mapper.createObjectNode();
                        ObjectNode respPayload = tmp.deepCopy();
                        respPayload.put("message", "player 2 has not joined the game");
                        sendMessage(msgType, respPayload, session);
                        log.warn("Game has not started yet. Awaiting second player");
                        return;
                    }
                   List<String> sessionList = this.gmRepo.getGameSessions(gameId);


                    boolean success = gm.sendMove(move, session.getId());

                    if(gm.getOverride()) {
                        return;
                    }

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

                    log.debug("Move processed: msg={}", str);
                } catch (Exception e) {
                    // TODO: handle exception
                    //e.printStackTrace();
                        mapper = new ObjectMapper();
                    String msgType = "moveUpdateError";
                    JsonNode tmp = mapper.createObjectNode();
                    ObjectNode respPayload = tmp.deepCopy();
                    respPayload.put("message", "problem processing player move");
                    sendMessage(msgType, respPayload, session);
                    log.error("Move processing error: payload={}", payload, e);
                }

            }
        } finally {
            MDC.clear();
        }
    }

    public void requestPromoteChoice(JsonNode choices) {

        ObjectMapper mapper = new ObjectMapper();
        String msgType = "requestPromoteChoice";
        String id = choices.get("id").asText();
        ((ObjectNode)choices).remove("id");
        ObjectNode respPayload = (ObjectNode)choices;
        sendMessage(msgType, (ObjectNode)respPayload, this.webSocketSessions.get(id));

    }

    @Override
    public void sendUpdate(JsonNode update) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String msgType = "stateUpdateResponse";
        JsonNode tmp = mapper.createObjectNode();
        ObjectNode outer = mapper.createObjectNode().set(msgType, update);
        String gameId = update.get("gameId").asText();


        String str = mapper.writeValueAsString(outer);
        TextMessage msg = new TextMessage(str);
        List<String> sessionList = this.gmRepo.getGameSessions(gameId);
        for(String user : sessionList) {
            WebSocketSession s = this.webSocketSessions.get(user);
            synchronized (s) {
                s.sendMessage(msg);
            }
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
            try {
                
                session.sendMessage(new TextMessage("Server failed to build response message"));
                log.error("Failure sending message: payload={}", payload, e);
            } catch (Exception e2) {
                // TODO: handle exception
                log.error("Socket failure: payload={}", payload, e2);
            }

        } catch(IOException e) {
            try {
                session.close(CloseStatus.SERVER_ERROR);
                
            } catch (Exception e1) {
                // TODO: handle exception
                log.error("Socket failure: payload={}", payload, e1);
            }
        }
    }
}
