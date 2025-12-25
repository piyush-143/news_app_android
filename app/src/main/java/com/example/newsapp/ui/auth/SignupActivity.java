package com.example.newsapp.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.R;
import com.example.newsapp.data.DbHelper;
import com.example.newsapp.ui.MainActivity;
import com.example.newsapp.utils.CustomSnackBar;

public class SignupActivity extends AppCompatActivity {
    EditText etName, etEmail, etPassword;
    Button btnSignup, btnSkip;
    TextView tvLogin;
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new DbHelper(this);

        // Binding new IDs
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSignup = findViewById(R.id.btnSignup);
        btnSkip = findViewById(R.id.btnSkip);
        tvLogin = findViewById(R.id.tvLogin);

        btnSignup.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            // Validation Logic
            if (name.isEmpty()) {
                etName.setError("Please enter your name");
                etName.requestFocus();
                return;
            }

            if (!email.contains("@")) {
                etEmail.setError("Please enter a valid email");
                etEmail.requestFocus();
                return;
            }

            if (pass.isEmpty() || pass.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                etPassword.requestFocus();
                return;
            }

            if (dbHelper.registerUser(email, pass, name)) {
                // UPDATED: Success Snackbar
                CustomSnackBar.showSuccess(this, findViewById(android.R.id.content), "Registration Successful");

                // Auto-login after signup
                getSharedPreferences("UserPrefs", MODE_PRIVATE).edit().putString("email", email).apply();
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity(); // Clear stack
            } else {
                // UPDATED: Error Snackbar
                CustomSnackBar.showError(this, findViewById(android.R.id.content), "Registration Failed. Email might be taken.");
            }
        });

        btnSkip.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        tvLogin.setOnClickListener(v -> {
            finish(); // Go back to Login
        });
    }

    // --- Global Touch Handler to Clear Focus ---
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}