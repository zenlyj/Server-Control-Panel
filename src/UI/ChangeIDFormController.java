package UI;

import Logic.ChangeIPCommand;
import Logic.Command;
import Logic.Parser;
import Logic.ParserException;
import Model.App;
import Model.Server;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ChangeIDFormController {
    private App app;
    private int selectedIndex;
    @FXML
    private TextField ipField;
    @FXML
    private TextField hostNameField;

    @FXML
    public void handleConfirm(Event e) {
        try {
            String hostName = hostNameField.getText().strip();
            String ip = Parser.parseIPAddress(ipField.getText());
            Command cmd = new ChangeIPCommand(app, selectedIndex, ip, hostName);
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

    public void init(App app, int selectedIndex) {
        this.app = app;
        this.selectedIndex = selectedIndex;
        Server serverToEdit = app.getServers().get(selectedIndex);
        this.hostNameField.setText(serverToEdit.getServerName());
        this.ipField.setText(serverToEdit.getIpAddress());
    }
}
