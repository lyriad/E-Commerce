package com.lyriad.e_commerce.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.eyalbira.loadingdots.LoadingDots;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lyriad.e_commerce.R;
import com.lyriad.e_commerce.Sessions.UserSession;
import com.lyriad.e_commerce.Tasks.SendDataTask;
import com.lyriad.e_commerce.Utils.Constants;
import com.lyriad.e_commerce.Utils.Response;
import com.lyriad.e_commerce.Utils.Validator;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class RegisterCategoryActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView image;
    private EditText name;
    private ExtendedFloatingActionButton regButton;
    private LoadingDots progress;

    private UserSession session;
    private Uri imageUri;
    private StorageReference fireStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_category);

        session = new UserSession(RegisterCategoryActivity.this);
        imageUri = null;
        fireStorage = FirebaseStorage.getInstance().getReference();

        ImageView backButton = findViewById(R.id.reg_category_back_button);
        image = findViewById(R.id.reg_category_image);
        name = findViewById(R.id.reg_category_name);
        regButton = findViewById(R.id.reg_category_button);
        progress = findViewById(R.id.reg_category_progress);

        backButton.setOnClickListener(this);
        image.setOnClickListener(this);
        regButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.reg_category_back_button:
                finish();
                break;

            case R.id.reg_category_image:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE);
                break;

            case R.id.reg_category_button:
                addCategory();
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK && requestCode == Constants.GALLERY_REQUEST_CODE) {
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }

    private void addCategory() {

        if (!Validator.isInternetConnected(RegisterCategoryActivity.this)) {

            new MaterialAlertDialogBuilder(RegisterCategoryActivity.this)
                    .setTitle("Network Error")
                    .setMessage("You are not connected to the internet")
                    .setPositiveButton("Try again", (dialog, which) -> addCategory())
                    .show();
            return;
        }

        if (Validator.isEmpty(name) || imageUri == null) {
            Toast.makeText(RegisterCategoryActivity.this, "The image can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        regButton.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        StorageReference filePath = fireStorage
                .child("category/" + UUID.randomUUID().toString() + ".jpg");

        final SendDataTask regPhotoTask = new SendDataTask(Constants.API_CATEGORIES, "PUT", response -> {
            Log.i("EVENT", "Photo saved successfully in api");
            finish();
        }, error -> {
            Log.i("WARNING", "Photo couldn't be saved in api");
            finish();
        });

        final SendDataTask regCategoryTask = new SendDataTask(Constants.API_CATEGORIES, "POST", (Response.Listener<JSONObject>) response -> {
            try {
                if (response.getBoolean("success")) {
                    filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                        try {
                            JSONObject photoData = new JSONObject();
                            photoData.put("id", response.getLong("id"));
                            photoData.put("name", response.getString("name"));
                            photoData.put("photo", uri.toString());

                            regPhotoTask.execute(photoData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterCategoryActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                            regButton.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        }
                    })).addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(RegisterCategoryActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                        regButton.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                    });
                    Toast.makeText(RegisterCategoryActivity.this, "Category added successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(RegisterCategoryActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    regButton.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(RegisterCategoryActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                regButton.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }, error -> {
            Toast.makeText(RegisterCategoryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            regButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        });

        try {
            JSONObject categoryData = new JSONObject();
            categoryData.put("active", true);
            categoryData.put("name", name.getText().toString().trim());
            categoryData.put("photo", JSONObject.NULL);
            categoryData.put("userId", session.getUserId());

            regCategoryTask.execute(categoryData);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterCategoryActivity.this, "There was an error", Toast.LENGTH_LONG).show();
            regButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
    }
}