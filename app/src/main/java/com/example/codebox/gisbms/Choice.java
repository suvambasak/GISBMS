package com.example.codebox.gisbms;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class Choice extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private Button maps, checkIn,worker,upload,displayCount;
    private ProgressDialog progress;
    TextView countSubText;

    private Vibrator vitrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);

        Intent intent = getIntent();
        boolean showInfo = intent.getBooleanExtra("showInfo",false);
        if (showInfo){
            new AlertDialog.Builder(this)
                    .setTitle("Unser Info")
                    .setMessage("Long tap on Check In button for Emergency utility!")
                    .setIcon(R.drawable.alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            Snackbar.make(findViewById(R.id.choiceView), "Welcome!", Snackbar.LENGTH_LONG).show();
                        }}).show();
        }

        vitrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        displayCount = (Button) findViewById(R.id.count);
        countSubText = (TextView) findViewById(R.id.countSubText);
        maps = (Button) findViewById(R.id.maps);
        checkIn = (Button) findViewById(R.id.checkin);
        worker = (Button) findViewById(R.id.worker);
        upload = (Button) findViewById(R.id.upload);


        progress = new ProgressDialog(this);

        maps.setOnClickListener(this);
        checkIn.setOnClickListener(this);
        worker.setOnClickListener(this);
        upload.setOnClickListener(this);
        displayCount.setOnClickListener(this);

        checkIn.setOnLongClickListener(this);

        new IssueCount().start();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.checkin:
                startActivity(new Intent(this,CheckIn.class));
                break;
            case R.id.maps:
                startActivity(new Intent(this,MapsActivity.class));
                break;
            case R.id.count:
                if (displayCount.getText().toString().equals("0")) {break;}
                startActivity(new Intent(this,MapsActivity.class));
                break;

            case R.id.worker:
                checkWorker(v);
                break;
            case R.id.upload:
                new Uploader().execute();
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        vitrator.vibrate(33);
        checkIn.setText("Emergency");
        Toast.makeText(this, "Emergency!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this,Emergency.class));
        return true;
    }

    @Override
    protected void onResume() {
        checkIn.setText("Check In");
        super.onResume();
    }

    private class Uploader extends AsyncTask<Void,Void,Void>{
        DbHelper mDbHelper;
        SQLiteDatabase db;
        String[] projection = {"issue", "comment", "time", "date", "lat", "lng", "accuracy"};

        public Uploader(){
            mDbHelper = new DbHelper(getApplicationContext());
            db = mDbHelper.getReadableDatabase();
        }

        @Override
        protected Void doInBackground(Void... params) {
            Cursor cursor = db.query("gisInfo",projection,null,null,null,null,null);

            while (cursor.moveToNext()){

                final String id = UserInfo.getInstance(getApplicationContext()).getAccoutnId();

                final String issueContent = cursor.getString(0);
                final String commentContent = cursor.getString(1);
                final String timeContent = cursor.getString(2);
                final String dataContent = cursor.getString(3);
                final String latContent = cursor.getString(4);
                final String lngContent = cursor.getString(5);
                final String accuracyContent = cursor.getString(6);


//                System.out.println(id);
//                System.out.println(issueContent);
//                System.out.println(commentContent);
//                System.out.println(timeContent);
//                System.out.println(dataContent);
//                System.out.println(latContent);
//                System.out.println(lngContent);
//                System.out.println(accuracyContent);


                StringRequest stringRequest = new StringRequest(Request.Method.POST,
                        Domain.UPDATE_ISSUE,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progress.dismiss();

                                try {
                                    JSONObject jo = new JSONObject(response);

                                    Toast.makeText(getApplicationContext(), "Uploading...", Toast.LENGTH_SHORT).show();

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
//                        Snackbar.make(v, error.getMessage(), Snackbar.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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

                RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            db.execSQL("delete from gisInfo");
        }
    }


    private void checkWorker(final View v){
        progress.setTitle("Checking");
        progress.setMessage("Wait...");
        progress.show();

        final String email = UserInfo.getInstance(getApplicationContext()).getEmailId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.WORKER_CHECK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getString("message").equals("Go")){
                                startActivity(new Intent(getApplicationContext(),WorkerJobList.class));
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
                params.put("email",email);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private class IssueCount extends Thread{

        public void run(){

            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    Domain.ISSUE_COUNT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progress.dismiss();

                            try {
                                JSONObject count = new JSONObject(response);
//                                Toast.makeText(getApplicationContext(), count.getString("count"), Toast.LENGTH_SHORT).show();

                                int limit = Integer.parseInt(count.getString("count"));


                                new DisplayCount(limit).start();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    return params;
                }
            };

            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }
    }

    private class DisplayCount extends Thread{
        int LIMIT;
        DisplayCount(int limit){
            LIMIT = limit;
            Choice.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    countSubText.setText("Issues in Progress...!");

                }
            });
        }

        public void run(){

            for(int i=1; i<=LIMIT; i++){
                try {
                    Thread.sleep(50);
                    final int counter  = i;
                    Choice.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            displayCount.setText(""+counter);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
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
