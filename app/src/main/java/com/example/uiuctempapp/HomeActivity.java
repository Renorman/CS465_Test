package com.example.uiuctempapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    TextView mapButton, scoreButton, editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);  // your XML filename

        mapButton = findViewById(R.id.mapButton);
//        scoreButton = findViewById(R.id.scoreButton);
//        editButton = findViewById(R.id.editButton);

        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(HomeActivity.this, MapsActivity.class);
                startActivity(mapIntent);
            }
        });
//
//        scoreButton.setOnClickListener(v ->
//                Toast.makeText(this, "Score", Toast.LENGTH_SHORT).show()
//        );
//
//        editButton.setOnClickListener(v ->
//                Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show()
//        );
    }
}