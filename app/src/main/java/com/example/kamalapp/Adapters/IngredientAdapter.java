package com.example.kamalapp.Adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kamalapp.R;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private List<Pair<String, String>> ingredients;
    private Context context;
    private RecyclerView recyclerView;

    private static final String[] CATEGORIES = {
            "Fruits/Vegetables", "Proteins/Dairy", "Grains/Spices",
            "Frozen/Packed Foods", "Beverages/Extra"
    };

    public IngredientAdapter(Context context) {
        this.context = context;
        this.ingredients = new ArrayList<>();
        // Add an initial empty ingredient
        this.ingredients.add(new Pair<>("Fruits/Vegetables", ""));
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Pair<String, String> ingredient = ingredients.get(position);

        // Set up the category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, CATEGORIES);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.categorySpinner.setAdapter(categoryAdapter);

        // Set selected category
        for (int i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i].equals(ingredient.first)) {
                holder.categorySpinner.setSelection(i);
                break;
            }
        }

        // Set ingredient name
        holder.ingredientEditText.setText(ingredient.second);

        // Set up remove button
        holder.removeButton.setOnClickListener(v -> {
            if (ingredients.size() > 1) {
                ingredients.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public void addIngredient() {
        ingredients.add(new Pair<>("Fruits/Vegetables", ""));
        notifyItemInserted(ingredients.size() - 1);
    }

    public List<Pair<String, String>> getIngredients() {
        List<Pair<String, String>> result = new ArrayList<>();

        // Get all visible views
        for (int i = 0; i < getItemCount(); i++) {
            IngredientViewHolder holder = (IngredientViewHolder) recyclerView.findViewHolderForAdapterPosition(i);

            if (holder != null) {
                String category = CATEGORIES[holder.categorySpinner.getSelectedItemPosition()];
                String name = holder.ingredientEditText.getText().toString().trim();

                if (!name.isEmpty()) {
                    result.add(new Pair<>(category, name));
                }
            } else {
                // If view holder is null (recycled), use stored data
                Pair<String, String> ingredient = ingredients.get(i);
                if (ingredient.second != null && !ingredient.second.isEmpty()) {
                    result.add(ingredient);
                }
            }
        }

        return result;
    }

    public void setIngredients(List<Pair<String, String>> ingredients) {
        this.ingredients = new ArrayList<>(ingredients);
        if (this.ingredients.isEmpty()) {
            this.ingredients.add(new Pair<>("Fruits/Vegetables", ""));
        }
        notifyDataSetChanged();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        Spinner categorySpinner;
        EditText ingredientEditText;
        ImageButton removeButton;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            categorySpinner = itemView.findViewById(R.id.spinner_ingredient_category);
            ingredientEditText = itemView.findViewById(R.id.edit_ingredient_name);
            removeButton = itemView.findViewById(R.id.button_remove_ingredient);
        }
    }
}