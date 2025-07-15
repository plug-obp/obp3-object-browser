package obp3.fx.objectbrowser;

import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import obp3.fx.objectbrowser.api.ObjectView;
import obp3.fx.objectbrowser.api.ObjectViewFor;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;
import java.util.*;

@ObjectViewFor(Object.class)
public class GenericObjectTreeView implements ObjectView {
    private TreeView<ObjectField> treeView = new TreeView<>();

    public GenericObjectTreeView() {
        treeView.setCellFactory(tv -> new StyledTreeCell());
    }
    @Override
    public String getName() {
        return "Generic Tree View";
    }

    @Override
    public Node getView() {
        return treeView;
    }

    @Override
    public void setObject(Object object) {
        treeView.setRoot(buildLazyTree("root", object));
    }

    private static final Set<Class<?>> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList(
            String.class,
            Boolean.class, Byte.class, Short.class, Integer.class, Long.class,
            Float.class, Double.class,
            Character.class
    ));

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || PRIMITIVE_TYPES.contains(clazz);
    }


    private TreeItem<ObjectField> buildLazyTree(String name, Object obj) {
        Class<?> type = obj != null ? obj.getClass() : Object.class;
        TreeItem<ObjectField> node = new TreeItem<>(new ObjectField(name, type, obj));

        if (obj == null || isPrimitiveOrWrapper(obj.getClass())) {
            return node; // no children
        }

        // Add dummy child to allow expansion
        if (type.isArray() || obj.getClass().getDeclaredFields().length > 0) {
            node.getChildren().add(new TreeItem<>(new ObjectField("Loading...", Object.class, null)));
        }



        node.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
            if (isNowExpanded && node.getChildren().size() == 1 &&
                    "Loading...".equals(node.getChildren().getFirst().getValue().name)) {

                node.getChildren().clear(); // remove dummy

                if (type.isArray()) {
                    pagedArray(node, obj, Array.getLength(obj), 0, 5);
                    return;
                }
//                System.out.println("----------- declared fields");
//                System.out.println(Arrays.toString(obj.getClass().getDeclaredFields()));
//                System.out.println("----------- all fields");
//                System.out.println(Arrays.toString(obj.getClass().getFields()));
                for (Field field : getAllFields(obj.getClass())) {
                    if (Modifier.isStatic(field.getModifiers())) {continue;}
                    try {
                        field.setAccessible(true);
                        Object value = field.get(obj);
                        var child = buildLazyTree(field.getName(), value);
                        node.getChildren().add(child);
                    } catch (IllegalAccessException | InaccessibleObjectException e) {
                        var ti = new TreeItem<>(
                                new ObjectField(field.getName(), Object.class, e.toString()));
                        node.getChildren().add(ti);
                        System.out.println(e.toString());
                    }
                }
            }
        });

        return node;
    }

    private List<Field> getAllFields(Class<?> type) {
        List<Field> result = new ArrayList<Field>();

        Class<?> i = type;
        while (i != null && i != Object.class) {
            Collections.addAll(result, i.getDeclaredFields());
            i = i.getSuperclass();
        }

        return result;
    }

    void pagedArray(TreeItem<ObjectField> container, Object array, int length, int start, int pageSize) {
        if (length == 0) return;
        int end = Math.min(start + pageSize, length);
        for (int i = start; i < end; i++) {
            container.getChildren().add(buildLazyTree(Objects.toString(i), Array.get(array, i)));

        }
        if (end < length) {
            var nextLoader = new TreeItem<>(new ObjectField("...", Object.class, null));
            nextLoader.getChildren().add(new TreeItem<>(new ObjectField("Loading...", Object.class, null)));
            nextLoader.setExpanded(false);
            container.getChildren().add(nextLoader);
            nextLoader.expandedProperty().addListener((obs, wasExpanded, isNowExpanded) -> {
                if (isNowExpanded) {
                    container.getChildren().remove(nextLoader);
                    pagedArray(container, array, length, end, pageSize);
                }
            });
        }
    }

    static class ObjectField {
        String name;
        Class<?> type;
        Object value;

        public ObjectField(String name, Class<?> type, Object value) {
            this.name = name;
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return name + ": " + type.getSimpleName() + " = " + format(value);
        }

        private String format(Object obj) {
            if (obj == null) return "null";
            String s = obj.getClass().isArray() ? arrayToString(obj) : Objects.toString(obj);
            return s.length() > 50 ? s.substring(0, 47) + "..." : s;
        }

        String arrayToString(Object array) {
            if (array == null) return "null";
            if (!array.getClass().isArray()) return array.toString();

            Class<?> compType = array.getClass().getComponentType();

            if (compType.isPrimitive()) {
                if (compType == int.class) return Arrays.toString((int[]) array);
                if (compType == long.class) return Arrays.toString((long[]) array);
                if (compType == double.class) return Arrays.toString((double[]) array);
                if (compType == float.class) return Arrays.toString((float[]) array);
                if (compType == boolean.class) return Arrays.toString((boolean[]) array);
                if (compType == char.class) return Arrays.toString((char[]) array);
                if (compType == byte.class) return Arrays.toString((byte[]) array);
                if (compType == short.class) return Arrays.toString((short[]) array);
            }

            // Object array
            return Arrays.toString((Object[]) array);
        }
    }
    static class StyledTreeCell extends TreeCell<ObjectField> {
        @Override
        protected void updateItem(ObjectField item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                Text name = new Text(item.name);
                name.setFont(Font.font("Menlo", FontPosture.REGULAR, 12));
                name.setFill(Color.DARKSLATEGRAY);
                if (item.name.equals("...")) {
                    setGraphic(new TextFlow(name));
                    return;
                }
                Text type = new Text(": " + item.type.getSimpleName());
                type.setFont(Font.font("Menlo", FontPosture.ITALIC, 12));
                type.setFill(Color.SLATEGRAY);
                Text value = new Text(" = " + item.format(item.value));
                value.setFont(Font.font("Menlo", FontPosture.REGULAR, 12));

                TextFlow flow = new TextFlow(name, type, value);
                setGraphic(flow);
            }
        }
    }
}
