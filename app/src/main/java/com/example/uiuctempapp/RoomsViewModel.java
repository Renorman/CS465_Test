package com.example.uiuctempapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class RoomsViewModel extends ViewModel {

    private final MutableLiveData<List<Room>> roomsLive = new MutableLiveData<>();
    private final MutableLiveData<List<Room>> favoritesLive = new MutableLiveData<>();

    private final List<Room> rooms = new ArrayList<>();

    public RoomsViewModel() {
        rooms.addAll(RoomData.loadGraingerRooms());
        roomsLive.setValue(rooms);
        updateFavorites();

    }

    // not used anymore
    private void loadDummyRooms() {
        rooms.add(new Room("3117", "Room 3117", "Everitt Laboratory", 70));
        rooms.add(new Room("1320", "Room 1320", "Digital Computer Lab", 72));
        rooms.add(new Room("057", "Room 057", "Grainger Basement", 68));
        rooms.add(new Room("1001", "Room 1001", "ECEB", 65));

        roomsLive.setValue(rooms);
        updateFavorites();
    }

    public LiveData<List<Room>> getRooms() {
        return roomsLive;
    }

    public LiveData<List<Room>> getFavorites() {
        return favoritesLive;
    }

    public Room getRoom(String id) {
        for (Room r : rooms) {
            if (r.id.equals(id)) return r;
        }
        return null;
    }

    public void toggleFavorite(String id) {
        Room r = getRoom(id);
        if (r != null) {
            r.isFavorite = !r.isFavorite;
            updateFavorites();
        }
    }

    private void updateFavorites() {
        List<Room> favs = new ArrayList<>();
        for (Room r : rooms) {
            if (r.isFavorite) favs.add(r);
        }
        favoritesLive.setValue(favs);
    }

    public List<Room> search(String query) {
        List<Room> results = new ArrayList<>();
        for (Room r : rooms) {
            if (r.name.toLowerCase().contains(query.toLowerCase())) {
                results.add(r);
            }
        }
        return results;
    }




    // Temp update --- make sure it works!!
    public void updateTemperature(String id, double newTemp) {
        Room r = getRoom(id);
        if (r != null) {
            r.currentTemp = newTemp;
            r.tempHistory.add(newTemp);
            if (r.tempHistory.size() > 40) {
                r.tempHistory.remove(0);
            }
            roomsLive.setValue(rooms);
        }
    }
}
