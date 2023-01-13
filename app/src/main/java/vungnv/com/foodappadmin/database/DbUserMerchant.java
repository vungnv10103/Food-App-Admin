package vungnv.com.foodappadmin.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import vungnv.com.foodappadmin.constant.Constant;

public class DbUserMerchant extends SQLiteOpenHelper implements Constant {

    public DbUserMerchant(@Nullable Context context) {
        super(context, DB_USER_MERCHANT, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableUserMerchant = "create table UserMerchant(" +
                "stt INTEGER PRIMARY KEY AUTOINCREMENT," +
                "id TEXT not null," +
                "status INTEGER not null," +
                "img TEXT ," +
                "name TEXT not null," +
                "email TEXT not null," +
                "pass TEXT not null," +
                "phoneNumber TEXT not null," +
                "restaurantName TEXT not null," +
                "feedback TEXT ," +
                "coordinates TEXT not null," +
                "address TEXT not null)";
        db.execSQL(createTableUserMerchant);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableUserMerchant = "drop table if exists UserMerchant";
        db.execSQL(dropTableUserMerchant);

        onCreate(db);

    }
}
