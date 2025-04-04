package com.example.kamalapp.Activities;
import android.annotation.SuppressLint;
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


public class RegistrationActivity extends AppCompatActivity {

    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private Button mRegisterButton;
    TextView textview;
    FirebaseAuth mAuth;
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
                sessionManager.logoutUser();
                navigateToLoginActivity();
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        
        // Initialize Firebase Auth if not already done
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.password);
        mConfirmPasswordView = findViewById(R.id.confirm_password);
        mRegisterButton = findViewById(R.id.register_button);
        textview = findViewById(R.id.login_link);

        textview.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                navigateToLoginActivity();
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });
    }

    private void attemptRegistration() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mConfirmPasswordView.setError(null);

        // Store values at the time of the registration attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check if passwords match.
        if (!password.equals(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_passwords_do_not_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt registration and focus the first form field with an error.
            focusView.requestFocus();
        } else {
            // Show progress indicator
            showProgress(true);
            
            // Proceed only if validation passes
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            showProgress(false);
                            
                            if (task.isSuccessful()) {
                                // Account created successfully
                                FirebaseUser user = mAuth.getCurrentUser();
                                
                                if (user != null) {
                                    // Create a new session for the registered user
                                    sessionManager.createLoginSession(user.getEmail(), user.getUid());
                                    
                                    Toast.makeText(RegistrationActivity.this, "Account created successfully!", 
                                            Toast.LENGTH_SHORT).show();
                                    
                                    // Navigate directly to main activity instead of login page
                                    navigateToMainActivity();
                                }
                            } else {
                                // If registration fails, display the error message
                                Toast.makeText(RegistrationActivity.this, 
                                        "Registration failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
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
    
    private void navigateToLoginActivity() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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