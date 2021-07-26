import Logic.util.PSCommand;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PSCommandTest {
    @Test
    public void declareStringVarTest() {
        String varName = "username";
        String varValue = "zenlyj";
        String command = PSCommand.declareStringVar(varName, varValue);
        assertEquals(command, "$username = 'zenlyj'");
    }

    @Test
    public void declareSecurePasswordVarTest() {
        String varName = "s3cur3P4ssword";
        String passVar = "p4ssword";
        String command = PSCommand.declareSecurePasswordVar(varName, passVar);
        assertEquals(command, "[securestring]$s3cur3P4ssword = ConvertTo-SecureString $p4ssword -AsPlainText -Force");
    }

    @Test
    public void declareCredsVarTest() {
        String varName = "cr3ds";
        String userNameVar = "username";
        String passVar = "p4ssword";
        String command = PSCommand.declareCredsVar(varName, userNameVar, passVar);
        assertEquals(command, "$cr3ds = New-Object System.Management.Automation.PSCredential ($username, $p4ssword)");
    }

    @Test
    public void shutdownCommandTest() {
        String ipVar = "ipAddress";
        String credentialsVar = "credentials";
        String command = PSCommand.shutdownCommand(ipVar, credentialsVar);
        assertEquals(command, "Stop-Computer -ComputerName $ipAddress -Credential $credentials -Force");
    }

    @Test
    public void setTrustedHostsTest() {
        String ipAddressesVar = "ipAddresses";
        String command = PSCommand.setTrustedHosts(ipAddressesVar);
        assertEquals(command, "Set-Item wsman:\\localhost\\client\\trustedhosts -value $ipAddresses -Force");
    }

    @Test
    public void declareSessionVarTest() {
        String sessionVar = "session";
        String ipVar = "ipAddress";
        String credsVar = "credentials";
        String command = PSCommand.declareSessionVar(sessionVar, ipVar, credsVar);
        assertEquals(command, "$session = New-PSSession -ComputerName $ipAddress -Credential $credentials");
    }

    @Test
    public void invokeCommandTest() {
        String sessionVar = "session";
        String codeBlock = PSCommand.declareStringVar("username", "zenlyj");
        String command = PSCommand.invokeCommand(sessionVar, codeBlock);
        assertEquals(command, "Invoke-Command -Session $session -ScriptBlock {$username = 'zenlyj'}");
    }

    @Test
    public void declareAdapterVarTest() {
        String adapterVar = "adapter";
        String connectionType = "ETHERNET";
        String command = PSCommand.declareAdapterVar(adapterVar, connectionType);
        assertEquals(command, "$adapter = Get-NetAdapter | % { Process { If (( $_.Status -eq \"up\" ) -and ($_.Name -eq \"ETHERNET\") ){ $_.ifIndex } }}");
    }

    @Test
    public void renameCommandTest() {
        String newNameVar = "newHostName";
        String command = PSCommand.renameCommand(newNameVar);
        assertEquals(command, "(Get-WmiObject Win32_ComputerSystem).Rename($newHostName)");
    }

    @Test
    public void newIPCommandTest() {
        String adapterVar = "adapter";
        String newIPVar = "newIP";
        String command = PSCommand.newIPCommand(adapterVar, newIPVar);
        assertEquals(command, "New-NetIPAddress -InterfaceIndex $adapter -IPAddress $newIP -PrefixLength 24");
    }

    @Test
    public void removeIPCommandTest() {
        String adapterVar = "adapter";
        String oldIPVar = "oldIP";
        String command = PSCommand.removeIPCommand(adapterVar, oldIPVar);
        assertEquals(command, "Remove-NetIPAddress -InterfaceIndex $adapter -IPAddress $oldIP -Confirm:$false");
    }

    @Test
    public void cmdKeyTest() {
        String serverIP = "172.30.1.1";
        String username = "username111";
        String password = "password222";
        String command = PSCommand.cmdKey(serverIP, username, password);
        assertEquals(command, "cmdkey /generic:TERMSRV/'172.30.1.1' /user:'username111' /pass:'password222'");
    }

    @Test
    public void mstscExecTest() {
        String serverIP = "172.20.3.34";
        String command = PSCommand.mstscExec(serverIP);
        assertEquals(command, "mstsc /v:172.20.3.34");
    }

    @Test
    public void declareDateTimeVarTest() {
        String dateTimeVar = "dateTime";
        String expression = PSCommand.getUpTime();
        String command = PSCommand.declareDateTimeVar(dateTimeVar, expression);
        assertEquals(command, "$dateTime = (Get-CimInstance Win32_OperatingSystem | Select-Object LastBootUpTime).LastBootUpTime");
    }

    @Test
    public void formatUpTimeTest() {
        String dateTimeVar = "dateTime";
        String command = PSCommand.formatUpTime(dateTimeVar);
        assertEquals(command, "$dateTime.toString('MM/dd/yyyy HH:mm:ss')");
    }
}

