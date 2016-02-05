package edu.toronto.ece1776.sample.componenthijacking;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Dynamically register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("edu.toronto.ece1776.action.UPDATE_LOCATION");
        filter.addAction("edu.toronto.ece1776.action.SEND_LOCATION");

        BroadcastReceiver receiver = new Receiver();
        registerReceiver(receiver, filter);
    }

    // This is a dynamically registered broadcast receiver that performs two actions (depending
    // on the intent received).  It can update the saved location and return the new location
    // to the sender of the intent.  It can also send the saved location to the URL specified
    // in the intent data.  Both flows are vulnerable to component hijacking.

    public class Receiver extends BroadcastReceiver {
        private Location location = null;

        public Receiver() {
            LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("edu.toronto.ece1776.action.UPDATE_LOCATION")) {
                LocationManager locationManager = (LocationManager)MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                // Send location back to intent sender
                setResultData(location.toString());

            } else if (intent.getAction().equals("edu.toronto.ece1776.action.SEND_LOCATION")) {
                try {
                    // Obtain URL from intent data
                    URL url = new URL(intent.getExtras().getString("url"));
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoInput(true);
                    urlConnection.setDoOutput(true);

                    OutputStream os = urlConnection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                    // Send saved location to URL
                    writer.write(location.toString());
                    writer.flush();
                    writer.close();

                    urlConnection.connect();

                } catch (Exception e) {
                    Log.e("MainActivity: ", e.toString());
                }
            }
        }
    }
}
