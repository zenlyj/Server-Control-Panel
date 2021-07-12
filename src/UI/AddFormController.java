package UI;

import Logic.AddCommand;
import Logic.Command;
import Logic.Parser;
import Logic.ParserException;
import Model.App;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXML;

public class AddFormController {
    private App app;
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
            Command cmd = new AddCommand(app, userName, password, serverName, ip);
            cmd.execute();
            Node source = (Node) e.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        } catch (ParserException ex){
            app.addHistory(ex.getMessage());
        }
    }

    @FXML
    public void handleCancel(Event e) {
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void setApp(App app) {
        this.app = app;
    }
}
