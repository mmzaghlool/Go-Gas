package com.gas.go.mostafa.gogas;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class DistActivity extends AppCompatActivity {

    private Button btMap;
    private Button btManual;

    private boolean isDist;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dist);
        btMap = (Button) findViewById(R.id.bt_map);
        btManual = (Button) findViewById(R.id.bt_manual);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // check if the user is dist or customer
        isDist = LoginActivity.getUserData().get("type").equals("Distributors");

        setTitle("Delivering Locations");


        btMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(DistActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });


        btManual.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isDist) {
                    Intent intent = new Intent(DistActivity.this, ManualActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(DistActivity.this, DeliveringDetailsActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.logout, menu);// Menu Resource, Menu

        final Drawable logout = getResources().getDrawable(R.drawable.logout);
        logout.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(logout);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                mAuth.signOut();
                Intent logoutIntent = new Intent(DistActivity.this, LoginActivity.class);
               DistActivity.this.finish();
                logoutIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(logoutIntent);
                return true;

            case R.id.action_support:
                Intent supportIntent = new Intent(DistActivity.this, MessageListActivity.class);
                startActivity(supportIntent);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
