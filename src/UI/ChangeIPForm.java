package UI;

import Logic.ChangeIPCommand;
import Logic.Command;
import Model.App;
import Model.Server;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChangeIPForm {

    private static GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        return grid;
    }

    private static void setTitle(GridPane grid, String title) {
        Text sceneTitle = new Text(title);
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);
    }

    private static TextField setIP(GridPane grid, String currIP) {
        Label ipAddress = new Label("IP Address:");
        TextField ipField = new TextField();
        ipField.setText(currIP);
        grid.add(ipAddress, 0, 1);
        grid.add(ipField, 1, 1);
        return ipField;
    }

    private static TextField setServerName(GridPane grid, String currServerName) {
        Label serverName = new Label("Server Name:");
        TextField serverNameField = new TextField();
        serverNameField.setText(currServerName);
        grid.add(serverName, 0, 2);
        grid.add(serverNameField, 1, 2);
        return serverNameField;
    }


    private static void setButtons(GridPane grid, Button confirmButton, Button cancelBtn) {
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(confirmButton);
        hbBtn.getChildren().add(cancelBtn);
        grid.add(hbBtn, 1, 3);
    }

    private static void setConfirmBtnHandler(App app, int selectedIdx, Button confirmBtn, TextField ipField, TextField serverName) {
        confirmBtn.setOnAction(value -> {
            Command cmd = new ChangeIPCommand(app, selectedIdx, ipField.getText(), serverName.getText());
            cmd.execute();
            Node source = (Node) value.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
    }

    private static void setCancelBtnHandler(Button cancelBtn) {
        cancelBtn.setOnAction(value -> {
            Node source = (Node) value.getSource();
            Stage stage = (Stage) source.getScene().getWindow();
            stage.close();
        });
    }

    public static Scene getForm(App app, int selectedIdx) {
        Server selectedServer = app.getServers().get(selectedIdx);
        GridPane grid = createGridPane();
        setTitle(grid, "Modify IP Address / Name");
        TextField ipField = setIP(grid, selectedServer.getIpAddress());
        TextField serverNameField = setServerName(grid, selectedServer.getServerName());
        Button confirmBtn = new Button("Confirm");
        setConfirmBtnHandler(app, selectedIdx, confirmBtn, ipField, serverNameField);
        Button cancelBtn = new Button("Cancel");
        setCancelBtnHandler(cancelBtn);
        setButtons(grid, confirmBtn, cancelBtn);

        return new Scene(grid, 300, 275);
    }
}
