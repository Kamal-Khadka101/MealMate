package com.example.kamalapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kamalapp.R;
import com.example.kamalapp.utils.SessionManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mLoginButton;
    private TextView mRegisterLink;
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    @Override
    public void onStart() {
        super.onStart();
        
        // Initialize Session Manager
        sessionManager = new SessionManager(this);
        
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        
        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            
            if (currentUser != null) {
                // User is logged in and Firebase auth is valid
                navigateToMainActivity();
            } else {
                // Session shows logged in but Firebase auth is missing
                // This could happen if Firebase auth expired or was invalidated
                sessionManager.logoutUser();
                Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI elements
        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login_button);
        mRegisterLink = findViewById(R.id.register_link);

        // Set OnClickListener for the login button
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        // Set OnClickListener for the registration link
        mRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to Registration Activity
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void attemptLogin() {
        // Reset errors
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first form field with an error
            focusView.requestFocus();
        } else {
            // Show a progress indicator and start the login process
            showProgress(true);

            // Perform login
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            showProgress(false);
                            
                            if (task.isSuccessful()) {
                                // Sign in success
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Create session
                                    sessionManager.createLoginSession(user.getEmail(), user.getUid());
                                    
                                    // Navigate to main activity
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                    navigateToMainActivity();
                                }
                            } else {
                                // If sign in fails, display a message to the user
                                String errorMessage = "Authentication failed";
                                if (task.getException() != null) {
                                    errorMessage += ": " + task.getException().getMessage();
                                }
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    
    private void showProgress(boolean show) {
        // You can implement a progress indicator here if needed
    }
    
    private void navigateToMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}