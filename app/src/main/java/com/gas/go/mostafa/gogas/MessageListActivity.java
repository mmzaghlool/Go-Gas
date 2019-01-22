package com.gas.go.mostafa.gogas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MessageListActivity extends AppCompatActivity {
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private DatabaseReference mDatabase;

    private String customerUID;
    private String userUID;
    private String name;
    private String type;
    private boolean isSupport;

    private EditText etMessage;
    private List<Message> messageList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        setTitle("Customer support");

        etMessage = (EditText) findViewById(R.id.edittext_chatbox);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(MessageListActivity.this, messageList);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MessageListActivity.this);
        mMessageRecycler.setLayoutManager(linearLayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);

        name = (String) LoginActivity.getUserData().get("name");
        type = (String) LoginActivity.getUserData().get("type");
        userUID = (String) LoginActivity.getUserData().get("uid");

        if (type.equals("Supports")) {
            customerUID = getIntent().getStringExtra("custUID");
            isSupport = true;
        } else {
            customerUID = (String) LoginActivity.getUserData().get("uid");
            isSupport = false;
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference messagesListener = mDatabase.child("Messages").child(customerUID);


        // database listener
        ChildEventListener userListener = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                // A new message has been added, add it to the displayed list
                Message message = dataSnapshot.getValue(Message.class);

                Log.d("message", message.getMessage());

                messageList.add(message);
                mMessageRecycler.setAdapter(mMessageAdapter);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("cancel", "load:onCancelled", databaseError.toException());
                // ...
            }
        };
        messagesListener.addChildEventListener(userListener);

    }


    public void buttonSend(View view) {
        DatabaseReference messagesListener = mDatabase.child("Messages").child(customerUID);
        final Calendar currentDate = Calendar.getInstance();

        String message = etMessage.getText().toString();
        String senderName = isSupport? "Customer Support": name;
        long timeStamp = currentDate.getTimeInMillis();

        User sender = new User(senderName, userUID);
        Message messageInit = new Message(message, sender, timeStamp);

//        Map update = new HashMap<String, Object>();
//        update.put(timeStamp + "", messageInit);

        etMessage.setText("");

        messagesListener.child(timeStamp + "").setValue(messageInit);

    }
}