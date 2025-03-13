// Models/GroceryViewModel.java
package com.example.kamalapp.Models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.kamalapp.data.MealDatabase;
import com.example.kamalapp.data.GroceryDao;
import com.example.kamalapp.data.GroceryItem;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroceryViewModel extends AndroidViewModel {

    private GroceryDao groceryDao;
    private LiveData<List<GroceryItem>> allGroceryItems;
    private ExecutorService executorService;

    public GroceryViewModel(@NonNull Application application) {
        super(application);
        MealDatabase database = MealDatabase.getDatabase(application);
        groceryDao = database.groceryDao();
        allGroceryItems = groceryDao.getAllGroceryItems();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<GroceryItem>> getAllGroceryItems() {
        return allGroceryItems;
    }

    public LiveData<GroceryItem> getGroceryItemById(int id) {
        return groceryDao.getGroceryItemById(id);
    }

    public LiveData<GroceryItem> getGroceryItemByMealId(int mealId) {
        return groceryDao.getGroceryItemByMealId(mealId);
    }

    public void insert(GroceryItem groceryItem) {
        executorService.execute(() -> groceryDao.insert(groceryItem));
    }

    public void update(GroceryItem groceryItem) {
        executorService.execute(() -> groceryDao.update(groceryItem));
    }

    public void delete(GroceryItem groceryItem) {
        executorService.execute(() -> groceryDao.delete(groceryItem));
    }

    public void togglePurchaseStatus(GroceryItem groceryItem) {
        groceryItem.setPurchased(!groceryItem.isPurchased());
        update(groceryItem);
    }

    public void deleteAll() {
        executorService.execute(() -> groceryDao.deleteAll());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}