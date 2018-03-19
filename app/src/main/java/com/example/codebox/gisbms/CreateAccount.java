package com.example.codebox.gisbms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class CreateAccount extends AppCompatActivity implements View.OnClickListener {
    private EditText nameEditText, emailEditText, aadhaarEditText, password, confirmPassword;
    private TextView passwordNotMatch;
    private Button submit;
    private ProgressDialog progress;
    private final int DISPLAY_TIME = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        aadhaarEditText = (EditText) findViewById(R.id.aadhaarEditText);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        passwordNotMatch = (TextView) findViewById(R.id.passwordNotMatch);
        submit = (Button) findViewById(R.id.submit);
        progress = new ProgressDialog(this);

        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == submit){
            if (password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())){

                if (password.getText().length()<3 ){
                    passwordNotMatch.setText("Password is too short");
                }else{
                    passwordNotMatch.setText("");
                    createUser(v);
                }
            }else{
                passwordNotMatch.setText("Check Password");
            }
        }
    }

    private void createUser(final View v){

        //show progress.
        progress.setMessage("Creating account...");
        progress.show();

        //getting values
        final String name = nameEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String aadhaar = aadhaarEditText.getText().toString().trim();
        final String pwd = password.getText().toString().trim();

        //making request.
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.NEW_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();

                        try {
                            JSONObject jo = new JSONObject(response);
                            Snackbar.make(v, jo.getString("message"), Snackbar.LENGTH_LONG).show();
                            if (jo.getString("message").equals("Successfull")){
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(CreateAccount.this,Login.class));
                                        CreateAccount.this.finish();
                                    }
                                },DISPLAY_TIME);
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

                params.put("name",name);
                params.put("email",email);
                params.put("aadhaar",aadhaar);
                params.put("password",pwd);

                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

}
