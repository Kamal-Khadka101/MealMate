package com.example.kamalapp.data;

import android.util.Pair;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "meals")
public class Meal {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "ingredients")
    private String ingredients;

    @ColumnInfo(name = "instructions")
    private String instructions;

    @ColumnInfo(name = "image_path")
    private String imagePath;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    // Constructors
    public Meal(String name, String ingredients, String instructions, String imagePath) {
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imagePath = imagePath;
        this.createdAt = System.currentTimeMillis();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Helper method to add a categorized ingredient to the ingredients list
    public void addCategorizedIngredient(String category, String ingredient) {
        String newEntry = category + ":" + ingredient;

        if (ingredients == null || ingredients.isEmpty()) {
            ingredients = newEntry;
        } else {
            ingredients += "\n" + newEntry;
        }
    }

    // Helper method to get ingredients with categories as a list
    public List<Pair<String, String>> getCategorizedIngredients() {
        List<Pair<String, String>> result = new ArrayList<>();

        if (ingredients != null && !ingredients.isEmpty()) {
            String[] lines = ingredients.split("\n");
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    if (parts.length == 2) {
                        result.add(new Pair<>(parts[0], parts[1]));
                    } else {
                        // Handle malformed data
                        result.add(new Pair<>("", line));
                    }
                } else {
                    // Handle old format with no category
                    result.add(new Pair<>("", line));
                }
            }
        }

        return result;
    }

    // Helper method to update ingredients list from categorized pairs
    public void setCategorizedIngredients(List<Pair<String, String>> categorizedIngredients) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < categorizedIngredients.size(); i++) {
            Pair<String, String> item = categorizedIngredients.get(i);
            sb.append(item.first).append(":").append(item.second);

            if (i < categorizedIngredients.size() - 1) {
                sb.append("\n");
            }
        }

        this.ingredients = sb.toString();
    }
}