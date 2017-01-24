package com.example.naman.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatScreen extends AppCompatActivity {

    public static String user = "Anonymous";

    private EditText message;
    private Button send;
    private FirebaseDatabase mRef;
    private DatabaseReference mDatabase;
    private ListView lv;
    MessageAdapter adapter;
    FirebaseAuth mAuth;
    ChildEventListener mListener;
    FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        firebase();
        initUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.signout:
                mAuth.signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    private void firebase() {
        mRef = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mAuth.getCurrentUser() != null)
                {
                    //signed in
                    Log.d(MainActivity.TAG, "Logged in" );
                    signIn();

                }
                else
                {
                    Log.d(MainActivity.TAG, "Logged out" );
                    signOut();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            1);
                }
            }
        };


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
                Toast.makeText(this,"Sign in successful!", Toast.LENGTH_SHORT).show();
            else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this,"Sign in failed!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void signIn()
    {
        user = mAuth.getCurrentUser().getDisplayName();
        mDatabase = mRef.getReference().child("message");

        if(mListener == null) {
            mListener = new Listener();
            mDatabase.addChildEventListener(mListener);
        }
    }

    private void signOut() {

        user= null;

        if(mDatabase!=null)
            mDatabase.removeEventListener(mListener);

    }

    private void initUI()
    {
        lv = (ListView) findViewById(R.id.listView);
        ArrayList<Message> object = new ArrayList<>();
        adapter = new MessageAdapter(this, object);
        lv.setAdapter(adapter);
        send = (Button) findViewById(R.id.send);
        message = (EditText)findViewById(R.id.message);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = mDatabase.push().getKey();
                String txt = message.getText().toString().trim();
                if(user != null) {
                    Message object = new Message(user, txt);
                    mDatabase.child(key).setValue(object);
                    message.setText("");
                }
                else
                {
                    //Else keep the user waiting with input intact
                    Toast.makeText(getApplicationContext(), "Loading messages.. Please wait!", Toast.LENGTH_LONG ).show();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(authListener !=null )
            mAuth.removeAuthStateListener(authListener);
        //adapter.clear();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mAuth.addAuthStateListener(authListener);

    }

    private class Listener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Log.d("ChatApp", "child added");
            Message message = dataSnapshot.getValue(Message.class);
            if(message.sender == null)
                //Prevent crashes in adapter due to null username
                return;
            adapter.add(message);

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
