package UI.controllers;

import Logic.commands.DeleteCommand;
import Logic.commands.ImportCommand;
import Logic.commands.PingCommand;
import Logic.commands.RemoteDesktopCommand;
import Logic.commands.ShutdownCommand;
import Model.App;
import Model.Server;
import UI.AddForm;
import UI.ChangeIPForm;
import UI.ChangeNameForm;
import UI.EditForm;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainPageFormController {
    private App app;
    private Server serverToShowTime;
    private final String noItemSelectedMessage = "There are currently no item(s) selected!";

    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button pingButton;
    @FXML
    private Button shutdownButton;
    @FXML
    private Button importButton;
    @FXML
    private Button editButton;
    @FXML
    private Button changeIPButton;
    @FXML
    private Button renameButton;
    @FXML
    private Button remoteDesktopButton;
    @FXML
    private Label userNameDetails;
    @FXML
    private Label passwordDetails;
    @FXML
    private Label serverNameDetails;
    @FXML
    private Label ipDetails;
    @FXML
    private Label uptime;
    @FXML
    private TextArea historyBox;
    @FXML
    private TableView<Server> tableView;

    @FXML
    public void handleAdd() {
        try {
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.setScene(AddForm.getForm(app));
            stage.show();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void handleDelete() {
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        boolean isSelected = selectionCheck();
        if (isSelected) {
            List<Server> serversToDelete = selectionModel.getSelectedItems();
            DeleteCommand deleteCommand = new DeleteCommand(app, serversToDelete);
            deleteCommand.execute();
        }
    }

    @FXML
    public void handlePing() {
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        boolean isSelected = selectionCheck();
        if (isSelected) {
            List<Server> serverToPing = selectionModel.getSelectedItems();
            PingCommand pingCommand = new PingCommand(app, serverToPing);
            pingCommand.execute();
        }
    }

    @FXML
    public void handleShutdown() {
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        boolean isSelected = selectionCheck();
        if (isSelected) {
            List<Server> serversToShutdown = selectionModel.getSelectedItems();
            ShutdownCommand shutdownCommand = new ShutdownCommand(app, serversToShutdown);
            shutdownCommand.execute();
        }
    }

    @FXML
    public void handleImport(Event e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File importFile = fileChooser.showOpenDialog(stage);
        if (importFile != null) {
            ImportCommand cmd = new ImportCommand(app, importFile);
            cmd.execute();
        }
    }

    @FXML
    public void handleEdit() {
        try {
            boolean isSelected = selectionCheck();
            TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
            if (isSelected) {
                Server selectedServer = selectionModel.getSelectedItem();
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setScene(EditForm.getForm(app, selectedServer));
                stage.show();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void handleChangeIP() {
        try {
            boolean isSelected = selectionCheck();
            TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
            if (isSelected) {
                Server selectedServer = selectionModel.getSelectedItem();
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setScene(ChangeIPForm.getForm(app, selectedServer));
                stage.show();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void handleRename() {
        try {
            boolean isSelected = selectionCheck();
            TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
            if (isSelected) {
                Server selectedServer = selectionModel.getSelectedItem();
                Stage stage = new Stage();
                stage.setResizable(false);
                stage.setScene(ChangeNameForm.getForm(app, selectedServer));
                stage.show();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void handleRemoteDesktop() {
        boolean isSelected = selectionCheck();
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        if (isSelected) {
            Server server = selectionModel.getSelectedItem();
            RemoteDesktopCommand remoteDesktopCommand = new RemoteDesktopCommand(app, server);
            remoteDesktopCommand.execute();
        }
    }

    private boolean selectionCheck() {
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        if (selectionModel.getSelectedItem() == null) {
            app.addHistory(noItemSelectedMessage);
            return false;
        }
        return true;
    }

    public void initTableView() {
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setStyle("-fx-font-size: 14; fx-font-family: Tahoma");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Server, String> column1 = new TableColumn<>("Server Name");
        column1.setCellValueFactory(new PropertyValueFactory<>("serverName"));
        TableColumn<Server, String> column2 = new TableColumn<>("Status");
        column2.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        setTableData(app.getServers());
        app.getServers().addListener((ListChangeListener<Server>) change -> setTableData(app.getServers()));
    }

    private void setTableData(List<Server> servers) {
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        List<Integer> selectedIndices = new ArrayList<>(selectionModel.getSelectedIndices());
        tableView.getItems().clear();
        servers.forEach(server -> tableView.getItems().add(server));
        for (Integer i : selectedIndices) {
            selectionModel.select(i);
        }
    }

    public void initHistoryBox() {
        StringProperty history = app.getHistory();
        history.addListener((observable, oldValue, newValue) -> {
            historyBox.setText(history.get());
            historyBox.setScrollTop(Double.MAX_VALUE);
        });
        historyBox.setEditable(false);
    }

    public void initServerDetails() {
        TableView.TableViewSelectionModel<Server> selectionModel = tableView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((obs, oldSelection, newSelection) -> setServerInfo(newSelection));
    }

    private void setServerInfo(Server newSelection) {
        if (Optional.ofNullable(newSelection).isEmpty()) {
            userNameDetails.setText("");
            passwordDetails.setText("");
            serverNameDetails.setText("");
            ipDetails.setText("");
            serverToShowTime = null;
        } else {
            userNameDetails.setText(String.format("User name: %s", newSelection.getUserName()));
            passwordDetails.setText(String.format("Password: %s", newSelection.getPassword()));
            serverNameDetails.setText(String.format("Server name: %s", newSelection.getServerName()));
            ipDetails.setText(String.format("IP Address: %s", newSelection.getIpAddress()));
            serverToShowTime = newSelection;
        }
    }

    private void initButtons() {
        Image add = new Image(MainPageFormController.class.getResourceAsStream("/images/plus.png"));
        Image delete = new Image(MainPageFormController.class.getResourceAsStream("/images/subtract.png"));
        Image ping = new Image(MainPageFormController.class.getResourceAsStream("/images/local-network.png"));
        Image shutdown = new Image(MainPageFormController.class.getResourceAsStream("/images/power.png"));
        Image edit = new Image(MainPageFormController.class.getResourceAsStream("/images/edit.png"));
        Image changeIP = new Image(MainPageFormController.class.getResourceAsStream("/images/remote-control.png"));
        Image changeName = new Image(MainPageFormController.class.getResourceAsStream("/images/remote-control.png"));
        Image remoteDesktop = new Image(MainPageFormController.class.getResourceAsStream("/images/slide.png"));
        Image massImport = new Image(MainPageFormController.class.getResourceAsStream("/images/import.png"));

        addButton.setGraphic(new ImageView(add));
        deleteButton.setGraphic(new ImageView(delete));
        pingButton.setGraphic(new ImageView(ping));
        shutdownButton.setGraphic(new ImageView(shutdown));
        editButton.setGraphic(new ImageView(edit));
        changeIPButton.setGraphic(new ImageView(changeIP));
        renameButton.setGraphic(new ImageView(changeName));
        remoteDesktopButton.setGraphic(new ImageView(remoteDesktop));
        importButton.setGraphic(new ImageView(massImport));
    }

    public void init(App app) {
        this.app = app;
        initButtons();
        initHistoryBox();
        initServerDetails();
        initTableView();
        ScheduledService<Void> service = new ScheduledService<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() {
                        if (Optional.ofNullable(serverToShowTime).isPresent()) {
                            Platform.runLater(() -> uptime.setText(String.format("Uptime: %s", serverToShowTime.upTime())));
                        } else {
                            Platform.runLater(() -> uptime.setText(""));
                        }
                        return null;
                    }
                };
            }
        };
        service.setPeriod(Duration.seconds(1));
        service.start();
    }
}
