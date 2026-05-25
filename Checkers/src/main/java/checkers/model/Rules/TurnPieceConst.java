package checkers.model.Rules;

import checkers.model.CheckersCoordPair;
import checkers.model.CheckersGameBoard;
import checkers.model.CheckersGamePiece;
import checkers.model.CheckersGameState;
import checkers.model.CheckersMove;
import checkers.model.CheckersPlayer;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;

public class TurnPieceConst extends Constraint {

    public TurnPieceConst(CheckersGameState gameState, CheckersGameBoard board) {
        super(gameState, board);
        this.name = "turnPiece";
    }

    @Override
    public Result checkMove(CheckersMove move) {

        int gameTurn = this.gameState.getTurn();
        CheckersPlayer p = this.gameState.getPlayerById(move.getPlayerId());
        String playerTurnName = p.getTurnName();
        CheckersCoordPair pos = this.board.getBoardPos(move.getStartX(), move.getStartY());
        CheckersGamePiece piece = pos.getPiece();

        if(piece != null && piece.getName().equals(playerTurnName)) 
            return new Result(true);
        return new Result(false);
    }
    
}
