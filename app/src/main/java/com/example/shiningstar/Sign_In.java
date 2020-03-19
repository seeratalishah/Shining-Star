package com.example.shiningstar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Sign_In extends AppCompatActivity {

    Button admin;
    Button parent;
    Button staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__in);

        admin = findViewById(R.id.signinadminbutton);
        parent = findViewById(R.id.signinparentbutton);
        staff = findViewById(R.id.loginstaffbutton);


        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Sign_In.this,Login_Admin.class);
                startActivity(i);
            }
        });

        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Sign_In.this,Login_Parent.class);
                startActivity(i);
            }
        });

        staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Sign_In.this,Login_Staff.class);
                startActivity(i);
            }
        });

    }
}
