// Fragments/RecipeDetailFragment.java
package com.example.kamalapp.Fragments;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.kamalapp.data.Meal;
import com.example.kamalapp.data.GroceryItem;
import com.example.kamalapp.Models.MealViewModel;
import com.example.kamalapp.Models.GroceryViewModel;
import com.example.kamalapp.R;

import java.io.File;

public class RecipeDetailFragment extends Fragment {

    private static final String ARG_RECIPE_ID = "recipe_id";
    private MealViewModel mealViewModel;
    private GroceryViewModel groceryViewModel;
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
        Button deleteButton = view.findViewById(R.id.button_delete);

        editButton.setOnClickListener(v -> {
            showEditDialog();
        });

        addToGroceryButton.setOnClickListener(v -> {
            addToGroceryList();
        });

        deleteButton.setOnClickListener(v -> {
            confirmAndDeleteRecipe();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        mealViewModel = new ViewModelProvider(requireActivity()).get(MealViewModel.class);
        groceryViewModel = new ViewModelProvider(requireActivity()).get(GroceryViewModel.class);

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

    private void showEditDialog() {
        // Create dialog layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_recipe, null);

        // Get references to EditText fields
        EditText nameEditText = dialogView.findViewById(R.id.edit_recipe_name);
        EditText ingredientsEditText = dialogView.findViewById(R.id.edit_recipe_ingredients);
        EditText instructionsEditText = dialogView.findViewById(R.id.edit_recipe_instructions);

        // Pre-fill with current meal data
        nameEditText.setText(currentMeal.getName());
        ingredientsEditText.setText(currentMeal.getIngredients());
        instructionsEditText.setText(currentMeal.getInstructions());

        // Create and show the dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Recipe")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    // Update the meal with new values
                    currentMeal.setName(nameEditText.getText().toString());
                    currentMeal.setIngredients(ingredientsEditText.getText().toString());
                    currentMeal.setInstructions(instructionsEditText.getText().toString());

                    // Save to database
                    mealViewModel.update(currentMeal);

                    // Update UI
                    populateUI(currentMeal);

                    Toast.makeText(getContext(), "Recipe updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void addToGroceryList() {
        // Create a new GroceryItem from the current meal
        GroceryItem groceryItem = new GroceryItem(
                currentMeal.getId(),
                currentMeal.getName(),
                currentMeal.getIngredients(),
                false // Not purchased by default
        );

        // Add to grocery list via ViewModel
        groceryViewModel.insert(groceryItem);

        Toast.makeText(getContext(), "Added to grocery list", Toast.LENGTH_SHORT).show();

        // Optionally navigate to grocery list fragment
        // requireActivity().getSupportFragmentManager().beginTransaction()
        //     .replace(R.id.fragment_container, new GroceryListFragment())
        //     .addToBackStack(null)
        //     .commit();
    }

    private void confirmAndDeleteRecipe() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete image file if it exists and is stored locally
                    if (currentMeal.getImagePath() != null && !currentMeal.getImagePath().isEmpty()) {
                        try {
                            // Only delete if it's a file URI, not a content or resource URI
                            Uri imageUri = Uri.parse(currentMeal.getImagePath());
                            if (imageUri.getScheme() != null && imageUri.getScheme().equals("file")) {
                                File imageFile = new File(imageUri.getPath());
                                if (imageFile.exists()) {
                                    imageFile.delete();
                                }
                            }
                        } catch (Exception e) {
                            // Log but don't interrupt deletion process
                            e.printStackTrace();
                        }
                    }

                    // Delete the meal
                    mealViewModel.delete(currentMeal);

                    // Display a confirmation message
                    Toast.makeText(getContext(), "Recipe deleted", Toast.LENGTH_SHORT).show();

                    // Navigate back
                    requireActivity().onBackPressed();
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // We're not inflating the menu_recipe_detail.xml anymore as requested
        super.onCreateOptionsMenu(menu, inflater);
    }
}