package Logic;

import Model.App;
import Model.Server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class PingCommand extends Command {
    private App app;
    private List<Integer> serverIndices;

    public PingCommand(App app, List<Integer> serverIndices) {
        this.app = app;
        this.serverIndices = new ArrayList<>(serverIndices);
    }

    @Override
    public void execute() {
        List<Server> servers = app.getServers();
        for (Integer serverIndex : serverIndices) {
            Server serverToCheck = servers.get(serverIndex);
            boolean isOnline = false;
            try {
                isOnline = InetAddress.getByName(serverToCheck.getIpAddress()).isReachable(500);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            Server updatedServer = new Server(serverToCheck);
            updatedServer.setStatus(isOnline);
            servers.set(serverIndex, updatedServer);
        }
    }
}
