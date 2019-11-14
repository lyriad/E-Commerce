package com.lyriad.e_commerce.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.lyriad.e_commerce.Activities.RegisterUserActivity;
import com.lyriad.e_commerce.R;
import com.lyriad.e_commerce.Sessions.UserSession;
import com.lyriad.e_commerce.Utils.Constants;
import com.lyriad.e_commerce.Utils.Parser;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private ImageView edit;
    private CircularImageView image;
    private TextView name, id, username, email, phone, createdAt, updatedAt;
    private LinearLayout providerLayout;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View homeView = inflater.inflate(R.layout.fragment_profile, container, false);

        edit = homeView.findViewById(R.id.user_profile_edit);
        image = homeView.findViewById(R.id.user_profile_image);
        name = homeView.findViewById(R.id.user_profile_name);
        id = homeView.findViewById(R.id.user_profile_id);
        username = homeView.findViewById(R.id.user_profile_username);
        email = homeView.findViewById(R.id.user_profile_email);
        phone = homeView.findViewById(R.id.user_profile_phone);
        createdAt = homeView.findViewById(R.id.user_profile_created_at);
        updatedAt = homeView.findViewById(R.id.user_profile_updated_at);
        providerLayout = homeView.findViewById(R.id.user_profile_provider);

        return homeView;
    }

    @Override
    public void onStart() {
        super.onStart();

        UserSession session = new UserSession(Objects.requireNonNull(getActivity()).getApplicationContext());

        Glide.with(Objects.requireNonNull(getActivity()).getApplicationContext()).load(session.getImage()).into(image);
        name.setText(session.getName());
        id.setText(String.format(Constants.LOCALE, "%d", session.getUserId()));
        username.setText(session.getUsername());
        email.setText(session.getEmail());
        phone.setText(session.getPhone());
        createdAt.setText(Parser.formatCalendar(session.getRegisterDate()));
        updatedAt.setText(Parser.formatCalendar(session.getLastUpdateDate()));

        if (session.isProvider()) {

            providerLayout.setVisibility(View.VISIBLE);
        }

        edit.setOnClickListener(v -> {

            Intent intent = new Intent(Objects.requireNonNull(getActivity()), RegisterUserActivity.class);
            intent.putExtra("action", "update");
            startActivity(intent);
        });
    }
}
