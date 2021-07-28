package UI.controllers;

import Logic.commands.ChangeIPCommand;
import Logic.util.Parser;
import Logic.util.ParserException;
import Model.App;
import Model.Server;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChangeIPFormController {
    private App app;
    private Server serverToEdit;
    @FXML
    private TextField ipField;

    @FXML
    public void handleConfirm(Event e) {
        try {
            String ip = Parser.parseIPAddress(ipField.getText());
            ChangeIPCommand cmd = new ChangeIPCommand(app, serverToEdit, ip);
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
        this.ipField.setText(serverToEdit.getIpAddress());
    }
}
