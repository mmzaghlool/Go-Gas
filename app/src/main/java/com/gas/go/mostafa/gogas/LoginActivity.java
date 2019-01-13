package com.gas.go.mostafa.gogas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button btRegister;
    private EditText etEmail;
    private EditText etPass;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static Map<String, Object> userData;

    public static Map<String, Object> getUserData() {
        return userData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            String uid = currentUser.getUid();
            String type = currentUser.getDisplayName();
            this.getUserData(uid, type);
        }

        etEmail= findViewById(R.id.et_email);
        etPass= findViewById(R.id.et_password);

        btRegister= findViewById(R.id.register);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openregisteractivity();
            }
        });
    }


    public void openregisteractivity(){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private void getUserData(String uid, final String type){

        DatabaseReference ref = mDatabase.child(type).child(uid);

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                userData = (Map) dataSnapshot.getValue();

                Intent intent;



                if (type.equals( "Distributors")){
                    intent = new Intent(LoginActivity.this, DistActivity.class);
                    intent.putExtra("dist", true);

                } else {
                    if (userData.get("type").equals("Supports"))
                        intent = new Intent(LoginActivity.this, CustomerSupportActivity.class);
                    else {
                        intent = new Intent(LoginActivity.this, DistActivity.class);
                        intent.putExtra("dist", false);
                    }
                }

                LoginActivity.this.finish();
                intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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

    public void buttonLogin(View view) {
        String email = etEmail.getText().toString();
        email = email.trim();
        Log.d("login email", email);
        String password = etPass.getText().toString();

        if (email.length() != 0 & password.length() != 0) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                String uid = user.getUid();
                                String type = user.getDisplayName();
                                getUserData(uid, type);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Check your email and password",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }
}
