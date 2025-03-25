// Fragments/GroceryListFragment.java
package com.example.kamalapp.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kamalapp.Adapters.GroceryAdapter;
import com.example.kamalapp.Models.GroceryViewModel;
import com.example.kamalapp.R;
import com.example.kamalapp.data.GroceryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroceryListFragment extends Fragment implements GroceryAdapter.OnGroceryItemClickListener, SensorEventListener {

    private static final int SMS_PERMISSION_REQUEST_CODE = 101;
    
    // Shake detection constants
    private static final float SHAKE_THRESHOLD = 12.0f; // Threshold for shake detection
    private static final int MIN_TIME_BETWEEN_SHAKES = 1000; // Minimum time between shakes in ms

    private GroceryViewModel groceryViewModel;
    private RecyclerView recyclerView;
    private GroceryAdapter adapter;
    private TextView emptyView;
    private FloatingActionButton sendSmsFab;
    private List<GroceryItem> currentGroceryItems = new ArrayList<>();
    
    // Shake detection variables
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastShakeTime = 0;
    private float lastX = 0;
    private float lastY = 0;
    private float lastZ = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);

        recyclerView = view.findViewById(R.id.grocery_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        sendSmsFab = view.findViewById(R.id.send_sms);

        // Setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroceryAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Setup FAB click listener
        sendSmsFab.setOnClickListener(v -> checkSmsPermissionAndSend());
        
        // Initialize shake detection
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        groceryViewModel = new ViewModelProvider(requireActivity()).get(GroceryViewModel.class);

        // Observe grocery items
        groceryViewModel.getAllGroceryItems().observe(getViewLifecycleOwner(), groceryItems -> {
            if (groceryItems != null && !groceryItems.isEmpty()) {
                currentGroceryItems = groceryItems;
                adapter.setGroceryItems(groceryItems);
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);

                // Only show FAB if there are unpurchased items
                boolean hasUnpurchasedItems = false;
                for (GroceryItem item : groceryItems) {
                    if (!item.isPurchased()) {
                        hasUnpurchasedItems = true;
                        break;
                    }
                }
                sendSmsFab.setVisibility(hasUnpurchasedItems ? View.VISIBLE : View.GONE);
            } else {
                currentGroceryItems = new ArrayList<>();
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                sendSmsFab.setVisibility(View.GONE);
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Register shake detector when fragment is visible
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // Unregister shake detector when fragment is not visible
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
    
    // SensorEventListener methods
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();
            
            if ((currentTime - lastShakeTime) > MIN_TIME_BETWEEN_SHAKES) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                
                float deltaX = Math.abs(lastX - x);
                float deltaY = Math.abs(lastY - y);
                float deltaZ = Math.abs(lastZ - z);
                
                // Check if the acceleration change is above the threshold
                if ((deltaX > SHAKE_THRESHOLD && deltaY > SHAKE_THRESHOLD) 
                        || (deltaX > SHAKE_THRESHOLD && deltaZ > SHAKE_THRESHOLD) 
                        || (deltaY > SHAKE_THRESHOLD && deltaZ > SHAKE_THRESHOLD)) {
                    
                    // Device was shaken, trigger SMS
                    lastShakeTime = currentTime;
                    Toast.makeText(requireContext(), "Shake detected! Preparing SMS...", Toast.LENGTH_SHORT).show();
                    checkSmsPermissionAndSend();
                }
                
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for this implementation
    }

    @Override
    public void onTogglePurchaseStatus(GroceryItem groceryItem) {
        groceryViewModel.togglePurchaseStatus(groceryItem);
    }

    @Override
    public void onDeleteItem(GroceryItem groceryItem) {
        groceryViewModel.delete(groceryItem);
    }

    private void checkSmsPermissionAndSend() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        } else {
            prepareSmsContent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                prepareSmsContent();
            } else {
                Toast.makeText(requireContext(), "Permission denied to send SMS", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void prepareSmsContent() {
        // Filter only unpurchased items
        List<GroceryItem> unpurchasedItems = new ArrayList<>();
        for (GroceryItem item : currentGroceryItems) {
            if (!item.isPurchased()) {
                unpurchasedItems.add(item);
            }
        }

        if (unpurchasedItems.isEmpty()) {
            Toast.makeText(requireContext(), "No unpurchased items to send", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format the message
        StringBuilder smsBuilder = new StringBuilder("Grocery Shopping List:\n\n");

        // Group ingredients by recipe and category
        Map<String, Map<String, List<String>>> organizedItems = new HashMap<>();

        for (GroceryItem item : unpurchasedItems) {
            String recipeName = item.getName();
            String ingredientsText = item.getIngredients();

            if (!organizedItems.containsKey(recipeName)) {
                organizedItems.put(recipeName, new HashMap<>());
            }

            // Parse ingredient lines
            String[] lines = ingredientsText.split("\n");
            for (String line : lines) {
                // Here we assume ingredients are just listed line by line
                // You might need to adjust this if your format is different
                if (!line.trim().isEmpty()) {
                    if (!organizedItems.get(recipeName).containsKey("Ingredients")) {
                        organizedItems.get(recipeName).put("Ingredients", new ArrayList<>());
                    }
                    organizedItems.get(recipeName).get("Ingredients").add(line.trim());
                }
            }
        }

        // Build formatted message
        for (Map.Entry<String, Map<String, List<String>>> recipeEntry : organizedItems.entrySet()) {
            smsBuilder.append(recipeEntry.getKey()).append(":\n");

            Map<String, List<String>> categories = recipeEntry.getValue();
            for (Map.Entry<String, List<String>> categoryEntry : categories.entrySet()) {
                List<String> ingredients = categoryEntry.getValue();
                for (String ingredient : ingredients) {
                    smsBuilder.append("- ").append(ingredient).append("\n");
                }
            }
            smsBuilder.append("\n");
        }

        // Open SMS app with prepared message
        sendSms(smsBuilder.toString().trim());
    }

    private void sendSms(String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:")); // This ensures only SMS apps respond
        intent.putExtra("sms_body", message);

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "No SMS app found", Toast.LENGTH_SHORT).show();
        }
    }
}