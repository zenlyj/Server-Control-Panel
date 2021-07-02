package Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Server {
    @JsonProperty("ipAddress")
    private String ipAddress;
    @JsonProperty("serverName")
    private String serverName;
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("password")
    private String password;
    @JsonIgnore
    private boolean isOnline;
    @JsonIgnore
    private String status;

    public Server(@JsonProperty("ipAddress") String ipAddress, @JsonProperty("serverName") String serverName, @JsonProperty("userName") String userName, @JsonProperty("password") String password) {
        this.ipAddress = ipAddress;
        this.serverName = serverName;
        this.userName = userName;
        this.password = password;
        boolean isOnline = false;
        setStatus(isOnline);
    }

    public Server(Server server) {
        this.ipAddress = server.getIpAddress();
        this.serverName = server.getServerName();
        this.userName = server.getUserName();
        this.password = server.getPassword();
        this.isOnline = server.getIsOnline();
        this.status = server.getStatus();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getServerName() {
        return serverName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setStatus(boolean isOnline) {
        this.isOnline = isOnline;
        this.status = isOnline ? "ONLINE" : "OFFLINE";
    }

    // used for tableview
    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object compared) {
        if (compared == this) {
            return true;
        }
        if (!(compared instanceof Server)) {
            return false;
        }
        Server cmpServer = (Server) compared;
        String cmpIP = cmpServer.getIpAddress();
        String cmpName = cmpServer.getServerName();
        return (cmpIP.equals(this.getIpAddress()) || cmpName.equals(this.getServerName()));
    }

    @Override
    public String toString() {
        return serverName;
    }
}
