package Logic.commands;

import Logic.util.PSCommand;
import Model.App;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
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
    private final String inChangeFailureMessage = "%s is currently being shut down or is undergoing IP/name change. Try again later!\n";
    private final String shutdownFailureMessage = "Failed to shutdown %s, ensure that the correct credentials are provided\n";
    private final String shutdownSuccessMessage = "%s successfully shut down!\n";
    private final String powershellUnavailableMessage = "Powershell is not available on this work station! Aborting shutdown operation...\n";

    public ShutdownCommand(App app, List<Server> servers) {
        this.app = app;
        this.servers = new ArrayList<>(servers);
    }

    private void updateMainApp(String response, Server server) {
        Platform.runLater(() -> {
            app.removeServerInChange(server);
            String commandResult = "";
            if (!response.isBlank()) {
                // Show error message
                commandResult = String.format(shutdownFailureMessage, server.getServerName());
            } else {
                commandResult = String.format(shutdownSuccessMessage, server.getServerName());
            }
            app.addHistory(commandResult);
        });
    }

    private String powerShellExec(Server server) {
        String powerShellResponse = "";
        try (PowerShell powerShell = PowerShell.openSession()) {
            Map<String, String> myConfig = new HashMap<>();
            myConfig.put("maxWait", "90000");
            powerShell.executeCommand(PSCommand.declareStringVar("serverIP", server.getIpAddress()));
            powerShell.executeCommand(PSCommand.declareStringVar("userName", server.getUserName()));
            powerShell.executeCommand(PSCommand.declareStringVar("password", server.getPassword()));
            powerShell.executeCommand(PSCommand.declareSecurePasswordVar("securePassword", "password"));
            powerShell.executeCommand(PSCommand.declareCredsVar("creds", "userName", "securePassword"));
            powerShellResponse = powerShell.configuration(myConfig).executeCommand(PSCommand.shutdownCommand("serverIP", "creds")).getCommandOutput();
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(() -> {
                app.addHistory(powershellUnavailableMessage);
            });
            powerShellResponse = powershellUnavailableMessage;
        }
        return powerShellResponse;
    }

    private boolean canShutdown(Server server) {
        if (!server.getIsOnline()) {
            app.addHistory(String.format(offlineFailureMessage, server.getServerName()));
            return false;
        }
        else if (app.isServerInChange(server)) {
            app.addHistory(String.format(inChangeFailureMessage, server.getServerName()));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void execute() {
        for (Server server : servers) {
            if (!canShutdown(server)) continue;
            app.setServerInChange(server);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    String response = powerShellExec(server);
                    updateMainApp(response, server);
                    return null;
                }
            };
            new Thread(task).start();
            app.addHistory(String.format(initShutdownMessage, server.getServerName()));
        }
    }
}
