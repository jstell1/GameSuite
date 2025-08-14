package gamesuite.server.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import gamesuite.core.model.NetGameState;
import gamesuite.core.model.Player;
import gamesuite.core.view.NetGameView;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/games")
public class GameRequestManager {
    private ServerGameManager gm;

    public GameRequestManager(ServerGameManager gm) {
        this.gm = gm;
    }

    @PostMapping
    public ResponseEntity<NetGameView> createGame(@RequestBody Player player1) {
        
        String gameId = this.gm.createGame(player1);
        NetGameView game = new NetGameState(this.gm.getGameView(gameId), gameId);
        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }
}
