package com.gas.go.mostafa.gogas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateTimeActivity extends AppCompatActivity {

    private Calendar date = Calendar.getInstance();
    final public String TAG = "Debug";
    final Calendar currentDate = Calendar.getInstance();

    private Button btAsSoonAsPossible;
    private Button btPick;
    private TextView tvTime;
    private TextView tvDate;

    private Map<String, Object> userData;
    private Map<String, Object> prevOrdred;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);
        setTitle("Pick Time and date");

        btAsSoonAsPossible = (Button) findViewById(R.id.bt_as_soon_as_possible);
        btPick = (Button) findViewById(R.id.bt_pick_date_time);
        tvTime = (TextView) findViewById(R.id.tv_time_val);
        tvDate = (TextView) findViewById(R.id.tv_date_val);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        userData = LoginActivity.getUserData();

        // calculate the previosly ordred qty in this day
        prevOrdred = new HashMap();
        this.getDayOrders();

        setTime();
        setDate();

    }

    public void buttonPickTimeDate(View v) {
        this.showDateTimePicker();
    }

    public void buttonAsSoonAsPossible(View v) {
        this.getDayOrders();
    }

    private void confirmRequest() {
/*        Intent returnIntent = new Intent();

        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d yyyy, h:mm a ");
        String date = format.format(this.date.getTime());

        returnIntent.putExtra("result", date);
        returnIntent.putExtra("timeStamp", this.date.getTimeInMillis() + "");
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
        */

        // a format used to detect the customer orders in specific day
        SimpleDateFormat customerFormat = new SimpleDateFormat("d-MM-yyyy");
        String customerDate = customerFormat.format(this.date.getTime());
        Log.d("customerDate", customerDate);


        Bundle extras = getIntent().getExtras();
        String uid = (String) userData.get("uid");
        String qty = extras.getString("qty");
        String payment = extras.getString("payment");
        String type = extras.getString("type");
        long timeStamp = this.date.getTimeInMillis();
        double lat = extras.getDouble("lat", 0.0);
        double lng = extras.getDouble("lng", 0.0);

        String area = "";
        String street = "";
        String building = "";
        if (lat == 0) {
            area = extras.getString("area");
            street = extras.getString("street");
            building = extras.getString("building");
        }


        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d yyyy, h:mm a ");
        String date = format.format(this.date.getTime());

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("qty", qty);
        updateData.put("payment", payment);
        updateData.put("type", type);
        updateData.put("area", area);
        updateData.put("street", street);
        updateData.put("building", building);
        updateData.put("lng", lng);
        updateData.put("lat", lat);
        updateData.put("timeStamp", timeStamp);
        updateData.put("date", date);
        updateData.put("name", userData.get("name"));
        updateData.put("uid", userData.get("uid"));


        // Write a message to the database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//        String key = database.child("requests").push().getKey();

        long oldOrdredQty = 0;

        if (prevOrdred != null)
            if (prevOrdred.containsKey(customerDate))
                oldOrdredQty = Long.parseLong(prevOrdred.get(customerDate) + "");

                Log.d("prevOrdred.)", prevOrdred.get(customerDate) + "");
        oldOrdredQty += Long.parseLong(qty);

        if (oldOrdredQty <= 5) {

            Map<String, Object> childUpdates = new HashMap<>();

            // manual location; no point on map
            if (lng == 0.0) {
                childUpdates.put("/requests/manual/" + timeStamp, updateData);
            } else {
                childUpdates.put("/requests/map/" + timeStamp, updateData);
            }



            childUpdates.put("/Customers/" + uid + "/requests/" + customerDate + ""  , oldOrdredQty);

            Log.d("childUpdates", childUpdates.keySet().toArray().toString() + "");

            database.updateChildren(childUpdates);

            Toast.makeText(this, "Done, request submitted", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(DateTimeActivity.this, DistActivity.class);
//        intent.putExtra("dist", false);
            DateTimeActivity.this.finish();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivity(intent);
        }

        // daily limit reached
        else {
            Toast.makeText(this, "Sorry you max quantity is 5/day", Toast.LENGTH_LONG).show();

        }
    }

    private void getDayOrders(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Bundle extras = getIntent().getExtras();
        String uid = (String) userData.get("uid");

        // get delivering locations
        DatabaseReference ref = database.child("Customers").child(uid).child("requests");

        // database listener
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                prevOrdred = (Map) dataSnapshot.getValue();

                confirmRequest();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("cancel", "load:onCancelled", databaseError.toException());
                // ...
            }
        };
        ref.addListenerForSingleValueEvent(userListener);
    }

    public void setDate() {
        SimpleDateFormat dateOnly = new SimpleDateFormat("EEE MMM d, yyyy");
        String date = dateOnly.format(this.currentDate.getTime());

        tvDate.setText(date);
//        return dateOnly.format(date);
    }

    public void setTime() {
        Date date = this.date.getTime();
        DateFormat dateFormat = new SimpleDateFormat("h:mm a");
        String formattedTime = dateFormat.format(date);

        tvTime.setText(formattedTime);
//        return formattedTime;
    }

    public void showDateTimePicker() {
        new DatePickerDialog(DateTimeActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(DateTimeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        setTime();
                        setDate();
                        getDayOrders();
                    }
                }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();
            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }


}
