package com.example.shiningstar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements Fragment_staff_class_list.OnFragmentInteractionListener {

    Button signin;
    Button signup;

    public void onFragmentInteraction(Uri uri){

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PrefManager pref = new PrefManager(this);

        if(pref.isDataSet()){
            signin(findViewById(R.id.signinbtn));
        }

        signin = findViewById(R.id.signinbutton);
        signup = findViewById(R.id.signupbtn);


       signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,Login.class);
                startActivity(i);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,RegisterUser.class);
                startActivity(i);
            }
        });

    }

    private void signin(View viewById) {
        
    }
}
