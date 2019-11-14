package com.lyriad.e_commerce.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.card.MaterialCardView;
import com.lyriad.e_commerce.Activities.RegisterCategoryActivity;
import com.lyriad.e_commerce.Adapters.RecyclerViewCategoryAdapter;
import com.lyriad.e_commerce.Models.Category;
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

public class CategoryFragment extends Fragment {

    private UserSession session;
    private List<Category> categories;
    private RecyclerViewCategoryAdapter categoryAdapter;
    private SwipeRefreshLayout pullToRefresh;

    public CategoryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new UserSession(Objects.requireNonNull(getActivity()).getApplicationContext());
        categories = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View homeView = inflater.inflate(R.layout.fragment_categories, container, false);

        RecyclerView categoryRecyclerView = homeView.findViewById(R.id.categories_recycler_view);
        categoryAdapter = new RecyclerViewCategoryAdapter(getActivity(), categories, session.isProvider());
        categoryRecyclerView.setAdapter(categoryAdapter);

        pullToRefresh = homeView.findViewById(R.id.categories_refresh_layout);
        pullToRefresh.setOnRefreshListener(this::refreshData);

        if (session.isProvider()) {
            MaterialCardView addButton = homeView.findViewById(R.id.categories_add_button);
            addButton.setVisibility(View.VISIBLE);

            addButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), RegisterCategoryActivity.class)));
        }

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

        categories.clear();

        new HttpGetTask(Constants.API_CATEGORIES, (Response.Listener<JSONArray>) response -> {
            try {
                if (response.getJSONObject(response.length() - 1).getBoolean("success")) {
                    for (int index = 0; index < response.length() - 1; index++) {
                        categories.add(new Category(
                                response.getJSONObject(index).getLong("id"),
                                response.getJSONObject(index).getLong("userId"),
                                response.getJSONObject(index).getLong("token"),
                                response.getJSONObject(index).getString("name"),
                                Uri.parse(response.getJSONObject(index).getString("photo")),
                                response.getJSONObject(index).getBoolean("active")
                        ));
                    }
                    pullToRefresh.setRefreshing(false);
                } else {
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
                    pullToRefresh.setRefreshing(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                pullToRefresh.setRefreshing(false);
                Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Error loading categories", Toast.LENGTH_SHORT).show();
            }
            categoryAdapter.notifyDataSetChanged();
        }, error -> {
            Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            pullToRefresh.setRefreshing(false);
        }).execute(new HashMap<>());
    }
}
