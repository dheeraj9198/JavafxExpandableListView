package sample;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    public static final EventBus bus = new EventBus();

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        TreeItem<ListItem> rootTreeItem = new TreeItem<ListItem>(null);

        for (int k = 0; k < 10; k++) {
            TreeItem<ListItem> headTreeItem = new TreeItem<ListItem>(new Head(String.valueOf("head : " + k)));
            for (int l = 0; l < 10; l++) {
                TreeItem<ListItem> leafTreeItem = new TreeItem<ListItem>(new Child(String.valueOf("child : " + l), String.valueOf("head : " + k)));
                headTreeItem.getChildren().add(leafTreeItem);
                leafTreeItem.getChildren().add(new TreeItem<ListItem>(null));
                headTreeItem.setExpanded(true);
            }
            rootTreeItem.getChildren().add(headTreeItem);
        }

        Callback<TreeView, TreeCell<ListItem>> cellFactory = new Callback<TreeView, TreeCell<ListItem>>() {
            public ClassRoomListCell call(javafx.scene.control.TreeView listView) {
                return new ClassRoomListCell();
            }
        };
        TreeView treeView = new TreeView<ListItem>(rootTreeItem);
        treeView.setShowRoot(false);
        treeView.setCellFactory(cellFactory);
        treeView.getStylesheets().add(getClass().getClassLoader().getResource("tree.css").toExternalForm());
        root.getChildren().add(treeView);
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public class ClassRoomListCell extends TreeCell<ListItem> {
        @Override
        protected void updateItem(final ListItem o, final boolean b) {
            super.updateItem(o, b);
            if (!b) {
                EventDispatcher originalDispatcher = getEventDispatcher();
                setEventDispatcher(new TreeMouseEventDispatcher(originalDispatcher));
                setText(null);
                if(o == null){
                    setGraphic(null);
                } else if (o instanceof Head) {
                    Label label = new Label(o.getName());
                   /* label.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        public void handle(MouseEvent event) {
                            bus.register(this);
                            bus.post(new HeadClickedEvent((Head) o));
                            bus.unregister(this);
                        }
                    });*/
                    setGraphic(label);
                } else if (o instanceof Child) {
                    Child child = (Child) o;
                    Label label = new Label(o.getName());
                    label.setStyle("-fx-background-color: antiquewhite");
                    setGraphic(label);
                }
            } else {
                setText(null);
                setGraphic(null);
            }
        }
    }

    class TreeMouseEventDispatcher implements EventDispatcher {
        private final EventDispatcher originalDispatcher;

        public TreeMouseEventDispatcher(EventDispatcher originalDispatcher) {
            this.originalDispatcher = originalDispatcher;
        }

        public Event dispatchEvent(Event event, EventDispatchChain tail) {
            if (event instanceof MouseEvent) {
                if (((MouseEvent) event).getButton() == MouseButton.PRIMARY
                        && ((MouseEvent) event).getClickCount() >= 2) {

                    if (!event.isConsumed()) {
                        // Implement your double-click behavior here, even your
                        // MouseEvent handlers will be ignored, i.e., the event consumed!
                    }

                    event.consume();
                }
            }
            return originalDispatcher.dispatchEvent(event, tail);
        }
    }

    public static class Head implements ListItem {
        private String name;

        public Head(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class Child implements ListItem {
        private String name;
        private String headName;
        private ListCell listCell;

        public Child(String name, String headName) {
            this.name = name;
            this.headName = headName;
        }

        public String getName() {
            return name;
        }

        public String getHeadName() {
            return headName;
        }

        public ListCell getListCell() {
            return listCell;
        }

        public void setListCell(ListCell listCell) {
            this.listCell = listCell;
        }
    }

    public interface ListItem {
        String getName();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
