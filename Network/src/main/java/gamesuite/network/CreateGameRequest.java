package gamesuite.network;

public class CreateGameRequest {
    String name;

    public CreateGameRequest(String name) {
        this.name = name;
    }

    public String getName() { return this.name; }
}
