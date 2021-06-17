package UI;

import Model.App;
import Model.Server;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class Table {
    private TableView tableView;

    public Table(App app) {
        this.tableView = new TableView();
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Server, String> column1 = new TableColumn<>("Server Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("serverName"));
        TableColumn<Server, String> column2 = new TableColumn<>("Status");
        column2.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        setTableData(app.getServers());
        app.getServers().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                setTableData(app.getServers());
            }
        });
    }

    private void setTableData(List<Server> servers) {
        tableView.getItems().clear();
        servers.forEach(server -> {
            tableView.getItems().add(server);
        });
    }

    public TableView getTable() {
        return this.tableView;
    }
}
