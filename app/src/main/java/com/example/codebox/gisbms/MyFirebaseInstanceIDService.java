package com.example.codebox.gisbms;


import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by codebox on 26/8/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        Log.i("Now Token : ",recent_token);

        UserInfo.getInstance(getApplicationContext()).saveToken(recent_token);


        if(UserInfo.getInstance(getApplicationContext()).isLoggedIn()){
            updateToken(recent_token);
        }

    }

    private void updateToken(final String token){
        Log.i("updateTokenMethod","called");

        final String id = UserInfo.getInstance(getApplicationContext()).getAccoutnId();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Domain.TOKEN_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);

                            if (jo.getString("message").equals("updated")){
                                Toast.makeText(getApplicationContext(), "Token Updated!", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("token",token);

                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}
