package com.example.uiuctempapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {
    private static final int ADD_CLASS_REQUEST = 1;
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
            startActivityForResult(intent, ADD_CLASS_REQUEST);
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_CLASS_REQUEST && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("CLASS_TITLE");
            String time = data.getStringExtra("CLASS_TIME");
            String location = data.getStringExtra("CLASS_LOCATION");

            if (title != null && time != null && location != null) {
                classList.add(new ClassItem(title, time, location));
                adapter.notifyItemInserted(classList.size() - 1);
            }
        }
    }

}

