package Logic;

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
import java.util.Optional;

public class SchedulePingCommand extends Command {
    private App app;
    private List<Server> serversSnapshot;
    private final String unknownHostMessage = "The following host is unknown: %s\n";
    private final String networkErrorMessage = "Unable to establish network connection to %s!\n";

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

    private void pingServers() {
        for (Server server : serversSnapshot) {
            boolean isOnline = false;
            try {
                isOnline = InetAddress.getByName(server.getIpAddress()).isReachable(300);
            } catch (UnknownHostException e) {
                app.addHistory(String.format(unknownHostMessage, server.getServerName()));
            } catch (IOException e) {
                app.addHistory(String.format(networkErrorMessage, server.getServerName()));
            } catch (IllegalArgumentException e) {
                // will never have a negative timeout
            }
            if (server.getIsOnline() && !isOnline) {
                // server goes offline
                server.setBootDatetime(null);
            }
            if (!server.getIsOnline() && isOnline) {
                // server boot up
                new UpdateUptimeCommand(app, server).execute();
            }
            server.setStatus(isOnline);
        }
    }

    private void updateMainApp() {
        Platform.runLater(() -> {
            int ptr = 0;
            for (int i = 0; i < serversSnapshot.size(); i++) {
                Server curr = serversSnapshot.get(i);
                boolean isEdited = app.isServerInEdit(i);
                boolean isDeleted = app.isServerInDelete(curr);
                if (isEdited) {
                    // release edited server
                    app.removeServerInEdit(i);
                }
                if (isDeleted) {
                    // release deleted server
                    app.removeServerInDelete(curr);
                    continue;
                }
                if (!isEdited) {
                    app.getServers().set(ptr, curr);
                }
                ptr++;
            }
        });
    }

    private void setSchedule() {
        ScheduledService<Void> service = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
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
