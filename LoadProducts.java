import javafx.collections.*;
import java.sql.*;

public class LoadProducts{

        //在庫リストをゲット
        public static ObservableList<Product> getProducts(DBHandler databaseHandler){
            ObservableList<Product> products = FXCollections.observableArrayList();

            ResultSet rs = databaseHandler.execQuery("SELECT * FROM inventory");
                try{
                    while(rs.next()){
                        //+ "     id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n"
                        //+ "     parent INT,\n"
                        //+ "     name VARCHAR(255),\n"
                        //+ "     price DOUBLE,\n"
                        //+ "     quantity INT,\n"
                        //+ "     PRIMARY KEY (id),\n"
                        //+ "     FOREIGN KEY (parent) REFERENCES category(id)"
                        int id = rs.getInt("id");
                        int parent = rs.getInt("parent");
                        String name = rs.getString("name");
                        double price  = rs.getDouble("price");
                        int quantity = rs.getInt("quantity");

                        ResultSet rs2 = databaseHandler.execQuery("SELECT name FROM category WHERE id = " + parent);
                        String parentName = "";
                        try{
                            rs2.next();
                            parentName = rs2.getString("name");
                        }catch(SQLException e1){
                            //
                        }

                        products.add(new Product(id, parentName, name, price, quantity));
                    }
                }catch(SQLException e){

                }
    
            return products;
        }

}