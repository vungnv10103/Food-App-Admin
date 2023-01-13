package vungnv.com.foodappadmin.fragments;

import android.os.Bundle;

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
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.util.Objects;

import vungnv.com.foodappadmin.MainActivity;
import vungnv.com.foodappadmin.R;
import vungnv.com.foodappadmin.adapter.ProductsAwaitingApprovalAdapter;
import vungnv.com.foodappadmin.constant.Constant;
import vungnv.com.foodappadmin.dao.ProductDAO;
import vungnv.com.foodappadmin.model.ProductDetailModel;

public class ProductAwaitingApprovalFragment extends Fragment implements Constant, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private ImageView imgBack;
    private Button btnFilter;
    private RecyclerView rcvListProductAwaiting;

    private ProductDAO productDAO;
    private ProductsAwaitingApprovalAdapter productsAwaitingApprovalAdapter;
    private List<ProductDetailModel> listProducts;


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

        listProduct();
        return view;
    }

    private void init(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_list_product);
        swipeRefreshLayout.setOnRefreshListener(this);
        btnFilter = view.findViewById(R.id.btnFilter);
        rcvListProductAwaiting = view.findViewById(R.id.rcvListProductAwaiting);
        productDAO = new ProductDAO(getContext());
    }

    private void listProduct() {
        listProducts = productDAO.getALLDefault();
        //Toast.makeText(getContext(), ""+ listProducts.size(), Toast.LENGTH_SHORT).show();
        if (listProducts.size() == 0) {
            return;
        }
        productsAwaitingApprovalAdapter = new ProductsAwaitingApprovalAdapter(getContext(), listProducts);
        rcvListProductAwaiting.setAdapter(productsAwaitingApprovalAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rcvListProductAwaiting.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rcvListProductAwaiting.getContext(),
                linearLayoutManager.getOrientation());
        rcvListProductAwaiting.addItemDecoration(dividerItemDecoration);
        rcvListProductAwaiting.setHasFixedSize(true);
        rcvListProductAwaiting.setNestedScrollingEnabled(false);
    }

    @Override
    public void onRefresh() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                // load data
                listProduct();

            }
        }, 1500);
    }
}