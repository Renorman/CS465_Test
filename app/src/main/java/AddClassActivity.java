package com.example.uiuctempapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class AddClassActivity extends AppCompatActivity {

    private EditText editClassName, editClassTime, editClassLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        editClassName = findViewById(R.id.editClassName);
        editClassTime = findViewById(R.id.editClassTime);
        editClassLocation = findViewById(R.id.editClassLocation);

        ImageButton backButton = findViewById(R.id.backButtonAddClass);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button saveButton = findViewById(R.id.saveButton);

        backButton.setOnClickListener(v -> finish());
        cancelButton.setOnClickListener(v -> finish());

        saveButton.setOnClickListener(v -> {
            String title = editClassName.getText().toString().trim();
            String time = editClassTime.getText().toString().trim();
            String location = editClassLocation.getText().toString().trim();

            if (title.isEmpty() || time.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent result = new Intent();
            result.putExtra("CLASS_TITLE", title);
            result.putExtra("CLASS_TIME", time);
            result.putExtra("CLASS_LOCATION", location);

            setResult(RESULT_OK, result);
            finish();
        });
    }
}
