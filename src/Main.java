import Logic.commands.SchedulePingCommand;
import Logic.util.Parser;
import Model.App;
import UI.MainPage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        App app = new App();
        MainPage mainPage = new MainPage();
        primaryStage.setTitle("Server Control Panel");
        Scene primaryScene = mainPage.getMainPage(app);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        SchedulePingCommand schedulePing = new SchedulePingCommand(app);
        schedulePing.execute();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}