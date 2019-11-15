package com.lyriad.e_commerce.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.eyalbira.loadingdots.LoadingDots;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.lyriad.e_commerce.Adapters.CategorySpinnerAdapter;
import com.lyriad.e_commerce.Models.Category;
import com.lyriad.e_commerce.R;
import com.lyriad.e_commerce.Sessions.UserSession;
import com.lyriad.e_commerce.Tasks.HttpGetTask;
import com.lyriad.e_commerce.Tasks.SendDataTask;
import com.lyriad.e_commerce.Utils.Constants;
import com.lyriad.e_commerce.Utils.Response;
import com.lyriad.e_commerce.Utils.Validator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

public class RegisterProductActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView image;
    private EditText name, price;
    private Spinner categorySpinner;
    private ExtendedFloatingActionButton regButton;
    private LoadingDots progress;

    private UserSession session;
    private Category[] categories;
    private Uri imageUri;
    private StorageReference fireStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_product);

        session = new UserSession(RegisterProductActivity.this);
        imageUri = null;
        fireStorage = FirebaseStorage.getInstance().getReference();

        ImageView backButton = findViewById(R.id.reg_product_back_button);
        image = findViewById(R.id.reg_product_image);
        name = findViewById(R.id.reg_product_name);
        price = findViewById(R.id.reg_product_price);
        categorySpinner = findViewById(R.id.reg_product_categories);
        regButton = findViewById(R.id.reg_product_button);
        progress = findViewById(R.id.reg_product_progress);

        backButton.setOnClickListener(this);
        image.setOnClickListener(this);
        regButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!Validator.isInternetConnected(RegisterProductActivity.this)) {

            new MaterialAlertDialogBuilder(RegisterProductActivity.this)
                    .setTitle("Network Error")
                    .setMessage("You are not connected to the internet")
                    .setPositiveButton("Try again", (dialog, which) -> RegisterProductActivity.this.onStart())
                    .setNegativeButton("Exit", (dialog, which) -> finish())
                    .show();
            return;
        }

        initSpinner();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.reg_product_back_button:
                finish();
                break;

            case R.id.reg_product_image:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE);
                break;

            case R.id.reg_product_button:
                addProduct();
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

    private void initSpinner() {

        new HttpGetTask(Constants.API_CATEGORIES, (Response.Listener<JSONArray>) response -> {
            try {
                categories = new Category[response.length()];
                categories[0] = new Category(-1, -1, -1, "<Select a category>", null, false);

                if (response.getJSONObject(response.length() - 1).getBoolean("success")) {
                    for (int index = 0; index < response.length() - 1; index++) {
                        categories[index + 1] = new Category(
                                response.getJSONObject(index).getLong("id"),
                                response.getJSONObject(index).getLong("userId"),
                                response.getJSONObject(index).getLong("token"),
                                response.getJSONObject(index).getString("name"),
                                Uri.parse(response.getJSONObject(index).getString("photo")),
                                response.getJSONObject(index).getBoolean("active")
                        );
                    }
                    CategorySpinnerAdapter spinnerAdapter = new CategorySpinnerAdapter(RegisterProductActivity.this, R.layout.spinner_item, categories);
                    categorySpinner.setAdapter(spinnerAdapter);
                } else {
                    Toast.makeText(RegisterProductActivity.this, "Error loading categories", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(RegisterProductActivity.this, "Error loading categories", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(RegisterProductActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }).execute(new HashMap<>());
    }

    private void addProduct() {

        if (!Validator.isInternetConnected(RegisterProductActivity.this)) {

            new MaterialAlertDialogBuilder(RegisterProductActivity.this)
                    .setTitle("Network Error")
                    .setMessage("You are not connected to the internet")
                    .setPositiveButton("Try again", (dialog, which) -> addProduct())
                    .show();
            return;
        }

        if (imageUri == null) {

            new MaterialAlertDialogBuilder(RegisterProductActivity.this)
                    .setTitle("Empty field")
                    .setMessage("The image can't be empty")
                    .setPositiveButton("Ok", null)
                    .show();
            return;

        } else if (Validator.isEmpty(name, price)) return;

        else if (categorySpinner.getSelectedItemPosition() == 0) {
            new MaterialAlertDialogBuilder(RegisterProductActivity.this)
                    .setTitle("Empty field")
                    .setMessage("You must select a category")
                    .setPositiveButton("Ok", null)
                    .show();
            return;
        }

        regButton.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);

        final String itemCode = UUID.randomUUID().toString().substring(0, 13);
        JSONObject productData = new JSONObject();

        String ext = imageUri.toString().substring(imageUri.toString().lastIndexOf('.'));
        StorageReference filePath = fireStorage
                .child("product/")
                .child(itemCode + ext);

        final SendDataTask regPhotoTask = new SendDataTask(Constants.API_PRODUCTS, "PUT", response -> {
            Log.i("EVENT", "Photo saved successfully in api");
            finish();
        }, error -> {
            Log.i("WARNING", "Photo couldn't be saved in api");
            finish();
        });

        final SendDataTask regProductTask = new SendDataTask(Constants.API_PRODUCTS, "POST", (Response.Listener<JSONObject>) response -> {
            try {
                if (response.getBoolean("success")) {
                    filePath.putFile(imageUri).addOnSuccessListener(taskSnapshot -> filePath.getDownloadUrl().addOnSuccessListener(uri -> {
                        try {
                            productData.put("photo", uri.toString());

                            regPhotoTask.execute(productData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterProductActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                            regButton.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        }
                    })).addOnFailureListener(e -> {
                        e.printStackTrace();
                        Toast.makeText(RegisterProductActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                        regButton.setVisibility(View.VISIBLE);
                        progress.setVisibility(View.GONE);
                    });
                    Toast.makeText(RegisterProductActivity.this, "Category added successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(RegisterProductActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    regButton.setVisibility(View.VISIBLE);
                    progress.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(RegisterProductActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                regButton.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }, error -> {
            Toast.makeText(RegisterProductActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            regButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        });

        try {
            productData.put("active", true);
            productData.put("categoryId", ((Category) categorySpinner.getSelectedItem()).getId());
            productData.put("itemCode", itemCode);
            productData.put("itemName", name.getText().toString().trim());
            productData.put("photo", JSONObject.NULL);
            productData.put("price", Double.parseDouble(price.getText().toString().trim()));
            productData.put("userId", session.getUserId());

            regProductTask.execute(productData);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RegisterProductActivity.this, "There was an error", Toast.LENGTH_LONG).show();
            regButton.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }
    }
}
