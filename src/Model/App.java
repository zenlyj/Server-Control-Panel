package Model;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class App {
    private ObservableList<Server> servers;
    private Set<Integer> serversInEdit;
    private LinkedList<Server> serversInDelete;
    private LinkedList<Server> serversInChange;
    private Storage db;
    private StringProperty history;
    private DateTimeFormatter timeFormatter;

    public App() {
        db = new Storage();
        List<Server> retrieved = db.retrieve();
        servers = FXCollections.observableList(retrieved);
        serversInEdit = new HashSet<>();
        serversInDelete = new LinkedList<>();
        serversInChange = new LinkedList<>();
        history = new SimpleStringProperty();
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    }

    public void commit(List<Server> servers) {
        db.store(servers);
    }

    public ObservableList<Server> getServers() {
        return this.servers;
    }

    public StringProperty getHistory() {
        return this.history;
    }

    public void addHistory(String newHistory) {
        String currentHistory = this.history.get();
        LocalTime time = LocalTime.now(ZoneId.systemDefault());
        newHistory = time.format(timeFormatter) + "  " + newHistory;
        String updatedHistory = currentHistory == null ? newHistory : currentHistory+newHistory;
        this.history.set(updatedHistory);
    }

    public void setServerInEdit(int serverInEdit) {
        this.serversInEdit.add(serverInEdit);
    }

    public void removeServerInEdit(int serverInEdit) {
        this.serversInEdit.remove(serverInEdit);
    }

    public void setServerInDelete(Server serverInDelete) {
        this.serversInDelete.add(serverInDelete);
    }

    public void removeServerInDelete(Server serverInDelete) {
        this.serversInDelete.remove(serverInDelete);
    }

    public void setServerInChange(Server serverInChange) {
        this.serversInChange.add(serverInChange);
    }

    public void removeServerInChange(Server serverInChange) {
        this.serversInChange.remove(serverInChange);
    }

    public boolean isServerInEdit(int serverIndex) {
        return this.serversInEdit.contains(serverIndex);
    }

    public boolean isServerInDelete(Server server) {
        return this.serversInDelete.contains(server);
    }

    public boolean isServerInChange(Server server) {
        return this.serversInChange.contains(server);
    }
}
