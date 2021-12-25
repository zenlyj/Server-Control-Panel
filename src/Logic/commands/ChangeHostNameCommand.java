package Logic.commands;

import Logic.util.PSCommand;
import Model.App;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;

public class ChangeHostNameCommand extends Command {
    private final App app;
    private final Server server;
    private final String newServerName;
    private final String failedConnectionMessage = "Failed to establish connection to %s";
    private final String psUnavailableMessage = "Powershell is not available on this work station! Aborting change name operation...";
    private final String offlineFailureMessage = "%s is offline! Aborting change name operation...";
    private final String initChangeMessage = "Initiated name change for %s";
    private final String changeNameSuccessMessage = "%1$s successfully renamed to %2$s";
    private final String changeNameFailureMessage = "Failed to rename %s, check that there are no existing servers with the same name";
    private final String inChangeFailureMessage = "%s is currently being shut down or is undergoing IP/name change. Try again later!";

    public ChangeHostNameCommand(App app, Server serverToEdit, String newServerName) {
        this.app = app;
        this.server = serverToEdit;
        this.newServerName = newServerName;
    }

    private boolean isRenamable() {
        boolean isDuplicate = false;
        for (Server server : app.getServers()) {
            isDuplicate = server.getServerName().equals(newServerName);
            if (isDuplicate) break;
        }
        if (isDuplicate) {
            Platform.runLater(()->app.addHistory(String.format(changeNameFailureMessage, server.getServerName())));
            return false;
        } else if (!server.getIsOnline()) {
            Platform.runLater(()->app.addHistory(String.format(offlineFailureMessage, server.getServerName())));
            return false;
        } else if (app.isServerInChange(server)) {
            Platform.runLater(()->app.addHistory(String.format(inChangeFailureMessage, server.getServerName())));
            return false;
        } else {
            return true;
        }
    }

    private boolean renameServer() {
        try (PowerShell powerShell = PowerShell.openSession()) {
            EstablishConnectionCommand cmd = new EstablishConnectionCommand(powerShell, new ArrayList<>(app.getServers()), server, "s");
            cmd.execute();
            if (cmd.isSuccess()) {
                powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.declareStringVar("newServerName", newServerName)));
                powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.renameCommand("newServerName")));
                return true;
            } else {
                Platform.runLater(() -> app.addHistory(String.format(failedConnectionMessage, server.getServerName())));
                return false;
            }
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(()->app.addHistory(psUnavailableMessage));
            return false;
        }
    }

    private void updateMainApp(boolean cmdSuccess) {
        Platform.runLater(() -> {
            if (cmdSuccess && app.getServers().contains(server)) {
                int serverIndex = app.getServers().indexOf(server);
                Server currServer = app.getServers().get(serverIndex);
                EditCommand editCmd = new EditCommand(app, currServer, currServer.getUserName(), currServer.getPassword(), newServerName, currServer.getIpAddress());
                editCmd.execute();
                app.addHistory(String.format(changeNameSuccessMessage, server.getServerName(), newServerName));
            }
            app.removeServerInChange(server);
        });
    }

    @Override
    public void execute() {
        if (isRenamable()) {
            app.setServerInChange(server);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    boolean cmdSuccess = renameServer();
                    updateMainApp(cmdSuccess);
                    return null;
                }
            };
            new Thread(task).start();
            app.addHistory(String.format(initChangeMessage, server.getServerName()));
        }
    }
}
