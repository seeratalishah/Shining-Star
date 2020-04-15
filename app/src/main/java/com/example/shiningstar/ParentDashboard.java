package com.example.shiningstar;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ParentDashboard extends AppCompatActivity {


    TextView name_parent;
    TextView email_parent;

    ImageButton add_child;
    ImageButton view_child;
    ImageButton profile;

    Button logout;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    FragmentManager fragmentmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);


        name_parent = findViewById(R.id.name);
        email_parent = findViewById(R.id.email);

        add_child = findViewById(R.id.add_children_parent);
        view_child = findViewById(R.id.view_children_parent);
        profile = findViewById(R.id.profile_parent);

        logout = findViewById(R.id.logoutbtn);


        getData();


        add_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentmanager = getSupportFragmentManager();
                ParentAddChild createFrag = new ParentAddChild();
                FragmentTransaction trans = fragmentmanager.beginTransaction();
                trans.add(R.id.parent_dashboard, createFrag);
                trans.addToBackStack(null);
                trans.commit();

            }
        });

        view_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),Parent.class);
                startActivity(i);

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrefManager pref = new PrefManager(getApplicationContext());
                pref.resetData();
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

            }
        });





    }


    private void getData(){

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String email_curr = firebaseAuth.getCurrentUser().getEmail();
        String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("parents").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name_small = dataSnapshot.getValue().toString();
                String name_big = name_small.substring(0, 1).toUpperCase() + name_small.substring(1);
                name_parent.setText(name_big);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        email_parent.setText(email_curr);

    }

}
