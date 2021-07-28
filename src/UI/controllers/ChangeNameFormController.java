package UI.controllers;

import Logic.commands.ChangeHostNameCommand;
import Model.App;
import Model.Server;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChangeNameFormController {
    private App app;
    private Server serverToEdit;
    @FXML
    private TextField hostNameField;

    @FXML
    public void handleConfirm(Event e) {
        String hostName = hostNameField.getText().strip();
        ChangeHostNameCommand cmd = new ChangeHostNameCommand(app, serverToEdit, hostName);
        cmd.execute();
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleCancel(Event e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void init(App app, Server selectedServer) {
        this.app = app;
        this.serverToEdit = selectedServer;
        this.hostNameField.setText(serverToEdit.getServerName());
    }
}
