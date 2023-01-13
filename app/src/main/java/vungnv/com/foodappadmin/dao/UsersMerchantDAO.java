package vungnv.com.foodappadmin.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import vungnv.com.foodappadmin.database.DbUserMerchant;
import vungnv.com.foodappadmin.model.UserMerchantModel;

public class UsersMerchantDAO {
    private final SQLiteDatabase db;

    public UsersMerchantDAO(Context context) {
        DbUserMerchant dbHelper = new DbUserMerchant(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(UserMerchantModel obj) {
        ContentValues values = new ContentValues();
        values.put("id", obj.id);
        values.put("status", obj.status);
        values.put("img", obj.img);
        values.put("name", obj.name);
        values.put("email", obj.email);
        values.put("pass", obj.pass);
        values.put("phoneNumber", obj.phoneNumber);
        values.put("restaurantName", obj.restaurantName);
        values.put("address", obj.address);
        values.put("coordinates", obj.coordinates);
        values.put("feedback", obj.feedback);

        return db.insert("UserMerchant", null, values);
    }


    public int updateProfile( UserMerchantModel obj) {
        ContentValues values = new ContentValues();
        values.put("name", obj.name);
        values.put("phoneNumber", obj.phoneNumber);
        values.put("address", obj.address);

        return db.update("UserMerchant", values, "email=?", new String[]{obj.email});
    }
    public int updateAll( UserMerchantModel obj) {
        ContentValues values = new ContentValues();
        values.put("img", obj.img);
        values.put("name", obj.name);
        values.put("pass", obj.pass);
        values.put("phoneNumber", obj.phoneNumber);
        values.put("coordinates", obj.coordinates);
        values.put("address", obj.address);

        return db.update("UserMerchant", values, "email=?", new String[]{obj.email});
    }

    public int updateImg(UserMerchantModel obj) {
        ContentValues values = new ContentValues();
        values.put("img", obj.img);
        return db.update("UserMerchant", values, "email=?", new String[]{obj.email});
    }

    public int updateFeedBack(UserMerchantModel obj) {
        ContentValues values = new ContentValues();
        values.put("feedback", obj.feedback);
        return db.update("UserMerchant", values, "email=?", new String[]{obj.email});
    }
    public int updatePass(UserMerchantModel obj){
        ContentValues values = new ContentValues();
        values.put("pass", obj.pass);
        return db.update("UserMerchant", values, "email=?", new String[]{obj.email});
    }

    public String getNameUser(String email) {
        String sql = "SELECT * FROM UserMerchant WHERE email=?";
        List<UserMerchantModel> list = getData(sql, email);
        return list.get(0).name;
    }
    public String getPhone(String email) {
        String sql = "SELECT * FROM UserMerchant WHERE email=?";
        List<UserMerchantModel> list = getData(sql, email);
        return list.get(0).phoneNumber;
    }
    public String getAddress(String email) {
        String sql = "SELECT * FROM UserMerchant WHERE email=?";
        List<UserMerchantModel> list = getData(sql, email);
        return list.get(0).address;
    }
    public String getFeedback(String email) {
        String sql = "SELECT * FROM UserMerchant WHERE email=?";
        List<UserMerchantModel> list = getData(sql, email);
        return list.get(0).feedback;
    }
    public String autoFillPassWord(String email) {
        String sql = "SELECT * FROM UserMerchant WHERE email=?";
        List<UserMerchantModel> list = getData(sql, email);
        return list.get(0).pass;
    }

    public String getUriImg(String email) {
        String sql = "SELECT * FROM UserMerchant WHERE email=?";
        List<UserMerchantModel> list = getData(sql, email);
        if (list.size() != 0){
            return list.get(0).img;
        }
       return "";
    }
    public String getIDUser(String email) {
        String sql = "SELECT * FROM UserMerchant WHERE email=?";
        List<UserMerchantModel> list = getData(sql, email);
        return list.get(0).id;
    }

    public String getCurrentPass(String email) {
        String sql = "SELECT * FROM UserMerchant WHERE email=?";
        List<UserMerchantModel> list = getData(sql, email);
        return list.get(0).pass;
    }

    public int delete(int id) {
        return db.delete("UserMerchant", "id=?", new String[]{String.valueOf(id)});
    }


    public List<UserMerchantModel> getALL() {
        String sql = "SELECT * FROM UserMerchant";
        return getData(sql);
    }
    public List<UserMerchantModel> getALLByEmail(String email) {
        String sql = "SELECT * FROM UserMerchant WHERE email=?";
        return getData(sql, email);
    }


    @SuppressLint("Range")
    private List<UserMerchantModel> getData(String sql, String... selectionArgs) {
        List<UserMerchantModel> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(sql, selectionArgs);
        while (cursor.moveToNext()) {
            UserMerchantModel obj = new UserMerchantModel();
            obj.stt = Integer.parseInt(cursor.getString(cursor.getColumnIndex("stt")));
            obj.id = cursor.getString(cursor.getColumnIndex("id"));
            obj.status = Integer.parseInt(cursor.getString(cursor.getColumnIndex("status")));
            obj.img = cursor.getString(cursor.getColumnIndex("img"));
            obj.name = cursor.getString(cursor.getColumnIndex("name"));
            obj.email = cursor.getString(cursor.getColumnIndex("email"));
            obj.pass = cursor.getString(cursor.getColumnIndex("pass"));
            obj.phoneNumber = cursor.getString(cursor.getColumnIndex("phoneNumber"));
            obj.restaurantName = cursor.getString(cursor.getColumnIndex("restaurantName"));
            obj.address = cursor.getString(cursor.getColumnIndex("address"));
            obj.coordinates = cursor.getString(cursor.getColumnIndex("coordinates"));
            obj.feedback = cursor.getString(cursor.getColumnIndex("feedback"));

            list.add(obj);

        }
        return list;

    }
    // check login in sqlite
    public boolean checkAccountExist(String email, String pass){
        String sql = "SELECT * FROM UserMerchant WHERE email=? AND pass=?";
        List<UserMerchantModel> list = getData(sql,email,pass);
        return list.size() != 0;

    }
}
