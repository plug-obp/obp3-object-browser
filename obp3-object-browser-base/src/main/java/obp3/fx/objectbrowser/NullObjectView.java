package obp3.fx.objectbrowser;

import javafx.scene.Node;
import javafx.scene.control.Label;
import obp3.fx.objectbrowser.api.ObjectView;
import obp3.fx.objectbrowser.api.ObjectViewFor;

@ObjectViewFor(Void.class)
public class NullObjectView implements ObjectView {
    public NullObjectView() {}
    @Override
    public String getName() {
        return "Null Object View";
    }

    @Override
    public Node getView() {
        return new Label("null");
    }

    @Override
    public void setObject(Object object) {

    }
}
