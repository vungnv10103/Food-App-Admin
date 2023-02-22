package vungnv.com.foodappadmin.adapter;

import android.annotation.SuppressLint;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import vungnv.com.foodappadmin.R;
import vungnv.com.foodappadmin.activities.ActiveProductActivity;
import vungnv.com.foodappadmin.constant.Constant;
import vungnv.com.foodappadmin.model.ProductModel;

public class ProductsAwaitingApprovalAdapter extends RecyclerView.Adapter<ProductsAwaitingApprovalAdapter.viewHolder> implements Constant {
    private Context context;
    private List<ProductModel> list;


    public ProductsAwaitingApprovalAdapter(Context context, List<ProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_awaiting_approval, parent, false);
        return new viewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductModel item = list.get(position);
        String idImage = item.img;
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
        holder.tvPrice.setText(item.price + " VNĐ");
        holder.btnSeeDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActiveProductActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("pos", position);
                bundle.putString("id", item.id);
                bundle.putString("idUser", item.idUser);
                bundle.putString("address", item.address);
                bundle.putString("img", item.img);
                bundle.putString("name", item.name);
                bundle.putString("type", item.type);
                bundle.putDouble("price", item.price);
                bundle.putString("timeDelay", item.timeDelay);
                bundle.putString("description", item.description);
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
        TextView tvTitle, tvPrice;
        ImageView img;
        Button btnSeeDetail;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct);
            tvTitle = itemView.findViewById(R.id.tvNameProduct);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnSeeDetail = itemView.findViewById(R.id.btnSeeDetail);

        }
    }
}
