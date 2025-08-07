package gamesuite;

import java.util.Scanner;

import gamesuite.control.GameManager;
import gamesuite.model.data.GameStateView;
import gamesuite.model.data.Move;
import gamesuite.model.data.Player;
import gamesuite.view.*;

public class App {

    public static void main(String[] args) {
        String input = "gui";
        if(args.length > 0 && args[0] == "cli")
            input = "cli";
        Scanner in = new Scanner(System.in);
        //System.out.println("Player 1 enter name: ");
        //String input = in.nextLine();
        Player p1 = new Player("Frodo", 0);
        //System.out.println("Player 2 enter name: ");
        //input = in.nextLine();
        Player p2 = new Player("Sam", 0);
        GameManager checkers = new GameManager(p1, p2);
        checkers.initBoard();
        GameStateView game = checkers.getGameStateView();

        GameUI ui = null;
        if(input == "cli") 
            ui = new TextGameCLI(game, in, checkers);   
        else
            ui = new GameGUI(game, checkers);

        ui.runGame();
    }
}
