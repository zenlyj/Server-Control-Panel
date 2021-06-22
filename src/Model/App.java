package Model;

import java.util.LinkedList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class App {
    private ObservableList<Server> servers;
    private LinkedList<Integer> serversInEdit;
    private Storage db;

    public App() {
        db = new Storage();
        List<Server> retrieved = db.retrieve();
        servers = FXCollections.observableList(retrieved);
        serversInEdit = new LinkedList<>();
    }

    public void commit(List<Server> servers) {
        db.store(servers);
    }

    public ObservableList<Server> getServers() {
        return this.servers;
    }

    public void setServerInEdit(int serverInEdit) {
        this.serversInEdit.push(serverInEdit);
    }

    public void removeServerInEdit(int serverInEdit) {
        this.serversInEdit.removeFirstOccurrence(serverInEdit);
    }

    public boolean isServerInEdit(int serverIndex) {
        return this.serversInEdit.contains(serverIndex);
    }
}
