package com.example.codebox.gisbms;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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

public class Job extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private int dbId;
    private double lat,lng;
    private String issue;
    private Button jobDone;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Job Location");
        setContentView(R.layout.activity_job);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        dbId = intent.getIntExtra("id",0);
        lat = Double.parseDouble(intent.getStringExtra("lat"));
        lng = Double.parseDouble(intent.getStringExtra("lng"));
        issue = intent.getStringExtra("issue");

        jobDone = (Button) findViewById(R.id.job_done);
        progress = new ProgressDialog(this);
        jobDone.setOnClickListener(this);

        Log.i("dbId",""+dbId);
        Log.i("lat",""+lat);
        Log.i("lng",""+lng);
        Log.i("issue",""+issue);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng plot = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(plot).title(issue));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(plot,13.3f));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.job_done:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Have you completed this one?")
                        .setIcon(R.drawable.alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
//                                Toast.makeText(getApplicationContext(), "job done", Toast.LENGTH_SHORT).show();
                                jobComplete();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                break;
        }
    }

    private void jobComplete(){
        progress.setMessage("Please wait...");
        progress.show();

        final String id = ""+dbId;
        final String email = UserInfo.getInstance(getApplicationContext()).getEmailId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.JOB_DONE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            JSONObject serverReply = new JSONObject(response);
                            if (serverReply.getString("message").equals("Done")){
                                onBackPressed();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();
                Snackbar.make(findViewById(R.id.jobLayout), error.getMessage(), Snackbar.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("email",email);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,WorkerJobList.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                makeLogout();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void makeLogout() {
        progress.setMessage("Logging out...");
        progress.show();

        final String id = UserInfo.getInstance(getApplicationContext()).getAccoutnId();


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.LOGOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();

                        try {
                            JSONObject jo = new JSONObject(response);
//                            Snackbar.make(v, jo.getString("message"), Snackbar.LENGTH_LONG).show();

                            if(jo.getString("message").equals("Logout")){

                                Log.i("LOGOUT","ok");

                                UserInfo.getInstance(getApplicationContext()).logOut();
                                finish();
                                startActivity(new Intent(getApplicationContext(), Login.class));
                            }
//                             Toast.makeText(getApplicationContext(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progress.hide();
//                Snackbar.make(v, error.getMessage(), Snackbar.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
