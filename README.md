# Server Control Panel
![](https://raw.githubusercontent.com/zenlyj/Server-Control-Panel/main/src/resources/documentation/General.PNG)

## Inspiration
During system integration, system engineers or system administrators are often
required to keep track of server status, as well as perform various configurations
on servers. For a small system of less than 20 servers, it is possible to do
the aforementioned manually, that is to say, ping each and every server manually to
check whether a server is online or offline, or remotely connect to each server
to shut down the servers after system testing.

However, this is not scalable with regard to larger systems of 50 or more servers.
To resolve this issue, remote scripting is often employed to automate
common and repetitive tasks like server shut down, changing ip address/host name,
server pinging. Despite the possibility of remote scripting, some system engineers
may not be very experienced with remote scripting and this is where `Server Control
Panel` comes into the picture.

## About
Server Control Panel is a Java desktop application that aims to provide an abstraction
for common remote scripting tasks on servers running on Windows OS. It is written in Java 11, and has a simple and intuitive
graphical user interface that runs on JavaFX 11. This application also employs
third party libraries [Jackson](https://github.com/FasterXML/jackson) and
[jPowerShell](https://github.com/profesorfalken/jPowerShell).

## How to Use
* Ensure that you have [OpenJDK Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) installed on your computer. Check by entering `java --version`
  in Windows `CMD` or `PowerShell`.
* Ensure that you have [JavaFX 11](https://gluonhq.com/download/javafx-11-0-2-sdk-windows/) installed on your computer.
* Ensure that your computer, and the monitored servers has [PowerShell 5.1](https://docs.microsoft.com/en-us/skypeforbusiness/set-up-your-computer-for-windows-powershell/download-and-install-windows-powershell-5-1).
  It should be preinstalled if you are using Windows 10 Anniversary Update, or Windows Server 2016
* Download the latest release of [Server Control Panel](https://github.com/zenlyj/server-control-panel/releases)
* Execute `winrm quickconfig` on `CMD` or `PowerShell` on your computer to ensure that your computer is configured to establish remote connections.
* Launch PowerShell as an `Administrator` and execute `java -jar --module-path <ABSOLUTE PATH TO YOUR JAVAFX LIB FOLDER> --add-modules javafx.controls,javafx.fxml <ABSOLUTE PATH TO YOUR SERVER CONTROL PANEL JAR FILE>`.
* Check out the [user guide](https://zenlyj.github.io/Server-Control-Panel/) for a detailed documentation of `Server Control Panel`.