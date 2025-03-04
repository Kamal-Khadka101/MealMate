package com.example.kamalapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MealDao {
    @Insert
    void insert(Meal meal);

    @Query("SELECT * FROM meals")
    LiveData<List<Meal>> getAllMeals(); // Change this line to return LiveData
}