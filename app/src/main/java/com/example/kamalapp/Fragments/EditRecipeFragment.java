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

import java.util.List;

public class EditRecipeFragment extends Fragment {

    private static final String ARG_MEAL_ID = "meal_id";
    private static final int PICK_IMAGE_REQUEST = 1;

    private int mealId;
    private MealViewModel mealViewModel;
    private Meal currentMeal;

    private EditText recipeNameEditText;
    private RecyclerView ingredientsRecyclerView;
    private IngredientAdapter ingredientAdapter;
    private EditText instructionsEditText;
    private Button addPhotoButton;
    private Button updateRecipeButton;
    private Button addIngredientButton;
    private ImageView photoPreview;
    private Uri selectedImageUri = null;

    public static EditRecipeFragment newInstance(int mealId) {
        EditRecipeFragment fragment = new EditRecipeFragment();
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
        View view = inflater.inflate(R.layout.fragment_edit_recipe, container, false);

        // Initialize views
        recipeNameEditText = view.findViewById(R.id.edit_recipe_name);
        ingredientsRecyclerView = view.findViewById(R.id.recycler_ingredients);
        instructionsEditText = view.findViewById(R.id.edit_instructions);
        addPhotoButton = view.findViewById(R.id.button_add_photo);
        updateRecipeButton = view.findViewById(R.id.button_update_recipe);
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

        // Set up update recipe button
        updateRecipeButton.setOnClickListener(v -> updateRecipe());

        // Load existing meal data
        mealViewModel.getMealById(mealId).observe(getViewLifecycleOwner(), meal -> {
            if (meal != null) {
                currentMeal = meal;
                populateUI(meal);
            }
        });

        return view;
    }

    private void populateUI(Meal meal) {
        recipeNameEditText.setText(meal.getName());
        instructionsEditText.setText(meal.getInstructions());

        // Set up ingredients
        List<Pair<String, String>> ingredients = meal.getCategorizedIngredients();
        ingredientAdapter.setIngredients(ingredients);

        // Load image if available
        if (meal.getImagePath() != null && !meal.getImagePath().isEmpty()) {
            try {
                selectedImageUri = Uri.parse(meal.getImagePath());
                Glide.with(requireContext())
                        .load(selectedImageUri)
                        .centerCrop()
                        .into(photoPreview);
                photoPreview.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                photoPreview.setVisibility(View.GONE);
            }
        } else {
            photoPreview.setVisibility(View.GONE);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();

            // Get permanent permissions for the URI
            final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
            requireActivity().getContentResolver().takePersistableUriPermission(
                    selectedImageUri, takeFlags);

            // Show preview
            Glide.with(this)
                    .load(selectedImageUri)
                    .centerCrop()
                    .into(photoPreview);

            photoPreview.setVisibility(View.VISIBLE);
        }
    }

    private void updateRecipe() {
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

        // Update meal fields
        currentMeal.setName(name);
        currentMeal.setInstructions(instructions);

        // Update categorized ingredients
        currentMeal.setCategorizedIngredients(ingredients);

        // Update image if changed
        if (selectedImageUri != null) {
            currentMeal.setImagePath(selectedImageUri.toString());
        }

        // Save to database
        mealViewModel.update(currentMeal);

        // Navigate back
        requireActivity().getSupportFragmentManager().popBackStack();

        // Show success message
        Toast.makeText(requireContext(), "Recipe updated successfully", Toast.LENGTH_SHORT).show();
    }
}