package com.example.shiningstar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {

    EditText userEmail;
    Button forgotpassbtn;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);


        userEmail = findViewById(R.id.forgotpassemail);
        forgotpassbtn = findViewById(R.id.submitbtn);

        firebaseAuth = FirebaseAuth.getInstance();

        forgotpassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
    }

    private void forgotPassword(){
        firebaseAuth.sendPasswordResetEmail(userEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(ForgotPass.this,"Password sent to your email",Toast.LENGTH_LONG).show();
                            Toast.makeText(ForgotPass.this,"Password sent to your email", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ForgotPass.this, Login.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(ForgotPass.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void validate(){
        if(!EmailValidator.getInstance().validate(userEmail.getText().toString().trim())){
            userEmail.setError("Invalid email address");
        }
        else{
            forgotPassword();
        }
    }
}
