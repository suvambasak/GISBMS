package com.example.codebox.gisbms;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.DrmStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog progress;
    private JSONArray dataSet;
    private JSONObject tuple;
    LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        progress = new ProgressDialog(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        progress.setTitle("Searching locations");
        progress.setMessage("Fetching...");
        progress.show();

        final String id = UserInfo.getInstance(getApplicationContext()).getAccoutnId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.ALL_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            dataSet = new JSONArray(response);
                            Log.d("Data","Fetched");
                            int index = dataSet.length();

                            for (int i=0; i<index; i++){
                                tuple = dataSet.getJSONObject(i);

                                location = new LatLng(tuple.getDouble("lat"),tuple.getDouble("lng"));
                                mMap.addMarker(new MarkerOptions().position(location).title( tuple.getString("updateId") + ", "+tuple.getString("issue")+","+tuple.getString("name")+","+tuple.getString("comment")));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,5.0f));
                            }


                            //Toast.makeText(getApplicationContext(), "Total " + index + " updates", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.map), "Total " + index + " updates!", Snackbar.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();
                //Snackbar.make(v, error.getMessage(), Snackbar.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
