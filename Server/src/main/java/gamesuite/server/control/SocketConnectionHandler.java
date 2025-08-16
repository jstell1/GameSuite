// Program to eastablish the socket connection

package gamesuite.server.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.network.MoveRequest;

// Socket-Connection Configuration class
public class SocketConnectionHandler extends TextWebSocketHandler {

    private List<WebSocketSession> webSocketSessions = Collections.synchronizedList(new ArrayList<>());
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        System.out.println(session.getId() + " Connected");

        webSocketSessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)throws Exception {
        super.afterConnectionClosed(session, status);
        System.out.println(session.getId() + " DisConnected");

        webSocketSessions.remove(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

        super.handleMessage(session, message);
        System.out.println(message.getPayload());
        String msg = ((TextMessage)message).getPayload();
        ObjectMapper mapper = new ObjectMapper();
        MoveRequest req = mapper.readValue(msg, MoveRequest.class);
        String str = mapper.writeValueAsString(req);
        System.out.println(req.toString());
        System.out.println(str);
        for (WebSocketSession webSocketSession : webSocketSessions) {
            webSocketSession.sendMessage(message);
        }
    }
}
