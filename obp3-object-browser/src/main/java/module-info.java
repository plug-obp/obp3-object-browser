module obp3.fx.objectbrowser {
    requires javafx.controls;
    requires javafx.graphics;
    requires obp3.fx.objectbrowser.api;
    provides obp3.fx.objectbrowser.api.ObjectView with
            obp3.fx.objectbrowser.NullObjectView,
            obp3.fx.objectbrowser.GenericObjectView,
            obp3.fx.objectbrowser.GenericObjectTreeView;
    uses obp3.fx.objectbrowser.api.ObjectView;
    exports obp3.fx.objectbrowser;
}