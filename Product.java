public class Product{
    private int id;
    private String parent;
    private String name;
    private double price;
    private int quantity;

    public Product(){
        this.id = 0;
        this.parent = "";
        this.name = "";
        this.price = 0;
        this.quantity = 0;
    }

    public Product(int id, String parent, String name, double price, int quantity){
        this.id = id;
        this.parent = parent;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId(){
        return id;
    }

    public String getParent(){
        return parent;
    }

    public void setParent(String parent){
        this.parent = parent;
    }

    public String getName(){
        return name;
    }    
    public void setName(String name){
        this.name = name;
    }
    public double getPrice(){
        return price;
    }    
    public void setPrice(double price){
        this.price = price;
    }
    public int getQuantity(){
        return quantity;
    }    
    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    
}