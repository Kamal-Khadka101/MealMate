// Fragments/RecipeDetailFragment.java
package com.example.kamalapp.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.kamalapp.data.Meal;
import com.example.kamalapp.Models.MealViewModel;
import com.example.kamalapp.R;

import java.io.File;

public class RecipeDetailFragment extends Fragment {

    private static final String ARG_RECIPE_ID = "recipe_id";
    private MealViewModel mealViewModel;
    private Meal currentMeal;
    private ImageView recipeImageView;
    private TextView nameTextView, ingredientsTextView, instructionsTextView;

    public static RecipeDetailFragment newInstance(int recipeId) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // Initialize UI components
        recipeImageView = view.findViewById(R.id.detail_recipe_image);
        nameTextView = view.findViewById(R.id.detail_recipe_name);
        ingredientsTextView = view.findViewById(R.id.detail_recipe_ingredients);
        instructionsTextView = view.findViewById(R.id.detail_recipe_instructions);

        // Add action buttons
        Button editButton = view.findViewById(R.id.button_edit);
        Button addToGroceryButton = view.findViewById(R.id.button_add_to_grocery);

        editButton.setOnClickListener(v -> {
            // TODO: Implement edit functionality
            // You could navigate to MealPlanningFragment with the current meal data
            Toast.makeText(getContext(), "Edit feature coming soon", Toast.LENGTH_SHORT).show();
        });

        addToGroceryButton.setOnClickListener(v -> {
            // TODO: Implement add to grocery list functionality
            Toast.makeText(getContext(), "Added to grocery list", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        mealViewModel = new ViewModelProvider(requireActivity()).get(MealViewModel.class);

        // Get recipe ID from arguments
        int recipeId = getArguments().getInt(ARG_RECIPE_ID);

        // Observe recipe data
        mealViewModel.getMealById(recipeId).observe(getViewLifecycleOwner(), meal -> {
            if (meal != null) {
                currentMeal = meal;
                populateUI(meal);
            }
        });
    }

    private void populateUI(Meal meal) {
        nameTextView.setText(meal.getName());
        ingredientsTextView.setText(meal.getIngredients());
        instructionsTextView.setText(meal.getInstructions());

        // Load image with Glide
        Glide.with(this)
                .load(Uri.parse(meal.getImagePath()))
                .centerCrop()
                .into(recipeImageView);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_recipe_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            if (currentMeal != null) {
                mealViewModel.delete(currentMeal);
                requireActivity().onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
