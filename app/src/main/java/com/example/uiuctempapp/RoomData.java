package com.example.uiuctempapp;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomData {

    public static List<Room> loadGraingerRooms() {
        List<Room> list = new ArrayList<>();

        MapsActivity helper = new MapsActivity();
        List<Map<LatLng, String>> floors = helper.getGraingerRooms();

        int idCounter = 1;

        for (Map<LatLng, String> floor : floors) {
            for (Map.Entry<LatLng, String> entry : floor.entrySet()) {

                String name = entry.getValue();
                if (name.trim().isEmpty()) continue;

                Room r = new Room("room_" + idCounter, name, "Grainger Library", 70);

                list.add(r);
                idCounter++;
            }
        }

        return list;
    }
}
