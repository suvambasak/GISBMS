package com.example.codebox.gisbms;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button login,newUser, forgotPassword;
    private EditText emailEditText, passwordEditText;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(UserInfo.getInstance(getApplicationContext()).isLoggedIn()){
            finish();
            startActivity(new Intent(getApplicationContext(),Choice.class));
        }

        newUser = (Button) findViewById(R.id.newUser);
        login = (Button) findViewById(R.id.login);
        forgotPassword = (Button) findViewById(R.id.forgotPassword);
        emailEditText = (EditText) findViewById(R.id.emailEdittext);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        progress = new ProgressDialog(this);

        newUser.setOnClickListener(this);
        login.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch( v.getId()){
            case R.id.newUser:
                startActivity(new Intent(this,CreateAccount.class));
                break;
            case R.id.login:
                verify(v);
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;

        }
    }

    private void verify(final View v){
        progress.setTitle("Verifying");
        progress.setMessage("Please wait...");
        progress.show();

        final String email = emailEditText.getText().toString().trim();
        final String pwd = passwordEditText.getText().toString().trim();
        final String token  = UserInfo.getInstance(getApplicationContext()).getToken();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();

                        try {
                            JSONObject jo = new JSONObject(response);
                            Snackbar.make(v, jo.getString("message"), Snackbar.LENGTH_LONG).show();

                            if(jo.getString("message").equals("Login successfull")){
                                UserInfo.getInstance(getApplicationContext()).userLogin(
                                        jo.getString("id"),
                                        jo.getString("email"));


                                Intent intent = new Intent(getApplicationContext(),Choice.class);
                                intent.putExtra("showInfo",true);
                                finish();
                                startActivity(intent);
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
                params.put("password",pwd);
                params.put("token",token);

                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
