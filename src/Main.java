import Logic.PSCommand;
import Logic.SchedulePingCommand;
import Model.App;
import UI.MainPage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(PSCommand.declareAdapterVar("ada", "NIC"));
        App app = new App();
        MainPage mainPage = new MainPage();
        primaryStage.setTitle("Server Manager");
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