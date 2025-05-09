package obp3.fx.objectbrowser.api;

import javafx.scene.Node;

public interface ObjectView {
    String getName();
    Node getView();
    void setObject(Object object);
}
