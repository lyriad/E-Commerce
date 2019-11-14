package com.lyriad.e_commerce.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.lyriad.e_commerce.Models.Product;
import com.lyriad.e_commerce.R;
import com.lyriad.e_commerce.Utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.CategoryViewHolder> {

    private Context mContext;
    private List<Product> products;

    public RecyclerViewProductAdapter(Context mContext, List<Product> products) {
        this.mContext = mContext;
        this.products = products != null ? products : new ArrayList<>();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {

        Glide.with(mContext).load(products.get(position).getImage()).into(holder.image);
        String productName = products.get(position).getName();
        holder.name.setText(productName.length() < 50 ? productName : productName.substring(0, 50) + "...");
        holder.price.setText(String.format(Constants.LOCALE, "$%.2f", products.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, price;

        CategoryViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.product_image);
            name = itemView.findViewById(R.id.product_name);
            price = itemView.findViewById(R.id.product_price);
        }
    }
}
