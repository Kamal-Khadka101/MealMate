package com.example.kamalapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meals")
public class Meal {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String instructions;
    private String ingredients;
    private String imagePath;

    public Meal(String name, String instructions, String ingredients, String imagePath) {
        this.name = name;
        this.instructions = instructions;
        this.ingredients = ingredients;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getImagePath() {
        return imagePath;
    }
}