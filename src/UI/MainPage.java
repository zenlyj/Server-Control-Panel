package UI;

import Logic.CheckConnectionCommand;
import Logic.DeleteCommand;
import Logic.PingCommand;
import Logic.ShutdownCommand;
import Model.App;
import Model.Server;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import javax.tools.Tool;
import java.util.List;

public class MainPage {
    private TableView tableView;
    private GridPane primaryGrid;
    private GridPane secondaryGrid;

    public GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        return grid;
    }

    private void setAddServerBtn(ToolBar toolBar, App app) {
        Button addButton = new Button("Add");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Stage stage = new Stage();
                stage.setScene(AddForm.getForm(app));
                stage.show();
            }
        });
        toolBar.getItems().add(addButton);
    }

    private void setDeleteServerBtn(ToolBar toolBar, App app) {
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<Server> serversToDelete = tableView.getSelectionModel().getSelectedItems();
                DeleteCommand deleteCommand = new DeleteCommand(app, serversToDelete);
                deleteCommand.execute();
            }
        });
        toolBar.getItems().add(deleteButton);
    }

    private void setShutdownBtn(ToolBar toolBar, App app) {
        Button shutdownButton = new Button("Shutdown");
        shutdownButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<Server> serversToShutdown = (List<Server>) tableView.getSelectionModel().getSelectedItems();
                ShutdownCommand shutdownCommand = new ShutdownCommand(serversToShutdown);
                shutdownCommand.execute();
            }
        });
        toolBar.getItems().add(shutdownButton);
    }

    private void setPingBtn(ToolBar toolBar, App app) {
        Button pingButton = new Button("Ping");
        pingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                List<Integer> serverIndices = tableView.getSelectionModel().getSelectedIndices();
                PingCommand pingCommand = new PingCommand(app, serverIndices);
                pingCommand.execute();
            }
        });
        toolBar.getItems().add(pingButton);
    }

    public void initTableView(GridPane grid, App app) {
        tableView = new TableView();
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        selectionModel.selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            setServerInfo(newSelection);
        });

        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Server, String> column1 = new TableColumn<>("Server Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("serverName"));
        TableColumn<Server, String> column2 = new TableColumn<>("Status");
        column2.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);

        grid.add(tableView, 0, 1);

        app.getServers().addListener(new ListChangeListener() {
            @Override
            public void onChanged(ListChangeListener.Change change) {
                setTableData(app.getServers());
            }
        });
    }

    public void setTableData(List<Server> servers) {
        tableView.getItems().clear();
        servers.forEach(server -> {
            tableView.getItems().add(server);
        });
    }

    public void initServerInfo(GridPane grid) {
        secondaryGrid = new GridPane();
        secondaryGrid.setAlignment(Pos.TOP_LEFT);
        secondaryGrid.setVgap(25);
        secondaryGrid.setHgap(10);
        secondaryGrid.setPadding(new Insets(5, 5, 5, 5));
        grid.add(secondaryGrid, 1, 1);
    }

    public void setServerInfo(Server server) {
        secondaryGrid.getChildren().clear();
        if (server == null) return;
        secondaryGrid.add(new Label("User name: " + server.getUserName()), 0, 0);
        secondaryGrid.add(new Label("Password " + server.getPassword()), 0, 1);
        secondaryGrid.add(new Label("Server name: " + server.getServerName()), 0, 2);
        secondaryGrid.add(new Label("IP Address: " + server.getIpAddress()), 0, 3);
    }

    public Scene getMainPage(App app) {
        GridPane grid = createGridPane();
        ToolBar toolBar = new ToolBar();
        HBox hb = new HBox(toolBar);
        grid.add(hb, 0, 0);
        setAddServerBtn(toolBar, app);
        setDeleteServerBtn(toolBar, app);
        setPingBtn(toolBar, app);
        setShutdownBtn(toolBar, app);
        initTableView(grid, app);
        setTableData(app.getServers());
        initServerInfo(grid);
        Scene scene = new Scene(grid, 500, 400);
        return scene;
    }
}
