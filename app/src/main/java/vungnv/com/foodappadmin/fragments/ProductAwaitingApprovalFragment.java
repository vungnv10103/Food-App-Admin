package vungnv.com.foodappadmin.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import vungnv.com.foodappadmin.MainActivity;
import vungnv.com.foodappadmin.R;
import vungnv.com.foodappadmin.activities.ListProductOfUserMerchantActivity;
import vungnv.com.foodappadmin.adapter.ProductsAwaitingApprovalAdapter;
import vungnv.com.foodappadmin.adapter.ProductsAwaitingGroupByUserApprovalAdapter;
import vungnv.com.foodappadmin.adapter.UsersAwaitingApprovalAdapter;
import vungnv.com.foodappadmin.constant.Constant;
import vungnv.com.foodappadmin.dao.ProductDAO;
import vungnv.com.foodappadmin.dao.UsersMerchantDAO;
import vungnv.com.foodappadmin.model.ProductDetailModel;
import vungnv.com.foodappadmin.model.UserMerchantModel;

public class ProductAwaitingApprovalFragment extends Fragment implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;

    private Button btnFilter;

    private RecyclerView rcvListUserMerchant;

    private ProductDAO productDAO;
    private ProductsAwaitingApprovalAdapter productsAwaitingApprovalAdapter;
    private List<ProductDetailModel> listProducts;

    private UsersMerchantDAO merchantDAO;
    private ProductsAwaitingGroupByUserApprovalAdapter usersAwaitingApprovalAdapter;
    private List<UserMerchantModel> listUserMerchant;
    private ArrayList<UserMerchantModel> aListUserMerchant;


    public ProductAwaitingApprovalFragment() {
        // Required empty public constructor
    }

    public static ProductAwaitingApprovalFragment newInstance() {
        return new ProductAwaitingApprovalFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).setTitle(AWAITING_APPROVAL);
        View view = inflater.inflate(R.layout.fragment_awaiting_approval, container, false);
        init(view);
        swipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green));

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Updating...", Toast.LENGTH_SHORT).show();
            }
        });
        listUserMerchant();

        return view;
    }

    private void init(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_product);
        swipeRefreshLayout.setOnRefreshListener(this);
        btnFilter = view.findViewById(R.id.btnFilter);

        rcvListUserMerchant = view.findViewById(R.id.rcvListUserMerchant);
        productDAO = new ProductDAO(getContext());
        merchantDAO = new UsersMerchantDAO(getContext());
    }

    private void listUserMerchant() {
        aListUserMerchant = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_user_merchant");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aListUserMerchant.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    UserMerchantModel model = snapshot1.getValue(UserMerchantModel.class);
                    if (model == null) {
                        return;
                    }

                    aListUserMerchant.add(model);

                }

                if (aListUserMerchant.size() == 0) {
                    Toast.makeText(getContext(), "Không tìm thấy bất kỳ tài khoản nào !!", Toast.LENGTH_SHORT).show();
                    return;
                }

                usersAwaitingApprovalAdapter = new ProductsAwaitingGroupByUserApprovalAdapter(getContext(), aListUserMerchant);
                rcvListUserMerchant.setAdapter(usersAwaitingApprovalAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                rcvListUserMerchant.setLayoutManager(linearLayoutManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListUserMerchant.getContext(),
                        linearLayoutManager.getOrientation());
                rcvListUserMerchant.addItemDecoration(dividerItemDecoration);
                rcvListUserMerchant.setHasFixedSize(true);
                rcvListUserMerchant.setNestedScrollingEnabled(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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