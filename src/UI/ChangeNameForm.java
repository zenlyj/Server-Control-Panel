package UI;

import Model.App;
import Model.Server;
import UI.controllers.ChangeNameFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class ChangeNameForm {
    public static Scene getForm(App app, Server selectedServer) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChangeNameForm.class.getResource("/fxml/ChangeNameForm.fxml"));
        GridPane grid = loader.<GridPane>load();
        ChangeNameFormController controller = loader.getController();
        controller.init(app, selectedServer);
        return new Scene(grid);
    }
}
