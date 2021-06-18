package Logic;

import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShutdownCommand extends Command {
    private List<Server> servers;

    public ShutdownCommand(List<Server> servers) {
        this.servers = new ArrayList<>(servers);
    }

    public void execute() {
        for (Server server : servers) {
            Task task = new Task<Void>() {
                @Override protected Void call() throws Exception {
                    powerShellExec(server);
                    return null;
                }
            };
            new Thread(task).start();
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
        } catch (PowerShellNotAvailableException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
