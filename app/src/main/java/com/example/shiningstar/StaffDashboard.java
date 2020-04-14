package com.example.shiningstar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StaffDashboard extends AppCompatActivity {

    Fragment_staff_class_list f = new Fragment_staff_class_list();

    TextView name_staff;
    TextView email_staff;

    ImageButton add_class;
    ImageButton view_class;
    ImageButton profile;

    Button logout;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    FragmentManager fragmentmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        name_staff = findViewById(R.id.name);
        email_staff = findViewById(R.id.email);

        add_class = findViewById(R.id.add_class_staff);
        view_class = findViewById(R.id.view_class_staff);
        profile = findViewById(R.id.profile_staff);

        logout = findViewById(R.id.logoutbtn);

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
                trans.replace(R.id.staff_dashboard, createFrag);
                trans.addToBackStack("a");
                trans.commit();

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

        email_staff.setText(email_curr);

    }

    public void AddNewClass()
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = firebaseAuth.getCurrentUser().getUid();

        LayoutInflater factory = LayoutInflater.from(StaffDashboard.this);


        final View textEntryView = factory.inflate(R.layout.class_data_layout, null);

        final EditText id = (EditText) textEntryView.findViewById(R.id.classid);
        final EditText name = (EditText) textEntryView.findViewById(R.id.classname);
        final EditText room = (EditText) textEntryView.findViewById(R.id.classroom);

        AlertDialog.Builder alert = new AlertDialog.Builder(StaffDashboard.this);
        alert.setTitle("Add new class");
        alert.setMessage("Enter class details");



        alert.setView(textEntryView);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                final String Classcode = id.getText().toString();
                final String Classname = name.getText().toString();
                final String Classroom = room.getText().toString();

                if(Classcode.isEmpty())
                    Toast.makeText(getApplicationContext(), "Class Code cannot be empty!", Toast.LENGTH_SHORT).show();
                else
                {
                    final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String currClasses = dataSnapshot.child("staff").child(uid).child("class_ids").getValue().toString();

                            if(currClasses.equals("0"))
                            {
                                dataRef.child("staff").child(uid).child("class_ids").setValue(Classcode + ",");
                                dataRef.child("classes").child(Classcode).child("class_name").setValue(Classname);
                                dataRef.child("classes").child(Classcode).child("class_room").setValue(Classroom);

                                dataRef.child("classes").child(Classcode).child("checked_in").setValue(0);
                                dataRef.child("classes").child(Classcode).child("checked_out").setValue(0);
                                dataRef.child("classes").child(Classcode).child("absent").setValue(0);

                                dataRef.child("classes").child(Classcode).child("children").setValue(0);

                                RefreshClassList();
                            }
                            else if(!isClassAssigned(currClasses, Classcode)) {
                                currClasses += Classcode + ",";
                                dataRef.child("staff").child(uid).child("class_ids").setValue(currClasses);
                                dataRef.child("classes").child(Classcode).child("class_name").setValue(Classname);
                                dataRef.child("classes").child(Classcode).child("class_room").setValue(Classroom);

                                dataRef.child("classes").child(Classcode).child("checked_in").setValue(0);
                                dataRef.child("classes").child(Classcode).child("checked_out").setValue(0);
                                dataRef.child("classes").child(Classcode).child("absent").setValue(0);


                                dataRef.child("classes").child(Classcode).child("children").setValue(0);

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
                R.id.staff_dashboard, new Fragment_staff_class_list()).commit();
        //getFragmentManager().popBackStack(null, getFragmentManager().POP_BACK_STACK_INCLUSIVE);
    }


}
