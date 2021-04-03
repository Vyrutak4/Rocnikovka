package com.example.clotheshelper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void getWeather(double[] coordinates) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + coordinates[0] + "&lon=" + coordinates[1] + "&units=metric&appid=3d2c5aae282e07c133989c701b44cf47";
        Log.d("GPS", url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        TextView txtVenkovniTeplota = findViewById(R.id.txtVenkovniTeplota);
                        Gson g = new Gson();
                        JsonObject p = g.fromJson(response, JsonObject.class);
                        double teplota = p.getAsJsonObject("main").get("temp").getAsDouble();
                        txtVenkovniTeplota.setText("Teplota je " + teplota);
                        updateUI(teplota);
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                TextView txtVenkovniTeplota = findViewById(R.id.txtVenkovniTeplota);
                txtVenkovniTeplota.setText(error.getMessage());
            }
        });
        queue.add(stringRequest);
        queue.start();
    }

    private void updateUI(double teplota) {

    }

    private double[] getPosition() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        } else {
            System.out.println("Location permissions available, starting location");
            String isLocationEnabled = "";
            if (lm.isLocationEnabled()) {
                isLocationEnabled = "true";
            } else {
                isLocationEnabled = "false";
            }
                //Toast.makeText(getApplicationContext(), isLocationEnabled, Toast.LENGTH_SHORT).show();
            Log.d("GPS Lokace", isLocationEnabled);
                //lm.isLocationEnabled();
        }
        double longitude = 0;
        double latitude = 1;

        try {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new double[]{latitude, longitude};
    }

    public void onBtnClick(View view) {
        EditText edtTxtMisto = findViewById(R.id.edtTxtMisto);
        TextView txtVenkovniTeplota = findViewById(R.id.txtVenkovniTeplota);
        TextView txtObleceni1 = findViewById(R.id.txtObleceni1);
        TextView txtObleceni2 = findViewById(R.id.txtObleceni2);
        TextView txtObleceni3 = findViewById(R.id.txtObleceni3);

        if (!edtTxtMisto.getText().toString().equals("")) {
            double i = Integer.parseInt(String.valueOf(edtTxtMisto.getText()));
            if (i < 5) {
                txtObleceni1.setText("Bunda, tricko, svetr/mikina, dlouhé kalhoty, ponožky, vysoké boty, rukavice a čepice");
                txtObleceni2.setText("Bunda, tricko, tilko, dlouhé kalhoty, ponožky, tenisky, rukavice a čepice");
                txtObleceni3.setText("Tricko, tilko, svetr/mikina, termopradlo, kraťasy, ponožky, ponožky, rukavice a čepice");
            } else if (i > 5 && i < 12) {
                txtObleceni1.setText("Bunda, tricko, dlouhé kalhoty, ponožky, vysoké boty, čepice");
                txtObleceni2.setText("Bunda, tricko, tilko, dlouhé kalhoty, ponožky, vysoké boty");
                txtObleceni3.setText("Tricko, svetr/mikina, termopradlo, kraťasy, ponožky, tenisky");
            } else if (i > 12 && i < 20) {
                txtObleceni1.setText("Tricko, dlouhé kalhoty, ponožky, tenisky");
                txtObleceni2.setText("Tricko, dlouhé kalhoty, svetr/mikina, ponožky, tenisky");
                txtObleceni3.setText("Tricko, svetr/mikina, kraťasy, žabky");
            } else {
                txtObleceni1.setText("Tricko, kraťasy, kotníkové ponožky, tenisky");
                txtObleceni2.setText("Tricko, kraťasy, kšiltovka, žabky");
                txtObleceni3.setText("Bez trička, plavky, žabky");
            }
        }


        //Log.d("GPS lokace", "gps = " + String.valueOf(getPosition()[0]) + ", " + String.valueOf(getPosition()[1]));
        getWeather(getPosition());
    }
}