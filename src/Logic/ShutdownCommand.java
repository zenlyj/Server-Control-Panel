package Logic;

import Model.App;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShutdownCommand extends Command {
    private App app;
    private List<Server> servers;
    private final String initShutdownMessage = "Initiated shutdown for %s \n";
    private final String offlineFailureMessage = "%s is offline! Aborting shutdown operation\n";
    private final String shutdownFailureMessage = "Failed to shutdown %s, ensure that the correct credentials are provided\n";
    private final String shutdownSuccessMessage = "%s successfully shut down!\n";
    private final String powershellUnavailableMessage = "Powershell is not available on this work station! Aborting shutdown operation...\n";

    public ShutdownCommand(App app, List<Server> servers) {
        this.app = app;
        this.servers = new ArrayList<>(servers);
    }

    public void execute() {
        for (Server server : servers) {
            if (server.getIsOnline()) {
                Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        powerShellExec(server);
                        return null;
                    }
                };
                new Thread(task).start();
                app.addHistory(String.format(initShutdownMessage, server.getServerName()));
            } else {
                app.addHistory(String.format(offlineFailureMessage, server.getServerName()));
            }
        }
    }

    private void powerShellExec(Server server) {
        try (PowerShell powerShell = PowerShell.openSession()) {
            Map<String, String> myConfig = new HashMap<>();
            myConfig.put("maxWait", "90000");
            powerShell.executeCommand(PSCommand.declareStringVar("serverIP", server.getIpAddress()));
            powerShell.executeCommand(PSCommand.declareStringVar("userName", server.getUserName()));
            powerShell.executeCommand(PSCommand.declareStringVar("password", server.getPassword()));
            powerShell.executeCommand(PSCommand.declareSecurePasswordVar("securePassword", "password"));
            powerShell.executeCommand(PSCommand.declareCredsVar("creds", "userName", "securePassword"));
            PowerShellResponse response = powerShell.configuration(myConfig).executeCommand(PSCommand.shutdownCommand("serverIP", "creds"));
            Platform.runLater(()->{
                String commandResult = "";
                if (!response.getCommandOutput().isBlank()) {
                    // Show error message
                    commandResult = String.format(shutdownFailureMessage, server.getServerName());
                } else {
                    commandResult = String.format(shutdownSuccessMessage, server.getServerName());
                }
               app.addHistory(commandResult);
            });
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(()->{
                app.addHistory(powershellUnavailableMessage);
            });
        }
    }
}
