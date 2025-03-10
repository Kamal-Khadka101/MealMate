// Repositories/RecipeRepository.java
package com.example.kamalapp.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.kamalapp.data.MealDao;
import com.example.kamalapp.data.Meal;
import com.example.kamalapp.data.MealDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeRepository {
    private final MealDao mealDao;
    private final ExecutorService executor;
    private final LiveData<List<Meal>> allMeals;

    public RecipeRepository(Application application) {
        MealDatabase database = MealDatabase.getDatabase(application);
        mealDao = database.mealDao();
        executor = Executors.newSingleThreadExecutor();
        allMeals = mealDao.getAllMeals();
    }

    public LiveData<List<Meal>> getAllMeals() {
        return allMeals;
    }

    public LiveData<Meal> getMealById(int id) {
        return mealDao.getMealById(id);
    }

    public LiveData<List<Meal>> searchMeals(String query) {
        return mealDao.searchMeals(query);
    }

    public void insert(Meal meal) {
        executor.execute(() -> mealDao.insertMeal(meal));
    }

    public void update(Meal meal) {
        executor.execute(() -> mealDao.updateMeal(meal));
    }

    public void delete(Meal meal) {
        executor.execute(() -> mealDao.deleteMeal(meal));
    }
}
