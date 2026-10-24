package gamesuite.client;

import java.io.IOException;
import java.util.Scanner;

import gamesuite.client.control.ClientConfigurer;
import gamesuite.client.control.ClientManager;
import gamesuite.client.control.ClientManagerImpl;
import gamesuite.client.control.GUIManager;
import gamesuite.client.view.GameGUI;
//import gamesuite.client.view.TextGameCLI;
import gamesuite.client.view.MainGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

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

            try {
                
                ClientManagerImpl cmg = new ClientManagerImpl(ip, port);
                
                
                GUIManager guiGM = new GUIManager();
                guiGM.setGamManager(cmg);
                cmg.setGUIManager(guiGM);
        
                //try {
                    //cmg.connect();
    
                    GameGUI ui = new GameGUI(guiGM);
                    guiGM.setGameGUI(ui);
                    MainGUI main = new MainGUI(cmg, guiGM);
                    cmg.setMainGUI(main);
                    guiGM.setMainGUI(main);
                    main.activate();
                    //ui.activate();
                //} catch (Exception e) {
                  //  e.printStackTrace();
                  //  return;
                ///
            } catch (Exception e) {
                // TODO: handle exception
                log.error("problem starting the app", e);
            }
        }


    }



}
