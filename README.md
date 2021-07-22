# Server Control Panel
![](https://raw.githubusercontent.com/zenlyj/Server-Control-Panel/docs/src/resources/documentation/General.PNG)

## Inspiration
During system integration, system engineers or system administrators are often
required to keep track of server status, as well as perform remote operations
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
* Launch PowerShell as an `Administrator` and execute `java --module-path <ABSOLUTE PATH TO YOUR JAVAFX LIB FOLDER> --add-modules javafx.controls,javafx.fxml <ABSOLUTE PATH TO YOUR SERVER CONTROL PANEL JAR FILE>`.

## App Layout
![](https://raw.githubusercontent.com/zenlyj/Server-Control-Panel/docs/src/resources/documentation/MainWindow.PNG)

### Function Buttons
Consists of various buttons that allows users to interact with the software.

### Server List
A list that displays the name and status of all monitored servers. Similar to the Windows File Explorer,
users are able to select individual entries by `Left Click`. It also supports multiple entry selection, whereby
users can either select all entries by `Ctrl-A`, or select multiple entries by `Ctrl-Left Click`.

### Server Information
Displays various information of the currently selected server. If multiple servers are selected,
information of the last selected server will be displayed.

### Application Log
Displays all events that occurred, as well as the time of occurrence.

## Features
### Create, Update, Read, Delete
* To add a server to monitor, click on the `Add` button. This creates a window
  which prompts you to enter `Username` and `Password`, these credentials must be
  of a user who is authorized to log in to the server and shut down the server. <br /> The `IP Address` and `Host Name`
  fields refer to the IP address and host name of the server respectively.
  
![](https://raw.githubusercontent.com/zenlyj/Server-Control-Panel/docs/src/resources/documentation/AddGuide.PNG)


* To add multiple servers to monitor, first enter all server details in `EXCEL`, then
  save the file as `COMMA-DELIMITED CSV` file. <br />
  
![](https://raw.githubusercontent.com/zenlyj/Server-Control-Panel/docs/src/resources/documentation/ExcelEntries.PNG)

![](https://raw.githubusercontent.com/zenlyj/Server-Control-Panel/docs/src/resources/documentation/ExcelFileType.PNG)


  Click on the `Import` button. This launches the `File Explorer` on your system,
  where you can select the `CSV` file.

![](https://raw.githubusercontent.com/zenlyj/Server-Control-Panel/docs/src/resources/documentation/ImportGuide.PNG)

* To edit details of a server, click on the `Edit` button. This creates a window
  which displays the current details of the server. Make the necessary changes
  and click on `Confirm` to change the details of the server.
  
![](https://raw.githubusercontent.com/zenlyj/Server-Control-Panel/docs/src/resources/documentation/EditGuide.PNG)


* To delete a server from the list of monitored servers, select the server
  in the list and click on the `Delete` button. To delete multiple servers in one
  go, you may first select multiple list entries by `Ctrl-Left Click`. You may
  also select all entries in the list by pressing `Ctrl-A`.

![](https://raw.githubusercontent.com/zenlyj/Server-Control-Panel/docs/src/resources/documentation/DeleteGuide.PNG)

* All server details are stored in a light weight `JSON` file named `data.json`, upon booting
  up `Server Control Panel`, the software reads from the `.json` file and restores the previous state
  of the application. For first time users, the `data.json` file will be created in the directory where the `.jar` file
  is executed.

### Real-Time Server Status
This feature will only work on servers that are connected to the same network as
the user's machine. Once a valid server is added to `Server Control Panel`, the application
keeps track of whether the server is online and updates its status in the `Server List`
component of the application. <br /> 

Apart from keeping track of whether a server is online or offline,
this application also displays the uptime of servers at the `Server Information` component. It is a
live clock that displays the number of days, hours, minutes and seconds since the server was boot up.

This feature utilizes `ICMP`. The monitored server's firewall
should be configured such that it does not block incoming `ICMP` requests from the user's machine, the server's firewall
should also allow outgoing `ICMP` responses.

The application automatically updates the status of the servers in 10 seconds intervals,
however, the user can also update the server status manually by selecting the server(s)
on the `Server List` and clicking the `Ping` button.

### Server Shutdown
This feature will only work on servers that are connected to the same network as
the user's machine. To shut down a server, simply select a server from the `Server List`
and click the `Shutdown` button. You may also shut down multiple servers by selecting multiple
servers from the `Server List` before clicking the `Shutdown` button. <br />

If a server is offline or undergoing name/ip change, the shut down operation will be aborted.

### IP Change
This feature will only work on servers that are connected to the same network as
the user's machine. To change the IP address of a server, simply select a server from the
`Server List` and click on the `Change IP` button. <br />

A new window will be created, prompting you to enter a new IP address. Make the necessary changes
and click on the `Confirm` button. <br />

If a server is offline, being shut down, or undergoing name change, the IP change operation will
be aborted.

### Host Name Change
Similar to IP change, select a server and click on the `Rename` button. Then make the necessary
changes and click on the `Confirm` button.

If a server is offline, being shut down, or undergoing ip address change, the rename operation will
be aborted.

### Remote Desktop
This feature will only work on servers that are connected to the same network as
the user's machine. The user's machine and server must also support `Remote Desktop Protocol`.

To establish a remote desktop connection with the server, select the server on the `Server List`
and click on the `Remote Desktop` button.