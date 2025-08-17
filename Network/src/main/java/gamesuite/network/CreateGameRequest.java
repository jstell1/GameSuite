package gamesuite.network;

public class CreateGameRequest {
    String name;
    String sessionId;

    public CreateGameRequest(String name, String sessionId) {
        this.name = name;
        this.sessionId = sessionId;
    }

    public String getName() { return this.name; }

    public String getSessionId() { return this.sessionId; }
}
