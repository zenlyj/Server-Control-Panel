package util;

import Model.App;
import Model.Server;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AppStub extends App {
    private final ObservableList<Server> servers;
    private LinkedList<Server> serversInEdit;
    private LinkedList<Server> serversInDelete;
    private final LinkedList<Server> serversInChange;
    private final StringProperty history;

    public AppStub() {
        this.servers = FXCollections.observableList(new ArrayList<>());
        this.serversInEdit = new LinkedList<>();
        this.serversInDelete = new LinkedList<>();
        this.serversInChange = new LinkedList<>();
        this.history = new SimpleStringProperty();
    }

    public AppStub(List<Server> servers) {
        this.servers = FXCollections.observableList(servers);
        this.serversInEdit = new LinkedList<>();
        this.serversInDelete = new LinkedList<>();
        this.serversInChange = new LinkedList<>();
        this.history = new SimpleStringProperty();
    }

    @Override
    public void addHistory(String result) {
        String currentHistory = this.history.get();
        String newHistory = String.format("%s\n", result);
        String updatedHistory = (currentHistory == null || currentHistory.length() > 9000) ? newHistory : String.format("%1$s%2$s", currentHistory, newHistory);
        this.history.set(updatedHistory);
    }

    @Override
    public StringProperty getHistory() {
        return this.history;
    }

    @Override
    public void commit(List<Server> servers) {
        // remove dependency from Storage class
    }

    @Override
    public ObservableList<Server> getServers() {
        return servers;
    }

    @Override
    public void setServerInDelete(Server serverInDelete) {
        this.serversInDelete.add(serverInDelete);
    }
}
