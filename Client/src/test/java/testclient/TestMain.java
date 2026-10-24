package testclient;

import gamesuite.client.control.ClientManager;
import gamesuite.client.control.GUIManager;
import gamesuite.client.view.GameGUI;
import gamesuite.client.view.MainGUI;

public class TestMain {

    public static void main(String[] args) {
        System.out.println("running test");
        ClientManager cmg = new MockClientManager();
        GUIManager guiGM = new GUIManager();
        guiGM.setGamManager(cmg);
        cmg.setGUIManager(guiGM);

        GameGUI ui = new GameGUI(guiGM);
        guiGM.setGameGUI(ui);
        MainGUI main = new MainGUI(cmg, guiGM);
        cmg.setMainGUI(main);
        guiGM.setMainGUI(main);
        main.activate();
    }
}
