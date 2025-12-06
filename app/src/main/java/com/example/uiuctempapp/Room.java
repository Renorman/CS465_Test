package com.example.uiuctempapp;

import java.util.ArrayList;
import java.util.List;

public class Room {

    public String id;
    public String name;
    public String building;
    public double currentTemp;

    public List<Double> tempHistory;

    public boolean isFavorite = false;

    public Room(String id, String name, String building, double currentTemp) {
        this.id = id;
        this.name = name;
        this.building = building;
        this.currentTemp = currentTemp;

        tempHistory = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            tempHistory.add(currentTemp);
        }
    }
}
