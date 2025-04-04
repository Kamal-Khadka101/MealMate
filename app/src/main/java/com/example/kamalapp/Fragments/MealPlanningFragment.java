package com.example.kamalapp.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kamalapp.R;
import com.example.kamalapp.Adapters.IngredientAdapter;
import com.example.kamalapp.data.Meal;
import com.example.kamalapp.Models.MealViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MealPlanningFragment extends Fragment {

    private EditText recipeNameEditText;
    private RecyclerView ingredientsRecyclerView;
    private IngredientAdapter ingredientAdapter;
    private EditText instructionsEditText;
    private Button addPhotoButton;
    private Button saveRecipeButton;
    private Button addIngredientButton;
    private ImageView photoPreview;
    private Uri selectedImageUri = null;
    private MealViewModel mealViewModel;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_planning, container, false);

        // Initialize views
        recipeNameEditText = view.findViewById(R.id.edit_recipe_name);
        ingredientsRecyclerView = view.findViewById(R.id.recycler_ingredients);
        instructionsEditText = view.findViewById(R.id.edit_instructions);
        addPhotoButton = view.findViewById(R.id.button_add_photo);
        saveRecipeButton = view.findViewById(R.id.button_save_recipe);
        addIngredientButton = view.findViewById(R.id.button_add_ingredient);
        photoPreview = view.findViewById(R.id.image_photo_preview);

        // Set up ViewModel
        mealViewModel = new ViewModelProvider(this).get(MealViewModel.class);

        // Set up ingredients RecyclerView
        ingredientAdapter = new IngredientAdapter(requireContext());
        ingredientsRecyclerView.setAdapter(ingredientAdapter);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set up add ingredient button
        addIngredientButton.setOnClickListener(v -> ingredientAdapter.addIngredient());

        // Set up add photo button
        addPhotoButton.setOnClickListener(v -> openImagePicker());

        // Set up save recipe button
        saveRecipeButton.setOnClickListener(v -> saveRecipe());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            // Take persistable permissions
            requireActivity().getContentResolver().takePersistableUriPermission(
                    selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Load image with Glide
            photoPreview.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(selectedImageUri)
                    .centerCrop()
                    .into(photoPreview);
        }
    }


    private void saveRecipe() {
        String name = recipeNameEditText.getText().toString().trim();
        List<Pair<String, String>> ingredients = ingredientAdapter.getIngredients();
        String instructions = instructionsEditText.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a recipe name", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ingredients.isEmpty()) {
            Toast.makeText(requireContext(), "Please add at least one ingredient", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new meal
        Meal meal = new Meal(name, "", instructions, selectedImageUri != null ? selectedImageUri.toString() : "");

        // Add all ingredients with categories
        meal.setCategorizedIngredients(ingredients);

        // Save to database
        mealViewModel.insert(meal);

        // Get the bottom navigation view from activity
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        // Set the selected item to recipes
        bottomNavigationView.setSelectedItemId(R.id.nav_recipes);

        // Show success message
        Toast.makeText(requireContext(), "Recipe saved successfully", Toast.LENGTH_SHORT).show();
    }
}