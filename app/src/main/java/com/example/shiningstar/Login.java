package com.example.shiningstar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    EditText name;
    EditText userid;
    EditText password;

    TextView forgotpass;

    Button signin;
    Button signup;

    RadioButton admin;
    RadioButton parent;
    RadioButton staff;

    private CheckBox keep_loggedin;

    ProgressBar pb;

    FirebaseAuth firebaseAuth;

    private PrefManager pref;

    private String email,pass,type;

    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        FirebaseApp.initializeApp(this);

        pref = new PrefManager(this);

        progressDialog = new ProgressDialog(this);

        name = findViewById(R.id.name);
        userid = findViewById(R.id.email);
        password = findViewById(R.id.password);

        signin = findViewById(R.id.signinbtn);
        signup = findViewById(R.id.signupbtn);

        admin = findViewById(R.id.admin_radio);
        parent = findViewById(R.id.parent_radio);
        staff = findViewById(R.id.staff_radio);

        keep_loggedin = findViewById(R.id.keeplogin_chkbox);

        forgotpass = findViewById(R.id.forgotpass);

        pb = findViewById(R.id.pb);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference databaseReference;

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validate();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, RegisterUser.class);
                startActivity(intent);
            }
        });

        if(pref.isDataSet()){LoginPressed();}

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, ForgotPass.class);
                startActivity(intent);
            }
        });


    }

    public void LoginPressed() {

        if(pref.isDataSet()){
            String[] loginData = pref.getLoginData();
            email = loginData[0];
            pass = loginData[1];
        } else {
            email = userid.getText().toString();
            pass = password.getText().toString();
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "User Id cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setTitle("Authenticating");
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        String uid = firebaseAuth.getCurrentUser().getUid();
                        checkUserType(uid);
                    }
                    else{
                        //Toast.makeText(Login.this, "Please verify your email address", Toast.LENGTH_LONG).show();
                        Toast.makeText(Login.this,"Please verify your Email Address",Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    Toast.makeText(Login.this, "Email or Password Incorrect", Toast.LENGTH_SHORT).show();
                    pref.resetData();
                }
            }
        });
    }


    private void checkUserType(String uid)
    {
        final boolean result = false;
        if(pref.isDataSet()){
            String[] loginData = pref.getLoginData();
            type = loginData[2];
            if(type.equalsIgnoreCase("staff")) {staff.setChecked(true);}
            else if(type.equalsIgnoreCase("parents")) {parent.setChecked(true);}
            else if(type.equalsIgnoreCase("admins")) {admin.setChecked(true);}
        }

        if(staff.isChecked())
        {
            type = "staff";
            Intent i = new Intent(this, StaffDashboard.class);
            LoginAs("staff", uid, i);
        }

        else if (parent.isChecked())
        {
            type = "parents";
            Intent i = new Intent(this, ParentDashboard.class);
            LoginAs("parents", uid, i);
        }

        else if (admin.isChecked())
        {
            type = "admins";
            Intent i = new Intent(this, Admin.class);
            LoginAs("admins", uid, i);
        }
    }


    private void LoginAs(final String usertype, final String uid, final Intent i)
    {

        databaseReference.child(usertype).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid)) {
                    if(!pref.isDataSet() && keep_loggedin.isChecked()){
                        pref.setLoginData(email,pass,type);
                    }
                    startActivity(i);
                    finish();
                }
                else
                {
                    Toast.makeText(Login.this, "You do not have access to " + usertype + " account", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    pref.resetData();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    private void validate(){
        if(!EmailValidator.getInstance().validate(userid.getText().toString().trim())){
            userid.setError("Invalid email address");
        }
        else if(password.length()==0 )
        {
            password.setError("Enter password");
        }
        else{
            LoginPressed();
        }
    }
}
