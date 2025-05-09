package obp3.fx.objectbrowser;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import obp3.fx.objectbrowser.api.ObjectView;
import obp3.fx.objectbrowser.api.ObjectViewFor;

import java.lang.reflect.Field;

@ObjectViewFor(Object.class)
public class GenericObjectView implements ObjectView {
    private VBox root = new VBox();

    public GenericObjectView() {
        root.setAlignment(Pos.CENTER);
    }

    @Override
    public String getName() {
        return "Generic Object View";
    }

    @Override
    public Node getView() {
        return root;
    }

    @Override
    public void setObject(Object obj) {
        root.getChildren().clear();
        if (obj == null) {
            root.getChildren().add(new Label("null"));
            return;
        }

        Class<?> cls = obj.getClass();
        root.getChildren().add(new Label("Type: " + cls.getName()));

        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                Label label = new Label(field.getName() + " = " + value);
                root.getChildren().add(label);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}