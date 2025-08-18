package gamesuite.server.control;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;

import gamesuite.core.control.CoreGameManager;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import gamesuite.core.network.CreateGameRequest;
import gamesuite.core.network.GameCreatedResponse;
import gamesuite.core.network.GameReadyResponse;
import gamesuite.core.network.JoinGameRequest;
import gamesuite.core.network.MoveRequest;
import gamesuite.core.network.WebSockServerMessage;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import gamesuite.core.model.Move;
import org.springframework.messaging.simp.SimpMessagingTemplate;


@RestController
public class GameRequestManager {
    private ServerGameRepo gmRepo;
    private SocketConnectionHandler handler;

    public GameRequestManager(ServerGameRepo gmRepo, SocketConnectionHandler handler) {
        this.gmRepo = gmRepo;
        this.handler = handler;
    }

    @PostMapping("/games")
    public ResponseEntity<GameCreatedResponse> createGame(@RequestBody CreateGameRequest msg) {
        String name = msg.getName();
        String sessionId = msg.getSessionId();
        if(name == null || sessionId == null || !this.handler.hasSocket(sessionId)) 
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        
        try {
            Player player1 = new Player(name, 0);
            GameBoard board = new GameBoard(8);
            String gameId = this.gmRepo.createGame(player1, board);
            GameState game = this.gmRepo.getGameView(gameId);
            this.gmRepo.setUserNum(sessionId , 1);
            this.gmRepo.addWebSocketToGame(gameId, sessionId);
            GameCreatedResponse resp = new GameCreatedResponse(gameId, game);
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("games/players")
    public ResponseEntity<GameCreatedResponse> joinGame(@RequestBody JoinGameRequest request) {

        String player = request.getPlayer();
        String gameId = request.getGameId();
        String sessionId = request.getSessionId();

        if(player == null || gameId == null || sessionId == null || !this.handler.hasSocket(sessionId))
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        if(!gmRepo.containsGame(gameId))
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);

        try {
            Player p2 = new Player(player, 0);
            GameBoard board = this.gmRepo.joinGame(p2, gameId);

            if(board == null)
                return new ResponseEntity<>(null, HttpStatus.CONFLICT);
            else {
                GameState game = this.gmRepo.getGM(gameId).getGameState();
                this.gmRepo.setUserNum(sessionId, 2);
                this.gmRepo.addWebSocketToGame(gameId, sessionId);
                GameCreatedResponse resp = new GameCreatedResponse(gameId, game);
                GameReadyResponse resp2 = new GameReadyResponse(board.getBoard(), gameId, game);
                WebSockServerMessage servMsg = new WebSockServerMessage(resp2, null, sessionId);
                ObjectMapper m = new ObjectMapper();
                String str = m.writeValueAsString(servMsg);
                this.handler.notifyPlayerJoined(gameId, str);
                return new ResponseEntity<>(resp, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
/* 
    @PatchMapping("games/updates")
    public ResponseEntity<GameCreatedResponse> makeMove(@RequestBody MoveRequest moveReq) {

        try {
            String gameId = moveReq.getGameId();
            Move move = moveReq.getMove();
    
            if()//this.gmRepo.rightPlayer(gameId, sessionId)) {
                GameManager gm = this.gmRepo.getGM(gameId);
                gm.sendMove(move);
                GameState game = gm.getGameState();
                GameCreatedResponse resp = new GameCreatedResponse(gameId, game);

                //msg.convertAndSend(resp);
                return new ResponseEntity<>(resp, HttpStatus.OK);
            }
    
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    */
}
