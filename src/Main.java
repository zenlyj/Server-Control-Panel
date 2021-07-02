import Logic.SchedulePingCommand;
import Model.App;
import Model.Server;
import UI.MainPage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application{

    private void setSchedule(App app) {
        ScheduledService<Void> service = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        System.out.println("Starting schedule...");
                        // deep copy of server list
                        List<Server> servers = new ArrayList<>();
                        for (Server server : app.getServers()) {
                            servers.add(new Server(server));
                        }

                        SchedulePingCommand spCommand = new SchedulePingCommand(servers);
                        spCommand.execute();

                        // update status of servers
                        Platform.runLater(() -> {
                            int ptr = 0;
                            for (int i = 0; i < servers.size(); i++) {
                                Server curr = servers.get(i);
                                boolean isEdited = app.isServerInEdit(i);
                                boolean isDeleted = app.isServerInDelete(curr);
                                if (isEdited) {
                                    app.removeServerInEdit(i);
                                }
                                if (isDeleted) {
                                    app.removeServerInDelete(curr);
                                    continue;
                                }
                                if (!isEdited) {
                                    app.getServers().set(ptr, curr);
                                }
                                ptr++;
                            }
                        });
                        return null;
                    }
                };
            }
        };
        service.setPeriod(Duration.seconds(10));
        service.start();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        App app = new App();
        MainPage mainPage = new MainPage();
        primaryStage.setTitle("Server Manager");
        Scene primaryScene = mainPage.getMainPage(app);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
        setSchedule(app);
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}