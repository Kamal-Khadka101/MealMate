package com.example.kamalapp.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.example.kamalapp.Models.MealViewModel;
import com.example.kamalapp.R;
import com.example.kamalapp.data.Meal;

public class MealPlanningFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private ImageView mealImageView;
    private EditText mealNameEditText, mealInstructionsEditText, mealIngredientsEditText;
    private Button saveMealButton;
    private Uri imageUri;
    private MealViewModel mealViewModel; // Declare the ViewModel

    public MealPlanningFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_planning, container, false);

        mealImageView = view.findViewById(R.id.meal_image);
        mealNameEditText = view.findViewById(R.id.meal_name);
        mealInstructionsEditText = view.findViewById(R.id.meal_instructions);
        mealIngredientsEditText = view.findViewById(R.id.meal_ingredients);
        saveMealButton = view.findViewById(R.id.save_meal_button);

        // Initialize the ViewModel
        mealViewModel = new ViewModelProvider(this).get(MealViewModel.class);

        mealImageView.setOnClickListener(v -> openGallery());

        saveMealButton.setOnClickListener(v -> saveMeal());

        return view;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            mealImageView.setImageURI(imageUri);
        }
    }

    private void saveMeal() {
        String name = mealNameEditText.getText().toString().trim();
        String instructions = mealInstructionsEditText.getText().toString().trim();
        String ingredients = mealIngredientsEditText.getText().toString().trim();

        if (name.isEmpty() || instructions.isEmpty() || ingredients.isEmpty() || imageUri == null) {
            Toast.makeText(getActivity(), "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        String imagePath = imageUri.toString(); // Store the image URI as a string

        Meal meal = new Meal(name, instructions, ingredients, imagePath);

        // Use the ViewModel to insert the meal
        mealViewModel.insert(meal);

        Toast.makeText(getActivity(), "Meal saved!", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void clearFields() {
        mealNameEditText.setText("");
        mealInstructionsEditText.setText("");
        mealIngredientsEditText.setText("");
        mealImageView.setImageResource(0);
        imageUri = null;
    }
}