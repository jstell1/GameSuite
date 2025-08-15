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
import gamesuite.network.JoinGameRequest;
import gamesuite.network.NetGameState;
import gamesuite.core.model.GameBoard;
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
    public ResponseEntity<NetGameView> createGame(@RequestBody Player player1) {
        if(player1 == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        try {
            GameBoard board = new GameBoard(8);
            String gameId = this.gmRepo.createGame(player1, board);
            NetGameState game = new NetGameState(gameId);
            return new ResponseEntity<>(null, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/players")
    public ResponseEntity<GameBoard> joinGame(@RequestBody JoinGameRequest request) {

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
                return new ResponseEntity<>(board, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
