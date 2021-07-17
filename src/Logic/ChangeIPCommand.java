package Logic;

import Model.App;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.List;

public class ChangeIPCommand extends Command {
    private App app;
    private Server server;
    private final String newIPAddress;
    private final int serverIdx;
    private final String changeIPSuccessMessage = "%1$s's ip address is changed from %2$s to %3$s\n";
    private final String failedConnectionMessage = "Failed to establish connection to %s\n";
    private final String psUnavailableMessage = "Powershell is not available on this work station! Aborting change ip operation...\n";
    private final String offlineFailureMessage = "%s is offline! Aborting change ip operation...\n";
    private final String initChangeMessage = "Initiated ip address change for %s\n";
    private final String changeIPFailureMessage = "Failed to change IP for %s, check that the current servers do not have the input IP address\n";

    public ChangeIPCommand(App app, int serverIdx, String newIPAddress) {
        this.app = app;
        this.serverIdx = serverIdx;
        this.server = app.getServers().get(serverIdx);
        this.newIPAddress = newIPAddress;
    }

    private void showError() {
        Platform.runLater(() -> app.addHistory(String.format(changeIPFailureMessage, server.getServerName())));
    }

    @Override
    public void execute() {
        if (server.getIsOnline()) {
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if (isIPChangable()) {
                        changeIP();
                        updateMainApp();
                    } else {
                        showError();
                    }
                    return null;
                }
            };
            new Thread(task).start();
            app.addHistory(String.format(initChangeMessage, server.getServerName()));
        } else {
            app.addHistory(String.format(offlineFailureMessage, server.getServerName()));
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

    private void changeIP() {
        try (PowerShell powerShell = PowerShell.openSession()) {
            PowerShellResponse response = createSession(powerShell);
            if (response.getCommandOutput().isBlank()) {
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

    private void updateMainApp() {
        Platform.runLater(() -> {
            EditCommand editCmd = new EditCommand(app, serverIdx, server.getUserName(), server.getPassword(), server.getServerName(), newIPAddress);
            editCmd.execute();
            app.addHistory(String.format(changeIPSuccessMessage, server.getServerName(), server.getIpAddress(), newIPAddress));
        });
    }

    private boolean isIPChangable() {
        for (Server server : app.getServers()) {
            boolean isDuplicate = server.getIpAddress().equals(newIPAddress);
            if (isDuplicate) return false;
        }
        return true;
    }
}
