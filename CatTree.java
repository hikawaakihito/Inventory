import javafx.scene.text.*;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.*;
import javafx.event.EventHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javafx.event.ActionEvent;

class IntRef { 
    public Integer value; 
  
    IntRef(Integer value){ 
        this.value = value; 
    } 
  
    @Override
    public String toString() 
    { 
        return String.valueOf(value); 
    } 
}

public class CatTree{
    public static Category currentCat;
    private static TreeView<Category> tree;
    private static ArrayList<TreeItem<Category>> branchList = new ArrayList<>();
    private static TreeItem<Category> root = new TreeItem<>(new Category(1, 0, "在庫カテゴリー"));
    private static HBox cat_HBox = new HBox();
    private static VBox cat_VBox =new VBox();

    public static VBox getCatTree(DBHandler databaseHandler){
        root.setExpanded(true);

        buildTree(databaseHandler);

        CatTree.currentCat = root.getValue();
        ProductTable.catInput = currentCat.getId();
        tree.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if(newValue != null){
                ProductTable.catInput = newValue.getValue().getId();
                setCurrentItem(currentCat, newValue.getValue());
                System.out.println(currentCat);
            }
        });

        TextField nameInput = new TextField();
        nameInput.setPromptText("カテゴリー名");
        nameInput.setMinWidth(100);

        Button addCategoryButton = new Button("新規追加");
        addCategoryButton.setOnAction(e -> addCategoryButtonClicked(databaseHandler, CatTree.currentCat, nameInput));
        Button deleteCategoryButton = new Button("削除（注意)");
        deleteCategoryButton.setOnAction(e -> deleteCategoryButtonClicked(databaseHandler, currentCat));

        //HBox
        cat_HBox.setSpacing(10);
        cat_HBox.getChildren().addAll(nameInput, addCategoryButton, deleteCategoryButton);

        //VBox
        cat_VBox.setPadding(new Insets(20,10,10,10));
        cat_VBox.setSpacing(20);
        cat_VBox.getChildren().addAll(tree, cat_HBox);
        
        return cat_VBox;
    }

    private static void buildTree(DBHandler databaseHandler){
        TreeItem<Category> parent = root;
        ResultSet rsCat = databaseHandler.execQuery("SELECT id, parent, name FROM category WHERE parent > 0 ORDER BY parent, name ASC");
        try{
            while(rsCat.next()){
                int id = rsCat.getInt("id");
                int parentId = rsCat.getInt("parent");
                String name = rsCat.getString("name");

                if(parentId == 1){
                    branchList.add(makeBranch(id, parentId, name, root));
                }else{
                    parent = branchList.get(getIndexByCatId(parentId));
                    branchList.add(makeBranch(id , parentId, name, parent));
                }
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

        tree = new TreeView<>(root);
        tree.setMinHeight(600);
        tree.setMaxHeight(600);
        tree.setShowRoot(true);
    }

    private static TreeItem<Category> makeBranch(int id, int parentId, String name, TreeItem<Category> parent){
        TreeItem<Category> item = new TreeItem<>(new Category(id, parentId, name));
        item.setExpanded(true);
        parent.getChildren().add(item);
        return item;
    }

    private static void addCategoryButtonClicked(DBHandler databaseHandler, Category currentCat, TextField nameInput){
        ResultSet existingItem = databaseHandler.execQuery("SELECT name, parent FROM category WHERE name = '" + nameInput.getText() + "' AND parent = " + currentCat.getId());
        String existingItemName = "";
        try{
            existingItem.next();
            existingItemName = existingItem.getString("name");
        }catch(Exception e){
            existingItemName = "";
            System.out.println(e.getLocalizedMessage());
        }
        if(existingItemName.equals(nameInput.getText())){
            ConfirmBox.alert("注意！", "選択したカテゴリーにその名前のカテゴリーがすでに存在しています。");
        }else{
            if(ConfirmBox.category(currentCat.getName(), nameInput.getText())){
                databaseHandler.execAction("INSERT INTO category (parent, name) VALUES (" + currentCat.getId() + ", '" + nameInput.getText() + "')");
                tree.setRoot(null);
                branchList = new ArrayList<>();
                root = new TreeItem<>(new Category(1, 0, "在庫カテゴリー"));
                root.setExpanded(true);
                TreeItem<Category> parent = root;
                ResultSet rsCat = databaseHandler.execQuery("SELECT id, parent, name FROM category WHERE parent > 0 ORDER BY parent, name ASC");
                try{
                    while(rsCat.next()){
                        int id = rsCat.getInt("id");
                        int parentId = rsCat.getInt("parent");
                        String name = rsCat.getString("name");
        
                        if(parentId == 1){
                            branchList.add(makeBranch(id, parentId, name, root));
                        }else{
                            parent = branchList.get(getIndexByCatId(parentId));
                            branchList.add(makeBranch(id , parentId, name, parent));
                        }
                    }
                }catch(SQLException e){
                    System.out.println(e.getMessage());
                }
        
                tree.setRoot(root);
                tree.setMinHeight(700);
                tree.setMaxHeight(700);
                tree.setShowRoot(true);
                nameInput.clear();
            }
        }
    }

    private static void deleteCategoryButtonClicked(DBHandler databaseHandler, Category currentCat){
        if(ConfirmBox.confirm("注意！！！", "本当に " + currentCat.getName() + " を完全に削除しますか？")){
            databaseHandler.execAction("DELETE FROM category WHERE id = " + currentCat.getId());

            databaseHandler.execAction("UPDATE category SET parent = " + currentCat.getParent() + ", name = (name || ' <<') WHERE parent = " + currentCat.getId());
            databaseHandler.execAction("UPDATE inventory SET parent = " + currentCat.getParent() + "WHERE parent = " + currentCat.getId());
            tree.setRoot(null);
            branchList = new ArrayList<>();
            root = new TreeItem<>(new Category(1, 0, "在庫カテゴリー"));
            root.setExpanded(true);
            TreeItem<Category> parent = root;
            ResultSet rsCat = databaseHandler.execQuery("SELECT id, parent, name FROM category WHERE parent > 0 ORDER BY parent, name ASC");
            try{
                while(rsCat.next()){
                    int id = rsCat.getInt("id");
                    int parentId = rsCat.getInt("parent");
                    String name = rsCat.getString("name");
    
                    if(parentId == 1){
                        branchList.add(makeBranch(id, parentId, name, root));
                    }else{
                        parent = branchList.get(getIndexByCatId(parentId));
                        branchList.add(makeBranch(id , parentId, name, parent));
                    }
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }
    
            tree.setRoot(root);
            tree.setMinHeight(700);
            tree.setMaxHeight(700);
            tree.setShowRoot(true);
        }

        ProductTable.reloadTable(databaseHandler);
    }

    public static void resetTree(){
        tree.setRoot(null);
        branchList = new ArrayList<>();
        root = new TreeItem<>(new Category(1, 0, "在庫カテゴリー"));
        CatTree.currentCat = root.getValue();
        ProductTable.catInput = currentCat.getId();
        root.setExpanded(true);

        tree.setRoot(root);
        tree.setMinHeight(700);
        tree.setMaxHeight(700);
        tree.setShowRoot(true);
    }

    private static void setCurrentItem(Category currentCat, Category selectedCat){
        currentCat.setId(selectedCat.getId());
        currentCat.setParent(selectedCat.getParent());
        currentCat.setName(selectedCat.getName());
    }

    private static int getIndexByCatId(int id){
        Category category;
        for (int i = 0; i < branchList.size(); i++) {
            category = branchList.get(i).getValue();
            if (category.getId() == id) {
                return i;
            }
        }
        return -1;
    }
}