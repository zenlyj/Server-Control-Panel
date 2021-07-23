package Logic.commands;

import Model.App;
import Model.Server;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class PingCommand extends Command {
    private final App app;
    private final List<Server> serversToPing;
    private final String unknownHostMessage = "The following host is unknown: %s";
    private final String networkErrorMessage = "Unable to establish network connection to %s!";

    public PingCommand(App app, List<Server> serverToPing) {
        this.app = app;
        this.serversToPing = new ArrayList<>(serverToPing);
    }

    private boolean canPing(Server server) {
        if (server == null) {
            return false;
        }
        if (!app.getServers().contains(server)) {
            return false;
        }
        if (app.isServerInDelete(server) || app.isServerInEdit(server)) {
            return false;
        }
        return true;
    }

    private void updateUptime(Server updatedServer, boolean isOnline) {
        boolean previousOnline = updatedServer.getIsOnline();
        if (previousOnline && !isOnline) {
            // server goes offline
            updatedServer.setBootDatetime(null);
        }
        if (!previousOnline && isOnline) {
            // server boot up
            new UpdateUptimeCommand(app, updatedServer).execute();
        }
    }

    @Override
    public void execute() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (Server server : serversToPing) {
                    if (!canPing(server)) continue;
                    try {
                        boolean isOnline = InetAddress.getByName(server.getIpAddress()).isReachable(300);
                        Server updatedServer = new Server(server);
                        updateUptime(updatedServer, isOnline);
                        updatedServer.setStatus(isOnline);
                        int serverIndex = app.getServers().indexOf(server);
                        Platform.runLater(()-> app.getServers().set(serverIndex, updatedServer));
                    } catch (UnknownHostException e) {
                        app.addHistory(String.format(unknownHostMessage, server.getServerName()));
                    } catch (IOException e) {
                        app.addHistory(String.format(networkErrorMessage, server.getServerName()));
                    } catch (IllegalArgumentException e) {
                        // Will never have a negative timeout
                    }
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}
