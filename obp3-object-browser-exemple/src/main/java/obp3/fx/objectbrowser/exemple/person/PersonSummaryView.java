package obp3.fx.objectbrowser.exemple.person;

import javafx.scene.Node;
import javafx.scene.control.Label;
import obp3.fx.objectbrowser.api.ObjectView;
import obp3.fx.objectbrowser.api.ObjectViewFor;


@ObjectViewFor(Person.class)
public class PersonSummaryView implements ObjectView {
    private Label summaryLabel = new Label();

    @Override
    public String getName() {
        return "Summary";
    }

    @Override
    public Node getView() {
        return summaryLabel;
    }

    @Override
    public void setObject(Object obj) {
        if (!(obj instanceof Person p)) return;
        summaryLabel.setText(p.name() + " " + p.age());
    }
}