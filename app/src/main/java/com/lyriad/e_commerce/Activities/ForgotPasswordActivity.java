package com.lyriad.e_commerce.Activities;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.eyalbira.loadingdots.LoadingDots;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.lyriad.e_commerce.R;
import com.lyriad.e_commerce.Tasks.SendDataTask;
import com.lyriad.e_commerce.Utils.Constants;
import com.lyriad.e_commerce.Utils.Response;
import com.lyriad.e_commerce.Utils.Validator;
import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email;
    private TextView result;
    private ExtendedFloatingActionButton forgotButton;
    private LoadingDots progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ImageView backButton = findViewById(R.id.forgot_password_back_button);
        email = findViewById(R.id.forgot_password_email);
        result = findViewById(R.id.forgot_password_result);
        forgotButton = findViewById(R.id.forgot_password_button);
        progress = findViewById(R.id.forgot_password_progress);

        backButton.setOnClickListener(this);
        forgotButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.forgot_password_back_button:
                finish();
                break;

            case R.id.forgot_password_button:
                retrievePassword();
                break;
        }
    }

    private void retrievePassword() {

        if (!Validator.isInternetConnected(ForgotPasswordActivity.this)) {

            new MaterialAlertDialogBuilder(ForgotPasswordActivity.this)
                    .setTitle("Network Error")
                    .setMessage("You are not connected to the internet")
                    .setPositiveButton("Try again", (dialog, which) -> retrievePassword())
                    .setNegativeButton("Exit", (dialog, which) -> finish())
                    .show();
            return;
        }

        if (!Validator.isEmailValid(email)) return;

        forgotButton.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        final SendDataTask retrievePasswordTask = new SendDataTask(Constants.API_FORGOT_PASSWORD, "POST", (Response.Listener<JSONObject>) response -> {
            try {
                result.setText(response.getString("message"));

                if (response.getBoolean("success")) {

                    Toast.makeText(ForgotPasswordActivity.this, "Password retrieved successfully", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(ForgotPasswordActivity.this, "Unable to retrieve password", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                result.setText("");
                Toast.makeText(ForgotPasswordActivity.this, "There was an error", Toast.LENGTH_LONG).show();
            }

            forgotButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);

        }, error -> {
            Toast.makeText(ForgotPasswordActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            result.setText("");
            forgotButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        });

        try {
            JSONObject retrievePasswordData = new JSONObject();
            retrievePasswordData.put("email", email.getText().toString().trim());

            retrievePasswordTask.execute(retrievePasswordData);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ForgotPasswordActivity.this, "There was an error", Toast.LENGTH_LONG).show();
            result.setText("");
            forgotButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
    }
}
