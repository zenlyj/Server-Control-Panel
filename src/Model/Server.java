package Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    @JsonIgnore
    private Optional<LocalDateTime> bootDateTime;

    public Server(@JsonProperty("ipAddress") String ipAddress, @JsonProperty("serverName") String serverName, @JsonProperty("userName") String userName, @JsonProperty("password") String password) {
        this.ipAddress = ipAddress;
        this.serverName = serverName;
        this.userName = userName;
        this.password = password;
        boolean isOnline = false;
        setStatus(isOnline);
        this.bootDateTime = Optional.empty();
    }

    public Server(Server server) {
        this.ipAddress = server.getIpAddress();
        this.serverName = server.getServerName();
        this.userName = server.getUserName();
        this.password = server.getPassword();
        this.isOnline = server.getIsOnline();
        this.status = server.getStatus();
        this.bootDateTime = server.getBootDateTime();
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

    public void setBootDatetime(LocalDateTime bootDateTime) {
        this.bootDateTime = Optional.ofNullable(bootDateTime);
    }

    public Optional<LocalDateTime> getBootDateTime() {
        return this.bootDateTime;
    }

    public String upTime() {
        long days = 0;
        long hours = 0;
        long minutes = 0;
        long seconds = 0;

        if (bootDateTime.isPresent()) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            Duration duration = Duration.between(bootDateTime.get(), currentDateTime);
            long uptime = duration.toSeconds();
            days = TimeUnit.SECONDS.toDays(uptime);
            uptime -= TimeUnit.DAYS.toSeconds(days);
            hours = TimeUnit.SECONDS.toHours(uptime);
            uptime -= TimeUnit.HOURS.toSeconds(hours);
            minutes = TimeUnit.SECONDS.toMinutes(uptime);
            uptime -= TimeUnit.MINUTES.toSeconds(minutes);
            seconds = TimeUnit.SECONDS.toSeconds(uptime);
        }
        return String.format("%1$s days %2$s hours %3$s minutes %4$s seconds", days, hours, minutes, seconds);
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
