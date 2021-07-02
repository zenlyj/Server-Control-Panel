package Logic;

import Model.App;
import Model.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
                isOnline = InetAddress.getByName(serverToCheck.getIpAddress()).isReachable(300);
            } catch (UnknownHostException e) {
                System.out.println("The following host is unknown: " + serverToCheck.getServerName());
            } catch (IOException e) {
                System.out.println("A network error has occurred!");
            } catch (IllegalArgumentException e) {
                // Will never have a negative timeout
            }
            Server updatedServer = new Server(serverToCheck);
            updatedServer.setStatus(isOnline);
            servers.set(serverIndex, updatedServer);
        }
    }
}
