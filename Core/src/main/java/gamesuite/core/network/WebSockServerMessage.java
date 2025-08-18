package gamesuite.core.network;

public class WebSockServerMessage {
    private GameReadyResponse resp1;
    private GameCreatedResponse resp2;
    private String sessionId;

    public WebSockServerMessage() {};
    public WebSockServerMessage(GameReadyResponse resp1, GameCreatedResponse resp2, String sessionId) {
        this.resp1 = resp1;
        this.resp2 = resp2;
        this.sessionId = sessionId;
    }

    public void setResp1(GameReadyResponse resp) { this.resp1 = resp; }

    public void setResp2(GameCreatedResponse resp) { this.resp2 = resp; }

    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public GameReadyResponse getResp1() { return this.resp1; }

    public GameCreatedResponse getResp2() { return this.resp2; }

    public String getSessionId() { return this.sessionId; }
}
