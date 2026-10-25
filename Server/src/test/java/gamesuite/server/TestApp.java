package gamesuite.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;

public class TestApp {
    String gameId;
    MockClient mc1;
    MockClient mc2;
    String ip;
    int port;
    boolean immediate;
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        int x = Integer.parseInt(args[2]);
        if(x == 1) {
            initialTest(args);

        } else if(x == 2) {
            openAndDrop(args);

        } else if(x == 3) {
            createGameAndDrop(args);
        }else if(x == 4) {
            createJoinDrop(args, 1000);
        } else if(x == 5) {
            manyCreates(args);
        } else if(x == 6) {
            manyCreatesJoins(args);
        }
    }

    public static void manyCreatesJoins(String[] args) {
        try {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);

            ArrayList<TestApp> list = new ArrayList<>();
            int j = 0;
            for(int i = 0; i < 15; i++) {
                new Thread(() -> {
                    try {

                        TestApp app = new TestApp();
                        app.ip = ip;
                        app.port = port;
                        app.mc1 = new MockClient(ip, port, app);
                        app.mc1.createGame("CheckersStatic", "BoardGameEngine", "Bob");
                        app.mc2 = new MockClient(ip, port, app);

                        while(app.gameId == null) {
                            Thread.sleep(1000);
                        }
                        app.mc2.joinGame("nancy", app.gameId);
                        synchronized (list) {
                            list.add(app);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }


            System.in.read();

            for(TestApp app : list) {
                app.mc2.quitGame(true);
            }

            System.in.read();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void manyCreates(String[] args){
        try {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);

            ArrayList<TestApp> list = new ArrayList<>();
            int j = 0;
            for(int i = 0; i < 15; i++) {
                new Thread(() -> {
                    try {

                        TestApp app = new TestApp();
                        app.ip = ip;
                        app.port = port;
                        app.mc1 = new MockClient(ip, port, app);
                       app.mc1.createGame("CheckersStatic", "BoardGameEngine", "Bob");

                       synchronized (list) {
                           list.add(app);
                       }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }


            System.in.read();



        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createJoinDrop(String[] args, int time) {
        try {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);
            TestApp app = new TestApp();
            app.ip = ip;
            app.port = port;

            app.mc1 = new MockClient(ip, port, app);

            app.mc1.createGame("CheckersStatic", "BoardGameEngine", "Bob");
            System.out.println("mc1 creating game");
            app.mc2 = new MockClient(app.ip, app.port, app);
            while(app.gameId == null) {
                Thread.sleep(1000);
            }
          //  app.mc1.quitGame(true);
            System.out.println("mc2 about to join");
            app.mc2.joinGame("Nancy", app.gameId);
            System.out.println("joined");


            Thread.sleep(time);
            ObjectNode move = mapper.createObjectNode();

            move.put("startX", 2);
            move.put("startY", 1);
            move.put("endX", 3);
            move.put("endY", 0);
            app.mc1.sendMove(move);
            System.out.println("HERE");
            System.in.read();
            app.mc1.quitGame(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openAndDrop(String[] args) {
        try {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);
            TestApp app = new TestApp();
            app.ip = ip;
            app.port = port;

            app.mc1 = new MockClient(ip, port, app);
            app.mc1.connect();
            throw new RuntimeException();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createGameAndDrop(String[] args) {
        try {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);
            TestApp app = new TestApp();
            app.ip = ip;
            app.port = port;

            app.mc1 = new MockClient(ip, port, app);
            app.mc1.createGame("CheckersStatic", "BoardGameEngine", "Bob");
            throw new RuntimeException();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initialTest(String[] args) {
        try {
            String ip = args[0];
            int port = Integer.parseInt(args[1]);
            TestApp app = new TestApp();
            app.ip = ip;
            app.port = port;

            app.mc1 = new MockClient(ip, port, app);
            app.mc1.createGame("CheckersStatic", "BoardGameEngine", "Bob");

            System.in.read();
            app.mc1.quitGame(true);
            Thread.sleep(5000);
            app.mc2.quitGame(true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void setGameId(String gameId) throws IOException, InterruptedException {
        this.gameId = gameId;
       // if(this.immediate) {
      //      return;
      //  }
      //  this.mc2 = new MockClient(this.ip, this.port, this);
      //  this.mc2.joinGame("Nancy", this.gameId);
      /// Thread.sleep(5000);
        //initiateGame();
    }

    public synchronized void initiateGame() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode move = mapper.createObjectNode();

        move.put("startX", 2);
        move.put("startY", 1);
        move.put("endX", 3);
        move.put("endY", 0);
        this.mc1.sendMove(move);
    }
}
