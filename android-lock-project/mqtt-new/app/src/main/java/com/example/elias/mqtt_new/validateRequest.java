package com.example.elias.mqtt_new;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;


public class validateRequest extends StringRequest {

    public validateRequest(String token, Response.Listener<String> listener,Response.ErrorListener errorListener) {

        super(String.format("http://188.166.44.160/hba_auth.php?action=%1$s&token=%2$s",
                "isValid",
                token), listener, errorListener);

    }

}