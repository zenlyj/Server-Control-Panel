package Model;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class App {
    private ObservableList<Server> servers;
    private int serverInEdit;
    private Storage db;

    public App() {
        db = new Storage();
        List<Server> retrieved = db.retrieve();
        servers = FXCollections.observableList(retrieved);
        serverInEdit = -1;
    }

    public void commit(List<Server> servers) {
        db.store(servers);
    }

    public ObservableList<Server> getServers() {
        return this.servers;
    }

    public void setServerInEdit(int serverInEdit) {
        this.serverInEdit = serverInEdit;
    }

    public int getServerInEdit() {
        return serverInEdit;
    }
}
