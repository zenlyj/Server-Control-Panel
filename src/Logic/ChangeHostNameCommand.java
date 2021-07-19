package Logic;

import Model.App;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.List;

public class ChangeHostNameCommand extends Command {
    private App app;
    private final int serverIdx;
    private final Server server;
    private final String newServerName;
    private final String failedConnectionMessage = "Failed to establish connection to %s\n";
    private final String psUnavailableMessage = "Powershell is not available on this work station! Aborting change name operation...\n";
    private final String offlineFailureMessage = "%s is offline! Aborting change name operation...\n";
    private final String initChangeMessage = "Initiated name change for %s\n";
    private final String changeNameSuccessMessage = "%1$s successfully renamed to %2$s\n";
    private final String changeNameFailureMessage = "Failed to rename %s, check that there are no existing servers with the same name\n";
    private final String inChangeFailureMessage = "%s is currently being shut down or is undergoing IP/name change. Try again later!\n";

    public ChangeHostNameCommand(App app, int serverIdx, String newServerName) {
        this.app = app;
        this.serverIdx = serverIdx;
        this.server = app.getServers().get(serverIdx);
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

    private String lstToString(List<Server> servers) {
        String res = "";
        for (Server s : servers) {
            res += s.getIpAddress() + ",";
        }
        return res.substring(0, res.length()-1);
    }

    private PowerShellResponse createSession(PowerShell powerShell) {
        powerShell.executeCommand(PSCommand.declareStringVar("allServerIP", lstToString(app.getServers())));
        powerShell.executeCommand(PSCommand.setTrustedHosts("allServerIP"));
        powerShell.executeCommand(PSCommand.declareStringVar("serverIP", server.getIpAddress()));
        powerShell.executeCommand(PSCommand.declareStringVar("userName", server.getUserName()));
        powerShell.executeCommand(PSCommand.declareStringVar("password", server.getPassword()));
        powerShell.executeCommand(PSCommand.declareSecurePasswordVar("securePassword", "password"));
        powerShell.executeCommand(PSCommand.declareCredsVar("creds", "userName", "securePassword"));
        return powerShell.executeCommand(PSCommand.declareSessionVar("s", "serverIP", "creds"));
    }

    private void renameServer() {
        try (PowerShell powerShell = PowerShell.openSession()) {
            PowerShellResponse response = createSession(powerShell);
            if (response.getCommandOutput().isBlank()) {
                powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.declareStringVar("newServerName", newServerName)));
                powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.renameCommand("newServerName")));
            } else {
                Platform.runLater(() -> app.addHistory(String.format(failedConnectionMessage, server.getServerName())));
            }
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(()->app.addHistory(psUnavailableMessage));
        }
    }

    private void updateMainApp() {
        Platform.runLater(() -> {
            if (!app.isServerInDelete(server)) {
                EditCommand editCmd = new EditCommand(app, serverIdx, server.getUserName(), server.getPassword(), newServerName, server.getIpAddress());
                editCmd.execute();
            }
            app.addHistory(String.format(changeNameSuccessMessage, server.getServerName(), newServerName));
            app.removeServerInChange(server);
        });
    }

    @Override
    public void execute() {
        if (isRenamable()) {
            app.setServerInChange(server);
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    renameServer();
                    updateMainApp();
                    return null;
                }
            };
            new Thread(task).start();
            app.addHistory(String.format(initChangeMessage, server.getServerName()));
        }
    }
}
