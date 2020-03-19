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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Parent extends AppCompatActivity {

    EditText email;
    EditText password;

    TextView forgotpass;
    Button signin;
    TextView signup;
    ProgressBar pb;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__parent);

        email = findViewById(R.id.emailparent);
        password = findViewById(R.id.passwordparent);
        signin =  findViewById(R.id.signinbuttonparent);
        signup =  findViewById(R.id.textViewregisterparent);
        forgotpass =  findViewById(R.id.forgotparent);
        pb =  findViewById(R.id.pb);

        firebaseAuth = FirebaseAuth.getInstance();


        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Parent.this, ForgotPass.class);
                startActivity(intent);
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

    }

    private void parentLogin(){
        pb.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        pb.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                Intent intent = new Intent (Login_Parent.this, Parent.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                            else{
                                //Toast.makeText(Login.this, "Please verify your email address", Toast.LENGTH_LONG).show();
                                Toast.makeText(Login_Parent.this,"Please verify your email address", Toast.LENGTH_SHORT).show();

                            }

                        }
                        else{
                            //Toast.makeText(Login.this, "Email or Password Incorrect!",Toast.LENGTH_LONG).show();
                            Toast.makeText(Login_Parent.this,"Email or Password Incorrect!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void validate(){
        if(!EmailValidator.getInstance().validate(email.getText().toString().trim())){
            email.setError("Invalid email address");
        }
        else if(password.length()==0 )
        {
            password.setError("Enter password");
        }
        else{
            parentLogin();
        }
    }
}
