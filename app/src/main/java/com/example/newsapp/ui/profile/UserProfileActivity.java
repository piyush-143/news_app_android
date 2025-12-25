package com.example.newsapp.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.data.DbHelper;
import com.example.newsapp.utils.CustomSnackBar;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView tvName, tvEmail;
    private DbHelper dbHelper;
    private String currentEmail;
    private String currentName = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dbHelper = new DbHelper(this);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentEmail = prefs.getString("email", "");

        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        ImageView btnEdit = findViewById(R.id.btnEdit);
        View btnCamera = findViewById(R.id.btnCamera);
        ImageView btnBack = findViewById(R.id.btnBack);

        // Safety Check
        if (tvName == null || tvEmail == null) {
            Toast.makeText(this, "Error loading profile view", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnBack.setOnClickListener(v -> finish());
        btnEdit.setOnClickListener(v -> showEditDialog());

        btnCamera.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        loadUserData();
    }

    private void loadUserData() {
        if (currentEmail == null || currentEmail.isEmpty()) return;

        Cursor cursor = null;
        try {
            cursor = dbHelper.getUserDetails(currentEmail);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex("Name");
                int imageIndex = cursor.getColumnIndex("Image");

                if (nameIndex != -1) {
                    String nameFromDb = cursor.getString(nameIndex);
                    if (nameFromDb != null) currentName = nameFromDb;
                }

                tvName.setText(currentName != null ? currentName : "User");
                tvEmail.setText(currentEmail);

                if (imageIndex != -1) {
                    String imagePath = cursor.getString(imageIndex);
                    if (imagePath != null && !imagePath.isEmpty()) {
                        Glide.with(this)
                                .load(imagePath)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_report_image)
                                .into(imgProfile);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        try {
                            dbHelper.updateProfileImage(currentEmail, selectedImage.toString());
                            Glide.with(this).load(selectedImage).into(imgProfile);
                            // UPDATED: Success Snackbar
                            CustomSnackBar.showSuccess(this, findViewById(android.R.id.content), "Profile picture updated");
                        } catch (Exception e) {
                            CustomSnackBar.showError(this, findViewById(android.R.id.content), "Failed to save image");
                        }
                    }
                }
            }
    );

    private void showEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_profile, null);

        EditText etName = view.findViewById(R.id.etName);
        EditText etEmail = view.findViewById(R.id.etEmail);
        Button btnSave = view.findViewById(R.id.btnSave);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        etName.setText(currentName);
        etEmail.setText(currentEmail);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty()) {
                // We use Toast inside dialogs because Snackbars might be hidden behind the dialog window
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dbHelper.updateProfile(currentEmail, newEmail, newName)) {
                if (!currentEmail.equals(newEmail)) {
                    getSharedPreferences("UserPrefs", MODE_PRIVATE)
                            .edit().putString("email", newEmail).apply();
                    currentEmail = newEmail;
                }
                loadUserData();
                dialog.dismiss();
                // UPDATED: Success Snackbar (Main Activity View)
                CustomSnackBar.showSuccess(this, findViewById(android.R.id.content), "Profile updated successfully");
            } else {
                Toast.makeText(this, "Update failed. Email might exist.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}