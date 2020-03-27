package com.example.shiningstar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText password;

    Button signin;
    Button signup;

    RadioButton admin;
    RadioButton parent;
    RadioButton staff;

    ProgressBar pb;

    FirebaseAuth firebaseAuth;

    DatabaseReference databaseReference;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        FirebaseApp.initializeApp(this);

        progressDialog = new ProgressDialog(this);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        signin = findViewById(R.id.signinbtn);
        signup = findViewById(R.id.signupbtn);

        admin = findViewById(R.id.admin_radio);
        parent = findViewById(R.id.parent_radio);
        staff = findViewById(R.id.staff_radio);

        pb = findViewById(R.id.pb);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getInstance().signOut();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validate();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterUser.this, Login.class);
                startActivity(intent);
            }
        });

    }

    private void UserType()
    {

        if(admin.isChecked())
        {
            final String names = name.getText().toString().trim();
            final String emails = email.getText().toString().trim();
            final String passwords = password.getText().toString().trim();

            progressDialog.setTitle("Registering");
            progressDialog.setMessage("Creating your account...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(emails, passwords)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {

                                AdminData data = new AdminData(
                                        names,
                                        emails,
                                        passwords
                                );

                                FirebaseDatabase.getInstance().getReference("admins")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                   /* Toast.makeText(Reg.this, "Registeration Successful, Please " +
                                                                    "check your email for verification.",
                                                            Toast.LENGTH_LONG).show(); */
                                                                Toast.makeText(RegisterUser.this,"Registeration Successful, Please check your email for verification.",
                                                                        Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(RegisterUser.this, Login.class);
                                                                startActivity(intent);
                                                            }
                                                            else{
                                                                Toast.makeText(RegisterUser.this,task.getException().getMessage(),
                                                                        Toast.LENGTH_LONG).show();
                                                            }

                                                        }
                                                    });
                                        }
                                        else{
                                            Toast.makeText(RegisterUser.this,"Signup Failed!",Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(RegisterUser.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();

                            }
                        }
                    });

        }

        else if (parent.isChecked())
        {
            final String names = name.getText().toString().trim();
            final String emails = email.getText().toString().trim();
            final String passwords = password.getText().toString().trim();


            progressDialog.setTitle("Registering");
            progressDialog.setMessage("Creating your account...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(emails, passwords)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {

                                final String uid = firebaseAuth.getCurrentUser().getUid();

                                AddUserName(names, uid);

                                ParentData data = new ParentData(
                                        names,
                                        emails,
                                        passwords
                                );

                                FirebaseDatabase.getInstance().getReference("parents")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            firebaseAuth.getCurrentUser().sendEmailVerification()
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                   /* Toast.makeText(Reg.this, "Registeration Successful, Please " +
                                                                    "check your email for verification.",
                                                            Toast.LENGTH_LONG).show(); */
                                                                Toast.makeText(RegisterUser.this,"Registeration Successful, Please check your email for verification.",
                                                                        Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(RegisterUser.this, Login.class);
                                                                startActivity(intent);
                                                            }
                                                            else{
                                                                Toast.makeText(RegisterUser.this,task.getException().getMessage(),
                                                                        Toast.LENGTH_LONG).show();
                                                            }

                                                        }
                                                    });
                                        }
                                        else{
                                            Toast.makeText(RegisterUser.this,"Signup Failed!",Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });
                            }
                            else{
                                Toast.makeText(RegisterUser.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();

                            }
                        }
                    });
        }

        else {
                final String names = name.getText().toString().trim();
                final String emails = email.getText().toString().trim();
                final String passwords = password.getText().toString().trim();


            progressDialog.setTitle("Registering");
            progressDialog.setMessage("Creating your account...");
            progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(emails, passwords)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {

                                    final String uid = firebaseAuth.getCurrentUser().getUid();

                                    AddUserName(names, uid);

                                    StaffData data = new StaffData(
                                            names,
                                            emails,
                                            passwords
                                    );

                                    FirebaseDatabase.getInstance().getReference("staff")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                firebaseAuth.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                   /* Toast.makeText(Reg.this, "Registeration Successful, Please " +
                                                                    "check your email for verification.",
                                                            Toast.LENGTH_LONG).show(); */
                                                                    Toast.makeText(RegisterUser.this,"Registeration Successful, Please check your email for verification.",
                                                                            Toast.LENGTH_SHORT).show();
                                                                    Intent intent = new Intent(RegisterUser.this, Login.class);
                                                                    startActivity(intent);
                                                                }
                                                                else{
                                                                    Toast.makeText(RegisterUser.this,task.getException().getMessage(),
                                                                            Toast.LENGTH_LONG).show();
                                                                }

                                                            }
                                                        });
                                            }
                                            else{
                                                Toast.makeText(RegisterUser.this,"Signup Failed!",Toast.LENGTH_LONG).show();

                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(RegisterUser.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();

                                }
                            }
                        });
            }
    }


    private void validate(){
        if(name.length()==0){
            name.setError("Enter name");
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
            UserType();
        }
    }

    private void AddUserName(String username, String uid)
    {
        if(staff.isChecked())
        {
            databaseReference.child("staff").child(uid).child("name").setValue(username);
            databaseReference.child("staff").child(uid).child("class_ids").setValue("0");
        }

        else if (parent.isChecked())
        {
            databaseReference.child("parents").child(uid).child("name").setValue(username);
            databaseReference.child("parents").child(uid).child("children").setValue("");
        }
    }

}
