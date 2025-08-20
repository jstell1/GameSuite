package gamesuite.client;

import java.util.Scanner;

import gamesuite.core.control.CoreGameManager;
import gamesuite.core.model.GameBoard;
import gamesuite.core.model.GameState;
import gamesuite.core.model.Player;
import gamesuite.client.control.ClientConfigurer;
import gamesuite.client.control.ClientGameManager;
import gamesuite.client.control.GUIManager;
import gamesuite.core.model.Player;
import gamesuite.client.view.GameBoardPanel;
import gamesuite.client.view.GameGUI;
import gamesuite.client.view.GameUI;
import gamesuite.client.view.TextGameCLI;

public class App {

    public static void main(String[] args) {

        ClientConfigurer config = new ClientConfigurer(args);
        
        String uiType = config.getUI();
        String ip = config.getIP();
        int port = config.getPort();

        Scanner in = new Scanner(System.in);
        String player1 = "Frodo";
        String player2 = "Sam";

        if(uiType == null)
            uiType = "gui";
        if(ip == null || port == 0) {
            ip = "localhost";
            port = 8080;
        }

        if(ip.equals("localhost") && port == 0) {
            //runLocal(uiType, ip, port, player1, player2, in);
        } else {
            ClientGameManager cmg = new ClientGameManager(ip, port);
            GUIManager guiGM = new GUIManager();
            guiGM.setGamManager(cmg);
            cmg.setGUIManager(guiGM);
    
            try {
                cmg.connect();
                GameGUI ui = new GameGUI(guiGM);
                guiGM.setGameGUI(ui);
                ui.activate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public static void runLocal(String uiType, String ip, int port, String player1, String player2, Scanner in) {
         
        

        if(ip == "localhost" && port == 0) {
            
            Player p1 = new Player(player1, 0);
            Player p2 = new Player(player2, 0);
            GameBoard board = new GameBoard(8);
            CoreGameManager checkers = new CoreGameManager(board, p1, p2);
            checkers.initBoard();
            GameState game = checkers.getGameState();
    
            GameUI ui = null;
            if(uiType.equals("cli")) 
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
        } else {

        }
    }
}
