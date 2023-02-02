package vungnv.com.foodappadmin.activities;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodappadmin.R;
import vungnv.com.foodappadmin.constant.Constant;
import vungnv.com.foodappadmin.dao.UsersMerchantDAO;
import vungnv.com.foodappadmin.model.ProductModel;
import vungnv.com.foodappadmin.model.UserMerchantModel;
import vungnv.com.foodappadmin.utils.ImagePicker;
import vungnv.com.foodappadmin.utils.NetworkChangeListener;

public class ActiveProductActivity extends AppCompatActivity implements Constant {
    private EditText edName, edCate, edPrice, edTime, edDesc;
    private ImageView img, imgBack;
    private Button btnSave;
    private TextView tvCancel;

    private UsersMerchantDAO merchantDAO;
    private UserMerchantModel itemUserMerchant;
    private ArrayList<UserMerchantModel> aListUserMerchant;

    private String fileName = "";
    private int temp = 0;

    private ArrayList<UserMerchantModel> aListUser;

    private SpotsDialog progressDialog;
    private final NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_product);

        init();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.pickImage(ActiveProductActivity.this, new ImagePicker.OnImagePickedListener() {
                    @Override
                    public void onImagePicked(Uri uri) {
                        progressDialog.show();
                        // Get a reference to the storage location
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

                        // Create a reference to the file to upload
                        fileName = uri.getLastPathSegment().substring(6);
                        StorageReference imageRef = storageRef.child("images_product/" + fileName);

                        Log.d(TAG, "onImagePicked: " + uri.getLastPathSegment().substring(6));
                        //Upload the file to the reference
                        UploadTask uploadTask = imageRef.putFile(uri);
                        img.setImageURI(uri);
                        temp++;
                        progressDialog.dismiss();


                    }
                });
            }
        });


        //Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/loadimg-8b067.appspot.com/o/images%2F2022_12_04_07_45_07?alt=media&token=0525ea03-1702-4542-9290-a60cac8a8d67").into(img);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data-type");
        if (bundle != null) {
            // get data

            int pos = bundle.getInt("pos");
            String address = bundle.getString("address");

            String img = bundle.getString("img");
            setImg(img);
            String name = bundle.getString("name");
            String type = bundle.getString("type");
            Double price = bundle.getDouble("price");
            String time = bundle.getString("timeDelay");
            String desc = bundle.getString("description");
//            Toast.makeText(ActiveProductActivity.this, "pos: " + pos, Toast.LENGTH_SHORT).show();
//            Toast.makeText(ActiveProductActivity.this, "coor: " + address, Toast.LENGTH_SHORT).show();

            // set data
            edName.setText(name);
            edCate.setText(type);
            edPrice.setText(String.valueOf(price));
            edTime.setText(time);
            edDesc.setText(desc);

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
                String idUser = bundle.getString("idUser");
                String img = bundle.getString("img");
                String address = bundle.getString("address");
                String name = edName.getText().toString().trim();
                String cate = edCate.getText().toString().trim();
                double price = Double.parseDouble(edPrice.getText().toString().trim());
                String time = edTime.getText().toString().trim();
                String desc = edDesc.getText().toString().trim();

                if (temp > 0) {
                    upLoadProductByUserID(idUser, cate, fileName, name, desc, time, price, address);
                    upLoadProductDefault(idUser, cate, fileName, name, desc, time, price, address);
                    updateListProductNoActive(idUser, name);
                } else {
                    upLoadProductByUserID(idUser, cate, img, name, desc, time, price, address);
                    upLoadProductDefault(idUser, cate, img, name, desc, time, price, address);
                    updateListProductNoActive(idUser, name);
                }


            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dialog dialog = new Dialog(ActiveProductActivity.this);
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
        merchantDAO = new UsersMerchantDAO(getApplicationContext());
        img = findViewById(R.id.imgProduct);
       imgBack = findViewById(R.id.imgBack);
        edName = findViewById(R.id.edNameProduct);
        edCate = findViewById(R.id.edCategory);
        edPrice = findViewById(R.id.edPrice);
        edTime = findViewById(R.id.edTime);
        edDesc = findViewById(R.id.edDesc);
        btnSave = findViewById(R.id.btnSave);
        tvCancel = findViewById(R.id.tvCancel);
        progressDialog = new SpotsDialog(ActiveProductActivity.this, R.style.Custom);
    }


    private void getCoordinate(String idUser) {
        aListUser = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_user_merchant/" + idUser + "/coordinates");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get db from firebase
                String coordinates = Objects.requireNonNull(snapshot.getValue()).toString();
                Log.d(TAG, "result coordinate: " + coordinates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setImg(String idImage) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("images_product/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
    private void upLoadProductByUserID(String idUser, String type, String img, String name, String description,
                               String timeDelay, double price, String coordinate) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_product/" + idUser);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long childCount = dataSnapshot.getChildrenCount();
                ProductModel user = new ProductModel(idUser, type, img, name, description, timeDelay, price, 0.0,
                        0.0, 0, 0, 2, coordinate, "", 0, 0);
                Map<String, Object> mListProduct = user.toMap();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                Map<String, Object> updates = new HashMap<>();
                updates.put("list_product/" + idUser + "/" + childCount, mListProduct);
                reference.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(ActiveProductActivity.this, REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();
                        clearForm();
                        Log.d(TAG, "upload product to firebase success ");
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
    private void upLoadProductDefault(String idUser, String type, String img, String name, String description,
                                       String timeDelay, double price, String coordinate) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("list_product_all");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long childCount = dataSnapshot.getChildrenCount();
                ProductModel user = new ProductModel(idUser, type, img, name, description, timeDelay, price,0.0,
                        0.0, 0, 0, 2, coordinate, "", 0, 0);
                Map<String, Object> mListProduct = user.toMap();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                Map<String, Object> updates = new HashMap<>();
                updates.put("list_product_all/" + childCount, mListProduct);
                reference.updateChildren(updates, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        Toast.makeText(ActiveProductActivity.this, REGISTER_SUCCESS, Toast.LENGTH_SHORT).show();
                        clearForm();
                        Log.d(TAG, "upload product to firebase success ");
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private void clearForm() {
        img.setImageResource(R.drawable.default_thumbnail);
        edName.setText("");
        edCate.setText("");
        edPrice.setText("");
        edTime.setText("");
        edDesc.setText("");
    }


    private void updateListProductNoActive(String idUser, String name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("list_product_not_active").child(idUser).child(name);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePicker.onActivityResult(requestCode, resultCode, data);
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