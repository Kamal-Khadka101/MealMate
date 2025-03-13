// Fragments/GroceryListFragment.java
package com.example.kamalapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kamalapp.Adapters.GroceryAdapter;
import com.example.kamalapp.Models.GroceryViewModel;
import com.example.kamalapp.R;
import com.example.kamalapp.data.GroceryItem;

import java.util.ArrayList;

public class GroceryListFragment extends Fragment implements GroceryAdapter.OnGroceryItemClickListener {

    private GroceryViewModel groceryViewModel;
    private RecyclerView recyclerView;
    private GroceryAdapter adapter;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery_list, container, false);

        recyclerView = view.findViewById(R.id.grocery_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);

        // Setup recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new GroceryAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

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
                adapter.setGroceryItems(groceryItems);
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onTogglePurchaseStatus(GroceryItem groceryItem) {
        groceryViewModel.togglePurchaseStatus(groceryItem);
    }

    @Override
    public void onDeleteItem(GroceryItem groceryItem) {
        groceryViewModel.delete(groceryItem);
    }
}