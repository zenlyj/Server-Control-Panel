# Server Control Panel

## Inspiration
During system integration, system engineers or system administrators are often 
required to keep track of servers status, as well as perform remote operations
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
* Ensure that you have [OpenJDK Java 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) installed on your computer.
* Ensure that you have [JavaFX 11](https://gluonhq.com/download/javafx-11-0-2-sdk-windows/) installed on your computer.
* Ensure that your computer and the monitored servers has [PowerShell 5.1](https://docs.microsoft.com/en-us/skypeforbusiness/set-up-your-computer-for-windows-powershell/download-and-install-windows-powershell-5-1).
  It should be preinstalled if you are using Windows 10 Anniversary Update, or Windows Server 2016
* Download the latest release of [Server Control Panel](https://github.com/zenlyj/server-control-panel/releases)
* Execute `winrm quickconfig` on `cmd` or `PowerShell` on your computer to ensure that your computer is configured to establish remote connections.
* Execute `java --module-path "Absolute path to your JavaFX lib folder" --add-modules javafx.controls,javafx.fxml "Absolute Path to your Server Control Panel jar file" `

## Features
### Create, Update, Read, Delete
* To add a server to monitor, click on the `Add` button. This creates a window
which prompts for you to enter `Username` and `Password`, these credentials must be
  of a user who is authorized to log in to the server and shut down the server. <br /> The `IP Address` and `Host Name`
  fields refer to the IP address and host name of the server respectively.
  

* To edit details of a server, click on the `Edit` button. This creates a window
which displays the current details of the server. Make the necessary changes
  and click on `Confirm` to change the details of the server.
  

* To delete a server from the list of monitored servers, select the server
in the list and click on the `Delete` button. To delete multiple servers in one
  go, you may first select multiple list entries by `Ctrl` + `Left Click`. You may
  also select all entries in the list by pressing `Ctrl A`.
  

* All server details are stored in a light weight `JSON` file named `data.json`, upon booting
up `Server Control Panel`, the software reads from the `.json` file and restores the previous state
  of the application. For first time users, the `data.json` file will be created in the directory where the `.jar` file
  is executed.