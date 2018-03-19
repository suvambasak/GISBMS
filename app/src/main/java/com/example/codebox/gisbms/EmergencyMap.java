package com.example.codebox.gisbms;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EmergencyMap extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private Button emergencyCompelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Emergency Service");
        setTheme(R.style.AppThemeEmergency);
        setContentView(R.layout.activity_emergency_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        emergencyCompelete = (Button) findViewById(R.id.emergencyCompelete);
        emergencyCompelete.setOnClickListener(this);
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

        // Add a marker in Sydney and move the camera
        LatLng EmgLocation = new LatLng(
                Double.parseDouble(UserInfo.getInstance(getApplicationContext()).getEmergencyLat()),
                Double.parseDouble(UserInfo.getInstance(getApplicationContext()).getEmergencyLng()));

        mMap.addMarker(new MarkerOptions().position(EmgLocation).title(
                UserInfo.getInstance(getApplicationContext()).getEmergencyTitle()
        ));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EmgLocation,10.3f));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emergencyCompelete:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Complete?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
//                                Toast.makeText(getApplicationContext(), "job done", Toast.LENGTH_SHORT).show();
                                UserInfo.getInstance(getApplicationContext()).completeEmergencyService();
                                finish();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

                break;
        }
    }
}
