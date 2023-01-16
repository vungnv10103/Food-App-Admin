package vungnv.com.foodappadmin.model;

import java.util.HashMap;

public class UserMerchantModel {
    public int stt;
    public String id;
    public int status;
    public String img;
    public String name;
    public String email;
    public String pass;
    public String phoneNumber;
    public String restaurantName;
    public String address;
    public String coordinates;
    public String feedback;


    public UserMerchantModel(int stt, String id, int status, String img, String name, String email, String pass, String phoneNumber, String restaurantName, String coordinates, String address, String feedback) {
        this.stt = stt;
        this.id = id;
        this.status = status;
        this.img = img;
        this.name = name;
        this.email = email;
        this.pass = pass;
        this.phoneNumber = phoneNumber;
        this.restaurantName = restaurantName;
        this.coordinates = coordinates;
        this.address = address;
        this.feedback = feedback;
    }

    public UserMerchantModel(String email, String pass) {
        this.email = email;
        this.pass = pass;
    }
    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("pass", pass);
        return result;
    }

    public UserMerchantModel() {
    }
}
