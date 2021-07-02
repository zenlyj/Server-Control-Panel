package UI;

import Model.App;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;

public class CommandHistory {
    private App app;
    private TextArea historyBox;

    public CommandHistory(App app) {
        this.app = app;
        this.historyBox = new TextArea();

        StringProperty history = app.getHistory();
        history.addListener((observable, oldValue, newValue) -> {
            historyBox.setText(history.get());
            historyBox.setScrollTop(Double.MAX_VALUE);
        });
        historyBox.setEditable(false);
    }

    public HBox getHistoryBox() {
        return new HBox(this.historyBox);
    }
}
