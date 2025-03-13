// data/GroceryItem.java
package com.example.kamalapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grocery_items")
public class GroceryItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int mealId; // Reference to original meal
    private String name;
    private String ingredients;
    private boolean isPurchased;

    public GroceryItem(int mealId, String name, String ingredients, boolean isPurchased) {
        this.mealId = mealId;
        this.name = name;
        this.ingredients = ingredients;
        this.isPurchased = isPurchased;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }
}