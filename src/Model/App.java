package Model;

import java.util.ArrayList;
import java.util.List;

import UI.MainPage;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class App {
    private ObservableList<Server> servers;
    private Storage db;

    public App() {
        db = new Storage();
        List<Server> retrieved = db.retrieve();
        servers = FXCollections.observableList(retrieved);
    }

    public void commit(List<Server> servers) {
        db.store(servers);
    }

    public ObservableList<Server> getServers() {
        return this.servers;
    }
}
