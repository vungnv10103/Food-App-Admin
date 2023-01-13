package vungnv.com.foodappadmin.model;

public class ProductModel {
    public int id;
    public String img;
    public String name;
    public String nameRestaurant;

    public ProductModel() {
    }

    public ProductModel(int id, String img, String name, String nameRestaurant) {
        this.id = id;
        this.img = img;
        this.name = name;
        this.nameRestaurant = nameRestaurant;
    }
}
