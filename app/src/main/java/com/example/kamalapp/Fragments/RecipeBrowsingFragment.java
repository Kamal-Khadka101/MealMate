// Fragments/RecipeBrowsingFragment.java
package com.example.kamalapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.kamalapp.Adapters.MealAdapter;
import com.example.kamalapp.Models.MealViewModel;
import com.example.kamalapp.R;

public class RecipeBrowsingFragment extends Fragment {

    private MealViewModel mealViewModel;
    private MealAdapter mealAdapter;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_browsing, container, false);

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_meals);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter
        mealAdapter = new MealAdapter(new MealAdapter.MealDiff());
        recyclerView.setAdapter(mealAdapter);

        // Initialize ViewModel
        mealViewModel = new ViewModelProvider(requireActivity()).get(MealViewModel.class);

        // Observe the LiveData
        mealViewModel.getAllMeals().observe(getViewLifecycleOwner(), meals -> {
            mealAdapter.submitList(meals);
        });

        // Set up search functionality
        searchView = view.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMeals(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchMeals(newText);
                return true;
            }
        });

        // Set up FAB for adding new recipes
        FloatingActionButton fab = view.findViewById(R.id.fab_add_recipe);
        fab.setOnClickListener(v -> {
            // Navigate to Meal Planning Fragment
            // Get the bottom navigation view from activity
            BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
            // Set the selected item to recipes
            bottomNavigationView.setSelectedItemId(R.id.nav_meal_planning);
        });

        return view;
    }

    private void searchMeals(String query) {
        if (query.isEmpty()) {
            mealViewModel.getAllMeals().observe(getViewLifecycleOwner(), meals -> {
                mealAdapter.submitList(meals);
            });
        } else {
            mealViewModel.searchMeals(query).observe(getViewLifecycleOwner(), meals -> {
                mealAdapter.submitList(meals);
            });
        }
    }
}

