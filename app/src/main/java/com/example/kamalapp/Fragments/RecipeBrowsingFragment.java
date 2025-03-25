// Fragments/RecipeBrowsingFragment.java
package com.example.kamalapp.Fragments;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.kamalapp.Adapters.MealAdapter;
import com.example.kamalapp.Models.MealViewModel;
import com.example.kamalapp.Models.GroceryViewModel;
import com.example.kamalapp.R;
import com.example.kamalapp.data.GroceryItem;
import com.example.kamalapp.data.Meal;

import java.util.List;

public class RecipeBrowsingFragment extends Fragment {

    private MealViewModel mealViewModel;
    private GroceryViewModel groceryViewModel;
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

        // Initialize ViewModels
        mealViewModel = new ViewModelProvider(requireActivity()).get(MealViewModel.class);
        groceryViewModel = new ViewModelProvider(requireActivity()).get(GroceryViewModel.class);

        // Set up swipe functionality
        setupSwipeActions(recyclerView);

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

    private void setupSwipeActions(RecyclerView recyclerView) {
        // Create ItemTouchHelper with swipe callbacks
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, 
                                @NonNull RecyclerView.ViewHolder target) {
                return false; // We're not handling move events
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Meal meal = mealAdapter.getCurrentList().get(position);
                
                if (direction == ItemTouchHelper.RIGHT) {
                    // Swipe right - Add to grocery list
                    addToGroceryList(meal);
                    // Reset the swipe to show the item again
                    mealAdapter.notifyItemChanged(position);
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Swipe left - Delete recipe
                    showDeleteConfirmation(meal, position);
                }
            }
            
            // Add visual feedback for swipe gestures
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, 
                                  @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, 
                                  int actionState, boolean isCurrentlyActive) {
                // Show visual cues for swipe actions
                View itemView = viewHolder.itemView;
                
                if (dX > 0) { // Swiping to the right (add to grocery)
                    // Draw green background
                    Paint paint = new Paint();
                    paint.setColor(ContextCompat.getColor(requireContext(), R.color.grocery_green));
                    
                    RectF background = new RectF(
                            itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + dX, itemView.getBottom());
                    c.drawRect(background, paint);
                } else if (dX < 0) { // Swiping to the left (delete)
                    // Draw red background
                    Paint paint = new Paint();
                    paint.setColor(ContextCompat.getColor(requireContext(), R.color.delete_red));
                    
                    RectF background = new RectF(
                            itemView.getRight() + dX, itemView.getTop(),
                            itemView.getRight(), itemView.getBottom());
                    c.drawRect(background, paint);
                }
                
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
        
        // Attach ItemTouchHelper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Method to add a meal to the grocery list
    private void addToGroceryList(Meal meal) {
        // Extract ingredients from the meal
        StringBuilder sb = new StringBuilder();
        List<Pair<String, String>> categorizedIngredients = meal.getCategorizedIngredients();
        
        if (!categorizedIngredients.isEmpty()) {
            for (Pair<String, String> ingredient : categorizedIngredients) {
                sb.append(ingredient.second).append("\n");
            }
        }
        
        String ingredientsText = sb.toString().trim();
        
        // Create a new GroceryItem
        GroceryItem groceryItem = new GroceryItem(
                meal.getId(),
                meal.getName(),
                ingredientsText,
                false // Not purchased by default
        );
        
        // Add to grocery list
        groceryViewModel.insert(groceryItem);
        
        // Show confirmation
        Toast.makeText(requireContext(), "Added to grocery list", Toast.LENGTH_SHORT).show();
    }

    // Method to show a confirmation dialog before deleting a recipe
    private void showDeleteConfirmation(Meal meal, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete this recipe?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the meal
                    mealViewModel.delete(meal);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // User canceled, reset the item view
                    mealAdapter.notifyItemChanged(position);
                })
                .setCancelable(false) // Prevent dismissing by tapping outside
                .show();
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

