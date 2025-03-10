// Models/MealViewModel.java
package com.example.kamalapp.Models;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.kamalapp.data.Meal;
import com.example.kamalapp.Repositories.RecipeRepository;

import java.util.List;

public class MealViewModel extends AndroidViewModel {

    private final RecipeRepository repository;
    private final LiveData<List<Meal>> allMeals;

    public MealViewModel(Application application) {
        super(application);
        repository = new RecipeRepository(application);
        allMeals = repository.getAllMeals();
    }

    public LiveData<List<Meal>> getAllMeals() {
        return allMeals;
    }

    public LiveData<Meal> getMealById(int id) {
        return repository.getMealById(id);
    }

    public LiveData<List<Meal>> searchMeals(String query) {
        return repository.searchMeals(query);
    }

    public void insert(Meal meal) {
        repository.insert(meal);
    }

    public void update(Meal meal) {
        repository.update(meal);
    }

    public void delete(Meal meal) {
        repository.delete(meal);
    }
}
