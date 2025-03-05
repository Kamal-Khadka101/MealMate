package com.example.kamalapp.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.kamalapp.Fragments.GroceryListFragment;
import com.example.kamalapp.Fragments.HomeFragment;
import com.example.kamalapp.Fragments.MealPlanningFragment;
import com.example.kamalapp.Fragments.RecipeBrowsingFragment;
import com.example.kamalapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView mMealPlanSummary;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMealPlanSummary = findViewById(R.id.meal_plan_summary);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set a summary for the week's meal plan (this can be dynamic based on user data)
        mMealPlanSummary.setText("This week's meal plan: Chicken Stir-Fry, Vegetable Quinoa Bowl, Spaghetti Carbonara");

        // Load the Home fragment by default
        loadFragment(new HomeFragment());

        // Set up the bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.nav_meal_planning) {
                    selectedFragment = new MealPlanningFragment();
                } else if (itemId == R.id.nav_grocery_list) {
                    selectedFragment = new GroceryListFragment();
                } else if (itemId == R.id.nav_recipes) {
                    selectedFragment = new RecipeBrowsingFragment();
                } else if (itemId == R.id.nav_logout) {// Handle logout
                    Toast.makeText(MainActivity.this, "Logging Out", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Close the Main Activity
                    return true;
                }
                return loadFragment(selectedFragment);
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        // Replace the current fragment with the selected one
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}