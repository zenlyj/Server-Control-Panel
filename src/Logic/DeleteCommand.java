package Logic;

import Model.App;
import Model.Server;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends Command {
    private App app;
    private List<Server> serversToDelete;

    public DeleteCommand(App app, List<Server> serversToDelete) {
        this.serversToDelete = new ArrayList<>(serversToDelete);
        this.app = app;
    }

    public void execute() {
        ObservableList<Server> servers = app.getServers();
        for (Server s : serversToDelete) {
            servers.remove(s);
        }
        app.commit(servers);
    }
}
