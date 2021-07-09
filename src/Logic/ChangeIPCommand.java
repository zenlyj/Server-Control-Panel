package Logic;

import Model.App;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;

public class ChangeIPCommand extends Command {
    private App app;
    private Server server;
    private final String newIPAddress;
    private final String newServerName;
    private final int serverIdx;

    public ChangeIPCommand(App app, int serverIdx, String newIPAddress, String newServerName) {
        this.app = app;
        this.serverIdx = serverIdx;
        this.server = app.getServers().get(serverIdx);
        this.newIPAddress = newIPAddress;
        this.newServerName = newServerName;
    }

    @Override
    public void execute() {
        if (server.getIsOnline()) {
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    powerShellExec();
                    return null;
                }
            };
            new Thread(task).start();
            app.addHistory("Initiated ip/name change for " + server.getServerName() + "\n");
        } else {
            String offlineFailureMessage = server.getServerName() + " is offline! Aborting change ip/name operation...\n";
            app.addHistory(offlineFailureMessage);
        }
    }

    private String lstToString(List<Server> servers) {
        String res = "";
        for (Server s : servers) {
            res += s.getIpAddress() + ",";
        }
        return res.substring(0, res.length()-1);
    }

    private boolean isIPChange() {
        return !(newIPAddress.equals(server.getIpAddress()));
    }

    private PowerShellResponse createSession(PowerShell powerShell) {
        powerShell.executeCommand(PSCommand.declareStringVar("serverIP", server.getIpAddress()));
        powerShell.executeCommand(PSCommand.declareStringVar("userName", server.getUserName()));
        powerShell.executeCommand(PSCommand.declareStringVar("password", server.getPassword()));
        powerShell.executeCommand(PSCommand.declareSecurePasswordVar("securePassword", "password"));
        powerShell.executeCommand(PSCommand.declareCredsVar("creds", "userName", "securePassword"));
        return powerShell.executeCommand(PSCommand.declareSessionVar("s", "serverIP", "creds"));
    }

    private void renameServer(PowerShell powerShell) {
        powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.declareStringVar("newServerName", newServerName)));
        powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.renameCommand("newServerName")));
    }

    private PowerShellResponse changeIP(PowerShell powerShell) {
        powerShell.executeCommand(PSCommand.declareStringVar("allServerIP", lstToString(app.getServers())));
        powerShell.executeCommand(PSCommand.setTrustedHosts("allServerIP"));
        powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.declareStringVar("newIPAddr", newIPAddress)));
        powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.declareStringVar("oldIPAddr", server.getIpAddress())));
        powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.declareAdapterVar("adapterIndex", "NIC1")));
        powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.newIPCommand("adapterIndex", "newIPAddr")));
        return powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.removeIPCommand("adapterIndex", "oldIPAddr")));
    }

    private void updateMainApp(boolean isChanged) {
        Platform.runLater(() -> {
            if (isChanged) {
                EditCommand editCmd = new EditCommand(app, serverIdx, server.getUserName(), server.getPassword(), newServerName, newIPAddress);
                editCmd.execute();
                app.addHistory(server.getServerName() + "'s IP address successfully changed from " + server.getIpAddress() + " to " + newIPAddress + "\n");
                app.addHistory(server.getServerName() + " sucessfully renamed to " + newServerName + "\n");
            } else {
                app.addHistory("Failed to change IP address of "+ server.getServerName() + "\n");
            }
        });
    }

    public void powerShellExec() {
        try (PowerShell powerShell = PowerShell.openSession()) {
            createSession(powerShell);
            renameServer(powerShell);
            boolean success = false;
            if (isIPChange()) {
                PowerShellResponse response = changeIP(powerShell);
                if (response.getCommandOutput().isBlank()) {
                    success = true;
                    List<Server> updatedServers = new ArrayList<>(app.getServers());
                    updatedServers.set(serverIdx, new Server(newIPAddress, newServerName, server.getUserName(), server.getPassword()));
                    powerShell.executeCommand(PSCommand.declareStringVar("updatedServerIP", lstToString(updatedServers)));
                    powerShell.executeCommand(PSCommand.setTrustedHosts("updatedServerIP"));
                } else {
                    app.addHistory("Failed to change IP");
                }
            } else {
                app.addHistory("No changes made to IP");
            }

            final boolean isChanged = success;
            updateMainApp(isChanged);
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(()->{
                app.addHistory("Powershell is not available on this work station! Aborting change IP operation...\n");
            });
        }
    }
}
