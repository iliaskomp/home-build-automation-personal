package com.example.elias.mqtt_new;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Project co-created by Ilias and PJ
 * Ilias implemented the MQTT-related things 
 * PJ the token-related things.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button connect = (Button)findViewById(R.id.connectButton);
        connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                EditText serverEditText = (EditText)findViewById(R.id.serverText);
                Button connectButton = (Button)findViewById(R.id.connectButton);
                Button lockButton = (Button)findViewById(R.id.lockButton);
                Button unlockButton = (Button)findViewById(R.id.unlockButton);
                Button getToken = (Button)findViewById(R.id.getToken);
                Button reset = (Button)findViewById(R.id.reset);

                TextView resultText = (TextView)findViewById(R.id.resultTextView);
                String server = serverEditText.getText().toString();
                if (MQTTUtils.connect(server)) {
                    connectButton.setEnabled(false);
                    serverEditText.setEnabled(false);
                    lockButton.setEnabled(true);
                    unlockButton.setEnabled(true);
                    getToken.setEnabled(true);
                    reset.setEnabled(true);
                    resultText.setText("Connected to the server.");
                } else {
                    resultText.setText("Error connecting the server.");
                }
            }

        });

        Button lock = (Button)findViewById(R.id.lockButton);
//        lock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                TextView resultText = (TextView)findViewById(R.id.resultTextView);
//                String topic = "SmartLock";
//                String payload = "LOCK";
//                MQTTUtils.pub(topic, payload);
//                resultText.setText("The door is now locked.");
//            }
//        });

        Button unlock = (Button)findViewById(R.id.unlockButton);
//        unlock.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                TextView resultText = (TextView)findViewById(R.id.resultTextView);
//                String topic = "SmartLock";
//                String payload = "UNLOCK";
//                MQTTUtils.pub(topic, payload);
//                resultText.setText("The door is now unlocked.");
//            }
//        });
    }

    public void getToken(View view){
        final Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String date = sharedPref.getString("date", null);
        System.out.println(date);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String today = df.format(c.getTime());


        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        };
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    System.out.println("response : "+ jsonResponse);
                    int Messagecode = Integer.parseInt(jsonResponse.optString("MessageCode"));
                    if(Messagecode==200){

                        SharedPreferences sharedPref = context.getSharedPreferences(
                                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        String token = jsonResponse.optString("token");
                        editor.putString("token", token);
                        editor.commit();
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setMessage("received token")
                                .setNegativeButton("OK", null)
                                .create()
                                .show();
                    }else{
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setMessage("Failed to get token")
                                .setNegativeButton("OK", null)
                                .create()
                                .show();
                    }
                }
                catch (JSONException e) {

                }
            }
        };
        if(!today.equals(date)){
            tokenRequest request = new tokenRequest(responseListener,errorListener);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(request);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("date", today);
            editor.commit();
        }else{
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setMessage("Already got a token today")
                    .setNegativeButton("OK", null)
                    .create()
                    .show();
        }

    }

    public void open(View view){

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        };
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    System.out.println("response : "+ jsonResponse);
                    int Messagecode = Integer.parseInt(jsonResponse.optString("MessageCode"));
                    TextView resultText = (TextView)findViewById(R.id.resultTextView);
                    if(Messagecode==200){
                        String topic = "SmartLock";
                        String payload = "LOCK";
                        MQTTUtils.pub(topic, payload);
                        resultText.setText("The door is now locked.");
                        System.out.println("send 1");
                    }else{
                        //failed
                        resultText.setText("You have no access");
                        System.out.println("lock not open");
                    }
                }
                catch (JSONException e) {

                }
            }
        };
        final Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);
        validateRequest request = new validateRequest(token,responseListener,errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }

    public void close(View view){

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        };
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    System.out.println("response : "+ jsonResponse);
                    int Messagecode = Integer.parseInt(jsonResponse.optString("MessageCode"));
                    TextView resultText = (TextView)findViewById(R.id.resultTextView);
                    if(Messagecode==200){
                        String topic = "SmartLock";
                        String payload = "UNLOCK";
                        MQTTUtils.pub(topic, payload);
                        resultText.setText("The door is now unlocked.");
                        System.out.println("send 0");
                    }else{
                        resultText.setText("You have no access");
                        System.out.println("lock not open");
                    }
                }
                catch (JSONException e) {

                }
            }
        };
        final Context context = this;
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String token = sharedPref.getString("token", null);
        validateRequest request = new validateRequest(token,responseListener,errorListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }

    public void resetToken(View view){
        SharedPreferences sharedPref = this.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("date", null);
        editor.commit();
    }
}
