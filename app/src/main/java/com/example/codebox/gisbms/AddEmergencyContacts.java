package com.example.codebox.gisbms;

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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddEmergencyContacts extends AppCompatActivity implements View.OnClickListener {


    private ProgressDialog progress;
    private LocationManager locationManager;
    private LocationListener listener;
    private EditText name, email, phone, lat, lng;
    private RadioGroup type;
    private Button setLocation, addContact;
    private String AMBULANCE = "ambulance", FIRE = "firebigade";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Emercency");
        setTheme(R.style.AppThemeEmergency);
        setContentView(R.layout.activity_add_emergency_contacts);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        name = (EditText) findViewById(R.id.orgName);
        email = (EditText) findViewById(R.id.orgEmail);
        phone = (EditText) findViewById(R.id.phone);
        lat = (EditText) findViewById(R.id.cLat);
        lng = (EditText) findViewById(R.id.cLng);
        type = (RadioGroup) findViewById(R.id.contactType);

        setLocation = (Button) findViewById(R.id.setLocation);
        addContact = (Button) findViewById(R.id.addContact);
        progress = new ProgressDialog(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("inside","onLocationChange");
                lat.setText("" + location.getLatitude());
                lng.setText("" + location.getLongitude());
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

        setLocation.setOnClickListener(this);
        addContact.setOnClickListener(this);
    }

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

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addContact:
                if (type.getCheckedRadioButtonId() == R.id.ambulanceContact){
                    //Toast.makeText(this, "ambulanceContact", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm")
                            .setMessage("Are you sure?")
                            .setIcon(R.drawable.alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    uploadDetails(AMBULANCE);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }
                if (type.getCheckedRadioButtonId() == R.id.fireContact){
                    new AlertDialog.Builder(this)
                            .setTitle("Confirm")
                            .setMessage("Are you sure?")
                            .setIcon(R.drawable.alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    uploadDetails(FIRE);
                                }})
                            .setNegativeButton(android.R.string.no, null).show();

                }
                break;
            case R.id.setLocation:
                setLocation();
                break;
        }
    }


    private void uploadDetails(final String type){
        progress.setTitle("Uploading");
        progress.setMessage("Please wait...");
        progress.show();

        final String nameContent = name.getText().toString().trim();
        final String emailContent = email.getText().toString().trim();
        final String phoneContent = phone.getText().toString().trim();
        final String latContent = lat.getText().toString().trim();
        final String lngContent = lng.getText().toString().trim();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.ADD_EMERGENCY_CONTACT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();

                        try {
                            JSONObject jo = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),WorkerJobList.class));

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
                params.put("name",nameContent);
                params.put("lat",latContent);
                params.put("lng",lngContent);
                params.put("phone",phoneContent);
                params.put("email",emailContent);
                params.put("type",type);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
