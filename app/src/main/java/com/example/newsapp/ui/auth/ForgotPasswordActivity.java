package com.example.newsapp.ui.auth;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newsapp.R;
import com.example.newsapp.data.DbHelper;
import com.example.newsapp.utils.CustomSnackBar;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail, etNewPass, etConfirmPass;
    Button btnVerify, btnReset, btnBack;
    LinearLayout layoutReset;
    DbHelper dbHelper;
    String verifiedEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        dbHelper = new DbHelper(this);

        etEmail = findViewById(R.id.etEmail);
        btnVerify = findViewById(R.id.btnVerify);
        btnBack = findViewById(R.id.btnBack);

        // Reset Section (Hidden initially)
        layoutReset = findViewById(R.id.layoutReset);
        etNewPass = findViewById(R.id.etNewPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnReset = findViewById(R.id.btnReset);

        // --- Step 1: Verify Email ---
        btnVerify.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty()) {
                etEmail.setError("Please enter your email");
                etEmail.requestFocus();
                return;
            }

            if (!email.contains("@")) {
                etEmail.setError("Invalid email format");
                etEmail.requestFocus();
                return;
            }

            if (dbHelper.checkEmailExists(email)) {
                verifiedEmail = email;
                etEmail.setEnabled(false); // Lock email field
                btnVerify.setVisibility(View.GONE);
                layoutReset.setVisibility(View.VISIBLE); // Reveal password fields

                // UPDATED: Success Snackbar
                CustomSnackBar.showSuccess(this, findViewById(android.R.id.content), "Email verified. Set new password.");
            } else {
                // UPDATED: Error Snackbar
                CustomSnackBar.showError(this, findViewById(android.R.id.content), "Email not found in our records.");
            }
        });

        // --- Step 2: Reset Password ---
        btnReset.setOnClickListener(v -> {
            String pass = etNewPass.getText().toString().trim();
            String confirm = etConfirmPass.getText().toString().trim();

            if (pass.isEmpty()) {
                etNewPass.setError("Enter new password");
                etNewPass.requestFocus();
                return;
            }

            if (pass.length() < 6) {
                etNewPass.setError("Password must be at least 6 characters");
                etNewPass.requestFocus();
                return;
            }

            if (!pass.equals(confirm)) {
                etConfirmPass.setError("Passwords do not match");
                etConfirmPass.requestFocus();
                return;
            }

            if (dbHelper.updatePassword(verifiedEmail, pass)) {
                // UPDATED: Success Snackbar
                CustomSnackBar.showSuccess(this, findViewById(android.R.id.content), "Password Reset Successful! Please Login.");

                // Close activity after short delay to let user see message
                new android.os.Handler().postDelayed(this::finish, 1500);
            } else {
                // UPDATED: Error Snackbar
                CustomSnackBar.showError(this, findViewById(android.R.id.content), "Reset Failed. Try again.");
            }
        });

        btnBack.setOnClickListener(v -> finish());
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