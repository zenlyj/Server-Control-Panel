package Logic.commands;

import Logic.util.PSCommand;
import Model.App;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.HashMap;
import java.util.Map;

public class RemoteDesktopCommand extends Command {
    private final App app;
    private final Server server;
    private final String offlineFailureMessage = "%s is offline! Aborting remote desktop operation";
    private final String powershellUnavailableMessage = "Powershell is not available on this work station! Aborting remote desktop operation...";
    private final String initRemoteDesktopMessage = "Initiated remote desktop connection to %s";

    public RemoteDesktopCommand(App app, Server server) {
        this.app = app;
        this.server = server;
    }

    private void powerShellExec(Server server) {
        try (PowerShell powerShell = PowerShell.openSession()) {
            Map<String, String> myConfig = new HashMap<>();
            myConfig.put("maxWait", "90000");
            powerShell.executeCommand(PSCommand.cmdKey(server.getIpAddress(), server.getUserName(), server.getPassword()));
            powerShell.configuration(myConfig).executeCommand(PSCommand.mstscExec(server.getIpAddress()));
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(()-> app.addHistory(powershellUnavailableMessage));
        }
    }

    @Override
    public void execute() {
        if (server.getIsOnline()) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    powerShellExec(server);
                    return null;
                }
            };
            new Thread(task).start();
            app.addHistory(String.format(initRemoteDesktopMessage, server.getServerName()));
        } else {
            app.addHistory(String.format(offlineFailureMessage, server.getServerName()));
        }
    }
}
