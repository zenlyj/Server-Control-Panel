package UI;

import Model.App;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class MainPage {
    public GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        return grid;
    }

    public Scene getMainPage(App app) {
        GridPane grid = createGridPane();
        Table table = new Table(app);
        FunctionBar functionBar = new FunctionBar(app, table.getTable());
        ServerDetails serverDetails = new ServerDetails(table.getTable());

        grid.add(functionBar.getFunctionBar(), 0, 0);
        grid.add(table.getTable(), 0, 1);
        grid.add(serverDetails.getDetails(), 1, 1);

        Scene scene = new Scene(grid, 500, 400);
        return scene;
    }
}
