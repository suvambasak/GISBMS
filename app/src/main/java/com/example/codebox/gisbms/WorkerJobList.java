package com.example.codebox.gisbms;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkerJobList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ProgressDialog progress;
    ArrayList<JobLocation> jobLocations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Job List");
        setContentView(R.layout.activity_worker_job_list);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        progress = new ProgressDialog(this);
        jobLocations = new ArrayList<JobLocation>();
        fetchJobs();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        JobLocation jobLocation = jobLocations.get(position);
        //Toast.makeText(this, "" + jobLocation.getId() , Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(WorkerJobList.this,Job.class);
        intent.putExtra("id",jobLocation.getId());
        intent.putExtra("lat",jobLocation.getLat());
        intent.putExtra("lng",jobLocation.getLng());
        intent.putExtra("issue",jobLocation.getJobTile());
        startActivity(intent);
        finish();
    }



    private void fetchJobs(){
        progress.setTitle("Fteching jobs");
        progress.setMessage("Wait while...");
        progress.show();

        final String email = UserInfo.getInstance(getApplicationContext()).getEmailId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.JOB_LIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {



                            JSONArray dataSet = new JSONArray(response);
                            Log.d("Data","Fetched");
                            int index = dataSet.length();
                            Log.d("length",""+index);


                            for (int i=0; i<index; i++){
                                JSONObject tuple = dataSet.getJSONObject(i);

                                jobLocations.add(new JobLocation(tuple.getInt("id"),
                                        tuple.getString("issue"),
                                        tuple.getString("lat"),
                                        tuple.getString("lng")));
                            }

                            JobLocationAdapter jobAdapter = new JobLocationAdapter(WorkerJobList.this,jobLocations);
                            ListView jobListview = (ListView) findViewById(R.id.activity_worker_job_list);
                            jobListview.setAdapter(jobAdapter);

                            jobListview.setOnItemClickListener(WorkerJobList.this);


                            //Toast.makeText(getApplicationContext(), "Total " + index + " updates", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(R.id.activity_worker_job_list), "Total " + index + " Job(s) TODO!", Snackbar.LENGTH_LONG).show();
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
                params.put("email",email);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_worker,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                new AlertDialog.Builder(this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure?")
                        .setIcon(R.drawable.alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                makeLogout();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
                break;
            case R.id.addEmergency:
                startActivity(new Intent(getApplicationContext(),AddEmergencyContacts.class));
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
