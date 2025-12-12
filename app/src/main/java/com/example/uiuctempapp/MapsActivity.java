package com.example.uiuctempapp;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.IndoorLevel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.uiuctempapp.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.Toast;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    // variables
    // GoogleMap-based variables
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    // Slider-based variables
    private int defaultTemp;
    private SeekBar calibrateTempBar;
    private TextView tempBarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set up slider
        calibrateTempBar = findViewById(R.id.calibrateBar);
        tempBarText = findViewById(R.id.tempText);

        tempBarText.setText(""+(calibrateTempBar.getProgress() * 10) + " Degrees");

        defaultTemp = calibrateTempBar.getProgress() * 10;

        // Back-to-home button
        ImageButton backButton = findViewById(R.id.MapstoBack);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(MapsActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // Calls pop-up text explanation for slider
        showDialog();
    }

    // Set up dialog box for the slider calibration explanation
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Calibration");
        builder.setMessage("Use the SLIDER in the upper-right corner to adjust your preferred temperature!");
        builder.setIcon(R.drawable.neutral_reaction);

        builder.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Setting up UI Elements
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // Establish all existing locations & markers
        List<Map<LatLng, String>> graingerRooms = getGraingerRooms();
        List<List<Marker>> graingerMarkers = makeMarkers(graingerRooms);


        //Set Listener Events
        calibrateTempBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tempBarText.setText(""+ (progress * 10) + " Degrees");

                defaultTemp = progress * 10;
                calibrateTemp(graingerMarkers);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Indoor-based listening events
        mMap.setOnIndoorStateChangeListener(new GoogleMap.OnIndoorStateChangeListener() {
            int currLevel = -1; // Essentially setting it to NULL

            // When a new building is in focus, update currLevel and show relevant markers
            @Override
            public void onIndoorBuildingFocused() {
                IndoorBuilding currBuilding = mMap.getFocusedBuilding();
                if (currBuilding != null) {
                    currLevel = currBuilding.getActiveLevelIndex();

                    showMarkers(graingerMarkers.get(currLevel));
                }

            }

            // When a new floor is clicked, hide all markers from previous floor, and show new markers
            @Override
            public void onIndoorLevelActivated(@NonNull IndoorBuilding currBuilding) {
                if (currLevel != -1) {
                    hideMarkers(graingerMarkers.get(currLevel));
                    currLevel = currBuilding.getActiveLevelIndex();
                    showMarkers(graingerMarkers.get(currLevel));
                }
            }
        });


        LatLng graingerLocation = new LatLng(40.11233883459755, -88.22689025040142);


        mMap.addMarker(new MarkerOptions().position(graingerLocation).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(graingerLocation, 20));

    }

    // Returns a List of lists for all Markers organized by each floor of the building
    public List<List<Marker>> makeMarkers(List<Map<LatLng, String>> allFloors) {
        // Final list to be returned
        List<List<Marker>> allMarkers = new ArrayList<>();

        // Iterate through each map/floor in the list of all floors
        for (Map<LatLng, String> floor : allFloors) {
            // List containing all markers on this floor
            List<Marker> floorMarkers = new ArrayList<>();
            // Iterate through each room (coordinates, name) in each floor's map of rooms
            for (Map.Entry<LatLng, String> room : floor.entrySet()) {
                // Find a random temperature between -10 - 110 degrees
                int randomTemp = -10 + (int) (Math.random() * ((110 - (-10)) + 1));


                Marker currMarker = mMap.addMarker(
                        new MarkerOptions()
                        .position(room.getKey())
                        .title(room.getValue())
                        .snippet(randomTemp + " degrees")
                );

                currMarker.setTag(randomTemp); // save for heatmap
                currMarker.setVisible(false); // hide on initialization

                // Set icon image based on room's randomly generated temperature
                updateIcon(currMarker, randomTemp);

                floorMarkers.add(currMarker);
            }
            allMarkers.add(floorMarkers);
        }
        return allMarkers;
    }


    // Updates all markers to reflect new defaultTemp
    public void calibrateTemp(List<List<Marker>> allFloors) {
        Log.d("PROGRESS", "running...");
        // Iterate through each map/floor in the list of all floors
        for (List<Marker> floor : allFloors) {
            for (Marker room : floor) {
                updateIcon(room, (int) room.getTag());
            }
        }
    }

    // Compares marker's temperature to passed in temperature, updates icon accordingly
    public void updateIcon(Marker marker, int markerTemp) {
        if (markerTemp > (defaultTemp + 10)) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.drawable.hot_reaction)));
        } else if (markerTemp < (defaultTemp - 10)) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.drawable.cold_reaction)));
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(getBitMap(R.drawable.neutral_reaction)));
        }

    }

    // Goes through Marker List and sets all to not visible
    public void hideMarkers(List<Marker> markers) {
        for (Marker marker : markers) {
            marker.setVisible(false);
        }
    }

    // Goes through Marker List and sets all to visible
    public void showMarkers(List<Marker> markers) {
        for (Marker marker : markers) {
            marker.setVisible(true);
        }
    }

    // Returns a scaled Bitmap of image id
    public Bitmap getBitMap(int id) {
        Bitmap initialBitmap = BitmapFactory.decodeResource(getResources(), id);
        // Make bitmap the size of a coordinate marker
        return Bitmap.createScaledBitmap(initialBitmap, 100, 100, false);
    }

    // Returns a List of all the Rooms (coordinates, name) for each floors in Grainger
    public List<Map<LatLng, String>> getGraingerRooms() {

        // Grainger's Basement Floor
        Map<LatLng, String> floorB1 = Map.of(
                new LatLng(40.11245281180666, -88.22729657085846), "CBTF Grainger - 057",
                new LatLng(40.11243574394597, -88.22692674123637),"Microforms",
                new LatLng(40.112413799547376, -88.22664458674019), "Engineering Documents",
                new LatLng(40.112511330153616, -88.22655691161425), "EWS IT Helpdesk",
                new LatLng(40.11241252187332, -88.22633188907322), "EWS Computer Lab"
                );
        // Grainger's First Floor
        Map<LatLng, String> floor1 = Map.of(
                new LatLng(40.11252104703671, -88.2270424028703), "Food Area",
                new LatLng(40.112550370874764, -88.22675708719282), "Espresso Royale Cafe- Grainger Engineering Library",
                new LatLng(40.112455499587114, -88.22679758968717), "Mathematics Library",
                new LatLng(40.11243601220192, -88.22708458605916), "Circulation Desk",
                new LatLng(40.1124483200248, -88.22701484862297), "Reference Desk",
                new LatLng(40.112477038269496, -88.227207967677), "New Books",
                new LatLng(40.11233960084567, -88.22728843394954), "New Periodicals",
                new LatLng(40.112460627845444, -88.22646767796985), " "
                );

        // Grainger's Second Floor
        Map<LatLng, String> floor2 = Map.of(
                new LatLng(40.11242471900297, -88.22686456932834), " ",
                new LatLng(40.11240520359445, -88.2272792306048), " ",
                new LatLng(40.11241130216022, -88.22645309775403), " ",
                new LatLng(40.112528394516836, -88.227057546307), "Room 229",
                new LatLng(40.11255034931127, -88.2269841831581), "Room 231"
                );

        // Grainger's Third Floor
        Map<LatLng, String> floor3 = Map.of(
                new LatLng(40.112428378141445, -88.22686935388154), " ",
                new LatLng(40.11239910502814, -88.22720746230695), " ",
                new LatLng(40.112408862733986, -88.22654081456251), " ",
                new LatLng(40.11252961422783, -88.22704319264743), " "
        );

        // Grainger's Fourth Floor
        Map<LatLng, String> floor4 = Map.of(
                new LatLng(40.11244680623018, -88.22685853520127), " ",
                new LatLng(40.112426071115166, -88.22640081294611), " ",
                new LatLng(40.112449245655064, -88.22729392954155), " ",
                new LatLng(40.11248339759419, -88.2271870745203), "Room 404",
                new LatLng(40.112284584279344, -88.22719026422243), "Room 414",
                new LatLng(40.11227360684012, -88.22741194852024), "Room 415",
                new LatLng(40.112523648071836, -88.22743746613723), "Room 403",
                new LatLng(40.11241265427269, -88.22739281030748), "Room 407",
                new LatLng(40.112421192263675, -88.22715836720117), "Room 408",
                new LatLng(40.11255536055261, -88.22699728724379), "Room 429"
                );

        // Create a List of each floor's map
        List<Map<LatLng, String>> allFloors =  new ArrayList<>() {{
            // Insert highest-floor to lowest floor
            add(floor4);
            add(floor3);
            add(floor2);
            add(floor1);
            add(floorB1);
        }};
        return allFloors;
    }

}