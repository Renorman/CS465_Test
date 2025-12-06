package com.example.uiuctempapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.activity.OnBackPressedCallback;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menuButton, profileButton;
    private ImageView mapButton;

    private RoomsViewModel roomsViewModel;
    private Room currentRoom;

    // Voting UI
    private SeekBar tempSlider;
    private TextView sliderValue;
    private Button submitVote;
    private final int baseTemp = 50;

    // Search UI
    private EditText searchBar;
    private ListView searchResults;
    private ArrayAdapter<String> searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        profileButton = findViewById(R.id.profileButton);
        mapButton = findViewById(R.id.mapButton);
        searchBar = findViewById(R.id.searchBar);
        searchResults = findViewById(R.id.searchResults);

        LinearLayout locationsContainer = findViewById(R.id.locationsContainer);

        roomsViewModel = new ViewModelProvider(this).get(RoomsViewModel.class);
        currentRoom = roomsViewModel.getRoom("room_1"); // default

        updateUI();

        // Favorites Drawer
        roomsViewModel.getFavorites().observe(this, favorites -> {
            locationsContainer.removeAllViews();

            for (Room room : favorites) {
                View item = getLayoutInflater().inflate(R.layout.favorite_item_layout, null);

                TextView name = item.findViewById(R.id.favoriteName);
                TextView temp = item.findViewById(R.id.favoriteTemp);

                name.setText(room.name);
                temp.setText(room.currentTemp + "°");

                item.setOnClickListener(v -> {
                    currentRoom = room;
                    updateUI();
                    drawerLayout.closeDrawers();
                });

                locationsContainer.addView(item);
            }
        });

        menuButton.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        mapButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MapsActivity.class));
        });

        profileButton.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        // Back Button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    HomeActivity.super.onBackPressed();
                }
            }
        });

        // Slider voting
        tempSlider = findViewById(R.id.tempSlider);
        sliderValue = findViewById(R.id.sliderValue);
        submitVote = findViewById(R.id.submitVote);

        sliderValue.setText("Selected: " + (baseTemp + tempSlider.getProgress()) + "°");

        tempSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sliderValue.setText("Selected: " + (baseTemp + progress) + "°");
            }

            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });

        submitVote.setOnClickListener(v -> {
            int selectedTemp = baseTemp + tempSlider.getProgress();
            submitVoteValue(selectedTemp);
        });

        // Search bar
        searchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        searchResults.setAdapter(searchAdapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence c, int s, int c2, int a) {}

            @Override
            public void onTextChanged(CharSequence c, int s, int b, int c2) {
                String query = c.toString().trim().toLowerCase();

                if (query.isEmpty()) {
                    searchResults.setVisibility(View.GONE);
                    searchAdapter.clear();
                    return;
                }

                ArrayList<String> matches = new ArrayList<>();

                for (Room room : roomsViewModel.getRooms().getValue()) {
                    if (room.name.toLowerCase().contains(query) ||
                            room.building.toLowerCase().contains(query)) {
                        matches.add(room.name);
                    }
                }

                if (matches.isEmpty()) {
                    searchResults.setVisibility(View.GONE);
                } else {
                    searchAdapter.clear();
                    searchAdapter.addAll(matches);
                    searchResults.setVisibility(View.VISIBLE);
                }
            }

            @Override public void afterTextChanged(Editable e) {}
        });

        searchResults.setOnItemClickListener((parent, view, pos, id) -> {
            String selectedName = searchAdapter.getItem(pos);

            for (Room r : roomsViewModel.getRooms().getValue()) {
                if (r.name.equals(selectedName)) {
                    currentRoom = r;
                    break;
                }
            }

            updateUI();
            searchResults.setVisibility(View.GONE);
            searchBar.setText("");
        });
    }

    private void updateUI() {
        if (currentRoom == null) return;

        TextView tempText = findViewById(R.id.tempText);
        TextView roomLabel = findViewById(R.id.roomNameLabel);
        TextView buildingLabel = findViewById(R.id.buildingLabel);

        tempText.setText(currentRoom.currentTemp + "°");
        roomLabel.setText(currentRoom.name);
        buildingLabel.setText(currentRoom.building);

        View root = findViewById(R.id.drawerLayout);
        int newColor = getBackgroundColorForTemp((int) currentRoom.currentTemp);

        // Background animation??
        int oldColor = ((ColorDrawable) root.getBackground()).getColor();
        ValueAnimator animator = ValueAnimator.ofArgb(oldColor, newColor);
        animator.setDuration(600);
        animator.addUpdateListener(a -> root.setBackgroundColor((int) a.getAnimatedValue()));
        animator.start();
    }

    // Background color
    private int getBackgroundColorForTemp(int temp) {
        if (temp <= 55) return 0xFF0D47A1; // dark blue
        if (temp <= 65) return 0xFF1976D2; // medium blue
        if (temp <= 75) return 0xFF43A047; // green
        if (temp <= 85) return 0xFFFDD835; // yellow
        return 0xFFE53935;                // red
    }

    // Submit vote
    private void submitVoteValue(int temperature) {
        if (currentRoom == null) return;

        roomsViewModel.updateTemperature(currentRoom.id, temperature);
        currentRoom = roomsViewModel.getRoom(currentRoom.id);
        updateUI();

        Toast.makeText(this, "Vote submitted: " + temperature + "°", Toast.LENGTH_SHORT).show();
    }
}
