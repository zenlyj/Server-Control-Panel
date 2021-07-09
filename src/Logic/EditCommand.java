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
    private final String editSuccessMessage = "%s has been successfully edited\n";
    private final String editFailureMessage = "%s already exists in the current list! Aborting edit operation...\n";
    private final String editNoChangeMessage = "No changes have been made to %s\n";

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
                app.addHistory(String.format(editSuccessMessage, oldServer.getServerName()));
            } else {
                // if edit failure, set old server back to index
                servers.set(selectedIndex, oldServer);
                app.addHistory(String.format(editFailureMessage, serverName));
            }
        } else {
            app.addHistory(String.format(editNoChangeMessage, oldServer.getServerName()));
        }
    }
}
