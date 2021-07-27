package Logic.commands;

import Logic.util.PSCommand;
import Model.Server;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;

import java.util.List;

public class EstablishConnectionCommand extends Command {
    private final List<Server> servers;
    private final Server serverToConnect;
    private PowerShellResponse response;
    private final PowerShell powerShell;
    private final String sessionVar;

    public EstablishConnectionCommand(PowerShell powerShell, List<Server> servers, Server serverToConnect, String sessionVar) {
        this.powerShell = powerShell;
        this.servers = servers;
        this.serverToConnect = serverToConnect;
        this.sessionVar = sessionVar;
    }

    private PowerShellResponse createSession(PowerShell powerShell) {
        powerShell.executeCommand(PSCommand.declareStringVar("allServerIP", lstToString(servers)));
        powerShell.executeCommand(PSCommand.setTrustedHosts("allServerIP"));
        powerShell.executeCommand(PSCommand.declareStringVar("serverIP", serverToConnect.getIpAddress()));
        powerShell.executeCommand(PSCommand.declareStringVar("userName", serverToConnect.getUserName()));
        powerShell.executeCommand(PSCommand.declareStringVar("password", serverToConnect.getPassword()));
        powerShell.executeCommand(PSCommand.declareSecurePasswordVar("securePassword", "password"));
        powerShell.executeCommand(PSCommand.declareCredsVar("creds", "userName", "securePassword"));
        return powerShell.executeCommand(PSCommand.declareSessionVar(sessionVar, "serverIP", "creds"));
    }

    private String lstToString(List<Server> servers) {
        StringBuilder res = new StringBuilder();
        for (Server s : servers) {
            res.append(String.format("%s,", s.getIpAddress()));
        }
        return res.substring(0, res.length()-1);
    }

    public boolean isSuccess() {
        return response.getCommandOutput().isBlank();
    }

    @Override
    public void execute() {
        response = createSession(powerShell);
    }
}
