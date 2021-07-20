package UI;

import Model.App;
import UI.controllers.ChangeIPFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class ChangeIPForm {
    public static Scene getForm(App app, int selectedIdx) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChangeIPForm.class.getResource("/fxml/ChangeIPForm.fxml"));
        GridPane grid = loader.<GridPane>load();
        ChangeIPFormController controller = loader.getController();
        controller.init(app, selectedIdx);
        return new Scene(grid);
    }
}
