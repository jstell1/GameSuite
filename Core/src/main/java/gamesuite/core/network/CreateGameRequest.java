package gamesuite.core.network;

public class CreateGameRequest {
    String name;
    String sessionId;

    public CreateGameRequest() {}

    public CreateGameRequest(String name, String sessionId) {
        this.name = name;
        this.sessionId = sessionId;
    }

    public void setName(String name) {
        if(this.name == null)
            this.name = name;
    }

    public void setSessionId(String sessionId) {
        if(this.sessionId == null)
            this.sessionId = sessionId;
    }

    public String getName() { return this.name; }

    public String getSessionId() { return this.sessionId; }
}
