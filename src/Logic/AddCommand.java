package Logic;

import Model.App;
import Model.Server;
import javafx.collections.ObservableList;

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
        Server newServer = new Server(ipAddress, serverName, userName, password);
        if (!servers.contains(newServer)) {
            servers.add(new Server(ipAddress, serverName, userName, password));
            app.commit(servers);
            String successMessage = serverName + " successfully added!\n";
            app.addHistory(successMessage);
        } else {
            String failureMessage = "Server already exists! Aborting add operation...\n";
            app.addHistory(failureMessage);
        }
    }
}
