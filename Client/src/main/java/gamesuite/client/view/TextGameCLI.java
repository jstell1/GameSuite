package gamesuite.client.view;

import java.util.Scanner;
import java.util.regex.Pattern;
import gamesuite.core.control.GameManager;
import gamesuite.core.model.CoordPair;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Move;
import gamesuite.core.model.Player;
import java.util.regex.Matcher;

public class TextGameCLI implements GameUI {

    private Player[] players;
    private static Pattern COORD_PATTERN; 
    private Scanner in;
    private GameBoard board;
    private int sideLength;
    private GameState game;
    private GameManager gm;

    public TextGameCLI(GameState game, GameBoard board, Scanner in, GameManager gm) {
        this.gm = gm;
        this.game = game;
        this.board = board;
        this.players = game.getPlayers();
        this.in = in;
        this.sideLength = this.board.getSideLength();
        String pattern = "^\\s*([1-" + this.sideLength + "])\\s*,\\s*([1-" + this.sideLength + "])\\s*$";
        COORD_PATTERN = Pattern.compile(pattern);
    }
    
    public Move getPlayerMove(int playerNum) { 
        CoordPair startPos = null;
        CoordPair endPos = null;
        
        if(playerNum == 1)
            System.out.print("Player 1 enter start coordinates (row, col): ");
        else 
            System.out.print("Player 2 enter start coordinates (row, col): ");

        String input = this.in.nextLine();
        Matcher matcher = COORD_PATTERN.matcher(input);
        if (matcher.matches()) {
            int x = Integer.parseInt(matcher.group(1)) - 1;
            int y = Integer.parseInt(matcher.group(2)) - 1;
            startPos = new CoordPair(x, y);
        }
        if(playerNum == 1)
            System.out.print("Player 1 enter end coordinates (row, col): ");
        else
            System.out.print("Player 2 enter end coordinates (row, col): ");

        input = this.in.nextLine();
        matcher = COORD_PATTERN.matcher(input);
        if (matcher.matches()) {
            int x = Integer.parseInt(matcher.group(1)) - 1;
            int y = Integer.parseInt(matcher.group(2)) - 1;
            endPos = new CoordPair(x, y);
        }
        if(startPos != null && endPos != null) {
            Move move = new Move(startPos.getX(), startPos.getY(), endPos.getX(), endPos.getY());
            return move;
        }
        return null;        
    }

    public void updateBoard(GameState game) {
        System.out.println("Player 1 points: " + game.getPlayerPoints(1));
        System.out.println("Player 2 points: " + game.getPlayerPoints(2));
    }

    public void displayBoard() { System.out.println(this.board.toString()); }

    @Override
    public void runGame() {
        while(!this.game.isGameOver()) {
            displayBoard();
            int turn = this.game.getTurn();
            Move move = getPlayerMove(turn);
            if(move != null) {
                this.gm.sendMove(move);
            }
        }
        if(this.game.getWinner() != null)
            System.out.println("Winner: " + this.game.getWinner().getName());
        else    
            System.out.println("Draw");
    }
}
