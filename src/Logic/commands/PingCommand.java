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

    public PingCommand(App app, List<Server> serversToPing) {
        this.app = app;
        this.serversToPing = listDeepCopy(serversToPing);
    }

    private List<Server> listDeepCopy(List<Server> listToCopy) {
        List<Server> res = new ArrayList<>();
        for (Server server : listToCopy) {
            res.add(new Server(server));
        }
        return res;
    }

    private void updateUptime(Server server, boolean isOnline) {
        boolean previousOnline = server.getIsOnline();
        if (previousOnline && !isOnline) {
            // server goes offline
            server.setBootDatetime(null);
        }
        if (!previousOnline && isOnline) {
            // server boot up
            new UpdateUptimeCommand(app, server).execute();
        }
    }

    private void updateMainApp() {
        Platform.runLater(()->{
            for (Server server : serversToPing) {
                if (!app.getServers().contains(server)) {
                    continue;
                }
                int serverIndex = app.getServers().indexOf(server);
                Server currServer = app.getServers().get(serverIndex);
                if (server.isSame(currServer)) {
                    app.getServers().set(serverIndex, server);
                }
            }
        });
    }

    @Override
    public void execute() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (Server server : serversToPing) {
                    try {
                        boolean isOnline = InetAddress.getByName(server.getIpAddress()).isReachable(150);
                        updateUptime(server, isOnline);
                        server.setStatus(isOnline);
                    } catch (UnknownHostException e) {
                        app.addHistory(String.format(unknownHostMessage, server.getServerName()));
                    } catch (IOException e) {
                        app.addHistory(String.format(networkErrorMessage, server.getServerName()));
                    } catch (IllegalArgumentException e) {
                        // Will never have a negative timeout
                    }
                }
                updateMainApp();
                return null;
            }
        };
        new Thread(task).start();
    }
}
