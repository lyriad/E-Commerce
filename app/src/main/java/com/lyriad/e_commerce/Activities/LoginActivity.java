package com.lyriad.e_commerce.Activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.eyalbira.loadingdots.LoadingDots;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.lyriad.e_commerce.Models.User;
import com.lyriad.e_commerce.R;
import com.lyriad.e_commerce.Tasks.SendDataTask;
import com.lyriad.e_commerce.Utils.*;
import com.lyriad.e_commerce.Sessions.UserSession;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email, password;
    private ExtendedFloatingActionButton signInButton;
    private LoadingDots progress;
    private UserSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.sign_in_email);
        password = findViewById(R.id.sign_in_password);
        TextView forgotPassword = findViewById(R.id.sign_in_forgot_password);
        TextView createAccount = findViewById(R.id.sign_in_create_account);
        signInButton = findViewById(R.id.sign_in_button);
        progress = findViewById(R.id.sign_in_progress);

        forgotPassword.setOnClickListener(this);
        createAccount.setOnClickListener(this);
        signInButton.setOnClickListener(this);

        session = new UserSession(getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(Constants.FIREBASE_AUTH_EMAIL, Constants.FIREBASE_AUTH_PASSWORD);
        if (session.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_forgot_password:

                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                break;

            case R.id.sign_in_button:

                Log.d("EVENT", "Pressed sign in button");
                logInUser();
                break;

            case R.id.sign_in_create_account:

                Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
                intent.putExtra("action", "register");
                startActivity(intent);
                break;

        }
    }

    private void logInUser() {

        if (!Validator.isInternetConnected(LoginActivity.this)) {

            new MaterialAlertDialogBuilder(LoginActivity.this)
                    .setTitle("Network Error")
                    .setMessage("You are not connected to the internet")
                    .setPositiveButton("Try again", (dialog, which) -> logInUser())
                    .show();
            return;
        }

        if (!Validator.isEmailValid(email) || Validator.isEmpty(password)) {
            Log.e("ERROR", "User did not pass validation");
            return;
        }
        Log.i("EVENT", "User passed validation");

        signInButton.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        final String emailStr = email.getText().toString().trim();
        final String passwordStr = password.getText().toString().trim();

        final SendDataTask loginTask = new SendDataTask(Constants.API_LOGIN, "POST", (Response.Listener<JSONObject>) response -> {
            try {
                if (response.getBoolean("success")) {
                    User user = new User(
                            response.getLong("id"),
                            response.getLong("token"),
                            Uri.parse(response.getString("photo")),
                            response.getString("name"),
                            response.getString("user"),
                            response.getString("email"),
                            response.getString("contact"),
                            response.getBoolean("isProvider"),
                            response.getLong("createdAt"),
                            response.getLong("updatedAt")
                    );

                    session.createUserSession(user);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    signInButton.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                signInButton.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }, error -> {
            Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            signInButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        });

        try {
            JSONObject loginData = new JSONObject();
            loginData.put("email", emailStr);
            loginData.put("password", passwordStr);

            loginTask.execute(loginData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
