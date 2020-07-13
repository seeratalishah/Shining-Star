package com.example.shiningstar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class Payment_s extends AppCompatActivity {

    ImageView image;
    TextView accno;
    Button sendrec;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseStorage storage;
    StorageReference storageReference;
    private String uid;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        image = findViewById(R.id.receipt);
        accno = findViewById(R.id.accno);
        sendrec = findViewById(R.id.sendrec);



        accno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialer();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });


        sendrec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();

            }
        });



    }


    private void dialer()
    {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(String.valueOf(accno)));
        startActivity(intent);
    }


    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    private void upload()
    {


        FirebaseAuth auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        user = auth.getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();


        if(image.getDrawable() == null)
        {
            Toast.makeText(getApplicationContext(),"Upload Image first",Toast.LENGTH_SHORT).show();
        }

        else {

            final String receiptID = db.push().getKey();

            final DatabaseReference staff = db.child("staff");


            staff.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    storage = FirebaseStorage.getInstance();
                    storageReference = storage.getReference();

                    String email = dataSnapshot.child("email").getValue().toString();

                    staff.child(uid).child("receiptID").setValue(receiptID);

                    String a = email + "_" + receiptID;

                    if(filePath != null)
                    {
                        final ProgressDialog progressDialog = new ProgressDialog(Payment_s.this);
                        progressDialog.setTitle("Uploading...");
                        progressDialog.show();


                        StorageReference ref = FirebaseStorage.getInstance().getReference().child("images").child(a);
                        ref.putFile(filePath)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        progressDialog.dismiss();


                                        Toast.makeText(Payment_s.this,"Receipt Sent Successfully", Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(Payment_s.this,Staff.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();

                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                                .getTotalByteCount());
                                        progressDialog.setMessage("Sending");
                                    }
                                });
                    }


                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(Payment_s.this, Staff.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }


}

