package com.gas.go.mostafa.gogas;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerSupportActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Map<String, Object> messagesData;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);

        setTitle("Customer Support");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getMessages();
    }


    private void getMessages() {

        // get delivering locations
        DatabaseReference ref = mDatabase.child("Messages");


        // database listener
        ValueEventListener dbListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Map dataSnapshotValue = (Map) dataSnapshot.getValue();
                messagesData = new HashMap();
                list = new ArrayList<String>();

                int counter = 0;
                if (dataSnapshotValue != null) {
                    for (Object i : dataSnapshotValue.keySet()) {
                        i = i + "";

                        Map data = (Map) dataSnapshotValue.get(i);
                        String firstMessage = (String) data.keySet().toArray()[0];
                        Map msg = (Map) data.get(firstMessage);
                        Map sender = (Map) msg.get("sender");
                        String nickname = (String) sender.get("nickname");
                        String uid = (String) sender.get("uid");

                        data.put("uid", uid);

                        list.add(nickname);
                        messagesData.put(counter + "", data);
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
        ref.addValueEventListener(dbListener);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.logout_support, menu);// Menu Resource, Menu

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
                Intent logoutIntent = new Intent(CustomerSupportActivity.this, LoginActivity.class);
                CustomerSupportActivity.this.finish();
                logoutIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(logoutIntent);
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void renderList() {
        final ListView listview = (ListView) findViewById(R.id.list_view);

        final CustomerSupportActivity.StableArrayAdapter adapter = new CustomerSupportActivity.StableArrayAdapter(this,
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
                                Map data = (Map) messagesData.get(position + "");
                                String uid = (String) data.get("uid");

                                Intent intent = new Intent(CustomerSupportActivity.this, MessageListActivity.class);
                                intent.putExtra("custUID", uid);
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
