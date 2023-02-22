package vungnv.com.foodappadmin.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vungnv.com.foodappadmin.R;
import vungnv.com.foodappadmin.constant.Constant;
import vungnv.com.foodappadmin.model.UserMerchantModel;
import vungnv.com.foodappadmin.utils.EncryptingPassword;
import vungnv.com.foodappadmin.utils.NetworkChangeListener;

public class ActiveAccountActivity extends AppCompatActivity implements Constant {
    private EditText edName, edNameRestaurant, edEmail, edPhone, edAddress, edCoordinate;
    private ImageView img, imgBack;
    private Button btnSave;
    private TextView tvCancel;

    private UserMerchantModel itemUserMerchant;
    private ArrayList<UserMerchantModel> aListUserMerchant;

    private final EncryptingPassword encryptingPassword = new EncryptingPassword();
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();

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
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert bundle != null;
                // active account and save in firebase auth
                String phone = edPhone.getText().toString().trim();
                String email = edEmail.getText().toString().trim();
                if (checkEmail(email)) {
                    if (checkPhoneNumber(phone)) {
                        int index = email.indexOf("@");
                        String userName = email.substring(0, index);
                        String domain = "@merchant.com";
                        String newEmail = userName + domain;
                        String pass = generatePassword();
                        String newPass = shuffledPassword(pass);
                        String imgID = bundle.getString("img");
                        String name = edName.getText().toString().trim();
                        String restaurantName = edNameRestaurant.getText().toString().trim();
                        String phoneNumber = edPhone.getText().toString().trim();
                        String address = edAddress.getText().toString().trim();
                        String coordinate = edCoordinate.getText().toString().trim();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.createUserWithEmailAndPassword(newEmail, newPass)
                                .addOnCompleteListener(ActiveAccountActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {

                                            Log.d(TAG, "info account: (email,pass)" + "(" + newEmail + "," + newPass + ")");

                                            DatabaseReference ref = FirebaseDatabase.getInstance()
                                                    .getReference().child("list_user_merchant_default")
                                                    .child(userName).child("status");
                                            ref.setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    upLoadUser(auth.getUid(), 1, imgID, name, newEmail, newPass, phoneNumber, restaurantName, address, coordinate);
                                                    updateListUserNoActive(String.valueOf(userName));
                                                    Toast.makeText(ActiveAccountActivity.this, ACTIVE_ACC_SUCCESS, Toast.LENGTH_SHORT).show();
                                                    onBackPressed();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(ActiveAccountActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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

                Dialog dialog = new Dialog(ActiveAccountActivity.this);
                dialog.setContentView(R.layout.dialog_confirm_delete);
                dialog.setCancelable(false);
                Button btnCancel, btnConfirm;

                btnCancel = dialog.findViewById(R.id.btnCancel);
                btnConfirm = dialog.findViewById(R.id.btnConfirm);
                btnConfirm.setOnClickListener(v1 -> {
                    //  remove item
                    dialog.dismiss();
                });
                btnCancel.setOnClickListener(v12 -> dialog.dismiss());
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setAttributes(lp);
                dialog.show();
            }
        });
    }

    private void init() {
        img = findViewById(R.id.imgMerchant);
        imgBack = findViewById(R.id.imgBack);
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
        storageRef.child("images_users/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    private String shuffledPassword(String oldPass) {
        List<Character> characters = new ArrayList<>();
        for (char c : oldPass.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);
        StringBuilder shuffledPassword = new StringBuilder();
        for (char c : characters) {
            shuffledPassword.append(c);
        }
        return shuffledPassword.toString();
    }

    private void upLoadUser(String id, int status, String img, String name, String email, String pass, String phoneNumber, String restaurantName, String address, String coordinate) {
        String encryptPass = encryptingPassword.EncryptPassword(pass);
        UserMerchantModel user = new UserMerchantModel(id, status, img, name, email, encryptPass, phoneNumber, restaurantName, address, coordinate);
        Map<String, Object> mListUser = user.toMap();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        Map<String, Object> updates = new HashMap<>();

        updates.put("list_user_merchant/" + id, mListUser);
        reference.updateChildren(updates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Log.d(TAG, "upload user to firebase success: ");
            }
        });
    }

    private void updateListUserNoActive(String userName) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("list_user_merchant_default").child(userName);
        reference.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Data deleted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting data", e);
                    }
                });
    }
    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}