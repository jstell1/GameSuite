package gamesuite;

import java.util.Scanner;

import gamesuite.control.GameManager;
import gamesuite.model.data.GameStateView;
import gamesuite.model.data.Move;
import gamesuite.model.data.Player;
import gamesuite.view.GameUI;

public class App {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Player 1 enter name: ");
        String input = in.nextLine();
        Player p1 = new Player(input, 0);
        System.out.println("Player 2 enter name: ");
        input = in.nextLine();
        Player p2 = new Player(input, 0);
        GameManager checkers = new GameManager(p1, p2);
        checkers.initBoard();
        GameStateView game = checkers.getGameStateView();
        GameUI cli = new GameUI(game, in);

        while(!checkers.gameOver()) {
            cli.displayBoard();
            int turn = checkers.getTurn();
            Move move = cli.getPlayerMove(turn);
            if(move != null) {
                game = checkers.sendMove(move);
            }
        }
        if(checkers.getWinner() != null)
            System.out.println("Winner" + checkers.getWinner().getName());
        else    
            System.out.println("Draw");
    }
}
