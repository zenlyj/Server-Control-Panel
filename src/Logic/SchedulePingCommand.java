package Logic;

import Model.Server;

import java.net.InetAddress;
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
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            server.setStatus(isOnline);
        }
    }
}
