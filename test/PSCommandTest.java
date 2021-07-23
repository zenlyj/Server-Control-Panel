import Logic.util.PSCommand;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PSCommandTest {
    @Test
    public void declareStringVar() {
        String varName = "username";
        String varValue = "zenlyj";
        String command = PSCommand.declareStringVar(varName, varValue);
        assertEquals(command, "$username = 'zenlyj'");
    }

    @Test
    public void declareSecurePasswordVar() {
        String varName = "s3cur3P4ssword";
        String passVar = "p4ssword";
        String command = PSCommand.declareSecurePasswordVar(varName, passVar);
        assertEquals(command, "[securestring]$s3cur3P4ssword = ConvertTo-SecureString $p4ssword -AsPlainText -Force");
    }

    @Test
    public void declareCredsVar() {
        String varName = "cr3ds";
        String userNameVar = "username";
        String passVar = "p4ssword";
        String command = PSCommand.declareCredsVar(varName, userNameVar, passVar);
        assertEquals(command, "$cr3ds = New-Object System.Management.Automation.PSCredential ($username, $p4ssword)");
    }
}
