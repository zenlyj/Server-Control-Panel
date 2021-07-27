package Logic.commands;

import Model.App;
import Model.Server;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SchedulePingCommand extends Command {
    private final App app;
    private List<Server> serversSnapshot;
    private final String unknownHostMessage = "The following host is unknown: %s";
    private final String networkErrorMessage = "Unable to establish network connection to %s!";

    public SchedulePingCommand(App app) {
        this.app = app;
    }

    private List<Server> listDeepCopy(List<Server> servers) {
        List<Server> serversSnapshot = new ArrayList<>();
        for (Server server : servers) {
            serversSnapshot.add(new Server(server));
        }
        return serversSnapshot;
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

    private void pingServers() {
        for (Server server : serversSnapshot) {
            try {
                boolean isOnline = InetAddress.getByName(server.getIpAddress()).isReachable(150);
                updateUptime(server, isOnline);
                server.setStatus(isOnline);
            } catch (UnknownHostException e) {
                app.addHistory(String.format(unknownHostMessage, server.getServerName()));
            } catch (IOException e) {
                app.addHistory(String.format(networkErrorMessage, server.getServerName()));
            } catch (IllegalArgumentException e) {
                // will never have a negative timeout
            }
        }
    }

    private void updateMainApp() {
        Platform.runLater(() -> {
            int ptr = 0;
            for (int i = 0; i < serversSnapshot.size(); i++) {
                Server curr = serversSnapshot.get(i);
                boolean isEdited = app.isServerInEdit(curr);
                boolean isDeleted = app.isServerInDelete(curr);
                if (!isEdited && !isDeleted) {
                    app.getServers().set(ptr, curr);
                }
                if (!isDeleted) {
                    ptr++;
                }
            }
            app.removeServersInDelete();
            app.removeServersInEdit();
        });
    }

    private void setSchedule() {
        ScheduledService<Void> service = new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        System.out.println("Starting schedule...");
                        serversSnapshot = listDeepCopy(app.getServers());
                        pingServers();
                        updateMainApp();
                        return null;
                    }
                };
            }
        };
        service.setPeriod(Duration.seconds(10));
        service.start();
    }

    @Override
    public void execute() {
        setSchedule();
    }
}
