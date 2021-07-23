package Logic.commands;

import Logic.util.PSCommand;
import Model.App;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;

public class ChangeIPCommand extends Command {
    private final App app;
    private final Server server;
    private final String newIPAddress;
    private final int serverIdx;
    private final String changeIPSuccessMessage = "%1$s's ip address is changed from %2$s to %3$s";
    private final String failedConnectionMessage = "Failed to establish connection to %s";
    private final String psUnavailableMessage = "Powershell is not available on this work station! Aborting change ip operation...";
    private final String offlineFailureMessage = "%s is offline! Aborting change ip operation...";
    private final String initChangeMessage = "Initiated ip address change for %s";
    private final String changeIPFailureMessage = "Failed to change IP for %s, check that the current servers do not have the input IP address";
    private final String inChangeFailureMessage = "%s is currently being shut down or is undergoing IP/name change. Try again later!";

    public ChangeIPCommand(App app, int serverIdx, String newIPAddress) {
        this.app = app;
        this.serverIdx = serverIdx;
        this.server = app.getServers().get(serverIdx);
        this.newIPAddress = newIPAddress;
    }

    private void changeIP() {
        try (PowerShell powerShell = PowerShell.openSession()) {
            EstablishConnectionCommand cmd = new EstablishConnectionCommand(powerShell, new ArrayList<>(app.getServers()), server, "s");
            cmd.execute();
            if (cmd.isSuccess()) {
                powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.declareStringVar("newIPAddr", newIPAddress)));
                powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.declareStringVar("oldIPAddr", server.getIpAddress())));
                powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.declareAdapterVar("adapterIndex", "NIC1")));
                powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.newIPCommand("adapterIndex", "newIPAddr")));
                powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.removeIPCommand("adapterIndex", "oldIPAddr")));
            } else {
                Platform.runLater(() -> app.addHistory(String.format(failedConnectionMessage, server.getServerName())));
            }
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(()->app.addHistory(psUnavailableMessage));
        }
    }

    private boolean isIPChangable() {
        boolean isDuplicate = false;
        for (Server server : app.getServers()) {
            isDuplicate = server.getIpAddress().equals(newIPAddress);
            if (isDuplicate) break;
        }
        if (isDuplicate) {
            Platform.runLater(()->app.addHistory(String.format(changeIPFailureMessage, server.getServerName())));
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

    private void updateMainApp() {
        Platform.runLater(() -> {
            if (!app.isServerInDelete(server)) {
                EditCommand editCmd = new EditCommand(app, serverIdx, server.getUserName(), server.getPassword(), server.getServerName(), newIPAddress);
                editCmd.execute();
            }
            app.addHistory(String.format(changeIPSuccessMessage, server.getServerName(), server.getIpAddress(), newIPAddress));
            app.removeServerInChange(server);
        });
    }

    @Override
    public void execute() {
        if (isIPChangable()) {
            app.setServerInChange(server);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    changeIP();
                    updateMainApp();
                    return null;
                }
            };
            new Thread(task).start();
            app.addHistory(String.format(initChangeMessage, server.getServerName()));
        }
    }
}
