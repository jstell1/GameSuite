// Program to eastablish the socket connection

package gamesuite.server.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.network.GameCreatedResponse;
import gamesuite.core.network.MoveRequest;
import gamesuite.core.network.WebSockServerMessage;

@Component
public class SocketConnectionHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> webSocketSessions = new ConcurrentHashMap<>();
    private ServerGameRepo gmRepo;

    public SocketConnectionHandler(ServerGameRepo gmRepo) {
        this.gmRepo = gmRepo;
    }

    public boolean hasSocket(String sessionId) {
        return this.webSocketSessions.containsKey(sessionId);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println(session.getId() + " Connected");
        WebSockServerMessage msg1 = new WebSockServerMessage(null, null, session.getId());
        ObjectMapper mapper = new ObjectMapper();
        String str = mapper.writeValueAsString(msg1);
        TextMessage msg = new TextMessage(str);
        webSocketSessions.put(session.getId(), session);
        session.sendMessage(msg);
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
        ObjectMapper mapper = new ObjectMapper();
        MoveRequest req = mapper.readValue(message.getPayload().toString(), MoveRequest.class);
        String gameId = req.getGameId();
        String sessionId = req.getSessionId();
        Move move = req.getMove();
        List<String> sessionList = this.gmRepo.getUserSessions(gameId);

        GameManager gm = this.gmRepo.getGM(gameId);
        gm.sendMove(move);
        GameState game = gm.getGameState();
        GameCreatedResponse resp = new GameCreatedResponse(gameId, game);
        WebSockServerMessage srvMsg = new WebSockServerMessage(null, resp, null);
        String str = mapper.writeValueAsString(srvMsg);
        TextMessage msg = new TextMessage(str);

        for(String user : sessionList)
            this.webSocketSessions.get(user).sendMessage(msg);
    }
}
