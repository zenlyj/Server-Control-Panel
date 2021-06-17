import Logic.CheckConnectionCommand;
import Logic.Command;
import Model.App;
import Model.Server;
import UI.MainPage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        App app = new App();
        MainPage mainPage = new MainPage();
        primaryStage.setTitle("Sever Manager");
        Scene primaryScene = mainPage.getMainPage(app);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}