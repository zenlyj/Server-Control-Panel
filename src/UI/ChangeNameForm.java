package UI;

import Model.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class ChangeNameForm {
    public static Scene getForm(App app, int selectedIdx) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(ChangeNameForm.class.getResource("/ChangeNameForm.fxml"));
        GridPane grid = loader.<GridPane>load();
        ChangeNameFormController controller = loader.getController();
        controller.init(app, selectedIdx);
        return new Scene(grid);
    }
}
