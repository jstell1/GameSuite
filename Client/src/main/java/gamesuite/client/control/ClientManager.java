package gamesuite.client.control;

import java.util.concurrent.CompletableFuture;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import gamesuite.core.model.Move;
import gamesuite.core.network.CreateGameRequest;
import gamesuite.core.network.GameCreatedResponse;
import gamesuite.core.network.GameReadyResponse;
import gamesuite.core.network.JoinGameRequest;
import gamesuite.core.network.MoveRequest;
import gamesuite.core.network.WebSockServerMessage;

public class ClientManager {
    private final WebSocketClient client = new StandardWebSocketClient();
    private RestTemplate restTemplate = new RestTemplate();
    private WebSocketSession session;
    private String sessionId;
    private String gameId;
    private CompletableFuture<String> sessionIdFuture = new CompletableFuture<>();
    private String baseUrl;
    private String wsUrl;
    private GUIManager guiGM;
    
    public ClientManager(String ip, int port) {
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
                //System.out.println("Connected to WebSocket");
                ClientManager.this.session = session;
                //ClientGameManager.this.sessionId = session.getId();
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
                String payload = message.getPayload().toString();
                //System.out.println(payload);
                ObjectMapper mapper = new ObjectMapper();
                WebSockServerMessage servMsg = mapper.readValue(payload, WebSockServerMessage.class);
                
                if(servMsg.getResp1() != null) {
                    GameReadyResponse msg = servMsg.getResp1();
                    ClientManager.this.guiGM.initGame(msg.getBoard(), msg.getGame());
                } else if(servMsg.getResp2() != null) {
                    GameCreatedResponse msg = servMsg.getResp2();
                    ClientManager.this.guiGM.setGameState(msg.getGame());
                    ClientManager.this.guiGM.update();
                } else if(servMsg.getResp1() == null && servMsg.getResp2() == null) {
                    ClientManager.this.sessionId = servMsg.getSessionId();
                    sessionIdFuture.complete(sessionId); 
                }

            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
                //System.out.println("WebSocket closed");
            }
        }, this.wsUrl).get();
    }

    public String awaitSessionId() throws Exception {
        return sessionIdFuture.get();
    }

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
