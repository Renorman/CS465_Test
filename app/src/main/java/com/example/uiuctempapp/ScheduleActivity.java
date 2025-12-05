package com.example.uiuctempapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import java.util.ArrayList;

public class ScheduleActivity extends AppCompatActivity {

    private static final int ADD_EDIT_CLASS_REQUEST = 1;

    RecyclerView recyclerView;
    ScheduleAdapter adapter;
    ArrayList<ClassItem> classList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // ---- Back arrow ----
        ImageButton backButton = findViewById(R.id.backButtonSchedule);
        backButton.setOnClickListener(v -> finish());

        // ---- RecyclerView ----
        recyclerView = findViewById(R.id.scheduleRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ---- Initial schedule items (from your sketch) ----
        classList = new ArrayList<>();
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

        // ---- Adapter: tap to EDIT, long-press to DELETE ----
        adapter = new ScheduleAdapter(classList, new ScheduleAdapter.OnItemInteractionListener() {
            @Override
            public void onItemClick(int position) {
                // Edit existing class
                ClassItem item = classList.get(position);
                Intent intent = new Intent(ScheduleActivity.this, AddClassActivity.class);
                intent.putExtra("MODE", "EDIT");
                intent.putExtra("INDEX", position);
                intent.putExtra("CLASS_TITLE", item.title);
                intent.putExtra("CLASS_TIME", item.time);
                intent.putExtra("CLASS_LOCATION", item.location);
                startActivityForResult(intent, ADD_EDIT_CLASS_REQUEST);
            }

            @Override
            public void onItemLongClick(int position) {
                // Confirm delete
                ClassItem item = classList.get(position);

                new AlertDialog.Builder(ScheduleActivity.this)
                        .setTitle("Delete class")
                        .setMessage("Remove \"" + item.title + "\" from your schedule?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            classList.remove(position);
                            adapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        // ---- + button: add new class ----
        ImageButton addButton = findViewById(R.id.addClassButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ScheduleActivity.this, AddClassActivity.class);
            intent.putExtra("MODE", "ADD");
            startActivityForResult(intent, ADD_EDIT_CLASS_REQUEST);
        });
    }

    // Receive result from AddClassActivity (both add & edit)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EDIT_CLASS_REQUEST && resultCode == RESULT_OK && data != null) {
            String mode = data.getStringExtra("MODE");
            String title = data.getStringExtra("CLASS_TITLE");
            String time = data.getStringExtra("CLASS_TIME");
            String location = data.getStringExtra("CLASS_LOCATION");
            int index = data.getIntExtra("INDEX", -1);

            if (title == null || time == null || location == null) return;

            if ("EDIT".equals(mode) && index >= 0 && index < classList.size()) {
                // Update existing class
                ClassItem item = classList.get(index);
                item.title = title;
                item.time = time;
                item.location = location;
                adapter.notifyItemChanged(index);
            } else {
                // Add new class
                classList.add(new ClassItem(title, time, location));
                adapter.notifyItemInserted(classList.size() - 1);
            }
        }
    }
}
