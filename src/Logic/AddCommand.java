package Logic;

import Model.App;
import Model.Server;
import javafx.collections.ObservableList;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class AddCommand extends Command {
    private App app;
    private String userName;
    private String password;
    private String serverName;
    private String ipAddress;

    public AddCommand(App app, String userName, String password, String serverName, String ipAddress) {
        this.app = app;
        this.userName = userName;
        this.password = password;
        this.serverName = serverName;
        this.ipAddress = ipAddress;
    }

    public void execute() {
        ObservableList<Server> servers = app.getServers();
        servers.add(new Server(ipAddress, serverName, userName, password));
        app.commit(servers);
    }
}
