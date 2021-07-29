import Logic.commands.AddCommand;
import Model.Server;
import org.junit.Test;
import util.AppStub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddCommandTest {
    private final String ipAddress = "172.30.12.12";
    private final String serverName = "SERVER-01";
    private final String userName = "username01";
    private final String password = "password2123";
    private final String addSuccessMessage = "%s successfully added!";
    private final String addFailureMessage = "%s already exists! Aborting add operation...";

    @Test
    public void validAddTest() {
        AppStub stub = new AppStub();
        AddCommand cmd = new AddCommand(stub, userName, password, serverName, ipAddress);
        cmd.execute();
        assertEquals(stub.getServers().get(0), new Server(ipAddress, serverName, userName, password));
        assertTrue(stub.getHistory().get().contains(String.format(addSuccessMessage, serverName)));
    }

    @Test
    public void duplicateServerNameAddTest() {
        AppStub stub = new AppStub();
        AddCommand validCmd = new AddCommand(stub, userName, password, serverName, ipAddress);
        AddCommand invalidCmd = new AddCommand(stub, userName, password, serverName, "172.30.11.11");
        validCmd.execute();
        invalidCmd.execute();
        assertEquals(stub.getServers().size(), 1);
        assertTrue(stub.getServers().contains(new Server(ipAddress, serverName, userName, password)));
        assertTrue(stub.getHistory().get().contains(String.format(addFailureMessage, serverName)));
    }

    @Test
    public void duplicateServerIPAddTest() {
        AppStub stub = new AppStub();
        AddCommand validCmd = new AddCommand(stub, userName, password, serverName, ipAddress);
        AddCommand invalidCmd = new AddCommand(stub, userName, password, "SERVER-02", ipAddress);
        validCmd.execute();
        invalidCmd.execute();
        assertEquals(stub.getServers().size(), 1);
        assertTrue(stub.getServers().contains(new Server(ipAddress, serverName, userName, password)));
        assertTrue(stub.getHistory().get().contains(String.format(addFailureMessage, "SERVER-02")));
    }
}
