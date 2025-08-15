package gamesuite.server.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus.Series;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import gamesuite.network.GameCreatedResponse;
import gamesuite.network.JoinGameRequest;
import gamesuite.network.NetGameState;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import gamesuite.network.NetGameView;


@RestController
@RequestMapping("/games")
public class GameRequestManager {
    private ServerGameRepo gmRepo;

    public GameRequestManager(ServerGameRepo gmRepo) {
        this.gmRepo = gmRepo;
    }

    @PostMapping
    public ResponseEntity<GameCreatedResponse> createGame(@RequestBody Player player1) {
        if(player1 == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            GameBoard board = new GameBoard(8);
            String gameId = this.gmRepo.createGame(player1, board);
            GameState game = this.gmRepo.getGameView(gameId);
            GameCreatedResponse resp = new GameCreatedResponse(gameId, game);
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/players")
    public ResponseEntity<CoordPair[][]> joinGame(@RequestBody JoinGameRequest request) {

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
            else
                return new ResponseEntity<>(board.getBoard(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping
    public ResponseEntity<GameState> makeMove(@RequestBody Move move) {

        
        return null;
    }
}
