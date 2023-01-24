package vungnv.com.foodappadmin.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import vungnv.com.foodappadmin.R;
import vungnv.com.foodappadmin.activities.ActiveAccountActivity;
import vungnv.com.foodappadmin.activities.ActiveProductActivity;
import vungnv.com.foodappadmin.activities.ListProductOfUserMerchantActivity;
import vungnv.com.foodappadmin.constant.Constant;
import vungnv.com.foodappadmin.dao.ProductDAO;
import vungnv.com.foodappadmin.model.ProductDetailModel;
import vungnv.com.foodappadmin.model.UserMerchantModel;

public class ProductsAwaitingGroupByUserApprovalAdapter extends RecyclerView.Adapter<ProductsAwaitingGroupByUserApprovalAdapter.viewHolder> implements Constant {
    private Context context;
    private List<UserMerchantModel> list;
    private ProductDAO productDAO;

    public ProductsAwaitingGroupByUserApprovalAdapter(Context context, List<UserMerchantModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_group_by_user_awaiting_approval, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        productDAO = new ProductDAO(context);
        UserMerchantModel item = list.get(position);
        String idImage = item.img;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("images_users/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                // Load the image using Glide
                Glide.with(context)
                        .load(uri)
                        .into(holder.img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d(TAG, "get image from firebase: " + exception.getMessage());
            }
        });
        holder.tvName.setText(item.name);
        holder.tvNameRestaurant.setText(item.restaurantName);
        holder.btnSeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ListProductOfUserMerchantActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", item.id);
                intent.putExtra("data-type", bundle);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvNameRestaurant;
        private ImageView img;
        private Button btnSeeDetail;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgMerchant);
            tvName = itemView.findViewById(R.id.tvNameMerchant);
            tvNameRestaurant = itemView.findViewById(R.id.tvRestaurantMerchant);
            btnSeeDetail = itemView.findViewById(R.id.btnSeeDetail);

        }
    }
}
