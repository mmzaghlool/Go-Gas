package com.gas.go.mostafa.gogas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapDetailsActivity extends AppCompatActivity {

    private Button btConfirm;
    private TextView tvName;
    private TextView tvBuildingNo;
    private TextView tvStreetNo;
    private TextView tvArea;
    private TextView tvType;
    private TextView tvQuantity;

    private LinearLayout tvBuildingNoKey;
    private LinearLayout tvStreetNoKey;
    private LinearLayout tvAreaKey;

    private boolean isManual;
    private long timeStamp;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_details);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        setTitle("Delivery details");

        btConfirm = (Button) findViewById(R.id.bt_confirm_delivery);
        tvName = (TextView) findViewById(R.id.tv_name_val);
        tvBuildingNo = (TextView) findViewById(R.id.tv_building_val);
        tvStreetNo = (TextView) findViewById(R.id.tv_street_val);
        tvArea = (TextView) findViewById(R.id.tv_area_val);
        tvType = (TextView) findViewById(R.id.tv_type_val);
        tvQuantity = (TextView) findViewById(R.id.tv_quantity_val);

        tvBuildingNoKey = (LinearLayout) findViewById(R.id.tv_building_key);
        tvStreetNoKey = (LinearLayout) findViewById(R.id.tv_street_key);
        tvAreaKey = (LinearLayout) findViewById(R.id.tv_area_key);


        Bundle data = getIntent().getExtras();


        String name = (String) data.get("name");
        String qty = (String) data.get("qty");
        String type = (String) data.get("type");
        isManual = (boolean) data.get("isManual");
        timeStamp = (long) data.get("timeStamp");

        tvName.setText( name);
        tvQuantity.setText( qty);
        tvType.setText( type);

        if (!isManual) {
            tvArea.setVisibility(View.GONE);
            tvBuildingNo.setVisibility(View.GONE);
            tvStreetNo.setVisibility(View.GONE);

            tvAreaKey.setVisibility(View.GONE);
            tvBuildingNoKey.setVisibility(View.GONE);
            tvStreetNoKey.setVisibility(View.GONE);
        } else {

            String building = (String) data.get("building");
            String street = (String) data.get("street");
            String area = (String) data.get("area");
            tvStreetNo.setText(street);
            tvArea.setText(area);
            tvBuildingNo.setText(building);
        }

        btConfirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                 // remove the node from the data base
                String type = isManual? "manual": "map";
                DatabaseReference ref = mDatabase.child("requests").child(type).child(timeStamp + "");
                ref.removeValue();

                Toast.makeText(MapDetailsActivity.this, "Delivery confirmed", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MapDetailsActivity.this, DistActivity.class);
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

    }


}
