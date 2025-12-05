package com.example.uiuctempapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    ArrayList<ClassItem> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Back button
        ImageButton backButton = findViewById(R.id.backButtonSchedule);
        backButton.setOnClickListener(v -> finish());

        // Initialize list
        classList = new ArrayList<>();

        // Example classes (from your sketch)
        classList.add(new ClassItem(
                "User Interface Design (LEC)",
                "Mon, Wed 9:30–10:45 AM",
                "Digital Computer Lab, Rm. 1320"
        ));

        classList.add(new ClassItem(
                "User Interface Design (LAB)",
                "Fri 3:30–4:50 PM",
                "Everitt Laboratory, Rm. 3117"
        ));

        classList.add(new ClassItem(
                "Chemistry Office Hours",
                "Mon 5:00–6:00 PM",
                "Noyes Laboratory, Rm. 100"
        ));

        // RecyclerView
        recyclerView = findViewById(R.id.scheduleRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheduleAdapter(classList);
        recyclerView.setAdapter(adapter);

        // "+" button → Add new class
        findViewById(R.id.addClassButton).setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, AddClassActivity.class);
            startActivity(intent);
        });
    }
}

