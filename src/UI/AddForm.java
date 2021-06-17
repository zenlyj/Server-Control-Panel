package UI;

import Logic.AddCommand;
import Logic.Command;
import Model.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AddForm {

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

    private static TextField setUserName(GridPane grid) {
        Label userName = new Label("User Name:");
        TextField userNameField = new TextField();
        grid.add(userName, 0, 1);
        grid.add(userNameField, 1, 1);
        return userNameField;
    }

    private static TextField setPassWord(GridPane grid) {
        Label password = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        grid.add(password, 0, 2);
        grid.add(passwordField, 1, 2);
        return passwordField;
    }

    private static TextField setServerName(GridPane grid) {
        Label serverName = new Label("Server Name:");
        TextField serverNameField = new TextField();
        grid.add(serverName, 0, 3);
        grid.add(serverNameField, 1, 3);
        return serverNameField;
    }

    private static TextField setIP(GridPane grid) {
        Label ipAddress = new Label("IP Address:");
        TextField ipField = new TextField();
        grid.add(ipAddress, 0, 4);
        grid.add(ipField, 1, 4);
        return ipField;
    }

    private static void setButtons(GridPane grid, Button confirmButton, Button cancelBtn) {
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(confirmButton);
        hbBtn.getChildren().add(cancelBtn);
        grid.add(hbBtn, 1, 5);
    }

    private static void setConfirmBtnHandler(App app, Button confirmBtn, TextField userNameField, TextField passwordField, TextField serverNameField, TextField ipField) {
        confirmBtn.setOnAction(value -> {
            Command cmd = new AddCommand(app, userNameField.getText(), passwordField.getText(), serverNameField.getText(), ipField.getText());
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


    public static Scene getForm(App app) {
        GridPane grid = createGridPane();
        setTitle(grid, "New Server");
        TextField usernameField = setUserName(grid);
        TextField passwordField = setPassWord(grid);
        TextField serverNameField = setServerName(grid);
        TextField ipField = setIP(grid);

        Button confirmBtn = new Button("Confirm");
        setConfirmBtnHandler(app, confirmBtn, usernameField, passwordField, serverNameField, ipField);
        Button cancelBtn = new Button("Cancel");
        setCancelBtnHandler(cancelBtn);
        setButtons(grid, confirmBtn, cancelBtn);

        return new Scene(grid, 300, 275);
    }

}
