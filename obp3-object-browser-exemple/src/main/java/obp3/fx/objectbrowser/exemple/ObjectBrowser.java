package obp3.fx.objectbrowser.exemple;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import obp3.fx.objectbrowser.api.ObjectView;
import obp3.fx.objectbrowser.ObjectViewContainer;
import obp3.fx.objectbrowser.ObjectViewFactory;
import obp3.fx.objectbrowser.exemple.person.Person;
import obp3.fx.objectbrowser.exemple.point.Point;

public class ObjectBrowser extends Application {
    private BorderPane root = new BorderPane();

    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Object Browser");

        Object sample = new Person("Alice", 30, new boolean[] {
                true, false, true, false, true,
                true, true, false, true, true,
                false, true, false, true, true,
                true, false, true});
        ObjectView view = ObjectViewFactory.instance.createView(sample);
        root.setCenter(new ObjectViewContainer( sample ).getView());
        root.setBottom(new ObjectViewContainer( new Point(2, 3)).getView());

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}