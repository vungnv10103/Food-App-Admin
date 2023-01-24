package vungnv.com.foodappadmin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import vungnv.com.foodappadmin.R;
import vungnv.com.foodappadmin.adapter.ProductsAwaitingApprovalAdapter;
import vungnv.com.foodappadmin.constant.Constant;
import vungnv.com.foodappadmin.dao.ProductDAO;
import vungnv.com.foodappadmin.model.ProductModel;

public class ListProductOfUserMerchantActivity extends AppCompatActivity implements Constant, SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private ImageView imgBack;
    private Button btnFilter;
    private RecyclerView rcvListProductOfUserMerchant;

    private ProductDAO productDAO;
    private ArrayList<ProductModel> aListProducts;
    private ProductsAwaitingApprovalAdapter productsAdapter;

    private SpotsDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product_of_user_merchant);

        init();
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data-type");
        if (bundle != null) {
            // get data
            String id = bundle.getString("id");
            listProduct(id);
            // set data

        }
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ListProductOfUserMerchantActivity.this, "Updating...", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void init() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_list_product_of_user);
        swipeRefreshLayout.setOnRefreshListener(this);
        btnFilter = findViewById(R.id.btnFilter);
        rcvListProductOfUserMerchant = findViewById(R.id.rcvListProductOfUserMerchant);
        productDAO = new ProductDAO(getApplicationContext());
        progressDialog = new SpotsDialog(ListProductOfUserMerchantActivity.this, R.style.Custom);
    }

    private void listProduct(String idUser) {
        aListProducts = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("list_product_not_active");
        ref.child(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                aListProducts.clear();
                for (DataSnapshot childSnapshot1 : dataSnapshot.getChildren()) {
                    ProductModel value = childSnapshot1.getValue(ProductModel.class);
                    assert value != null;
                    aListProducts.add(value);
                }

                productsAdapter = new ProductsAwaitingApprovalAdapter(ListProductOfUserMerchantActivity.this, aListProducts);
                rcvListProductOfUserMerchant.setAdapter(productsAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ListProductOfUserMerchantActivity.this, RecyclerView.VERTICAL, false);
                rcvListProductOfUserMerchant.setLayoutManager(linearLayoutManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListProductOfUserMerchant.getContext(),
                        linearLayoutManager.getOrientation());
                rcvListProductOfUserMerchant.addItemDecoration(dividerItemDecoration);
                rcvListProductOfUserMerchant.setHasFixedSize(true);
                rcvListProductOfUserMerchant.setNestedScrollingEnabled(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });

        progressDialog.dismiss();

    }

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                // load data

            }
        }, 1500);

    }
}