package com.example.mitest.mitest;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mitest.mitest.Adaptor.DeliveryListAdaptor;
import com.example.mitest.mitest.Model.Delivery;
import com.example.mitest.mitest.Model.Location;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    private DeliveryListAdaptor mDeliveryListAdaptor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(Constant.TITLE_NAME_LIST);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDelegate().onStart();

        // Check whether the permission already granted
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            // If permission already granted
            loadDeliveryFromAPI();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadDeliveryFromAPI();
                } else {
                    // This part if there is no permission
                }
                return;
            }
        }
    }

    private void loadDeliveryFromAPI() {
        // Check mobile on connection
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);
            // Get the url from Constant
            String url = Constant.API_URL_DOMAIN + Constant.API_URL_PATH;

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                // Save the response into Shared Preference for offline purposes
                                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(Constant.SHARED_PREF_API_RESPONSE, response);
                                editor.commit();

                                setupApiResponseIntoView(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // If fail, retry again.
                    loadDeliveryFromAPI();
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else {
            // Prompt Toast to user that no internet connection
            Toast.makeText(getApplicationContext(), Constant.TEXT_NAME_OFFLINE, Toast.LENGTH_LONG).show();
            // Load Shared Preferences if there is no connectivity
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            String sharedPrefString = sharedPref.getString(Constant.SHARED_PREF_API_RESPONSE, "");
            if (sharedPrefString != null && !sharedPrefString.isEmpty()) {
                this.setupApiResponseIntoView(sharedPrefString);
            }
        }
    }

    private void setupApiResponseIntoView(String input) {
        // Convert API's String message into ArrayList
        ArrayList<Delivery> deliveries = new Gson().fromJson(input, new TypeToken<List<Delivery>>(){}.getType());
        mDeliveryListAdaptor = new DeliveryListAdaptor(getApplicationContext(), deliveries);
        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setAdapter(mDeliveryListAdaptor);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(MainActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
    }
}
