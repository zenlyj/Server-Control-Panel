package Logic.commands;

import Model.App;
import Model.Server;

import java.util.List;

public class EditCommand extends Command {
    private final App app;
    private final Server serverToEdit;
    private final String userName;
    private final String password;
    private final String serverName;
    private final String ipAddress;
    private final String editSuccessMessage = "%s has been successfully edited";
    private final String editFailureMessage = "%s already exists in the current list! Aborting edit operation...";
    private final String editNoChangeMessage = "No changes have been made to %s";
    private final String notExistingMessage = "%s deleted during edit! Aborting edit operation...";

    public EditCommand(App app, Server serverToEdit, String userName, String password, String serverName, String ipAddress) {
        this.app = app;
        this.serverToEdit = serverToEdit;
        this.userName = userName;
        this.password = password;
        this.serverName = serverName;
        this.ipAddress = ipAddress;
    }

    private boolean isEditable(int serverIndex) {
        boolean isChanged = (!ipAddress.equals(serverToEdit.getIpAddress())) ||
                (!serverName.equals(serverToEdit.getServerName())) ||
                (!userName.equals(serverToEdit.getUserName())) ||
                (!password.equals(serverToEdit.getPassword()));
        boolean isPresent = serverIndex != -1;
        if (!isChanged) {
            app.addHistory(String.format(editNoChangeMessage, serverToEdit.getServerName()));
            return false;
        }
        if (!isPresent) {
            app.addHistory(String.format(notExistingMessage, serverToEdit.getServerName()));
            return false;
        }
        return true;
    }

    @Override
    public void execute() {
        List<Server> servers = app.getServers();
        app.setServerInEdit(serverToEdit);
        int serverIndex = app.getServers().indexOf(serverToEdit);
        if (!isEditable(serverIndex)) return;
        Server updatedServer = new Server(ipAddress, serverName, userName, password);
        // erase old server
        servers.set(serverIndex, new Server("", "", "", ""));
        if (!servers.contains(updatedServer)) {
            servers.set(serverIndex, updatedServer);
            app.commit(servers);
            app.addHistory(String.format(editSuccessMessage, serverToEdit.getServerName()));
        } else {
            // if edit failure, set old server back to index
            servers.set(serverIndex, serverToEdit);
            app.addHistory(String.format(editFailureMessage, serverName));
        }
    }
}
