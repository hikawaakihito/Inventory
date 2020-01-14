import java.sql.*;
import javax.swing.JOptionPane;

import javafx.scene.control.Alert;

public class DBHandler{
    private static final String DB_URL = "jdbc:derby:theDB;create=true";
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
    private static Connection conn = null;
    private static Statement stmt = null;

    public DBHandler(){
        createConnection();
        setupCategoryTable();
        setupInventoryTable();
    }

    public void createConnection(){
        try{
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DB_URL);
        }catch(Exception e){
            //e.printStackTrace();
        }
    }

    public void setupCategoryTable(){
        final String TABLE_NAME = "category";
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME, null);

            if(tables.next()){
                System.out.println("テーブル「" + TABLE_NAME + "」は既存です。そのまま使用します。");
            }else{
                stmt.execute("CREATE TABLE " + TABLE_NAME + " (\n"
                        + "     id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n"
                        + "     parent INT,\n"
                        + "     name VARCHAR(255),\n"
                        + "     PRIMARY KEY (id)\n"
                        + " )");
            }
        }catch(SQLException e){
            //System.err.println(e.getMessage() + "... DB　か　category TABLE セットアップエラー");
        }finally{
            ResultSet rootItem = execQuery("SELECT id FROM category WHERE parent = 0 AND id = 1");
            int rootItemId = -1;
            try{
                rootItem.next();
                rootItemId = rootItem.getInt("id");
            }catch(Exception e){
                //System.out.println(e.getMessage());
            }
            if(rootItemId != 1){
                if(execAction("INSERT INTO category (parent, name) VALUES (0, '在庫カテゴリー')")){
                    //Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    //alert.setContentText("初期化が成功しました...");
                    //alert.showAndWait();
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("Root Setup Failure");
                    alert.showAndWait();
                }
            }
        }
    }

    public void setupInventoryTable(){
        final String TABLE_NAME = "inventory";
        try{
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME, null);
            if(tables.next()){
                System.out.println("テーブル「" + TABLE_NAME + "」は既存です。そのまま使用します。");
            }else{
                stmt.execute("CREATE TABLE " + TABLE_NAME + " (\n"
                        + "     id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n"
                        + "     parent INT,\n"
                        + "     name VARCHAR(255),\n"
                        + "     price DOUBLE,\n"
                        + "     quantity INT,\n"
                        + "     PRIMARY KEY (id),\n"
                        + "     FOREIGN KEY (parent) REFERENCES category(id)"
                        + " )");
            }
        }catch(SQLException e){
            //System.err.println(e.getMessage() + "... DB　か　inventory TABLE セットアップエラー");
        }finally{

        }
    }

    public ResultSet execQuery(String query){
        try{
            stmt = conn.createStatement();
            ResultSet result = stmt.executeQuery(query);
            return result;
        }catch(SQLException e){
            //System.out.println("Exception at execQuery:" + e.getLocalizedMessage());
            return null;
        }finally{
        }
    }

    public boolean execAction(String statement){
        try{
            stmt = conn.createStatement();
            stmt.execute(statement);
            return true;
        }catch(SQLException e){
            //JOptionPane.showMessageDialog(null, "Error:" + e.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            //System.out.println("Exception at execAction:" + e.getLocalizedMessage());
            return false;
        }finally{
        }
    }
}