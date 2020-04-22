package com.example.shiningstar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Parent extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    TextView name_parent;

    ImageButton add_child;
    ImageButton view_child;
    ImageButton profile;

    FragmentManager fragmentmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_parent);
        toolbar.setTitle("Parent");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        name_parent = findViewById(R.id.name_welcome);
        add_child = findViewById(R.id.add_child_parent);
        view_child = findViewById(R.id.view_child_parent);
        profile = findViewById(R.id.profile_parent);

        getData();


        add_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentmanager = getSupportFragmentManager();
                ParentAddChild createFrag = new ParentAddChild();
                FragmentTransaction trans = fragmentmanager.beginTransaction();
                trans.replace(R.id.content_holder_parent, createFrag);
                trans.addToBackStack(null);
                trans.commit();

            }
        });

        view_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentmanager = getSupportFragmentManager();
                ParentChildList createFrag = new ParentChildList();
                FragmentTransaction trans = fragmentmanager.beginTransaction();
                trans.replace(R.id.content_holder_parent, createFrag);
                trans.addToBackStack(null);
                trans.commit();

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentmanager = getSupportFragmentManager();
                ParentProfile createFrag = new ParentProfile();
                FragmentTransaction trans = fragmentmanager.beginTransaction();
                trans.replace(R.id.content_holder_parent, createFrag);
                trans.addToBackStack(null);
                trans.commit();

            }
        });




        //getSupportFragmentManager().beginTransaction().replace(R.id.content_holder_parent,new ParentChildList()).commit();

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

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout_parent) {
            PrefManager pref = new PrefManager(this);
            pref.resetData();
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(this,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
