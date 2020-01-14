import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import javafx.geometry.*;
import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

public class Inventory extends Application{


    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Stage window;
        window = primaryStage;
        window.setTitle("簡易在庫管理システム - sodacs.jp");
        Scene mainScene;

        DBHandler databaseHandler = new DBHandler();

        ScrollBar sc = new ScrollBar();
        sc.setMin(0);
        sc.setMax(100);
        sc.setValue(50);
        
        BorderPane layout = new BorderPane();
        layout.setTop(HeadMenu.getHeadMenu(window, databaseHandler));
        layout.setLeft(CatTree.getCatTree(databaseHandler));
        layout.setCenter(ProductTable.getProductTable(databaseHandler));

        mainScene = new Scene(layout, 1100, 750);
        mainScene.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                Node focusOwner = mainScene.getFocusOwner();
                if (focusOwner instanceof Button) {
                    ((Button) focusOwner).fire();
                }
            }
        });

        window.setScene(mainScene);
        window.show();
    }
}