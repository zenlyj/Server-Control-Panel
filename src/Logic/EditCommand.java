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
        Server oldServer = servers.get(selectedIndex);
        Server updatedServer = new Server(ipAddress, serverName, userName, password);
        boolean isModified = false;
        isModified = (!ipAddress.equals(oldServer.getIpAddress())) ||
                     (!serverName.equals(oldServer.getServerName())) ||
                     (!userName.equals(oldServer.getUserName())) ||
                     (!password.equals(oldServer.getPassword()));
        if (isModified) {
            // erase old server
            servers.set(selectedIndex, new Server("", "", "", ""));
            if (!servers.contains(updatedServer)) {
                servers.set(selectedIndex, updatedServer);
                app.commit(servers);
                String successMessage = oldServer.getServerName() + " has been successfully edited\n";
                app.addHistory(successMessage);
            } else {
                // if edit failure, set old server back to index
                servers.set(selectedIndex, oldServer);
                String failureMessage = "Server already exists in the current list! Aborting edit operation...\n";
                app.addHistory(failureMessage);
            }
        } else {
            String noChangeMessage = "No changes have been made to " + serverName + "\n";
            app.addHistory(noChangeMessage);
        }
    }
}
