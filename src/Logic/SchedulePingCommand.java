package Logic;

import Model.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class SchedulePingCommand extends Command {
    private List<Server> servers;

    public SchedulePingCommand(List<Server> servers) {
        this.servers = servers;
    }

    @Override
    public void execute() {
        for (Server server : servers) {
            boolean isOnline = false;
            try {
                isOnline = InetAddress.getByName(server.getIpAddress()).isReachable(300);
            } catch (UnknownHostException e) {
                System.out.println("The following host is unknown: " + server.getServerName());
            } catch (IOException e) {
                System.out.println("A network error has occurred!");
            } catch (IllegalArgumentException e) {
                // will never have a negative timeout
            }
            server.setStatus(isOnline);
        }
    }
}
