package UI;

import Model.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class MainPage {
    public Scene getMainPage(App app) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainPage.class.getResource("/MainPageForm.fxml"));
        GridPane grid = loader.<GridPane>load();
        MainPageFormController controller = loader.getController();
        controller.init(app);
        return new Scene(grid);
    }
}
