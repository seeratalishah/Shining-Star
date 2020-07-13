package com.example.shiningstar;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class StaffProfile extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private EditText names;
    private EditText email;
    private EditText password;

    Button update;

    FirebaseAuth firebaseAuth;

    DatabaseReference databaseReference;



    public StaffProfile() {
        // Required empty public constructor
    }


    public static ParentProfile newInstance(String param1, String param2) {
        ParentProfile fragment = new ParentProfile();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_staff_profile, container, false);


        names = view.findViewById(R.id.nameEdit);
        email = view.findViewById(R.id.emailEdit);
        password = view.findViewById(R.id.passwordEdit);

        update = view.findViewById(R.id.update);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        UserData();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
                updateEmail();
            }
        });

        return view;
    }


    private void UserData() {

        final String uid = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child("staff").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String n = dataSnapshot.child("name").getValue().toString();
                String e = dataSnapshot.child("email").getValue().toString();
                String p = dataSnapshot.child("password").getValue().toString();

                names.setText(n);
                email.setText(e);
                password.setText(p);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void UpdateData(){

        final String namesss = names.getText().toString().trim();
        final String emails = email.getText().toString().trim();
        final String passwords = password.getText().toString().trim();

        final String uid = firebaseAuth.getCurrentUser().getUid();

        databaseReference.child("staff").child(uid).child("name").setValue(namesss);
        databaseReference.child("staff").child(uid).child("email").setValue(emails);
        databaseReference.child("staff").child(uid).child("password").setValue(passwords);


    }


    private void validate(){
        if(names.length()==0){
            names.setError("Enter name");
        }
        else if(!EmailValidator.getInstance().validate(email.getText().toString().trim())){
            email.setError("Invalid email address");
        }
        else if(password.length()==0){
            password.setError("Enter password");
        }
        else if(password.length() < 6){
            password.setError("Password should be greater than 6 characters");
        }
        else{
            UpdateData();
            Toast.makeText(getContext(), "Data updated successfully",Toast.LENGTH_LONG).show();
        }
    }


    private void updateEmail(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String emails = email.getText().toString().trim();
        final String uid = firebaseAuth.getCurrentUser().getUid();

        user.updateEmail(emails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Email Updated, Check Verification Email", Toast.LENGTH_LONG).show();
                        }
                    }
                });

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });

        if(user.isEmailVerified()){
            databaseReference.child("staff").child(uid).child("email").setValue(emails);
        }

    }

}
