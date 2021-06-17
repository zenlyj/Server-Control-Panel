package UI;

import Model.Server;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

public class ServerDetails {
    private GridPane detailsGrid;
    private TableView tableView;

    public ServerDetails(TableView tableView) {
        this.detailsGrid = new GridPane();
        detailsGrid.setAlignment(Pos.TOP_LEFT);
        detailsGrid.setVgap(25);
        detailsGrid.setHgap(10);
        detailsGrid.setPadding(new Insets(5, 5, 5, 5));

        this.tableView = tableView;
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            setServerInfo(newSelection);
        });
    }

    private void setServerInfo(Server server) {
        detailsGrid.getChildren().clear();
        if (server == null) return;
        detailsGrid.add(new Label("User name: " + server.getUserName()), 0, 0);
        detailsGrid.add(new Label("Password " + server.getPassword()), 0, 1);
        detailsGrid.add(new Label("Server name: " + server.getServerName()), 0, 2);
        detailsGrid.add(new Label("IP Address: " + server.getIpAddress()), 0, 3);
    }

    public GridPane getDetails() {
        return this.detailsGrid;
    }
}
