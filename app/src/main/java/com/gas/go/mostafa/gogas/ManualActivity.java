package com.gas.go.mostafa.gogas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManualActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private long currentTime;
    private Map<String, Object> locationsData;
    private ArrayList<String> list ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        setTitle("Delivering locations");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Calendar currentDate = Calendar.getInstance();
        currentTime = currentDate.getTimeInMillis();
        getLocations();
    }

    private void getLocations() {

        // get delivering locations
        DatabaseReference ref = mDatabase.child("requests/manual");


        // database listener
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Map dataSnapshotValue = (Map) dataSnapshot.getValue();
                locationsData = new HashMap();
                list = new ArrayList<String>();

                int counter = 0;
                if (dataSnapshotValue != null) {
                    for (Object i : dataSnapshotValue.keySet()) {
                        i = i + "";

                        Log.d("current time", currentTime + "");

                        if (currentTime <= Long.parseLong((String) i)) {
                            continue;
                        }

                        Log.d("i", (String) i);
                        Map data = (Map) dataSnapshotValue.get(i);

                        String area = (String) data.get("area");
                        String building = (String) data.get("building");
                        String street = (String) data.get("street");

                        list.add(area + ", " + street + ", " + building);
                        locationsData.put(counter + "", data);
                        counter++;
                    }
                }
                renderList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("cancel", "load:onCancelled", databaseError.toException());
                // ...
            }
        };
        ref.addValueEventListener(userListener);


    }

    private void renderList() {
        final ListView listview = (ListView) findViewById(R.id.list_view);

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, long id) {


                view.animate().setDuration(200).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                Map data = (Map) locationsData.get(position + "");

                                String area = (String) data.get("area");
                                String building = (String) data.get("building");
                                String street = (String) data.get("street");
                                String name = (String) data.get("name");
                                String qty = (String) data.get("qty");
                                String type = (String) data.get("type");
                                long timeStamp = (long) data.get("timeStamp");

                                Intent intent = new Intent(ManualActivity.this, MapDetailsActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("building", building);
                                intent.putExtra("street", street);
                                intent.putExtra("area", area);
                                intent.putExtra("qty", qty);
                                intent.putExtra("type", type);
                                intent.putExtra("timeStamp", timeStamp);
                                intent.putExtra("isManual", true);
                                startActivity(intent);

                                view.setAlpha(1);
                            }
                        });
            }

        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
