package com.example.uiuctempapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // -----------------------------
        // BACK BUTTON → go to home screen
        // -----------------------------
        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // -----------------------------
        // SCHEDULE ICON → go to schedule screen
        // -----------------------------
        LinearLayout scheduleSection = findViewById(R.id.scheduleSection);
        // IMPORTANT: Your XML must have android:id="@+id/scheduleSection"

        scheduleSection.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ScheduleActivity.class);
            startActivity(intent);
        });
    }
}
