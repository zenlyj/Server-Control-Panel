package UI.controllers;

import Logic.commands.Command;
import Logic.commands.EditCommand;
import Logic.util.Parser;
import Logic.util.ParserException;
import Model.App;
import Model.Server;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditFormController {
    private App app;
    private Server serverToEdit;
    @FXML
    private TextField userNameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField ipField;
    @FXML
    private TextField hostNameField;

    @FXML
    public void handleConfirm(Event e) {
        try {
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String serverName = hostNameField.getText().strip();
            String ip = Parser.parseIPAddress(ipField.getText());
            Command cmd = new EditCommand(app, serverToEdit, userName, password, serverName, ip);
            cmd.execute();
            Node source = (Node) e.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        } catch (ParserException ex) {
            app.addHistory(ex.getMessage());
        }
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
        this.userNameField.setText(serverToEdit.getUserName());
        this.passwordField.setText(serverToEdit.getPassword());
        this.hostNameField.setText(serverToEdit.getServerName());
        this.ipField.setText(serverToEdit.getIpAddress());
    }
}
