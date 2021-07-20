import Logic.commands.SchedulePingCommand;
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
        primaryStage.setTitle("Server Manager");
        Scene primaryScene = mainPage.getMainPage(app);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        SchedulePingCommand schedulePing = new SchedulePingCommand(app);
        schedulePing.execute();
//        app.getServers().get(0).setBootDatetime(LocalDateTime.of(2021,7,16,12,12));
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}