package UI;

import Logic.DeleteCommand;
import Logic.PingCommand;
import Logic.ShutdownCommand;
import Model.App;
import Model.Server;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class FunctionBar {
    private ToolBar toolBar;
    private App app;
    private TableView tableView;

    public FunctionBar(App app, TableView tableView) {
        this.toolBar = new ToolBar();
        this.app = app;
        this.tableView = tableView;
    }

    private void setAddServerBtn() {
        Button addButton = new Button("Add");
        addButton.setOnAction(actionEvent -> {
            Stage stage = new Stage();
            stage.setScene(AddForm.getForm(app));
            stage.show();
        });
        toolBar.getItems().add(addButton);
    }

    private void setDeleteServerBtn() {
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(actionEvent -> {
            List<Server> serversToDelete = tableView.getSelectionModel().getSelectedItems();
            DeleteCommand deleteCommand = new DeleteCommand(app, serversToDelete);
            deleteCommand.execute();
        });
        toolBar.getItems().add(deleteButton);
    }

    private void setEditServerBtn() {
        Button editButton = new Button("Edit");
        editButton.setOnAction(actionEvent -> {
            int selectedIdx = tableView.getSelectionModel().getSelectedIndex();
            Stage stage = new Stage();
            stage.setScene(EditForm.getForm(app, selectedIdx));
            stage.show();
        });
        editButton.setVisible(false);
        tableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener) change -> {
            int numSelected = tableView.getSelectionModel().getSelectedItems().size();
            if (numSelected != 1) {
                editButton.setVisible(false);
            } else {
                editButton.setVisible(true);
            }
        });
        toolBar.getItems().add(editButton);
    }

    private void setShutdownBtn() {
        Button shutdownButton = new Button("Shutdown");
        shutdownButton.setOnAction(actionEvent -> {
            List<Server> serversToShutdown = (List<Server>) tableView.getSelectionModel().getSelectedItems();
            ShutdownCommand shutdownCommand = new ShutdownCommand(app, serversToShutdown);
            shutdownCommand.execute();
        });
        toolBar.getItems().add(shutdownButton);
    }

    private void setPingBtn() {
        Button pingButton = new Button("Ping");
        pingButton.setOnAction(actionEvent -> {
            List<Integer> serverIndices = tableView.getSelectionModel().getSelectedIndices();
            PingCommand pingCommand = new PingCommand(app, serverIndices);
            pingCommand.execute();
        });
        toolBar.getItems().add(pingButton);
    }

    private void setChangeIPBtn() {
        Button changeIPButton = new Button("Change IP");
        changeIPButton.setOnAction(actionEvent -> {
            int serverIdx = tableView.getSelectionModel().getSelectedIndex();
            Stage stage = new Stage();
            stage.setScene(ChangeIPForm.getForm(app, serverIdx));
            stage.show();
        });
        changeIPButton.setVisible(false);
        tableView.getSelectionModel().getSelectedItems().addListener((ListChangeListener) change -> {
            if (tableView.getSelectionModel().getSelectedItems().size() != 1) {
                changeIPButton.setVisible(false);
            } else {
                changeIPButton.setVisible(true);
            }
        });
        toolBar.getItems().add(changeIPButton);
    }

    public HBox getFunctionBar() {
        setAddServerBtn();
        setDeleteServerBtn();
        setPingBtn();
        setShutdownBtn();
        setEditServerBtn();
        setChangeIPBtn();
        HBox hbox = new HBox(toolBar);
        return hbox;
    }

}
