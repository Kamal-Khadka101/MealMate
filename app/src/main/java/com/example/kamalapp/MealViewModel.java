package com.example.kamalapp;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.kamalapp.data.Meal;
import com.example.kamalapp.data.MealDatabase;

import java.util.List;

public class MealViewModel extends AndroidViewModel {
    private MealDatabase database;
    private LiveData<List<Meal>> allMeals;

    public MealViewModel(@NonNull Application application) {
        super(application);
        database = MealDatabase.getInstance(application);
        allMeals = database.mealDao().getAllMeals(); // Assuming you have a LiveData version of this method
    }

    public void insert(Meal meal) {
        new Thread(() -> database.mealDao().insert(meal)).start();
    }

    public LiveData<List<Meal>> getAllMeals() {
        return allMeals;
    }
}