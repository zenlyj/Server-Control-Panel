package Logic.commands;

import Logic.util.Parser;
import Logic.util.ParserException;
import Model.App;

import java.io.File;
import java.util.List;

public class ImportCommand extends Command {
    private App app;
    private File importFile;

    public ImportCommand(App app, File importFile) {
        this.app = app;
        this.importFile = importFile;
    }

    @Override
    public void execute() {
        try {
            List<List<String>> serversToAdd = Parser.parseCSV(importFile);
            for (List<String> serverInfo : serversToAdd) {
                String userName = serverInfo.get(0);
                String password = serverInfo.get(1);
                String serverName = serverInfo.get(2);
                String ipAddress = serverInfo.get(3);
                AddCommand cmd = new AddCommand(app, userName, password, serverName, ipAddress);
                cmd.execute();
            }
        } catch (ParserException ex) {
            app.addHistory(ex.getMessage());
        }
    }
}
