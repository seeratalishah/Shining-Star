package com.example.shiningstar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Sign_Up extends AppCompatActivity {

    Button admin;
    Button parent;
    Button staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up);


        admin = findViewById(R.id.signupadminbutton);
        parent = findViewById(R.id.signupparentbutton);
        staff = findViewById(R.id.signupstaffbutton);


        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Sign_Up.this,Register_Admin.class);
                startActivity(i);
            }
        });

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Sign_Up.this,Register_Parent.class);
                startActivity(i);
            }
        });

        staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Sign_Up.this,Register_Staff.class);
                startActivity(i);
            }
        });

    }
}
