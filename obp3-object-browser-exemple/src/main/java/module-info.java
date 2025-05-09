import obp3.fx.objectbrowser.exemple.person.PersonSummaryView;
import obp3.fx.objectbrowser.exemple.person.PersonView;

module obp3.fx.objectbrowser.exemple {
    requires javafx.controls;
    requires obp3.fx.objectbrowser.api;
    requires obp3.fx.objectbrowser;
    provides obp3.fx.objectbrowser.api.ObjectView with
            PersonView,
            PersonSummaryView;
    uses obp3.fx.objectbrowser.api.ObjectView;
    opens obp3.fx.objectbrowser.exemple.person;
    opens obp3.fx.objectbrowser.exemple.point;
    exports obp3.fx.objectbrowser.exemple;
}