package UI;

import Model.App;
import UI.controllers.EditFormController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class EditForm {
    public static Scene getForm(App app, int selectedIdx) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(EditForm.class.getResource("/fxml/EditForm.fxml"));
        GridPane grid = loader.<GridPane>load();
        EditFormController controller = loader.getController();
        controller.init(app, selectedIdx);
        return new Scene(grid);
    }
}
