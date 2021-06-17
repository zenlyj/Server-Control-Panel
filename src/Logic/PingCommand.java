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
                isOnline = InetAddress.getByName(serverToCheck.getIpAddress()).isReachable(1000);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            Server updatedServer = new Server(serverToCheck);
            if (isOnline) {
                updatedServer.setStatus(true);
            } else {
                updatedServer.setStatus(false);
            }
            servers.set(serverIndex, updatedServer);
        }
    }
}
