package obp3.fx.objectbrowser.exemple.person;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import obp3.fx.objectbrowser.api.ObjectView;
import obp3.fx.objectbrowser.api.ObjectViewFor;

@ObjectViewFor(Person.class)
public class PersonView implements ObjectView {
    private VBox root = new VBox();
    private Label nameLabel = new Label();
    private Label ageLabel = new Label();

    @Override
    public String getName() {
        return "Person View";
    }

    @Override
    public Node getView() {
        return root;
    }

    @Override
    public void setObject(Object obj) {
        if (!(obj instanceof Person p)) return;
        nameLabel.setText("Name: " + p.name());
        ageLabel.setText("Age: " + p.age());
        root.getChildren().setAll(nameLabel, ageLabel);
    }
}