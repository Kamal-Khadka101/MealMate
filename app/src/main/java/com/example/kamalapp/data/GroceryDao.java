// data/GroceryDao.java
package com.example.kamalapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GroceryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GroceryItem groceryItem);

    @Update
    void update(GroceryItem groceryItem);

    @Delete
    void delete(GroceryItem groceryItem);

    @Query("SELECT * FROM grocery_items ORDER BY name ASC")
    LiveData<List<GroceryItem>> getAllGroceryItems();

    @Query("SELECT * FROM grocery_items WHERE id = :id")
    LiveData<GroceryItem> getGroceryItemById(int id);

    @Query("SELECT * FROM grocery_items WHERE mealId = :mealId")
    LiveData<GroceryItem> getGroceryItemByMealId(int mealId);

    @Query("DELETE FROM grocery_items")
    void deleteAll();
}