package gamesuite.core.control;

import gamesuite.core.model.Move;
import gamesuite.core.network.GameCreatedResponse;

public abstract class AbstractGameManager implements GameManager {

    @Override
    public abstract void sendMove(Move move);

    @Override
    public GameCreatedResponse createGame(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createGame'");
    }

    @Override
    public GameCreatedResponse joinGame(String name, String gameId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'joinGame'");
    }
    
}
