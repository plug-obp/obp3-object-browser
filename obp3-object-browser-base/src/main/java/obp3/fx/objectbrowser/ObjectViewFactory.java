package obp3.fx.objectbrowser;

import obp3.fx.objectbrowser.api.ObjectView;
import obp3.fx.objectbrowser.api.ObjectViewFor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectViewFactory {
    public static final ObjectViewFactory instance = new ObjectViewFactory();

    private ObjectViewFactory() {
        init();
    }

    private final Map<Class<?>, List<Class<? extends ObjectView>>> viewMap = new HashMap<>();

    public void register(Class<?> cls, Class<? extends ObjectView> viewClass) {
        viewMap.computeIfAbsent(cls, k -> new ArrayList<>()).add(viewClass);
    }

    void init() {
        ServiceLoader<ObjectView> loader = ServiceLoader.load(ObjectView.class);
        for (ObjectView view : loader) {
            Class<? extends ObjectView> viewClass = view.getClass();
            System.err.println(viewClass.getName());
            Class<?> objClass = viewClass.getAnnotation(ObjectViewFor.class).value();
            viewMap.computeIfAbsent(objClass, k -> new ArrayList<>()).add(viewClass);
        }
    }
    public ObjectView createView(Object obj) {
        if (obj == null) return new NullObjectView();
        Class<?> cls = obj.getClass();

        List<Class<? extends ObjectView>> viewClasses = viewMap.get(cls);
        Class<? extends ObjectView> viewClass = viewClasses == null ? null : viewClasses.getFirst();
        if (viewClass != null) {
            try {
                ObjectView view = viewClass.getDeclaredConstructor().newInstance();
                view.setObject(obj);
                return view;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Fallback to generic
        ObjectView generic = new GenericObjectView();
        generic.setObject(obj);
        return generic;
    }

    public List<Class<? extends ObjectView>> getViews(Object obj) {
        if (obj == null) return Collections.singletonList(NullObjectView.class);
        Class<?> cls = obj.getClass();

        List<Class<? extends ObjectView>> viewClasses = viewMap.getOrDefault(cls, Collections.emptyList());

        return Stream.concat(Stream.concat(viewClasses.stream(), Stream.of(GenericObjectTreeView.class)), Stream.of(GenericObjectView.class))
                .collect(Collectors.toList());
    }
}