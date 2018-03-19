package com.example.codebox.gisbms;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CheckIn extends AppCompatActivity implements View.OnClickListener {

    private Spinner issue;
    private EditText comment, time, date, lat, lng, accuracy;
    private Button update,setLocation, saveInfo;
    private LocationManager locationManager;
    private LocationListener listener;
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Check IN");
        setContentView(R.layout.activity_check_in);

        if (getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        update = (Button) findViewById(R.id.update);
        setLocation = (Button) findViewById(R.id.setLocation);
        saveInfo = (Button) findViewById(R.id.saveInfo);
        comment = (EditText) findViewById(R.id.comment);
        time = (EditText) findViewById(R.id.time);
        date = (EditText) findViewById(R.id.date);
        lat = (EditText) findViewById(R.id.lat);
        lng = (EditText) findViewById(R.id.lng);
        accuracy = (EditText) findViewById(R.id.accuracy);
        issue = (Spinner) findViewById(R.id.issue);
        progress = new ProgressDialog(this);

        //Setting time and date.
        final Calendar calendar = Calendar.getInstance();
        time.setText(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) +":"+ calendar.get(Calendar.SECOND));
        date.setText(calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DAY_OF_MONTH));

        //Setting location.
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("inside","onLocationChange");
                lat.setText("" + location.getLatitude());
                lng.setText("" + location.getLongitude());
                accuracy.setText("" + location.getAccuracy());
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
        update.setOnClickListener(this);
        setLocation.setOnClickListener(this);
        saveInfo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.update:
               if (issue.getSelectedItem().toString().equals("Select")){
                   Snackbar.make(v, "Select one issue.", Snackbar.LENGTH_LONG).show();
                   break;
               }

               if (lat.getText().toString().equals("") || lng.getText().toString().equals("")){
                   Snackbar.make(v, "Please check your location!", Snackbar.LENGTH_LONG).show();
                   break;
               }
               update(v);
               break;
            case R.id.setLocation:
                setLocation();
                Log.i("Set Location","Call");
                break;
            case R.id.saveInfo:
                if (issue.getSelectedItem().toString().equals("Select")){
                    Snackbar.make(v, "Select one issue.", Snackbar.LENGTH_LONG).show();
                    break;
                }
                if (lat.getText().toString().equals("") || lng.getText().toString().equals("")){
                    Snackbar.make(v, "Please check your location!", Snackbar.LENGTH_LONG).show();
                    break;
                }
                progress.setMessage("Saving info...");
                progress.show();

                DbHelper mDbHelper = new DbHelper(getApplicationContext());
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                values.put("issue",issue.getSelectedItem().toString());
                values.put("comment",comment.getText().toString());
                values.put("time",time.getText().toString());
                values.put("date",date.getText().toString());
                values.put("lat",lat.getText().toString());
                values.put("lng",lng.getText().toString());
                values.put("accuracy",accuracy.getText().toString());

                long newRowId = db.insert("gisInfo", null, values);

                progress.dismiss();

                Log.i("DB update ID :: ", ""+newRowId);

                Toast.makeText(this, "Infromation saved.", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }

    private void update(final View v) {
        progress.setMessage("Updating your info...");
        progress.show();

        final String id = UserInfo.getInstance(getApplicationContext()).getAccoutnId();
        final String issueContent = issue.getSelectedItem().toString();
        final String commentContent = comment.getText().toString();
        final String timeContent = time.getText().toString();
        final String dataContent = date.getText().toString();
        final String latContent = lat.getText().toString();
        final String lngContent = lng.getText().toString();
        final String accuracyContent = accuracy.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.UPDATE_ISSUE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();

                        try {
                            JSONObject jo = new JSONObject(response);
                            Snackbar.make(v, jo.getString("message"), Snackbar.LENGTH_LONG).show();
                            if (jo.getString("message").equals("updated")){
                                startActivity(new Intent(getApplicationContext(),Choice.class));
                                finish();
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
                Snackbar.make(v, error.getMessage(), Snackbar.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("userId",id);
                params.put("issue",issueContent);
                params.put("comment",commentContent);
                params.put("time",timeContent);
                params.put("date",dataContent);
                params.put("lat",latContent);
                params.put("lng",lngContent);
                params.put("accuracy",accuracyContent);

                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
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
    protected void onPause() {
        locationManager.removeUpdates(listener);
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
