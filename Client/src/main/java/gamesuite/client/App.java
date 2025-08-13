package gamesuite.client;

import java.util.Scanner;

import gamesuite.core.model.Player;
import gamesuite.core.view.GameStateView;
import gamesuite.client.control.GUIManager;
import gamesuite.client.control.GameManager;
import gamesuite.core.model.Player;
import gamesuite.core.view.GameStateView;
import gamesuite.client.view.GameUI;
import gamesuite.client.view.TextGameCLI;

public class App {

    public static void main(String[] args) {
        String input = "gui";
        if(args.length > 0 && args[0].equals("cli"))
            input = "cli";
        Scanner in = new Scanner(System.in);
        Player p1 = new Player("Frodo", 0);
        Player p2 = new Player("Sam", 0);
        GameManager checkers = new GameManager(p1, p2);
        checkers.initBoard();
        GameStateView game = checkers.getGameStateView();

        GameUI ui = null;
        if(input.equals("cli")) 
            ui = new TextGameCLI(game, in, checkers);   
        else
            ui = new GUIManager(checkers);
        ui.runGame();
    }
}
