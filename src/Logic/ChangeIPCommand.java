package Logic;

import Model.App;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class ChangeIPCommand extends Command {
    private App app;
    private Server server;
    private final String newIPAddress;
    private final String newServerName;

    public ChangeIPCommand(App app, int serverIdx, String newIPAddress, String newServerName) {
        this.app = app;
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


    public void powerShellExec() {
        // TODO: set new IP address to trusted hosts list
        // TODO: Update server object in list
        // set-item wsman:\localhost\client\trustedhosts -value {IPADDRESS}
        // winrm quickconfig
        
        try (PowerShell powerShell = PowerShell.openSession()) {
            powerShell.executeCommand("$serverIP='" + server.getIpAddress() + "'");
            powerShell.executeCommand("$userName='" + server.getUserName() + "'");
            powerShell.executeCommand("$password='" + server.getPassword() + "'");
            powerShell.executeCommand("[securestring]$securePassword = ConvertTo-SecureString $password -AsPlainText -Force");
            powerShell.executeCommand("$creds = New-Object System.Management.Automation.PSCredential ($userName, $securePassword)");
            powerShell.executeCommand("$s = New-PSSession -ComputerName " + server.getIpAddress() + " -Credential $creds");
            powerShell.executeCommand("Invoke-Command -Session $s -ScriptBlock {$newIPAddr='" + newIPAddress + "'}");
            powerShell.executeCommand("Invoke-Command -Session $s -ScriptBlock {$oldIPAddr='" + server.getIpAddress() + "'}");
            powerShell.executeCommand("Invoke-Command -Session $s -ScriptBlock {$adapterIndex = Get-NetAdapter | % { Process { If (( $_.Status -eq \"up\" ) -and ($_.Name -eq \"NIC1\") ){ $_.ifIndex } }}}");
            powerShell.executeCommand("Invoke-Command -Session $s -ScriptBlock {$newServerName='" + newServerName + "'}");
            powerShell.executeCommand("Invoke-Command -Session $s -ScriptBlock {(Get-WmiObject Win32_ComputerSystem).Rename($newServerName)}");
            powerShell.executeCommand("Invoke-Command -Session $s -ScriptBlock {New-NetIPAddress -InterfaceIndex $adapterIndex -IPAddress $newIPAddr -PrefixLength 24}");
            PowerShellResponse response = powerShell.executeCommand("Invoke-Command -Session $s -ScriptBlock {Remove-NetIPAddress -InterfaceIndex $adapterIndex -IPAddress $oldIPAddr -Confirm:$false}");

            Platform.runLater(()->{
                String commandResult = response.getCommandOutput();
                if (commandResult.isBlank()) {
                    app.addHistory(server.getServerName() + "'s IP address successfully changed from " + server.getIpAddress() + " to " + newIPAddress + "\n");
                    app.addHistory(server.getServerName() + " sucessfully renamed to " + newServerName + "\n");
                } else {
                    System.out.println(response.getCommandOutput());
                    app.addHistory("Failed to change IP address of "+ server.getServerName() + "\n");
                }
            });
        } catch (PowerShellNotAvailableException ex) {
            Platform.runLater(()->{
                app.addHistory("Powershell is not available on this work station! Aborting change IP operation...\n");
            });
        }
    }
}
