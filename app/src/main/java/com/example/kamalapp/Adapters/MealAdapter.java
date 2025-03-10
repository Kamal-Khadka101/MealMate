// Adapters/MealAdapter.java
package com.example.kamalapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kamalapp.data.Meal;
import com.example.kamalapp.Fragments.RecipeDetailFragment;
import com.example.kamalapp.R;

import java.io.File;

public class MealAdapter extends ListAdapter<Meal, MealAdapter.MealViewHolder> {

    public MealAdapter(@NonNull DiffUtil.ItemCallback<Meal> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal current = getItem(position);
        holder.bind(current);
    }

    class MealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView mealNameTextView;
        private final ImageView mealImageView;
        private Meal meal;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealNameTextView = itemView.findViewById(R.id.text_meal_name);
            mealImageView = itemView.findViewById(R.id.image_meal_thumbnail);
            itemView.setOnClickListener(this);
        }

        void bind(Meal meal) {
            this.meal = meal;
            mealNameTextView.setText(meal.getName());

            // Load image with Glide
            Glide.with(itemView.getContext())
                    .load(new File(meal.getImagePath()))
                    .centerCrop()
                    .placeholder(R.drawable.ic_placeholder)
                    .into(mealImageView);
        }

        @Override
        public void onClick(View v) {
            // Navigate to detail fragment
            RecipeDetailFragment detailFragment = RecipeDetailFragment.newInstance(meal.getId());
            ((FragmentActivity) v.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public static class MealDiff extends DiffUtil.ItemCallback<Meal> {
        @Override
        public boolean areItemsTheSame(@NonNull Meal oldItem, @NonNull Meal newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Meal oldItem, @NonNull Meal newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getIngredients().equals(newItem.getIngredients()) &&
                    oldItem.getInstructions().equals(newItem.getInstructions()) &&
                    oldItem.getImagePath().equals(newItem.getImagePath());
        }
    }
}

