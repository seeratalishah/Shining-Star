package com.example.shiningstar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminDashboard extends AppCompatActivity {

    TextView name_parent;

    ImageButton news;
    ImageButton event;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    FragmentManager fragmentmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        name_parent = findViewById(R.id.name);

        news = findViewById(R.id.notify_news);
        event = findViewById(R.id.mark_event);



        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentmanager = getSupportFragmentManager();
                AdminNews createFrag = new AdminNews();
                FragmentTransaction trans = fragmentmanager.beginTransaction();
                trans.replace(R.id.admin_dashboard, createFrag);
                trans.addToBackStack(null);
                trans.commit();

            }
        });

        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentmanager = getSupportFragmentManager();
                AdminEvent createFrag = new AdminEvent();
                FragmentTransaction trans = fragmentmanager.beginTransaction();
                trans.replace(R.id.admin_dashboard, createFrag);
                trans.addToBackStack(null);
                trans.commit();

            }
        });




        getData();
    }


    private void getData(){

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String email_curr = firebaseAuth.getCurrentUser().getEmail();
        String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("admins").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
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

    }


}
