// Adapters/GroceryAdapter.java
package com.example.kamalapp.Adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kamalapp.R;
import com.example.kamalapp.data.GroceryItem;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder> {

    private List<GroceryItem> groceryItems;
    private OnGroceryItemClickListener listener;

    public interface OnGroceryItemClickListener {
        void onTogglePurchaseStatus(GroceryItem groceryItem);
        void onDeleteItem(GroceryItem groceryItem);
    }

    public GroceryAdapter(List<GroceryItem> groceryItems, OnGroceryItemClickListener listener) {
        this.groceryItems = groceryItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grocery, parent, false);
        return new GroceryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryViewHolder holder, int position) {
        GroceryItem currentItem = groceryItems.get(position);
        holder.bind(currentItem);
    }

    @Override
    public int getItemCount() {
        return groceryItems.size();
    }

    public void setGroceryItems(List<GroceryItem> groceryItems) {
        this.groceryItems = groceryItems;
        notifyDataSetChanged();
    }

    public class GroceryViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView ingredientsTextView;
        private Button purchaseButton;
        private Button deleteButton;

        public GroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.grocery_recipe_name);
            ingredientsTextView = itemView.findViewById(R.id.grocery_recipe_ingredients);
            purchaseButton = itemView.findViewById(R.id.button_toggle_purchase);
            deleteButton = itemView.findViewById(R.id.button_delete_grocery);
        }

        public void bind(GroceryItem groceryItem) {
            nameTextView.setText(groceryItem.getName());
            ingredientsTextView.setText(groceryItem.getIngredients());

            // Update text style based on purchase status
            if (groceryItem.isPurchased()) {
                nameTextView.setPaintFlags(nameTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                ingredientsTextView.setPaintFlags(ingredientsTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                purchaseButton.setText("Unpurchased");
            } else {
                nameTextView.setPaintFlags(nameTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                ingredientsTextView.setPaintFlags(ingredientsTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                purchaseButton.setText("Purchased");
            }

            purchaseButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTogglePurchaseStatus(groceryItem);
                }
            });

            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteItem(groceryItem);
                }
            });
        }
    }
}