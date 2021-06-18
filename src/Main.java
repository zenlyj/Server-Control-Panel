import Logic.PingCommand;
import Model.App;
import UI.MainPage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application{

    private void setPingSchedule(App app) {
        Timeline pingCycle = new Timeline(
                new KeyFrame(Duration.minutes(1),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                PingCommand pingCommand = new PingCommand(app);
                                pingCommand.execute();
                            }
                        }));
        pingCycle.setCycleCount(Timeline.INDEFINITE);
        pingCycle.play();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        App app = new App();
        MainPage mainPage = new MainPage();
        primaryStage.setTitle("Sever Manager");
        Scene primaryScene = mainPage.getMainPage(app);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        setPingSchedule(app);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}