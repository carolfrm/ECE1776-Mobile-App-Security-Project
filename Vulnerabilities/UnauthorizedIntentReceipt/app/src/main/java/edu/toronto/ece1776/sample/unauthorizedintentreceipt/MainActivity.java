package edu.toronto.ece1776.sample.unauthorizedintentreceipt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendImplicitIntent();
    }

    private void sendImplicitIntent() {
        Intent intent = new Intent();
        intent.setAction("com.example.davidlie.ece1776.SEND_ACTION");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
