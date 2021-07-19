package UI;

import Logic.DeleteCommand;
import Logic.ImportCommand;
import Logic.PingCommand;
import Logic.RemoteDesktopCommand;
import Logic.ShutdownCommand;
import Model.App;
import Model.Server;
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
    private Optional<Server> serverToShowTime = Optional.empty();
    private final String noItemSelectedMessage = "There are currently no item(s) selected!\n";

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
    private TableView tableView;

    @FXML
    public void handleAdd(Event e) {
        try {
            Stage stage = new Stage();
            stage.setScene(AddForm.getForm(app));
            stage.show();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void handleDelete(Event e) {
        try {
            boolean isSelected = selectionCheck();
            if (isSelected) {
                List<Server> serversToDelete = tableView.getSelectionModel().getSelectedItems();
                DeleteCommand deleteCommand = new DeleteCommand(app, serversToDelete);
                deleteCommand.execute();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void handlePing(Event e) {
        boolean isSelected = selectionCheck();
        if (isSelected) {
            List<Integer> serverIndices = tableView.getSelectionModel().getSelectedIndices();
            PingCommand pingCommand = new PingCommand(app, serverIndices);
            pingCommand.execute();
        }
    }

    @FXML
    public void handleShutdown(Event e) {
        boolean isSelected = selectionCheck();
        if (isSelected) {
            List<Server> serversToShutdown = (List<Server>) tableView.getSelectionModel().getSelectedItems();
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
    public void handleEdit(Event e) {
        try {
            boolean isSelected = selectionCheck();
            if (isSelected) {
                int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
                Stage stage = new Stage();
                stage.setScene(EditForm.getForm(app, selectedIndex));
                stage.show();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void handleChangeIP(Event e) {
        try {
            boolean isSelected = selectionCheck();
            if (isSelected) {
                int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
                Stage stage = new Stage();
                stage.setScene(ChangeIPForm.getForm(app, selectedIndex));
                stage.show();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void handleRename(Event e) {
        try {
            boolean isSelected = selectionCheck();
            if (isSelected) {
                int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
                Stage stage = new Stage();
                stage.setScene(ChangeNameForm.getForm(app, selectedIndex));
                stage.show();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    public void handleRemoteDesktop(Event e) {
        boolean isSelected = selectionCheck();
        if (isSelected) {
            Server server = (Server) tableView.getSelectionModel().getSelectedItem();
            RemoteDesktopCommand remoteDesktopCommand = new RemoteDesktopCommand(app, server);
            remoteDesktopCommand.execute();
        }
    }

    private boolean selectionCheck() {
        if (tableView.getSelectionModel().getSelectedItem() == null) {
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
        app.getServers().addListener((ListChangeListener) change -> setTableData(app.getServers()));
    }

    private void setTableData(List<Server> servers) {
        List<Integer> selectedIndices = new ArrayList<>(tableView.getSelectionModel().getSelectedIndices());
        tableView.getItems().clear();
        servers.forEach(server -> {
            tableView.getItems().add(server);
        });
        for (Integer i : selectedIndices) {
            tableView.getSelectionModel().select(i.intValue());
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
        selectionModel.selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            setServerInfo(Optional.ofNullable(newSelection));
        });
    }

    private void setServerInfo(Optional<Server> newSelection) {
        if (newSelection.isEmpty()) {
            userNameDetails.setText("");
            passwordDetails.setText("");
            serverNameDetails.setText("");
            ipDetails.setText("");
            serverToShowTime = Optional.empty();
        } else {
            userNameDetails.setText(String.format("User name: %s", newSelection.get().getUserName()));
            passwordDetails.setText(String.format("Password: %s", newSelection.get().getPassword()));
            serverNameDetails.setText(String.format("Server name: %s", newSelection.get().getServerName()));
            ipDetails.setText(String.format("IP Address: %s", newSelection.get().getIpAddress()));
            serverToShowTime = newSelection;
        }
    }

    private void initButtons() {
        Image add = new Image(MainPageFormController.class.getResourceAsStream("/plus.png"));
        Image delete = new Image(MainPageFormController.class.getResourceAsStream("/substract.png"));
        Image ping = new Image(MainPageFormController.class.getResourceAsStream("/local-network.png"));
        Image shutdown = new Image(MainPageFormController.class.getResourceAsStream("/power.png"));
        Image edit = new Image(MainPageFormController.class.getResourceAsStream("/edit.png"));
        Image changeIP = new Image(MainPageFormController.class.getResourceAsStream("/remote-control.png"));
        Image changeName = new Image(MainPageFormController.class.getResourceAsStream("/remote-control.png"));
        Image remoteDesktop = new Image(MainPageFormController.class.getResourceAsStream("/slide.png"));
        Image massImport = new Image(MainPageFormController.class.getResourceAsStream("/import.png"));

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
        ScheduledService<Void> service = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        if (serverToShowTime.isPresent()) {
                            Platform.runLater(() -> uptime.setText("Uptime: " + serverToShowTime.get().upTime()));
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
