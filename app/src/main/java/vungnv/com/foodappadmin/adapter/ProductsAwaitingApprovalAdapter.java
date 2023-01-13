package vungnv.com.foodappadmin.adapter;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
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
import vungnv.com.foodappadmin.constant.Constant;
import vungnv.com.foodappadmin.dao.ProductDAO;
import vungnv.com.foodappadmin.model.ProductDetailModel;
import vungnv.com.foodappadmin.model.ProductModel;

public class ProductsAwaitingApprovalAdapter extends RecyclerView.Adapter<ProductsAwaitingApprovalAdapter.viewHolder> implements Constant {
    private Context context;
    private List<ProductDetailModel> list;
    private ProductDAO productDAO;

    public ProductsAwaitingApprovalAdapter(Context context, List<ProductDetailModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_awaiting_approval, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        productDAO = new ProductDAO(context);
        ProductDetailModel item = list.get(position);
        String idImage = productDAO.getUriImg(item.id);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        storageRef.child("images_product/" + idImage).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        holder.tvTitle.setText(item.name);
        holder.tvNameRestaurant.setText(item.type);
        holder.btnSeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Updating...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvNameRestaurant;
        private ImageView img;
        private Button btnSeeDetail;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct);
            tvTitle = itemView.findViewById(R.id.tvNameProduct);
            tvNameRestaurant = itemView.findViewById(R.id.tvNameMerchant);
           btnSeeDetail = itemView.findViewById(R.id.btnSeeDetail);

        }
    }
}
