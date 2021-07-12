package UI;

import Model.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

import java.net.URL;

public class EditForm {
    public static Scene getForm(App app, int selectedIdx) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL("file:///C:/school/kratos/src/FXML/EditForm.fxml"));
        GridPane grid = loader.<GridPane>load();
        EditFormController controller = loader.getController();
        controller.init(app, selectedIdx);
        return new Scene(grid);
    }
}
