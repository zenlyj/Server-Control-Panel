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

import java.util.ArrayList;
import java.util.Optional;

public class UpdateUptimeCommand extends Command {
    private final App app;
    private final Server server;
    private final String powershellUnavailableMessage = "Powershell is not available on this work station! Aborting change name operation...";
    private final String failedConnectionMessage = "Failed to establish connection to %s";

    public UpdateUptimeCommand(App app, Server server) {
        this.app = app;
        this.server = server;
    }

    private Optional<String> powerShellExec() {
        Optional<String> res = Optional.empty();
        try (PowerShell powerShell = PowerShell.openSession()) {
            EstablishConnectionCommand cmd = new EstablishConnectionCommand(powerShell, new ArrayList<>(app.getServers()), server, "s");
            cmd.execute();
            if (cmd.isSuccess()) {
                powerShell.executeCommand(PSCommand.declareDateTimeVar("datetime", PSCommand.invokeCommand("s", PSCommand.getUpTime())));
                PowerShellResponse info = powerShell.executeCommand(PSCommand.formatUpTime("datetime"));
                res = Optional.of(info.getCommandOutput());
            } else {
                Platform.runLater(() -> app.addHistory(String.format(failedConnectionMessage, server.getServerName())));
            }
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(()-> app.addHistory(powershellUnavailableMessage));
        }
        return res;
    }

    private void updateMainApp(String bootDateTime) {
        Platform.runLater(() -> {
            for (Server s : app.getServers()) {
                if (s.equals(server) && s.getIsOnline()) {
                    s.setBootDatetime(Parser.parseDateTime(bootDateTime));
                    app.setServerInEdit(server);
                }
            }
        });
    }

    @Override
    public void execute() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Optional<String> info = powerShellExec();
                if (info.isPresent()) {
                    System.out.println("updating " + server.getServerName());
                    String bootDateTime = info.get();
                    updateMainApp(bootDateTime);
                }
                return null;
            }
        };
        new Thread(task).start();
    }
}
