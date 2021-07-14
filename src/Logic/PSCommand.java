package Logic;

public class PSCommand {
    public static String declareStringVar(String name, String val) {
        return String.format("$%1$s = '%2$s'", name, val);
    }

    public static String declareSecurePasswordVar(String name, String passVar) {
        return String.format("[securestring]$%1$s = ConvertTo-SecureString $%2$s -AsPlainText -Force", name, passVar);
    }

    public static String declareCredsVar(String name, String userVar, String passVar) {
        return String.format("$%1$s = New-Object System.Management.Automation.PSCredential ($%2$s, $%3$s)", name, userVar, passVar);
    }

    public static String shutdownCommand(String ipVar, String credsVar) {
        return String.format("Stop-Computer -ComputerName $%1$s -Credential $%2$s -Force", ipVar, credsVar);
    }

    public static String setTrustedHosts(String serverIPVar) {
        return String.format("Set-Item wsman:\\localhost\\client\\trustedhosts -value $%s -Force", serverIPVar);
    }

    public static String declareSessionVar(String sessionVar, String ipVar, String credsVar) {
        return String.format("$%1$s = New-PSSession -ComputerName $%2$s -Credential $%3$s", sessionVar, ipVar, credsVar);
    }

    public static String invokeCommand(String sessionVar, String scriptBlock) {
        return String.format("Invoke-Command -Session $%1$s -ScriptBlock {%2$s}", sessionVar, scriptBlock);
    }

    public static String declareAdapterVar(String name, String connectionType) {
        return String.format("$%1$s = Get-NetAdapter | %% { Process { If (( $_.Status -eq \"up\" ) -and ($_.Name -eq \"%2$s\") ){ $_.ifIndex } }}}", name, connectionType);
    }

    public static String renameCommand(String newNameVar) {
        return String.format("(Get-WmiObject Win32_ComputerSystem).Rename($%s)", newNameVar);
    }

    public static String newIPCommand(String adapterVar, String newIPVar) {
        return String.format("New-NetIPAddress -InterfaceIndex $%1$s -IPAddress $%2$s -PrefixLength 24", adapterVar, newIPVar);
    }

    public static String removeIPCommand(String adapterVar, String oldIPVar) {
        return String.format("Remove-NetIPAddress -InterfaceIndex $%1$s -IPAddress $%2$s -Confirm:$false", adapterVar, oldIPVar);
    }

    public static String cmdKey(String serverIPVar, String userNameVar, String passwordVar) {
        return String.format("cmdkey /generic:TERMSRV/'%1$s' /user:'%2$s' /pass:'%3$s'", serverIPVar, userNameVar, passwordVar);
    }

    public static String mstscExec(String serverIPVar) {
        return String.format("mstsc /v:%s", serverIPVar);
    }
}
