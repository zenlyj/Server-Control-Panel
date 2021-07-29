import Logic.commands.DeleteCommand;
import Model.Server;
import org.junit.Test;
import util.AppStub;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteCommandTest {
    private final Server server01 = new Server("172.30.12.12", "SERVER-01", "username01", "password01");
    private final Server server02 = new Server("172.30.22.22", "SERVER-02", "username02", "password02");
    private final Server server03 = new Server("172.30.33.33", "SERVER-03", "username03", "password03");
    private final String deleteSuccessMessage = "%s has been successfully deleted";

    private AppStub constructStub() {
        List<Server> servers = new ArrayList<>();
        servers.add(server01);
        servers.add(server02);
        servers.add(server03);
        return new AppStub(servers);
    }

    @Test
    public void deleteServerTest() {
        AppStub stub = constructStub();
        List<Server> serverToDelete = new ArrayList<>();
        serverToDelete.add(server02);
        DeleteCommand cmd = new DeleteCommand(stub, serverToDelete);
        cmd.execute();
        assertEquals(stub.getServers().size(), 2);
        assertEquals(stub.getServers().get(1), server03);
        assertTrue(stub.getHistory().get().contains(String.format(deleteSuccessMessage, server02)));
    }

    @Test
    public void deleteMultipleServerTest() {
        AppStub stub = constructStub();
        List<Server> serversToDelete = new ArrayList<>();
        serversToDelete.add(server01);
        serversToDelete.add(server03);
        DeleteCommand cmd = new DeleteCommand(stub, serversToDelete);
        cmd.execute();
        assertEquals(stub.getServers().size(), 1);
        assertEquals(stub.getServers().get(0), server02);
        assertTrue(stub.getHistory().get().contains(String.format(deleteSuccessMessage, server01)));
        assertTrue(stub.getHistory().get().contains(String.format(deleteSuccessMessage, server03)));
    }
}
