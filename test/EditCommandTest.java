import Logic.commands.EditCommand;
import Model.Server;
import org.junit.Test;
import util.AppStub;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EditCommandTest {
    private final String editSuccessMessage = "%s has been successfully edited";
    private final String editFailureMessage = "%s already exists in the current list! Aborting edit operation...";
    private final String editNoChangeMessage = "No changes have been made to %s";
    private final String notExistingMessage = "%s deleted during edit! Aborting edit operation...";
    private final Server server01 = new Server("172.30.12.12", "SERVER-01", "username01", "password01");
    private final Server server02 = new Server("172.30.22.22", "SERVER-02", "username02", "password02");
    private final Server server03 = new Server("172.30.33.33", "SERVER-03", "username03", "password03");

    private AppStub constructStub() {
        List<Server> servers = new ArrayList<>();
        servers.add(server01);
        servers.add(server02);
        servers.add(server03);
        return new AppStub(servers);
    }

    @Test
    public void editUserNameSuccessTest() {
        AppStub stub = constructStub();
        String newUsername = "zenlyj";
        EditCommand cmd = new EditCommand(stub, server01, newUsername, server01.getPassword(), server01.getServerName(), server01.getIpAddress());
        cmd.execute();
        assertEquals(stub.getServers().get(0).getUserName(), newUsername);
        assertTrue(stub.getHistory().get().contains(String.format(editSuccessMessage, server01.getServerName())));
    }

    @Test
    public void editPasswordSuccessTest() {
        AppStub stub = constructStub();
        String newPassword = "asd123";
        EditCommand cmd = new EditCommand(stub, server02, server02.getUserName(), newPassword, server02.getServerName(), server02.getIpAddress());
        cmd.execute();
        assertEquals(stub.getServers().get(1).getPassword(), newPassword);
        assertTrue(stub.getHistory().get().contains(String.format(editSuccessMessage, server02.getServerName())));
    }

    @Test
    public void editHostNameSuccessTest() {
        AppStub stub = constructStub();
        String newHostName = "SERVER-122";
        EditCommand cmd = new EditCommand(stub, server02, server02.getUserName(), server02.getPassword(), newHostName, server02.getIpAddress());
        cmd.execute();
        assertEquals(stub.getServers().get(1).getServerName(), newHostName);
        assertTrue(stub.getHistory().get().contains(String.format(editSuccessMessage, server02.getServerName())));
    }

    @Test
    public void editIPSuccessTest() {
        AppStub stub = constructStub();
        String newIPAddress = "172.30.122.122";
        EditCommand cmd = new EditCommand(stub, server02, server02.getUserName(), server02.getPassword(), server02.getServerName(), newIPAddress);
        cmd.execute();
        assertEquals(stub.getServers().get(1).getIpAddress(), newIPAddress);
        assertTrue(stub.getHistory().get().contains(String.format(editSuccessMessage, server02.getServerName())));
    }

    @Test
    public void editNoChangeTest() {
        AppStub stub = constructStub();
        EditCommand cmd = new EditCommand(stub, server03, server03.getUserName(), server03.getPassword(), server03.getServerName(), server03.getIpAddress());
        cmd.execute();
        Server server = stub.getServers().get(2);
        assertEquals(server.getServerName(), server03.getServerName());
        assertEquals(server.getIpAddress(), server03.getIpAddress());
        assertEquals(server.getUserName(), server03.getUserName());
        assertEquals(server.getPassword(), server03.getPassword());
        assertTrue(stub.getHistory().get().contains(String.format(editNoChangeMessage, server03.getServerName())));
    }

    @Test
    public void editDuplicateTest() {
        AppStub stub = constructStub();
        EditCommand existingNameCommand = new EditCommand(stub, server03, server03.getUserName(), server03.getPassword(), server02.getServerName(), server03.getIpAddress());
        EditCommand existingIPCommand = new EditCommand(stub, server03, server03.getUserName(), server03.getPassword(), server03.getServerName(), server01.getIpAddress());
        existingNameCommand.execute();
        existingIPCommand.execute();
        Server server = stub.getServers().get(2);
        assertEquals(server.getServerName(), server03.getServerName());
        assertEquals(server.getIpAddress(), server03.getIpAddress());
        assertEquals(server.getUserName(), server03.getUserName());
        assertEquals(server.getPassword(), server03.getPassword());
        assertTrue(stub.getHistory().get().contains(String.format(editFailureMessage, server02.getServerName())));
    }

    @Test
    public void editNotExistTest() {
        AppStub stub = constructStub();
        stub.getServers().remove(0);
        EditCommand cmd = new EditCommand(stub, server01, "user", "pass", "server", "172.1.1.1");
        cmd.execute();
        assertEquals(stub.getServers().get(0), server02);
        assertEquals(stub.getServers().get(1), server03);
        assertTrue(stub.getHistory().get().contains(String.format(notExistingMessage, server01)));
    }
}
