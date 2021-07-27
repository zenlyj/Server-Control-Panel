package Model;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class App {
    private final ObservableList<Server> servers;
    private LinkedList<Server> serversInEdit;
    private LinkedList<Server> serversInDelete;
    private final LinkedList<Server> serversInChange;
    private final Storage db;
    private final StringProperty history;
    private final DateTimeFormatter timeFormatter;

    public App() {
        db = new Storage();
        List<Server> retrieved = db.retrieve();
        servers = FXCollections.observableList(retrieved);
        serversInEdit = new LinkedList<>();
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
        newHistory = String.format("%1$s %2$s\n", time.format(timeFormatter), newHistory);
        String updatedHistory = (currentHistory == null || currentHistory.length() > 9000) ? newHistory : String.format("%1$s%2$s", currentHistory, newHistory);
        this.history.set(updatedHistory);
    }

    public void setServerInEdit(Server serverInEdit) {
        this.serversInEdit.add(serverInEdit);
    }

    public void setServerInDelete(Server serverInDelete) {
        this.serversInDelete.add(serverInDelete);
    }

    public void setServerInChange(Server serverInChange) {
        this.serversInChange.add(serverInChange);
    }

    public void removeServerInChange(Server serverInChange) {
        this.serversInChange.remove(serverInChange);
    }

    public void removeServersInEdit() {
        this.serversInEdit = new LinkedList<>();
    }

    public void removeServersInDelete() {
        this.serversInDelete = new LinkedList<>();
    }

    public boolean isServerInEdit(Server serverIndex) {
        return this.serversInEdit.contains(serverIndex);
    }

    public boolean isServerInDelete(Server server) {
        return this.serversInDelete.contains(server);
    }

    public boolean isServerInChange(Server server) {
        return this.serversInChange.contains(server);
    }
}
