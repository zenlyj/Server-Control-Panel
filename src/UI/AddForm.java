package UI;

import Model.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class AddForm {
    public static Scene getForm(App app) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(AddForm.class.getResource("/AddForm.fxml"));
        GridPane grid = loader.<GridPane>load();
        AddFormController controller = loader.getController();
        controller.setApp(app);
        return new Scene(grid);
    }
}
