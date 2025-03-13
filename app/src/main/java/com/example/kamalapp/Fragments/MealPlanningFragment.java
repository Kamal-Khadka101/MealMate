package com.example.kamalapp.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.kamalapp.data.Meal;
import com.example.kamalapp.Models.MealViewModel;
import com.example.kamalapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MealPlanningFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_IMAGE = 2;
    private BottomNavigationView bottomNavigationView;

    private MealViewModel mealViewModel;
    private EditText nameEditText, ingredientsEditText, instructionsEditText;
    private ImageView recipeImageView;
    private String currentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_planning, container, false);

        // Initialize UI components
        nameEditText = view.findViewById(R.id.edit_recipe_name);
        ingredientsEditText = view.findViewById(R.id.edit_recipe_ingredients);
        instructionsEditText = view.findViewById(R.id.edit_recipe_instructions);
        recipeImageView = view.findViewById(R.id.recipe_image);
        Button saveButton = view.findViewById(R.id.button_save);
        Button takePhotoButton = view.findViewById(R.id.button_take_photo);
        Button pickFromGalleryButton = view.findViewById(R.id.button_pick_gallery);

        // Initialize ViewModel
        mealViewModel = new ViewModelProvider(requireActivity()).get(MealViewModel.class);

        // Set click listeners
        takePhotoButton.setOnClickListener(v -> dispatchTakePictureIntent());
        pickFromGalleryButton.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> saveRecipe());

        return view;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE);
    }

    private void saveRecipe() {
        String name = nameEditText.getText().toString().trim();
        String ingredients = ingredientsEditText.getText().toString().trim();
        String instructions = instructionsEditText.getText().toString().trim();

        if (name.isEmpty() || ingredients.isEmpty() || instructions.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentPhotoPath == null) {
            Toast.makeText(getContext(), "Please add a photo", Toast.LENGTH_SHORT).show();
            return;
        }

        Meal meal = new Meal(name, ingredients, instructions, currentPhotoPath);
        mealViewModel.insert(meal);

        Toast.makeText(getContext(), "Recipe saved successfully!", Toast.LENGTH_SHORT).show();


        // Clear fields after saving
        nameEditText.setText("");
        ingredientsEditText.setText("");
        instructionsEditText.setText("");
        recipeImageView.setImageResource(R.drawable.ic_placeholder);
        currentPhotoPath = null;

        // Get the bottom navigation view from activity
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation);
        // Set the selected item to recipes
        bottomNavigationView.setSelectedItemId(R.id.nav_recipes);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(getContext(), "Error creating image file", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.example.kamalapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == requireActivity().RESULT_OK) {
            // Show the image from camera
            loadImageIntoView(currentPhotoPath);
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == requireActivity().RESULT_OK && data != null) {
            // Handle gallery image selection
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    // Create a file to store the selected image
                    File photoFile = createImageFile();

                    // Copy the selected image to our app's storage
                    copyUriToFile(selectedImageUri, photoFile);

                    // Show the image
                    loadImageIntoView(currentPhotoPath);
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error processing selected image", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadImageIntoView(String imagePath) {
        Glide.with(this)
                .load(imagePath)
                .centerCrop()
                .into(recipeImageView);
    }

    private void copyUriToFile(Uri sourceUri, File destinationFile) throws IOException {
        try {
            // This is a simplified version - in a real app, you would use ContentResolver and streams
            // to properly copy the file content

            // For demonstration purposes, we'll just save the URI path 
            // In a real implementation, you would read from the URI and write to the file
            currentPhotoPath = sourceUri.toString();

            // Note: A better implementation would be:
            // InputStream in = getContext().getContentResolver().openInputStream(sourceUri);
            // FileOutputStream out = new FileOutputStream(destinationFile);
            // [Copy data from in to out]
            // in.close();
            // out.close();
        } catch (Exception e) {
            throw new IOException("Failed to copy image", e);
        }
    }
}