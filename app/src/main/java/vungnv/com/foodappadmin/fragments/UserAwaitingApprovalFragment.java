package vungnv.com.foodappadmin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import vungnv.com.foodappadmin.adapter.ProductsAwaitingApprovalAdapter;
import vungnv.com.foodappadmin.adapter.UsersAwaitingApprovalAdapter;
import vungnv.com.foodappadmin.constant.Constant;
import vungnv.com.foodappadmin.dao.ProductDAO;
import vungnv.com.foodappadmin.dao.UsersMerchantDAO;
import vungnv.com.foodappadmin.model.UserMerchantModel;

public class UserAwaitingApprovalFragment extends Fragment implements Constant, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private ImageView imgBack;
    private Button btnFilter;
    private RecyclerView rcvListUserAwaiting;

    private List<UserMerchantModel> listUser;
    private ArrayList<UserMerchantModel> aListUser;
    private UsersMerchantDAO merchantDAO;
    private UserMerchantModel itemUser;
    private UsersAwaitingApprovalAdapter usersAwaitingApprovalAdapter;


    public UserAwaitingApprovalFragment() {
        // Required empty public constructor
    }

    public static UserAwaitingApprovalFragment newInstance() {
        return new UserAwaitingApprovalFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_awaiting_approval, container, false);
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

        listUserFromFB();
        return view;
    }

    private void init(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_user);
        swipeRefreshLayout.setOnRefreshListener(this);
        btnFilter = view.findViewById(R.id.btnFilter);
        rcvListUserAwaiting = view.findViewById(R.id.rcvListUserAwaiting);
        merchantDAO = new UsersMerchantDAO(getContext());
    }

    private void listUser() {
        listUser = merchantDAO.getALL();
        //Toast.makeText(getContext(), ""+ listProducts.size(), Toast.LENGTH_SHORT).show();
        if (listUser.size() == 0) {
            return;
        }
        usersAwaitingApprovalAdapter = new UsersAwaitingApprovalAdapter(getContext(), listUser);
        rcvListUserAwaiting.setAdapter(usersAwaitingApprovalAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvListUserAwaiting.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListUserAwaiting.getContext(),
                linearLayoutManager.getOrientation());
        rcvListUserAwaiting.addItemDecoration(dividerItemDecoration);
        rcvListUserAwaiting.setHasFixedSize(true);
        rcvListUserAwaiting.setNestedScrollingEnabled(false);
    }

    private void listUserFromFB() {
        aListUser = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("list_user_merchant");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                aListUser.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    UserMerchantModel model = snapshot1.getValue(UserMerchantModel.class);
                    assert model != null;
                    // get account not active
                    if (model.status == 0) {
                        aListUser.add(model);
                    }

                }

                if (aListUser.size() == 0) {
                    return;
                }

// add data to db local
//                listUser = merchantDAO.getALL();
//                for (int i = listUser.size(); i < aListUser.size(); i++) {
//                    // Log.d(TAG, "name: " + aListProducts.get(i).name);
//                    UserMerchantModel item = aListUser.get(i);
//                    itemUser = new UserMerchantModel();
//                    itemUser.id = item.id;
//                    itemUser.status = item.status;
//                    itemUser.img = item.img;
//                    itemUser.name = item.name;
//                    itemUser.email = item.email;
//                    itemUser.pass = item.pass;
//                    itemUser.phoneNumber = item.phoneNumber;
//                    itemUser.restaurantName = item.restaurantName;
//                    itemUser.address = item.address;
//                    itemUser.coordinates = item.coordinates;
//                    itemUser.feedback = item.feedback;
//                    if (merchantDAO.insert(itemUser) > 0) {
//                        Log.d(TAG, "update db user_merchant success ");
//
//                    }
//                }
//                listUser = merchantDAO.getALL();
                //Toast.makeText(getContext(), ""+ listUser.size(), Toast.LENGTH_SHORT).show();
                usersAwaitingApprovalAdapter = new UsersAwaitingApprovalAdapter(getContext(), aListUser);
                rcvListUserAwaiting.setAdapter(usersAwaitingApprovalAdapter);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
                rcvListUserAwaiting.setLayoutManager(linearLayoutManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListUserAwaiting.getContext(),
                        linearLayoutManager.getOrientation());
                rcvListUserAwaiting.addItemDecoration(dividerItemDecoration);
                rcvListUserAwaiting.setHasFixedSize(true);
                rcvListUserAwaiting.setNestedScrollingEnabled(false);

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
                listUserFromFB();

            }
        }, 1500);

    }
}