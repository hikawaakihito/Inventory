import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.*;
import javafx.geometry.*;
import javafx.collections.*;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import java.sql.*;

public class ProductTable{
    //在庫の表を作ります
    private static TableView<Product> table;
    public static int catInput;
    private static TableColumn<Product, String> idColumn;

    public static TableColumn<Product, String> getIdColumn(){
        return idColumn;
    }

    public static VBox getProductTable(DBHandler databaseHandler){

        idColumn = new TableColumn<>("商品番号");
        idColumn.setMinWidth(50);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Product, String> catColumn = new TableColumn<>("カテゴリー");
        catColumn.setMinWidth(100);
        catColumn.setCellValueFactory(new PropertyValueFactory<>("parent"));
        TableColumn<Product, String> nameColumn = new TableColumn<>("商品名");
        nameColumn.setMinWidth(200);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Product, Double> priceColumn = new TableColumn<>("原価");
        priceColumn.setMinWidth(100);
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("現在庫");
        quantityColumn.setMinWidth(50);
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        table = new TableView<>();
        table.setMinHeight(700);
        table.setMaxHeight(700);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setMinWidth(600);
        table.setItems(LoadProducts.getProducts(databaseHandler));
        viewColumn(idColumn, false);
        table.getColumns().addAll(idColumn, catColumn, nameColumn, priceColumn, quantityColumn);



        VBox t_VBox = new VBox(10);
        t_VBox.setPadding(new Insets(20, 20, 20, 20));
        t_VBox.getChildren().addAll(table, ProductTable.getProductControls(databaseHandler));

        return t_VBox;
    }

    //在庫の編集操作
    public static HBox getProductControls(DBHandler databaseHandler){
        TextField nameInput, priceInput, quantityInput, adjustInput;

        nameInput = new TextField();
        nameInput.setPromptText("商品名");
        nameInput.setMinWidth(100);
        priceInput = new TextField();
        priceInput.setPromptText("原価");
        priceInput.setMinWidth(100);
        quantityInput = new TextField();
        quantityInput.setPromptText("現在庫数");
        quantityInput.setMinWidth(100);
        Button addProductButton = new Button("新規追加");
        addProductButton.setOnAction(e -> addProductButtonClicked(databaseHandler, ProductTable.catInput, nameInput, priceInput, quantityInput));

        adjustInput = new TextField();
        adjustInput.setPromptText("現在庫数");
        adjustInput.setMinWidth(50);
        Button adjustInputButton = new Button("在庫を増減");
        adjustInputButton.setOnAction(e -> adjustInputButtonClicked(databaseHandler, adjustInput));

        Button deleteProductButton = new Button("削除（注意）");
        deleteProductButton.setOnAction(e -> deleteProductButtonClicked(databaseHandler));

        //Table HBox
        HBox t_HBox = new HBox();
        t_HBox.setPadding(new Insets(10,10,10,0));
        t_HBox.setSpacing(10);
        t_HBox.getChildren().addAll(nameInput, priceInput, quantityInput, addProductButton, adjustInput, adjustInputButton, deleteProductButton);

        return t_HBox;
    }

    public static void viewColumn(TableColumn<Product, String> column, boolean showColumn){
        if(showColumn)  column.setVisible(true);
        else column.setVisible(false);
    }

    public static void reloadTable(DBHandler databaseHandler){
        table.setItems(LoadProducts.getProducts(databaseHandler));
    }
    
    private static void addProductButtonClicked(DBHandler databaseHandler, int catInput, TextField nameInput, TextField priceInput, TextField quantityInput){
        try{            
            if(Double.parseDouble(priceInput.getText()) >= 0 && Integer.parseInt(quantityInput.getText()) >= 0){
                            ResultSet rs = databaseHandler.execQuery("SELECT name FROM category WHERE id = " + catInput);
                String parentName = "";
                try{
                    rs.next();
                    parentName = rs.getString("name");
                }catch(SQLException e){
                    //
                }
                if(ConfirmBox.item(parentName, nameInput.getText(), Double.parseDouble(priceInput.getText()), Integer.parseInt(quantityInput.getText()))){
                    databaseHandler.execAction("INSERT INTO inventory (parent, name, price, quantity) VALUES (" + catInput + ", '" + nameInput.getText() + "', " + Double.parseDouble(priceInput.getText()) + ", " + Integer.parseInt(quantityInput.getText()) + ")");
                
                    table.setItems(LoadProducts.getProducts(databaseHandler));
                    nameInput.clear();
                    priceInput.clear();
                    quantityInput.clear();
                }
            }else{
                ConfirmBox.alert("エラー", "原価と現在庫は0以上でないと行けません。");
            }
        }catch(NumberFormatException e1){
            ConfirmBox.alert("エラー", "原価は整数もしくは実数、現在庫は整数でないと行けません。");
        }
    }

    private static void adjustInputButtonClicked(DBHandler databaseHandler, TextField adjustInput){////////////////////////////////////////////////////////////////////////////////////
        try{            
            if(Integer.parseInt(adjustInput.getText()) >= 0){
                ObservableList<Product> productSelected, allProducts;
                allProducts = table.getItems();
                productSelected = table.getSelectionModel().getSelectedItems();
                Product prod = new Product();
                for(Product cat : productSelected){
                    prod = cat;
                }
                if(prod.getId() > 0){
                    if(ConfirmBox.confirm("在庫の増減", prod.getParent() + " | " + prod.getName() + " | " + prod.getPrice() + "円 | " + prod.getQuantity() + "個　" + "\n\n"
                    + "の在庫を" + Integer.parseInt(adjustInput.getText()) + "個にしますか？")){
                        databaseHandler.execAction("UPDATE inventory SET quantity = " + Integer.parseInt(adjustInput.getText()) + " WHERE id = " + prod.getId());

                        table.setItems(LoadProducts.getProducts(databaseHandler));
                        adjustInput.clear();
                    }
                }else{
                    ConfirmBox.alert("エラー", "在庫項目を選択して下さい。");
                }
            }else{
                ConfirmBox.alert("エラー", "現在庫は0以上でないと行けません。");
            }
        }catch(NumberFormatException e1){
            ConfirmBox.alert("エラー", "現在庫は整数でないと行けません。");
        }
    }

    private static void deleteProductButtonClicked(DBHandler databaseHandler){
        ObservableList<Product> productSelected, allProducts;
        allProducts = table.getItems();
        productSelected = table.getSelectionModel().getSelectedItems();
        Product prod = new Product();
        for(Product cat : productSelected){
            prod = cat;
        }
        if(ConfirmBox.confirm("注意！！！", prod.getParent() + " | " + prod.getName() + " | " + prod.getPrice() + "円 | " + prod.getQuantity() + "個　" + "\n\n"
                            + "を本当に完全にしますか？")){
            databaseHandler.execAction("DELETE FROM inventory WHERE id = " + prod.getId());
            try{
                productSelected.forEach(allProducts::remove);
            }catch(Exception e){

            }
        }
    }
}