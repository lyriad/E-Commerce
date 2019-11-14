package com.lyriad.e_commerce.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.card.MaterialCardView;
import com.lyriad.e_commerce.Activities.RegisterProductActivity;
import com.lyriad.e_commerce.Adapters.RecyclerViewProductAdapter;
import com.lyriad.e_commerce.Models.Category;
import com.lyriad.e_commerce.Models.Product;
import com.lyriad.e_commerce.R;
import com.lyriad.e_commerce.Sessions.UserSession;
import com.lyriad.e_commerce.Tasks.HttpGetTask;
import com.lyriad.e_commerce.Utils.Constants;
import com.lyriad.e_commerce.Utils.Response;
import com.lyriad.e_commerce.Utils.Validator;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ProductsFragment extends Fragment {

    private UserSession session;
    private List<Product> products;
    private RecyclerViewProductAdapter productAdapter;
    private SwipeRefreshLayout pullToRefresh;
    private boolean showAll;
    private Category category;

    public ProductsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new UserSession(Objects.requireNonNull(getActivity()).getApplicationContext());
        products = new ArrayList<>();
        Bundle args = getArguments();

        try {
            showAll = args.getBoolean("all");

        } catch (NullPointerException e) {
            showAll = true;
        }

        try {
            category = (Category) args.getSerializable("category");

        } catch (NullPointerException e) {
            category = new Category(-1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View homeView = inflater.inflate(R.layout.fragment_products, container, false);

        pullToRefresh = homeView.findViewById(R.id.products_refresh_layout);
        pullToRefresh.setOnRefreshListener(this::refreshData);

        TextView titleView = homeView.findViewById(R.id.products_title);
        titleView.setText(showAll ? "All products" : String.format(Constants.LOCALE, "Category: %s", category.getName()));

        RecyclerView categoryRecyclerView = homeView.findViewById(R.id.products_recycler_view);
        productAdapter = new RecyclerViewProductAdapter(getActivity(), products);
        categoryRecyclerView.setAdapter(productAdapter);

        MaterialCardView addButton = homeView.findViewById(R.id.products_add_button);
        addButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), RegisterProductActivity.class)));

        return homeView;
    }

    @Override
    public void onStart() {
        super.onStart();
        pullToRefresh.setRefreshing(true);
        refreshData();
    }

    private void refreshData() {

        if (!Validator.isInternetConnected(getContext())) {
            Toast.makeText(getContext(), "You are not connected to the internet", Toast.LENGTH_SHORT).show();
            pullToRefresh.setRefreshing(false);
            return;
        }

        products.clear();

        new HttpGetTask(Constants.API_PRODUCTS, (Response.Listener<JSONArray>) response -> {
            try {
                if (response.getJSONObject(response.length() - 1).getBoolean("success")) {
                    for (int index = 0; index < response.length() - 1; index++) {

                        if (showAll || response.getJSONObject(index).getLong("categoryId") == category.getId()) {

                            products.add(new Product(
                                    response.getJSONObject(index).getString("itemCode"),
                                    response.getJSONObject(index).getString("itemName"),
                                    response.getJSONObject(index).getDouble("price"),
                                    Uri.parse(response.getJSONObject(index).getString("photo")),
                                    new Category(response.getJSONObject(index).getLong("categoryId")),
                                    response.getJSONObject(index).getLong("userId"),
                                    response.getJSONObject(index).getBoolean("active")
                            ));
                        }
                    }
                    pullToRefresh.setRefreshing(false);
                } else {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Error loading products", Toast.LENGTH_SHORT).show();
                    pullToRefresh.setRefreshing(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                pullToRefresh.setRefreshing(false);
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Error loading products", Toast.LENGTH_SHORT).show();
            }
            productAdapter.notifyDataSetChanged();
        }, error -> {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            pullToRefresh.setRefreshing(false);
        }).execute(new HashMap<>());
    }
}
