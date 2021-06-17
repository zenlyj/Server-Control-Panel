package Logic;

import Model.App;
import Model.Server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class CheckConnectionCommand extends Command {
    private App app;
    private List<Integer> serverIndices;
    private int PORTNUM = 3389;

    public CheckConnectionCommand(App app, List<Integer> serverIndices) {
        this.app = app;
        this.serverIndices = new ArrayList<>(serverIndices);
    }

    public void execute() {
        List<Server> servers = app.getServers();
        for (Integer serverIndex : serverIndices) {
            Server serverToCheck = servers.get(serverIndex);
            String serverIP = serverToCheck.getIpAddress();
            Socket socket;
            boolean avail = false;
            try {
                socket = new Socket(serverIP, PORTNUM);
                avail = socket.isConnected();
                socket.close();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            if (avail) {
                serverToCheck.setStatus(true);
            } else {
                serverToCheck.setStatus(false);
            }
            Server updatedServer = new Server(serverToCheck);
            servers.set(serverIndex, updatedServer);
        }
    }
}
