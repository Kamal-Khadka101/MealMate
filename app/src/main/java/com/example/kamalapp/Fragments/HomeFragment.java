package com.example.kamalapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.kamalapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Find the Get Started button
        Button getStartedButton = view.findViewById(R.id.button_get_started);

        // Set click listener to navigate to MealPlanning Fragment
        getStartedButton.setOnClickListener(v -> {
            // Navigate to Meal Planning Fragment
            // Get the bottom navigation view from activity
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
            // Set the selected item to recipes
            bottomNavigationView.setSelectedItemId(R.id.nav_meal_planning);
        });
    }
}