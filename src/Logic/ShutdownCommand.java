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
                app.addHistory("Initiated shutdown for " + server.getServerName() + "\n");
            } else {
                String offlineFailureMessage = server.getServerName() + " is offline! Aborting shutdown operation...\n";
                app.addHistory(offlineFailureMessage);
            }
        }
    }

    private void powerShellExec(Server server) {
        try (PowerShell powerShell = PowerShell.openSession()) {
            Map<String, String> myConfig = new HashMap<>();
            myConfig.put("maxWait", "90000");
            powerShell.executeCommand("$serverIP='" + server.getIpAddress() + "'");
            powerShell.executeCommand("$userName='" + server.getUserName() + "'");
            powerShell.executeCommand("$password='" + server.getPassword() + "'");
            powerShell.executeCommand("[securestring]$securePassword = ConvertTo-SecureString $password -AsPlainText -Force");
            powerShell.executeCommand("$creds = New-Object System.Management.Automation.PSCredential ($userName, $securePassword)");
            PowerShellResponse response = powerShell.configuration(myConfig).executeCommand("Stop-Computer -ComputerName $serverIP -Credential $creds -Force");
            Platform.runLater(()->{
                String commandResult = "";
                if (!response.getCommandOutput().isBlank()) {
                    // Show error message
                    commandResult = "Failed to shutdown " + server.getServerName() + ", ensure that the correct credentials are provided\n";
                } else {
                    commandResult = server.getServerName() + " successfully shut down\n";
                }
               app.addHistory(commandResult);
            });
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(()->{
                app.addHistory("Powershell is not available on this work station! Aborting shutdown operation...\n");
            });
        }
    }
}
