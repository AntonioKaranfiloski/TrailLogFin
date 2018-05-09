package com.progy.elan.trailrunlog;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
public class DatabaseActivity extends AppCompatActivity {

    private static final String TAG = "AddToDatabase";
    private Button btnCreate;
    private EditText edtTitle, edtDescription, edtKilometers, edtDenivelation;
    private String userID;

    private String noteID;
    private boolean isExist;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);


        try {
            noteID = getIntent().getStringExtra("noteId");

            //Toast.makeText(this, noteID, Toast.LENGTH_SHORT).show();

            if (!noteID.trim().equals("")) {
                isExist = true;
            } else {
                isExist = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



        findViewById(R.id.btn_view_trainings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DatabaseActivity.this, ViewDatabase.class);
                startActivity(intent);
            }
        });

        btnCreate = (Button) findViewById(R.id.btn_insert_training);
        edtTitle = (EditText) findViewById(R.id.edtText_Title);
        edtDescription = (EditText) findViewById(R.id.edtText_Description);
        edtKilometers = (EditText) findViewById(R.id.edtText_Kilometers);
        edtDenivelation = (EditText) findViewById(R.id.edtText_Denivelation);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        myRef = mFirebaseDatabase.getReference();
        final FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Notes").child(mAuth.getCurrentUser().getUid());

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "onDataChange: Added information to database: \n" +
                        dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Submit pressed.");
                String title = edtTitle.getText().toString().trim();
                String description = edtDescription.getText().toString().trim();
                String kilometers = edtKilometers.getText().toString().trim();
                String denivelation = edtDenivelation.getText().toString().trim();

                Log.d(TAG, "onClick: Attempting to submit to database: \n" +
                        "title: " + title + "\n" +
                        "description: " + description + "\n" +
                        "kilometers " + kilometers + "\n" +
                        "denivelation " + denivelation + "\n"
                );

                //handle the exception if the EditText fields are null
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)&& !TextUtils.isEmpty(kilometers)&& !TextUtils.isEmpty(denivelation)) {
                    createNote(title, description, kilometers, denivelation);
                } else {
                    //Snackbar.make(view, "Fill empty fields", Snackbar.LENGTH_SHORT).show();
                    toastMessage("Fill Emty fields");
                }
            }
        });

        putData();
    }

    private void putData() {
        if (isExist) {
            myRef.child(noteID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("title") && dataSnapshot.hasChild("description") && dataSnapshot.hasChild("kilometers") && dataSnapshot.hasChild("denivelation")) {
                        String title = dataSnapshot.child("title").getValue().toString();
                        String description = dataSnapshot.child("description").getValue().toString();
                        String kilometers = dataSnapshot.child("kilometers").getValue().toString();
                        String denivelation = dataSnapshot.child("denivelation").getValue().toString();
                        edtTitle.setText(title);
                        edtDescription.setText(description);
                        edtKilometers.setText(kilometers);
                        edtDenivelation.setText(denivelation);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }


    private void createNote(String title, String description, String kilometers, String denivelation) {

        if (mAuth.getCurrentUser() != null) {

            if (isExist) {
                // UPDATE A NOTE
                Map updateMap = new HashMap();
                updateMap.put("title", edtTitle.getText().toString().trim());
                updateMap.put("content", edtDescription.getText().toString().trim());
                updateMap.put("content", edtKilometers.getText().toString().trim());
                updateMap.put("content", edtDenivelation.getText().toString().trim());
                updateMap.put("timestamp", ServerValue.TIMESTAMP);

                myRef.child(noteID).updateChildren(updateMap);

                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
            } else {
                // CREATE A NEW NOTE
                final DatabaseReference newNoteRef = myRef.push();

                final Map noteMap = new HashMap();
                noteMap.put("title", title);
                noteMap.put("description", description);
                noteMap.put("kilometers", kilometers);
                noteMap.put("denivelation", denivelation);
                noteMap.put("timestamp", ServerValue.TIMESTAMP);

                Thread mainThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newNoteRef.setValue(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(DatabaseActivity.this, "Note added to database", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DatabaseActivity.this, "ERROR: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                });
                mainThread.start();
            }



        } else {
            Toast.makeText(this, "USERS IS NOT SIGNED IN", Toast.LENGTH_SHORT).show();
        }

    }




}

