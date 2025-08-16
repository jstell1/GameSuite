package gamesuite.server.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;

import gamesuite.network.CreateGameRequest;
import gamesuite.network.GameCreatedResponse;
import gamesuite.network.JoinGameRequest;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;

@RestController
@RequestMapping("/games")
public class GameRequestManager {
    private ServerGameRepo gmRepo;

    public GameRequestManager(ServerGameRepo gmRepo) {
        this.gmRepo = gmRepo;
    }

    @PostMapping
    public ResponseEntity<GameCreatedResponse> createGame(@RequestBody CreateGameRequest msg) {
        String name = msg.getName();
        if(name == null) 
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        
        try {
            Player player1 = new Player(name, 0);
            GameBoard board = new GameBoard(8);
            String gameId = this.gmRepo.createGame(player1, board);
            GameState game = this.gmRepo.getGameView(gameId);
            String userId = this.gmRepo.setUserToGame(gameId, 1);
            GameCreatedResponse resp = new GameCreatedResponse(gameId, game, userId);
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/players")
    public ResponseEntity<GameCreatedResponse> joinGame(@RequestBody JoinGameRequest request) {

        Player player = request.getPlayer();
        String gameId = request.getGameId();
        if(player == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        if(!gmRepo.contains(gameId))
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        try {
            GameBoard board = this.gmRepo.joinGame(player, gameId);

            if(board == null)
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            else {
                GameState game = gmRepo.getGM(gameId).getGameState();
                String userId = gmRepo.setUserToGame(gameId, 2);
                GameCreatedResponse resp = new GameCreatedResponse(gameId, game, userId);
                return new ResponseEntity<>(resp, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping
    public ResponseEntity<GameState> makeMove(@RequestBody Move move) {


        return null;
    }
}
