import Model.Server;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTest {
    private final String ipAddress = "172.30.12.12";
    private final String serverName = "SERVER-01";
    private final String userName = "username01";
    private final String password = "password2123";

    @Test
    public void offlineServerConstructTest() {
        Server server = new Server(ipAddress, serverName, userName, password);
        assertFalse(server.getIsOnline());
        assertFalse(server.getBootDateTime().isPresent());
    }

    @Test
    public void serverCopyConstructTest() {
        Server server = new Server(ipAddress, serverName, userName, password);
        server.setStatus(true);
        Server serverCopy = new Server(server);
        boolean isSame = serverCopy.getIpAddress().equals(ipAddress) &&
                serverCopy.getServerName().equals(serverName) &&
                serverCopy.getUserName().equals(userName) &&
                serverCopy.getPassword().equals(password) &&
                serverCopy.getIsOnline() &&
                serverCopy.getBootDateTime().isEmpty();
        assertTrue(isSame);
    }

    @Test
    public void tableViewStatusTest() {
        Server server = new Server(ipAddress, serverName, userName, password);
        assertEquals(server.getStatus(), "OFFLINE");
        server.setStatus(true);
        assertEquals(server.getStatus(), "ONLINE");
    }

    @Test
    public void uptimeTest() {
        Server server = new Server(ipAddress, serverName, userName, password);
        assertEquals(server.upTime(), "0 days 0 hours 0 minutes 0 seconds");
        LocalDateTime referenceDateTime = LocalDateTime.now();
        server.setBootDatetime(referenceDateTime);
        assertEquals(server.upTime(), "0 days 0 hours 0 minutes 0 seconds");
    }

    @Test
    public void sameServerTest() {
        Server server = new Server(ipAddress, serverName, userName, password);
        Server sameIPServer = new Server(ipAddress, "SERVER-02", userName, password);
        assertEquals(server, sameIPServer);
        Server sameNameServer = new Server("172.30.12.13", serverName, userName, password);
        assertEquals(server, sameNameServer);
    }

    @Test
    public void differentServerTest() {
        Server server = new Server(ipAddress, serverName, userName, password);
        Server diffServer = new Server("172.30.12.13", "SERVER-02", userName, password);
        assertNotEquals(server, diffServer);
    }
}
