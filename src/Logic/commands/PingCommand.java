package Logic.commands;

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
    private final String unknownHostMessage = "The following host is unknown: %s\n";
    private final String networkErrorMessage = "Unable to establish network connection to %s!\n";

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
                app.addHistory(String.format(unknownHostMessage, serverToCheck.getServerName()));
            } catch (IOException e) {
                app.addHistory(String.format(networkErrorMessage, serverToCheck.getServerName()));
            } catch (IllegalArgumentException e) {
                // Will never have a negative timeout
            }
            Server updatedServer = new Server(serverToCheck);
            updatedServer.setStatus(isOnline);
            servers.set(serverIndex, updatedServer);
        }
    }
}
