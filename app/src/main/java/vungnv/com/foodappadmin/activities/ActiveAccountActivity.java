package vungnv.com.foodappadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vungnv.com.foodappadmin.R;
import vungnv.com.foodappadmin.constant.Constant;

public class ActiveAccountActivity extends AppCompatActivity implements Constant {
    private EditText edName, edNameRestaurant, edEmail, edPhone, edAddress, edCoordinate;
    private ImageView img;
    private Button btnSave;
    private TextView tvCancel;

    private static final SecureRandom random = new SecureRandom();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_account);

        init();


        //Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/loadimg-8b067.appspot.com/o/images%2F2022_12_04_07_45_07?alt=media&token=0525ea03-1702-4542-9290-a60cac8a8d67").into(img);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data-type");
        if (bundle != null) {
            // get data
            String img = bundle.getString("img");
            setImg(img);
            String name = bundle.getString("name");
            String nameRestaurant = bundle.getString("restaurantName");
            String email = bundle.getString("email");
            String phoneNumber = bundle.getString("phoneNumber");
            String address = bundle.getString("address");
            String coordinate = bundle.getString("coordinates");

            // set data
            edName.setText(name);
            edNameRestaurant.setText(nameRestaurant);
            edEmail.setText(email);
            edPhone.setText(phoneNumber);
            edAddress.setText(address);
            edCoordinate.setText(coordinate);
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // active account and save in firebase auth
                String phone = edPhone.getText().toString().trim();
                String email = edEmail.getText().toString().trim();
                if (checkEmail(email)) {
                    if (checkPhoneNumber(phone)) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        String pass = generatePassword();
                        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(ActiveAccountActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ActiveAccountActivity.this, "Đăng kí thành  công !", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                }

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void init() {
        img = findViewById(R.id.imgMerchant);
        edName = findViewById(R.id.edNameMerchant);
        edNameRestaurant = findViewById(R.id.edRestaurantName);
        edEmail = findViewById(R.id.edEmail);
        edPhone = findViewById(R.id.edPhoneNumber);
        edAddress = findViewById(R.id.edAddress);
        edCoordinate = findViewById(R.id.edCoordinate);
        btnSave = findViewById(R.id.btnSave);
        tvCancel = findViewById(R.id.tvCancel);
    }


    private void setImg(String idImage) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("images/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                // Load the image using Glide
                Glide.with(getApplicationContext())
                        .load(uri)
                        .into(img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, "get image from firebase: " + exception.getMessage());
            }
        });
    }

    private boolean checkPhoneNumber(String phoneNumber) {
        String regex = "^(03|08)[0-9]{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        if (!matcher.matches()) {
            Toast.makeText(getApplicationContext(), WRONG_PHONE_FORMAT, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            Toast.makeText(getApplicationContext(), WRONG_EMAIL_FORMAT, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String generatePassword() {
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int randomIndex = random.nextInt(LOWERCASE_CHARS.length());
            password.append(LOWERCASE_CHARS.charAt(randomIndex));
        }

        for (int i = 0; i < 2; i++) {
            int randomIndex = random.nextInt(UPPERCASE_CHARS.length());
            password.append(UPPERCASE_CHARS.charAt(randomIndex));
        }

        for (int i = 0; i < 2; i++) {
            int randomIndex = random.nextInt(SPECIAL_CHARS.length());
            password.append(SPECIAL_CHARS.charAt(randomIndex));
        }
        return password.toString();
    }
}