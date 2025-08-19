package gamesuite.client.control;

import java.util.HashSet;
import java.util.Set;

public class ClientConfigurer {
    private String args[];
    private String ip;
    private int port;
    private String ui;
    private Set<String> validArgs = new HashSet<>();

    public ClientConfigurer(String args[]) {
        this.validArgs.add("ui");
        this.validArgs.add("ip");
        this.validArgs.add("port");
        this.args = args;

        for(String arg : args) {
            if(arg.contains("=")) {
                String[] params = arg.split("=");
                if(this.validArgs.contains(params[0])) {
                    if(params[0].equals("ui") && (params[1].equals("cli") || params[1].equals("gui"))) {
                        ui = params[1];
                    } else if(params[0].equals("ip")) {
                        ip = params[1];
                    } else if(params[0].equals("port")) {
                        try {
                            port = Integer.parseUnsignedInt(params[1]);
                        } catch (Exception e) {}
                    }
                }
            }
        }
    }

    public String getUI() { return this.ui; }

    public String getIP() { return this.ip; }

    public int getPort() { return this.port; }
}
