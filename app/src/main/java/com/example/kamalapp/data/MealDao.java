// Data/MealDao.java
package com.example.kamalapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kamalapp.data.Meal;

import java.util.List;

@Dao
public interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMeal(Meal meal);

    @Update
    void updateMeal(Meal meal);

    @Delete
    void deleteMeal(Meal meal);

    @Query("SELECT * FROM meals ORDER BY created_at DESC")
    LiveData<List<Meal>> getAllMeals();

    @Query("SELECT * FROM meals WHERE id = :mealId")
    LiveData<Meal> getMealById(int mealId);

    @Query("SELECT * FROM meals WHERE name LIKE '%' || :searchQuery || '%'")
    LiveData<List<Meal>> searchMeals(String searchQuery);
}
