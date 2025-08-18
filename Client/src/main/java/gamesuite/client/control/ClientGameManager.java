package gamesuite.client.control;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import gamesuite.core.control.GameManager;
import gamesuite.core.model.Move;
import gamesuite.core.network.CreateGameRequest;
import gamesuite.core.network.GameCreatedResponse;
import gamesuite.core.network.GameReadyResponse;
import gamesuite.core.network.JoinGameRequest;
import gamesuite.core.network.MoveRequest;
import gamesuite.core.network.WebSockServerMessage;

public class ClientGameManager implements GameManager {
    private final WebSocketClient client = new StandardWebSocketClient();
    private RestTemplate restTemplate = new RestTemplate();
    private WebSocketSession session;
    private String sessionId;
    private String gameId;
    private CompletableFuture<String> sessionIdFuture = new CompletableFuture<>();
    private String baseUrl;
    private String wsUrl;
    private GUIManager guiGM;
    
    public ClientGameManager(String ip, int port) {
        this.baseUrl = "http://" + ip + ":" + port;
        this.wsUrl = "ws://" + ip + ":" + port + "/ingame";
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    public void setGUIManager(GUIManager guiGM) {
        if(this.guiGM == null)
            this.guiGM = guiGM;
    }

    public void connect() throws Exception {
        this.client.execute(new AbstractWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                System.out.println("Connected to WebSocket");
                ClientGameManager.this.session = session;
                //ClientGameManager.this.sessionId = session.getId();
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                String payload = message.getPayload().toString();
                ObjectMapper mapper = new ObjectMapper();
                WebSockServerMessage servMsg = mapper.readValue(payload, WebSockServerMessage.class);
                
                if(servMsg.getResp1() != null) {
                    GameReadyResponse msg = servMsg.getResp1();
                    ClientGameManager.this.guiGM.initGame(msg.getBoard(), msg.getGame());
                } else if(servMsg.getResp2() != null) {
                    GameCreatedResponse msg = servMsg.getResp2();
                    ClientGameManager.this.guiGM.setGameState(msg.getGame());
                    ClientGameManager.this.guiGM.update();
                } else if(servMsg.getResp1() == null && servMsg.getResp2() == null) {
                    ClientGameManager.this.sessionId = servMsg.getSessionId();
                    sessionIdFuture.complete(sessionId); 
                }

            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
                System.out.println("WebSocket closed");
            }
        }, this.wsUrl).get();
    }

    public String awaitSessionId() throws Exception {
        return sessionIdFuture.get();
    }

    @Override
    public void sendMove(Move move) {
        if (session != null && session.isOpen()) {

            MoveRequest request = new MoveRequest(this.sessionId, this.gameId, move);
            ObjectMapper mapper = new ObjectMapper();

            try {
                
                String str = mapper.writeValueAsString(request);
                TextMessage msg = new TextMessage(str);
                session.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public GameCreatedResponse createGame(String playerName) {
       
        try {
            if (sessionId == null) {
                sessionId = sessionIdFuture.get(); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

       
        String url = this.baseUrl + "/games";

       CreateGameRequest request = new CreateGameRequest(playerName, this.sessionId);

       try {
        
           ResponseEntity<GameCreatedResponse> response 
                   = restTemplate.postForEntity(url, request, GameCreatedResponse.class);
            this.gameId = response.getBody().getGameId();
            return response.getBody();
       } catch (Exception e) {}
       return null;
    }

    @Override
    public GameCreatedResponse joinGame(String name, String gameId) {
        String url = baseUrl + "/games/players";

        JoinGameRequest request = new JoinGameRequest();
        request.setPlayer(name);
        request.setGameId(gameId);
        request.setSessionId(this.sessionId);

        try {
            GameCreatedResponse response = restTemplate.patchForObject(url, request, GameCreatedResponse.class);
            this.gameId = response.getGameId();
            return response;
        } catch (Exception e) {}
        return null;
    }
}
