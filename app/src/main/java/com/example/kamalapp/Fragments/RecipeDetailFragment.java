package com.example.kamalapp.Fragments;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.kamalapp.R;
import com.example.kamalapp.data.Meal;
import com.example.kamalapp.data.GroceryItem;
import com.example.kamalapp.Models.MealViewModel;
import com.example.kamalapp.Models.GroceryViewModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecipeDetailFragment extends Fragment {

    private static final String ARG_MEAL_ID = "meal_id";
    private int mealId;
    private MealViewModel mealViewModel;
    private GroceryViewModel groceryViewModel;
    private TextView nameTextView;
    private TextView ingredientsTextView;
    private TextView instructionsTextView;
    private ImageView mealImageView;
    private Button editButton;
    private Button deleteButton;
    private Button addToGroceryButton;
    private Meal currentMeal;

    public static RecipeDetailFragment newInstance(int mealId) {
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MEAL_ID, mealId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealId = getArguments().getInt(ARG_MEAL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // Initialize views
        nameTextView = view.findViewById(R.id.text_recipe_name);
        ingredientsTextView = view.findViewById(R.id.text_ingredients);
        instructionsTextView = view.findViewById(R.id.text_instructions);
        mealImageView = view.findViewById(R.id.image_recipe);
        editButton = view.findViewById(R.id.button_edit);
        deleteButton = view.findViewById(R.id.button_delete);
        addToGroceryButton = view.findViewById(R.id.button_add_to_grocery);

        // Set up view models
        mealViewModel = new ViewModelProvider(this).get(MealViewModel.class);
        groceryViewModel = new ViewModelProvider(this).get(GroceryViewModel.class);

        // Observe meal data
        mealViewModel.getMealById(mealId).observe(getViewLifecycleOwner(), meal -> {
            if (meal != null) {
                currentMeal = meal;
                displayMealDetails(meal);
            }
        });

        // Set up buttons
        editButton.setOnClickListener(v -> editRecipe());
        deleteButton.setOnClickListener(v -> deleteRecipe());
        addToGroceryButton.setOnClickListener(v -> addToGroceryList());

        return view;
    }

    private void displayMealDetails(Meal meal) {
        nameTextView.setText(meal.getName());

        // Display categorized ingredients
        List<Pair<String, String>> categorizedIngredients = meal.getCategorizedIngredients();

        if (!categorizedIngredients.isEmpty()) {
            // Group ingredients by category
            Map<String, List<String>> ingredientsByCategory = new LinkedHashMap<>();

            for (Pair<String, String> ingredient : categorizedIngredients) {
                String category = ingredient.first;
                String name = ingredient.second;

                if (category.isEmpty()) {
                    category = "Other"; // For old format data
                }

                if (!ingredientsByCategory.containsKey(category)) {
                    ingredientsByCategory.put(category, new ArrayList<>());
                }

                ingredientsByCategory.get(category).add(name);
            }

            // Build formatted text
            StringBuilder sb = new StringBuilder();

            for (Map.Entry<String, List<String>> entry : ingredientsByCategory.entrySet()) {
                // Add category header
                sb.append(entry.getKey()).append(":\n");

                // Add all ingredients in this category
                for (String ingredient : entry.getValue()) {
                    sb.append("• ").append(ingredient).append("\n");
                }

                sb.append("\n");
            }

            ingredientsTextView.setText(sb.toString());
        } else {
            ingredientsTextView.setText("No ingredients listed");
        }

        instructionsTextView.setText(meal.getInstructions());

        // Load image if available
        if (meal.getImagePath() != null && !meal.getImagePath().isEmpty()) {
            try {
                Uri imageUri = Uri.parse(meal.getImagePath());
                Glide.with(requireContext())
                        .load(imageUri)
                        .centerCrop()
                        .into(mealImageView);
            } catch (Exception e) {
                mealImageView.setImageResource(R.drawable.ic_placeholder);
            }
        } else {
            mealImageView.setImageResource(R.drawable.ic_placeholder);
        }
    }

    private void editRecipe() {
        // Navigate to edit fragment
        com.example.kamalapp.Fragments.EditRecipeFragment editFragment = EditRecipeFragment.newInstance(mealId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .addToBackStack(null)
                .commit();
    }

    private void deleteRecipe() {
        // Show confirmation dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    mealViewModel.delete(currentMeal);
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addToGroceryList() {
        // First, get a simple string representation of the ingredients
        String ingredientsText = "";

        // If using categorized ingredients, we need to extract just the ingredient names
        List<Pair<String, String>> categorizedIngredients = currentMeal.getCategorizedIngredients();
        if (!categorizedIngredients.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Pair<String, String> ingredient : categorizedIngredients) {
                sb.append(ingredient.second).append("\n");
            }
            ingredientsText = sb.toString().trim();
        } else {
            // Fallback if no categorized ingredients (shouldn't happen but just in case)
            ingredientsText = ingredientsTextView.getText().toString();
        }

        // Create a new GroceryItem from the current meal
        GroceryItem groceryItem = new GroceryItem(
                currentMeal.getId(),
                currentMeal.getName(),
                ingredientsText,
                false // Not purchased by default
        );

        // Add to grocery list via ViewModel
        groceryViewModel.insert(groceryItem);

        Toast.makeText(getContext(), "Added to grocery list", Toast.LENGTH_SHORT).show();
    }
}