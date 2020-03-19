package com.example.shiningstar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register_Parent extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText password;
    Button signup;
    TextView signin;
    ProgressBar pb;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register__parent);

        FirebaseApp.initializeApp(this);

        firebaseAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.editnameparent);
        email = findViewById(R.id.editemailparent);
        password = findViewById(R.id.editnpasswordparent);
        signup = findViewById(R.id.signupbuttonparent);
        signin = findViewById(R.id.textViewsigninparent);
        pb = findViewById(R.id.pb);



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validate();
            }
        });


    }

    private void saveParentData()
    {
        final String names = name.getText().toString().trim();
        final String emails = email.getText().toString().trim();
        final String passwords = password.getText().toString().trim();

        pb.setVisibility(View.VISIBLE);
        firebaseAuth.createUserWithEmailAndPassword(emails, passwords)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pb.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            AuthenticationData parent = new AuthenticationData(
                                    names,
                                    emails,
                                    passwords
                            );

                            FirebaseDatabase.getInstance().getReference("parents")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(parent).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                                                            Toast.makeText(Register_Parent.this,"Registeration Successful, Please check your email for verification.",
                                                                    Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(Register_Parent.this, Login_Parent.class);
                                                            startActivity(intent);
                                                        }
                                                        else{
                                                            Toast.makeText(Register_Parent.this,task.getException().getMessage(),
                                                                    Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                });
                                    }
                                    else{
                                        Toast.makeText(Register_Parent.this,"Signup Failed!",Toast.LENGTH_LONG).show();

                                    }
                                }
                            });


                        }
                        else{
                            Toast.makeText(Register_Parent.this, task.getException().getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }
                });

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
            saveParentData();
        }
    }
}
