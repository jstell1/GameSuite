package gamesuite.client;

import java.util.Scanner;

import gamesuite.core.control.GameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Player;
import gamesuite.client.control.GUIManager;
import gamesuite.core.model.Player;
import gamesuite.client.view.GameBoardPanel;
import gamesuite.client.view.GameGUI;
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
        GameBoard board = new GameBoard(8);
        GameManager checkers = new GameManager(board, p1, p2);
        checkers.initBoard();
        GameState game = checkers.getGameState();

        GameUI ui = null;
        if(input.equals("cli")) 
            ui = new TextGameCLI(game, board, in, checkers);   
        else {
            GUIManager mang = new GUIManager(checkers, game);
            GameBoardPanel gameBoard = new GameBoardPanel(board, 600, mang);
            mang.setBoard(gameBoard);
            GameGUI gui = new GameGUI(game.getTurn(), gameBoard);
            mang.setGameGUI(gui);
            ui = mang;
        }
        ui.runGame();
    }
}
