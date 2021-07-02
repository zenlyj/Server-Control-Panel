package Logic;

import Model.App;
import Model.Server;

import java.util.List;

public class EditCommand extends Command {
    private App app;
    private int selectedIndex;
    private String userName;
    private String password;
    private String serverName;
    private String ipAddress;

    public EditCommand(App app, int selectedIndex, String userName, String password, String serverName, String ipAddress) {
        this.app = app;
        this.selectedIndex = selectedIndex;
        this.userName = userName;
        this.password = password;
        this.serverName = serverName;
        this.ipAddress = ipAddress;
    }

    @Override
    public void execute() {
        app.setServerInEdit(selectedIndex);
        List<Server> servers = app.getServers();
        Server updatedServer = new Server(ipAddress, serverName, userName, password);
        servers.set(selectedIndex, updatedServer);
        app.commit(servers);
    }
}
