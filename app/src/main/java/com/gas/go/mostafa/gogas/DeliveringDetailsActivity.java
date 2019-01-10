package com.gas.go.mostafa.gogas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class DeliveringDetailsActivity extends AppCompatActivity {
    private EditText etArea;
    private EditText etStreet;
    private EditText etBuilding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivering_details);
        setTitle("Delivering location");

        etArea= findViewById(R.id.et_area);
        etStreet= findViewById(R.id.et_street);
        etBuilding= findViewById(R.id.et_building);

    }

    public void buttonConfirmLocation(View v){

        String area = etArea.getText().toString();
        area = area.trim();

        String street = etStreet.getText().toString();
        street = street.trim();

        String building = etBuilding.getText().toString();
        building = building.trim();

        Intent data = getIntent();
        double lng = data.getDoubleExtra("lng", 0.0);
        double lat= data.getDoubleExtra("lat", 0.0);

        Log.d("lng", lng + "");
        Log.d("lat", lat+ "");
        Log.d("area", area.length() + "");
        Log.d("street", street);
        Log.d("building", building);

        Intent confirmDelivery = new Intent(DeliveringDetailsActivity.this, BookingActivity.class);
        confirmDelivery.putExtra("lng", lng);
        confirmDelivery.putExtra("lat", lat);
        confirmDelivery.putExtra("area", area);
        confirmDelivery.putExtra("street", street);
        confirmDelivery.putExtra("building", building);

        // location validation
        // there are a point on map
        if (lng != 0.0){
            startActivity(confirmDelivery);
            // manual location
        } else if (area.length() != 0 & building.length() != 0 & street.length() != 0) {
            startActivity(confirmDelivery);

            // no location details or point in map
        } else {
            final AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(DeliveringDetailsActivity.this);
            } else {
                builder = new AlertDialog.Builder(DeliveringDetailsActivity.this);
            }
            builder.setTitle("Please add Location")
                    .setMessage("You haven't enter any details or specified the delivering location on map")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }




    }
}
