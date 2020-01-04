import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.stage.*;
import javafx.scene.control.*;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

public class HeadMenu{

    public static MenuBar getHeadMenu(Stage window, DBHandler databaseHandler){
        //------------- ファイル ---------------------
        Menu fileMenu = new Menu("ファイル(_F)");

        MenuItem reset = new MenuItem("リセット...");
        reset.setOnAction(e -> {
            if(ConfirmBox.confirm("注意！！！","全てのデータがなくなります。本当にリセットしますか？")){     
                databaseHandler.execAction("DELETE FROM inventory");
                ProductTable.reloadTable(databaseHandler);
                databaseHandler.execAction("DELETE FROM category WHERE id > 1");
                CatTree.resetTree();
            }
        });
        fileMenu.getItems().add(reset);
        fileMenu.getItems().add(new SeparatorMenuItem());
        MenuItem importDb = new MenuItem("インポート...");
        importDb.setDisable(true);
        fileMenu.getItems().add(importDb);
        MenuItem exportDb = new MenuItem("エクスポート...");
        exportDb.setOnAction(e -> {
            if(ConfirmBox.confirm("エクスポートについて...", "一つのファイル名を決めて頂きますが、カテゴリー用の(ファイル名)_Cat.delと、在庫リスト用の(ファイル名)_Inv.delの二つのファイルを出力します。")){

                FileChooser fileChooser = new FileChooser();
                
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("DEL files (*.del)", "*.del");
                fileChooser.getExtensionFilters().add(extFilter);
                fileChooser.setTitle("エキスポート先とファイル名を決めて下さい↓");
    
                File file = fileChooser.showSaveDialog(window);
    
                if (file != null) {
                    databaseHandler.execAction("CALL SYSCS_UTIL.SYSCS_EXPORT_QUERY('SELECT * FROM category', '" + file + "_Cat.del', null, null, null)");
                    databaseHandler.execAction("CALL SYSCS_UTIL.SYSCS_EXPORT_QUERY('SELECT * FROM inventory', '" + file + "_Inv.del', null, null, null)");
                }
            }
        });
        fileMenu.getItems().add(exportDb);
        fileMenu.getItems().add(new SeparatorMenuItem());
        MenuItem settings = new MenuItem("設定");
        settings.setDisable(true);
        fileMenu.getItems().add(settings);
        fileMenu.getItems().add(new SeparatorMenuItem());
        MenuItem close = new MenuItem("終了");
        close.setOnAction(e -> window.close());
        fileMenu.getItems().add(close);



        //------------------- 表示 ---------------------------
        Menu viewMenu = new Menu("表示(_V)");

        CheckMenuItem showIdNum = new CheckMenuItem("商品番号を表示する");
        showIdNum.setOnAction(e -> {
            if(showIdNum.isSelected()){
                ProductTable.viewColumn(ProductTable.getIdColumn(), true);
            }else{
                ProductTable.viewColumn(ProductTable.getIdColumn(),false);
            }
        });
        viewMenu.getItems().addAll(showIdNum);
    


        //--------------------------- ヘルプ ----------------------------
        Menu helpMenu = new Menu("ヘルプ(_H)");
        MenuItem about = new MenuItem("sodacs.jpにお問い合わせ下さい...");
        about.setDisable(true);
        about.setOnAction(e -> {
                
        });
        helpMenu.getItems().add(about);
    
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, viewMenu, helpMenu);
        
        return menuBar;
    }
}