package Logic.commands;

import Logic.util.PSCommand;
import Logic.util.Parser;
import Model.App;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.List;
import java.util.Optional;

public class UpdateUptimeCommand extends Command {
    private final App app;
    private final Server server;
    private final String powershellUnavailableMessage = "Powershell is not available on this work station! Aborting change name operation...\n";
    private final String failedConnectionMessage = "Failed to establish connection to %s\n";

    public UpdateUptimeCommand(App app, Server server) {
        this.app = app;
        this.server = server;
    }

    private String lstToString(List<Server> servers) {
        StringBuilder res = new StringBuilder();
        for (Server s : servers) {
            res.append(String.format("%s,", s.getIpAddress()));
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

    private Optional<String> powerShellExec() {
        Optional<String> res = Optional.empty();
        try (PowerShell powerShell = PowerShell.openSession()) {
            PowerShellResponse response = createSession(powerShell);
            if (response.getCommandOutput().isBlank()) {
                PowerShellResponse info = powerShell.executeCommand(PSCommand.invokeCommand("s", PSCommand.getUpTime()));
                res = Optional.of(info.getCommandOutput());
            } else {
                Platform.runLater(() -> app.addHistory(String.format(failedConnectionMessage, server.getServerName())));
            }
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(()-> app.addHistory(powershellUnavailableMessage));
        }
        return res;
    }

    @Override
    public void execute() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Optional<String> info = powerShellExec();
                if (info.isPresent()) {
                    String bootDateTime = info.get().split("\n")[3];
                    Platform.runLater(() -> {
                        for (Server s : app.getServers()) {
                            if (s.equals(server)) {
                                s.setBootDatetime(Parser.parseDateTime(bootDateTime));
                            }
                        }
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}
