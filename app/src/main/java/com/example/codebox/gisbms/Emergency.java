package com.example.codebox.gisbms;

import android.*;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Emergency extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView emergencyContact;
    private ArrayList<String> contactType;
    private LocationManager locationManager;
    private LocationListener listener;
    private EditText eLat, eLng, eAccuracy;
    private Button eSetLocation, infomAmbulance, informFirebrigade;
    private ProgressDialog progress;
    private String AMBULANCE = "Ambulance", FIRE = "Fire";
    private int DISPLAY_TIME = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Emergency");
        setTheme(R.style.AppThemeEmergency);
        setContentView(R.layout.activity_emergency);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        eLat = (EditText) findViewById(R.id.eLat);
        eLng = (EditText) findViewById(R.id.eLng);
        eAccuracy = (EditText) findViewById(R.id.eAccuracy);
        informFirebrigade = (Button) findViewById(R.id.informFirebrigade);
        infomAmbulance = (Button) findViewById(R.id.infomAmbulance);
        eSetLocation = (Button) findViewById(R.id.eSetLocation);
        progress = new ProgressDialog(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("inside","onLocationChange");
                eLat.setText("" + location.getLatitude());
                eLng.setText("" + location.getLongitude());
                eAccuracy.setText("" + location.getAccuracy());
                Log.i("Location ::","called");
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        setLocation();


        contactType = new ArrayList<String>();
        contactType.add("Ambulance");
        contactType.add("Fire Brigade");

        ArrayAdapter<String> contactItem = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactType);

        emergencyContact = (ListView) findViewById(R.id.emergencyContact);
        emergencyContact.setAdapter(contactItem);
        emergencyContact.setOnItemClickListener(this);

        informFirebrigade.setOnClickListener(this);
        infomAmbulance.setOnClickListener(this);
        eSetLocation.setOnClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //String selectedOption = contactType.get(position);
        Intent intent = new Intent(this,Contacts.class);
        //Toast.makeText(this, ""+contactType.get(position), Toast.LENGTH_SHORT).show();
        intent.putExtra("title",contactType.get(position));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                setLocation();
                break;
            default:
                break;
        }
    }

    private void setLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }

        Log.d("inside","setLocation()");
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 0, listener);
        else
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, listener);

    }

    protected void onPause() {
        locationManager.removeUpdates(listener);
        super.onPause();
    }

    private void infrom(final String infotmTo){
        progress.setTitle("Alarm");
        progress.setMessage("Please wait...");
        progress.show();

        final String id = UserInfo.getInstance(getApplicationContext()).getAccoutnId();
        final String lat = eLat.getText().toString().trim();
        final String lng = eLng.getText().toString().trim();


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.ALARM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();

                        try {
                            JSONObject jo = new JSONObject(response);

                            if (jo.getString("message").equals("Calling...")){
                                Toast.makeText(getApplicationContext(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(Emergency.this,Choice.class));
                                        Emergency.this.finish();
                                    }
                                },DISPLAY_TIME);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();
//                Snackbar.make(v, error.getMessage(), Snackbar.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("lat",lat);
                params.put("lng",lng);
                params.put("type",infotmTo);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.eSetLocation:
                setLocation();
                Log.i("Set Location emer","Call");
                break;
            case R.id.infomAmbulance:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure?")
                        .setIcon(R.drawable.alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                infrom(AMBULANCE);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case R.id.informFirebrigade:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure?")
                        .setIcon(R.drawable.alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                infrom(FIRE);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
