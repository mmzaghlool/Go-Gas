package com.gas.go.mostafa.gogas;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etDistID;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etName;
    private Switch switchDist;
    private FirebaseAuth mAuth;
    private boolean isDistChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        switchDist = (Switch) findViewById(R.id.switch_dist);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_pass);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_pass);
        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etDistID = (EditText) findViewById(R.id.et_dist_id);
        etDistID.setVisibility(View.GONE);

        switchDist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDistChecked = isChecked;
                if(isChecked){
                    etDistID.setVisibility(View.VISIBLE);
                }else {
                    etDistID.setVisibility(View.GONE);
                }
            }
        });
    }

    public void buttonRegister(View view) {
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        final String confirmPassword = etConfirmPassword.getText().toString();
        final String name = etName.getText().toString();
        final String phone = etPhone.getText().toString();
        final String distID= etDistID.getText().toString();

        Log.v("email", email);
        Log.v("password", password);
        if (email.length() != 0 & password.length() != 0 & confirmPassword.length() != 0 &
                name.length() != 0 & phone.length() != 8 ) {

            if ((isDistChecked & distID.length() != 0) || !isDistChecked) {
                if (password.equals(confirmPassword)) {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    Log.v("task", task.getResult() + "");
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String uid = user.getUid();

                                        Map<String, Object> childUpdates = new HashMap<>();
                                        Map<String, Object> updateData = new HashMap<>();
                                        updateData.put("uid", uid);
                                        updateData.put("email", email);
                                        updateData.put("name", name);
                                        updateData.put("phone", phone);

                                        String type;

                                        if (isDistChecked){
                                            updateData.put("distID", distID);
                                            type = "Distributors";
                                            updateData.put("type", type);
                                            childUpdates.put("/Distributors/" + uid, updateData);
                                        } else {
                                            type = "Customers";
                                            updateData.put("type", type);
                                            childUpdates.put("/Customers/" + uid, updateData);
                                        }
                                        // Write a message to the database
                                        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                        database.updateChildren(childUpdates);


                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(type)
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(RegisterActivity.this, "Done, Email created", Toast.LENGTH_LONG).show();
                                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                            startActivity(intent);

                                                        }
                                                    }
                                                });
//                            updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, "Wrong or used Email",
                                                Toast.LENGTH_LONG).show();
//                            updateUI(null);
                                    }

                                    // ...
                                }
                            });
                } else {
                    // If password and confirmation field not the same, display a message to the user.
                    Toast.makeText(RegisterActivity.this, "Password and password confirmation is't the same",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                // If Distributor id is empty, display a message to the user.
                Toast.makeText(RegisterActivity.this, "Distributor staff no. is required",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            // If data missing, display a message to the user.
            Toast.makeText(RegisterActivity.this, "All fields are mandatory, and phone must be 8 numbers",
                    Toast.LENGTH_LONG).show();
        }
    }
}
