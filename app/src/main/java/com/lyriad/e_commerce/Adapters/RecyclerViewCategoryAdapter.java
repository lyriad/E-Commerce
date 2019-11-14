package com.lyriad.e_commerce.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.lyriad.e_commerce.Fragments.ProductsFragment;
import com.lyriad.e_commerce.Models.Category;
import com.lyriad.e_commerce.R;
import com.lyriad.e_commerce.Utils.FragmentNavigationManager;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewCategoryAdapter extends RecyclerView.Adapter<RecyclerViewCategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private List<Category> categories;
    private boolean isProvider;

    public RecyclerViewCategoryAdapter(Context mContext, List<Category> categories, boolean isProvider) {
        this.mContext = mContext;
        this.categories = categories != null ? categories : new ArrayList<>();
        this.isProvider = isProvider;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, final int position) {

        Glide.with(mContext).load(categories.get(position).getImage()).into(holder.image);
        holder.name.setText(categories.get(position).getName());

        holder.image.setOnClickListener(v -> {

            Bundle args = new Bundle();
            args.putSerializable("category", categories.get(position));
            ProductsFragment fragment = new ProductsFragment();
            fragment.setArguments(args);
            FragmentNavigationManager.getInstance().showFragment(fragment, false);
        });

        if (isProvider) {
            holder.settings.setVisibility(View.VISIBLE);

            holder.settings.setOnClickListener(v -> Toast.makeText(mContext, categories.get(position).getName(), Toast.LENGTH_SHORT).show());
        }

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {

        ImageView settings, image;
        TextView name;

        CategoryViewHolder(View itemView) {
            super(itemView);

            settings = itemView.findViewById(R.id.category_settings);
            image = itemView.findViewById(R.id.category_image);
            name = itemView.findViewById(R.id.category_name);
        }

    }
}
