package Logic;

import Model.App;
import Model.Server;
import javafx.concurrent.Task;

import java.util.List;

public class EditCommand extends Command {
    private App app;
    private int selectedIdx;
    private String userName;
    private String password;
    private String serverName;
    private String ipAddress;

    public EditCommand(App app, int selectedIdx, String userName, String password, String serverName, String ipAddress) {
        this.app = app;
        this.selectedIdx = selectedIdx;
        this.userName = userName;
        this.password = password;
        this.serverName = serverName;
        this.ipAddress = ipAddress;
    }

    @Override
    public void execute() {
        app.setServerInEdit(selectedIdx);
        List<Server> servers = app.getServers();
        Server updatedServer = new Server(ipAddress, serverName, userName, password);
        servers.set(selectedIdx, updatedServer);
        app.commit(servers);
        Task<Void> task = new Task<Void>() {
            @Override protected Void call() throws Exception {
                Thread.sleep(10000);
                app.setServerInEdit(-1);
                return null;
            }
        };
        new Thread(task).start();
    }
}
