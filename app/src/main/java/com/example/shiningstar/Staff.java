package com.example.shiningstar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Staff extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        Fragment_staff_class_list.OnFragmentInteractionListener {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    Fragment_staff_class_list f = new Fragment_staff_class_list();

    TextView name_staff;

    ImageButton add_class;
    ImageButton view_class;
    ImageButton profile;

    FragmentManager fragmentmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_staff);
        toolbar.setTitle("Staff");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        name_staff = findViewById(R.id.name_welcome);

        add_class = findViewById(R.id.add_class_staff);
        view_class = findViewById(R.id.view_class_staff);
        profile = findViewById(R.id.profile_staff);

        getData();


        add_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AddNewClass();

            }
        });

        view_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentmanager = getSupportFragmentManager();
                Fragment_staff_class_list createFrag = new Fragment_staff_class_list();
                FragmentTransaction trans = fragmentmanager.beginTransaction();
                trans.remove(createFrag);
                trans.replace(R.id.content_holder_staff, createFrag);
                trans.addToBackStack("a");
                trans.commit();

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragmentmanager = getSupportFragmentManager();
                StaffProfile createFrag = new StaffProfile();
                FragmentTransaction trans = fragmentmanager.beginTransaction();
                trans.replace(R.id.content_holder_staff, createFrag);
                trans.addToBackStack(null);
                trans.commit();

            }
        });




        //getSupportFragmentManager().beginTransaction().replace(R.id.content_holder_staff,new Fragment_staff_class_list()).commit();

    }

    private void getData(){

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        String email_curr = firebaseAuth.getCurrentUser().getEmail();
        String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference.child("staff").child(uid).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name_small = dataSnapshot.getValue().toString();
                String name_big = name_small.substring(0, 1).toUpperCase() + name_small.substring(1);
                name_staff.setText(name_big);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void AddNewClass()
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final String uid = firebaseAuth.getCurrentUser().getUid();

        LayoutInflater factory = LayoutInflater.from(Staff.this);


        final View textEntryView = factory.inflate(R.layout.class_data_layout, null);

        final EditText name = (EditText) textEntryView.findViewById(R.id.classname);
        final EditText room = (EditText) textEntryView.findViewById(R.id.classroom);

        AlertDialog.Builder alert = new AlertDialog.Builder(Staff.this);
        alert.setTitle("Add new room");
        alert.setMessage("Enter room details");

        final String roomId = db.push().getKey();

        alert.setView(textEntryView);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //final String Classcode = id.getText().toString();
                final String Classname = name.getText().toString();
                final String Classroom = room.getText().toString();

                final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String currClasses = dataSnapshot.child("staff").child(uid).child("class_ids").getValue().toString();

                            if(currClasses.equals("0"))
                            {
                                dataRef.child("staff").child(uid).child("class_ids").setValue(roomId + ",");
                                dataRef.child("classes").child(roomId).child("class_name").setValue(Classname);
                                dataRef.child("classes").child(roomId).child("class_room").setValue(Classroom);

                                dataRef.child("classes").child(roomId).child("checked_in").setValue(0);
                                dataRef.child("classes").child(roomId).child("checked_out").setValue(0);
                                dataRef.child("classes").child(roomId).child("absent").setValue(0);

                                dataRef.child("classes").child(roomId).child("children").setValue(0);

                                RefreshClassList();
                            }
                            else if(!isClassAssigned(currClasses, roomId)) {
                                currClasses += roomId + ",";
                                dataRef.child("staff").child(uid).child("class_ids").setValue(currClasses);
                                dataRef.child("classes").child(roomId).child("class_name").setValue(Classname);
                                dataRef.child("classes").child(roomId).child("class_room").setValue(Classroom);

                                dataRef.child("classes").child(roomId).child("checked_in").setValue(0);
                                dataRef.child("classes").child(roomId).child("checked_out").setValue(0);
                                dataRef.child("classes").child(roomId).child("absent").setValue(0);


                                dataRef.child("classes").child(roomId).child("children").setValue(0);

                                Toast.makeText(getApplicationContext(), "Your new class has been added", Toast.LENGTH_SHORT).show();
                                RefreshClassList();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "You have been assigned to this class already!", Toast.LENGTH_LONG).show();
                            }

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }


    private boolean isClassAssigned(String classList, String NewClass)
    {
        return classList.contains(NewClass);
    }

    private void RefreshClassList()
    {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.content_holder_staff, new Fragment_staff_class_list()).commit();
        //getFragmentManager().popBackStack(null, getFragmentManager().POP_BACK_STACK_INCLUSIVE);
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


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.logout_staff) {
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
