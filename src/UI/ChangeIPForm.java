package UI;

import Model.App;
import Model.Server;
import UI.controllers.ChangeIPFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class ChangeIPForm {
    public static Scene getForm(App app, Server selectedServer) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChangeIPForm.class.getResource("/fxml/ChangeIPForm.fxml"));
        GridPane grid = loader.<GridPane>load();
        ChangeIPFormController controller = loader.getController();
        controller.init(app, selectedServer);
        return new Scene(grid);
    }
}
