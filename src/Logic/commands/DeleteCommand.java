package Logic.commands;

import Model.App;
import Model.Server;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends Command {
    private App app;
    private List<Server> serversToDelete;
    private final String deleteSuccessMessage = "%s has been successfully deleted\n";

    public DeleteCommand(App app, List<Server> serversToDelete) {
        this.serversToDelete = new ArrayList<>(serversToDelete);
        this.app = app;
    }

    public void execute() {
        ObservableList<Server> servers = app.getServers();
        for (Server server : serversToDelete) {
            app.setServerInDelete(server);
            servers.remove(server);
            app.addHistory(String.format(deleteSuccessMessage, server.getServerName()));
        }
        app.commit(servers);
    }
}
