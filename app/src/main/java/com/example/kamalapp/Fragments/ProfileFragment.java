package com.example.kamalapp.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kamalapp.Activities.LoginActivity;
import com.example.kamalapp.Models.MealViewModel;
import com.example.kamalapp.R;
import com.example.kamalapp.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView userEmailTextView;
    private TextView accountCreatedDateTextView;
    private TextView recipesCountTextView;
    private Button logoutButton;
    private FirebaseAuth mAuth;
    private MealViewModel mealViewModel;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        
        // Initialize Session Manager
        sessionManager = new SessionManager(requireContext());

        // Initialize ViewModel
        mealViewModel = new ViewModelProvider(requireActivity()).get(MealViewModel.class);

        // Initialize views
        userEmailTextView = view.findViewById(R.id.user_email);
        accountCreatedDateTextView = view.findViewById(R.id.account_created_date);
        recipesCountTextView = view.findViewById(R.id.recipes_count);
        logoutButton = view.findViewById(R.id.logout_button);

        // Setup UI with user data
        setupUserProfile();

        // Setup logout button
        logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        return view;
    }

    private void setupUserProfile() {
        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        
        if (currentUser != null) {
            // Set user email
            userEmailTextView.setText(currentUser.getEmail());
            
            // Set account creation date
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(new Date(currentUser.getMetadata().getCreationTimestamp()));
            accountCreatedDateTextView.setText(formattedDate);
            
            // Count recipes
            mealViewModel.getAllMeals().observe(getViewLifecycleOwner(), meals -> {
                recipesCountTextView.setText(String.valueOf(meals.size()));
            });
        } else {
            // Not logged in or session expired, use session data if available
            String email = sessionManager.getUserEmail();
            if (email != null && !email.isEmpty()) {
                userEmailTextView.setText(email);
            } else {
                userEmailTextView.setText("Not logged in");
            }
            
            // Use session login time for account date if available
            long lastLogin = sessionManager.getLastLoginTime();
            if (lastLogin > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(new Date(lastLogin));
                accountCreatedDateTextView.setText(formattedDate);
            } else {
                accountCreatedDateTextView.setText("N/A");
            }
            
            recipesCountTextView.setText("0");
        }
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("No", null)
                .show();
    }

    private void performLogout() {
        // Sign out from Firebase
        mAuth.signOut();
        
        // Clear session data
        sessionManager.logoutUser();
        
        // Navigate to Login Activity
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish(); // Close the Main Activity
    }
} 