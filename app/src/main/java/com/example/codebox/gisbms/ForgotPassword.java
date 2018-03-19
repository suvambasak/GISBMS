package com.example.codebox.gisbms;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {

    private TextView activityName, userHelp;
    private Button next;
    private EditText textInput;
    private int state = 0;
    private String code = null, pwd = null, confirmPwd = null, email = null;
    private int DISPLAY_TIME = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        activityName = (TextView) findViewById(R.id.activityName);
        userHelp = (TextView) findViewById(R.id.userHelp);
        next = (Button) findViewById(R.id.nextbutton);
        textInput = (EditText) findViewById(R.id.textInput);

        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(state){
            case 0:
                forgotPwd(v);
                break;
            case 1:
                Toast.makeText(this, ""+state, Toast.LENGTH_SHORT).show();
                textInput.setEnabled(false);
                next.setEnabled(false);
                code = textInput.getText().toString();
                Log.i("Code ",code);

                textInput.setEnabled(true);
                next.setEnabled(true);
                activityName.setText("Set new password");
                userHelp.setText("");
                textInput.setText("");
                textInput.setHint("password");
                textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                textInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                ++state;
                break;

            case 2:
                Toast.makeText(this, ""+state, Toast.LENGTH_SHORT).show();
                textInput.setEnabled(false);
                next.setEnabled(false);
                pwd = textInput.getText().toString();
                Log.i("Pwd",pwd);

                textInput.setEnabled(true);
                next.setEnabled(true);
                activityName.setText("Confirm your password");
                userHelp.setText("Type again");
                textInput.setText("");
                textInput.setHint("Confirm password");
                textInput.setInputType(InputType.TYPE_CLASS_TEXT);
                textInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                next.setText("Done");
                ++state;
                break;
            case 3:
                Toast.makeText(this, ""+state, Toast.LENGTH_SHORT).show();
                textInput.setEnabled(false);
                next.setEnabled(false);
                confirmPwd = textInput.getText().toString();
                Log.i("confirm pwd",confirmPwd);

                if (pwd.equals(confirmPwd)){
                    userHelp.setText("Password Matched! Please wait...");
                    changePwd();
//                    Toast.makeText(this, "okay", Toast.LENGTH_SHORT).show();
                } else {
                    textInput.setEnabled(true);
                    next.setEnabled(true);
                    userHelp.setText("Password not matched!");
                    textInput.setText("");
                }
                break;
        }
    }

    private void changePwd(){
        final String mEmail = email;
        final String mCode = code;
        final String mPwd = pwd;

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.RESET_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getString("message").equals("Done")){
                                activityName.setText("Password Changed!");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(ForgotPassword.this,Login.class));
                                        ForgotPassword.this.finish();
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

                //Snackbar.make(v, error.getMessage(), Snackbar.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("email",mEmail);
                params.put("code",mCode);
                params.put("newPwd",mPwd);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void forgotPwd(final View v){
        final String mEmail = textInput.getText().toString().trim();
        textInput.setEnabled(false);
        next.setEnabled(false);
        email = mEmail;

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.FORGOT_PASSWORD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jo = new JSONObject(response);
                            if(jo.getString("message").equals("next")){
                                ++state;
                                textInput.setText("");
                                textInput.setEnabled(true);
                                next.setEnabled(true);

                                activityName.setText("6 Digit code");
                                userHelp.setText("Check your Email");
                                textInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                                textInput.setHint("Code");
                            }
//                             Toast.makeText(getApplicationContext(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Snackbar.make(v, error.getMessage(), Snackbar.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                params.put("email",mEmail);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


}
