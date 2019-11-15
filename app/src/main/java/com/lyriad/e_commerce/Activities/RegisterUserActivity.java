package com.lyriad.e_commerce.Activities;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bumptech.glide.Glide;
import com.eyalbira.loadingdots.LoadingDots;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lyriad.e_commerce.R;
import com.lyriad.e_commerce.Sessions.UserSession;
import com.lyriad.e_commerce.Tasks.SendDataTask;
import com.lyriad.e_commerce.Utils.Constants;
import com.lyriad.e_commerce.Utils.Response;
import com.lyriad.e_commerce.Utils.Validator;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Objects;
import java.util.UUID;

public class RegisterUserActivity extends AppCompatActivity implements View.OnClickListener {

    private UserSession session;

    private CircularImageView image;
    private EditText name, username, email, phone, password, confirmPassword;
    private MaterialCheckBox provider;
    private ExtendedFloatingActionButton regButton;
    private LoadingDots progress;

    private StorageReference fireStorage;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        fireStorage = FirebaseStorage.getInstance().getReference();
        imageUri = null;

        ImageView backButton = findViewById(R.id.reg_user_back_button);
        RelativeLayout imageLayout = findViewById(R.id.reg_user_image_layout);
        image = findViewById(R.id.reg_user_image);
        name = findViewById(R.id.reg_user_name);
        username = findViewById(R.id.reg_user_username);
        email = findViewById(R.id.reg_user_email);
        phone = findViewById(R.id.reg_user_phone);
        password = findViewById(R.id.reg_user_password);
        confirmPassword = findViewById(R.id.reg_user_confirm_password);
        provider = findViewById(R.id.reg_user_provider);
        regButton = findViewById(R.id.reg_user_button);
        progress = findViewById(R.id.reg_user_progress);

        if (Objects.equals(getIntent().getStringExtra("action"), "update")) {

            session = new UserSession(RegisterUserActivity.this);

            Glide.with(RegisterUserActivity.this).load(session.getImage()).into(image);
            name.setText(session.getName());
            username.setText(session.getUsername());
            email.setText(session.getEmail());
            phone.setText(session.getPhone());
            provider.setChecked(session.isProvider());

            TextInputLayout passwordLayout = findViewById(R.id.reg_user_password_layout);
            TextInputLayout confirmPasswordLayout = findViewById(R.id.reg_user_confirm_password_layout);

            username.setEnabled(false);
            email.setEnabled(false);
            passwordLayout.setVisibility(View.GONE);
            confirmPasswordLayout.setVisibility(View.GONE);
            regButton.setText("Update user");
        }

        backButton.setOnClickListener(this);
        imageLayout.setOnClickListener(this);
        regButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.reg_user_back_button:
                finish();
                break;

            case R.id.reg_user_image_layout:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
                break;

            case R.id.reg_user_button:

                if (Objects.equals(getIntent().getStringExtra("action"), "update")) {

                    updateUser();

                } else {

                    registerUser();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE &&
                resultCode == RESULT_OK && data != null){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            image.setImageURI(imageUri);

        }
    }

    private void registerUser() {

        if (!Validator.isInternetConnected(RegisterUserActivity.this)) {

            new MaterialAlertDialogBuilder(RegisterUserActivity.this)
                    .setTitle("Network Error")
                    .setMessage("You are not connected to the internet")
                    .setPositiveButton("Try again", (dialog, which) -> registerUser())
                    .setNegativeButton("Exit", (dialog, which) -> finish())
                    .show();
            return;
        }

        if (!allFieldsValid()
            || Validator.isPasswordValid(password)
            || password.getText().toString().equals(confirmPassword.getText().toString())
            || imageUri == null) {

            return;
        }

        regButton.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        String ext = imageUri.toString().substring(imageUri.toString().lastIndexOf('.'));
        StorageReference filePath = fireStorage
                .child("profile")
                .child(UUID.randomUUID().toString() + ext);

        final SendDataTask regPhotoTask = new SendDataTask(Constants.API_UPDATE_USER, "PUT", response -> {
            Log.i("EVENT", "Photo saved successfully in api");
            finish();
        }, error -> {
            Log.i("WARNING", "Photo couldn't be saved in api");
            finish();
        });

        final SendDataTask regUserTask = new SendDataTask(Constants.API_REGISTER_USER, "POST", (Response.Listener<JSONObject>) response -> {
            try {
                if (response.getBoolean("success")) {
                    filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                        try {
                            JSONObject updateData = new JSONObject();
                            updateData.put("contact", response.getString("contact"));
                            updateData.put("isProvider", response.getBoolean("isProvider"));
                            updateData.put("name", response.getString("name"));
                            updateData.put("photo", uri.toString());
                            updateData.put("id", response.getLong("ID"));

                            regPhotoTask.execute(updateData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterUserActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                            regButton.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        }
                    })).addOnFailureListener(e -> {
                        Toast.makeText(RegisterUserActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                        regButton.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                    });
                } else {
                    Toast.makeText(RegisterUserActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    regButton.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(RegisterUserActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                regButton.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }, error -> {
            Toast.makeText(RegisterUserActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            regButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        });

        try {
            JSONObject registerData = new JSONObject();
            registerData.put("contact", phone.getText().toString().trim());
            registerData.put("email", email.getText().toString().trim());
            registerData.put("isProvider", provider.isChecked());
            registerData.put("name", name.getText().toString().trim());
            registerData.put("photo", JSONObject.NULL);
            registerData.put("user", username.getText().toString().trim());
            registerData.put("password", password.getText().toString());

            regUserTask.execute(registerData);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterUserActivity.this, "There was an error", Toast.LENGTH_LONG).show();
            regButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
    }

    private void updateUser () {

        final String[] imgLink = new String[1];

        if (!Validator.isInternetConnected(RegisterUserActivity.this)) {

            new MaterialAlertDialogBuilder(RegisterUserActivity.this)
                    .setTitle("Network Error")
                    .setMessage("You are not connected to the internet")
                    .setPositiveButton("Try again", (dialog, which) -> updateUser())
                    .setNegativeButton("Exit", (dialog, which) -> finish())
                    .show();
            return;
        }

        if (!allFieldsValid()) {
            return;
        }

        regButton.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        if (imageUri != null) {

            String ext = imageUri.toString().substring(imageUri.toString().lastIndexOf('.'));
            StorageReference filePath = fireStorage
                    .child("profile")
                    .child(UUID.randomUUID().toString() + ext);

            filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> imgLink[0] = uri.toString()));

        } else {

            imgLink[0] = session.getImage().toString();
        }

        final SendDataTask updateUserTask = new SendDataTask(Constants.API_UPDATE_USER, "PUT", response -> {
            session.setName(name.getText().toString().trim());
            session.setImage(imgLink[0]);
            session.setPhone(phone.getText().toString().trim());
            session.setProvider(provider.isChecked());
            Log.i("EVENT", "User updated successfully");
            finish();
        }, error -> {
            Log.i("ERROR", "Error updating user in api");
            Toast.makeText(RegisterUserActivity.this, "Error updating user", Toast.LENGTH_LONG).show();
            regButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        });

        try {
            JSONObject updateData = new JSONObject();
            updateData.put("contact", phone.getText().toString().trim());
            updateData.put("isProvider", provider.isChecked());
            updateData.put("name", name.getText().toString().trim());
            updateData.put("photo", imgLink[0]);
            updateData.put("id", session.getUserId());

            updateUserTask.execute(updateData);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterUserActivity.this, "There was an error", Toast.LENGTH_LONG).show();
            regButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
    }

    private boolean allFieldsValid() {

        return (Validator.isEmpty(name, username)
            || Validator.isEmailValid(email)
            || Validator.isPhoneValid(phone));
    }
}
