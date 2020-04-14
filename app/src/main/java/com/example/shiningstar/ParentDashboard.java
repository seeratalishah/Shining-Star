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
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ParentDashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


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


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_parent);
        toolbar.setTitle("Shining Star");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener (this);

        final View hView =  navigationView.getHeaderView(0);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String email_curr = firebaseAuth.getCurrentUser().getEmail();
        String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("parents").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView nav_username = (TextView) hView.findViewById(R.id.name_parent);
                String name_small = dataSnapshot.getValue().toString();
                String name_big = name_small.substring(0, 1).toUpperCase() + name_small.substring(1);
                nav_username.setText(name_big);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        TextView nav_email = (TextView) hView.findViewById(R.id.email_parent);
        nav_email.setText(email_curr);



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

    }

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
