package com.example.shiningstar;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentAddChild extends Fragment {

    private Toolbar toolbar;

    FirebaseStorage storage;
    StorageReference storageReference;

    private String uid;
    private FirebaseUser user;

    DatabaseReference db;

    String currentClass = "";

    private EditText childID, childName;
    private ImageButton imageChild;

    private String name;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    public ParentAddChild() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_parent_add_child, container, false);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_parent);
        toolbar.setTitle("Add Child");

        childID = (EditText) v.findViewById(R.id.editText_addChild);
        childName = (EditText) v.findViewById(R.id.childname);
        imageChild = v.findViewById(R.id.imagechild);
        Button addChildButton = (Button) v.findViewById(R.id.button_addChild);

        imageChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();
            }
        });

        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    addChildIfExixts();
            }
        });

        return v;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        toolbar.getMenu().clear();
        inflater.inflate(R.menu.empty_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void addChildIfExixts(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference childrenTb = db.child("children");
        childrenTb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                addChildToParent();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void addChildToParent(){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        uid = auth.getCurrentUser().getUid();
        user = auth.getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();


        if(childID.getText().toString().isEmpty()){
            Toast.makeText(getContext(),"Enter Child ID",Toast.LENGTH_SHORT).show();
        }
        else if(imageChild.getDrawable() == null)
        {
            Toast.makeText(getContext(),"Upload Image first",Toast.LENGTH_SHORT).show();
        }

        final String childId = childID.getText().toString();
        name = childName.getText().toString();

        final DatabaseReference parent = db.child("parents");
        final DatabaseReference children = db.child("children");
        final DatabaseReference classes = db.child("classes");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String currentChildren = dataSnapshot.child("parents").child("children").getValue().toString();

                String AllClasses = dataSnapshot.child("staff").child("class_ids").getValue().toString();


                String[] myList = AllClasses.split(",");


                storage = FirebaseStorage.getInstance();
                storageReference = storage.getReference();


                if(currentChildren.equals("0"))
                {
                    parent.child("children").setValue(childId+',');

                    if(dataSnapshot.hasChild("classes")){

                        for (String currClassID : myList) {
                            classes.child(currClassID).child("children").setValue(childId+',');
                        }


                    }

                    children.child(childId).child("name").setValue(name);
                    children.child(childId).child("id").setValue(childId);
                    children.child(childId).child("pic").setValue(name);
                    children.child(childId).child("act_ids").setValue(0);
                    Toast.makeText(getContext(),"Child added",Toast.LENGTH_SHORT).show();
                    GoBackToChildList();
                }
                else if(!isClassAssigned(currentChildren, childId)) {
                    currentChildren += childId + ",";
                    parent.child("children").setValue(currentChildren);

                    if(dataSnapshot.hasChild("classes")){

                        for (String currClassID : myList) {
                            classes.child(currClassID).child("children").setValue(currentChildren);
                        }


                    }


                    children.child(childId).child("name").setValue(name);
                    children.child(childId).child("id").setValue(childId);
                    children.child(childId).child("pic").setValue(name);
                    children.child(childId).child("act_ids").setValue(0);
                    Toast.makeText(getContext(),"Child added",Toast.LENGTH_SHORT).show();
                    GoBackToChildList();
                }
                else {
                    Toast.makeText(getContext(), "A child already exists with this ID", Toast.LENGTH_LONG).show();
                }


                if(filePath != null)
                {
                    final ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setTitle("Uploading...");
                    progressDialog.show();


                    StorageReference ref = FirebaseStorage.getInstance().getReference().child("images").child(name);
                    ref.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();

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
                                    progressDialog.setMessage("Uploaded "+(int)progress+"%");
                                }
                            });
                }



            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageChild.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    private boolean isClassAssigned(String classList, String NewClass)
    {
        return classList.contains(NewClass);
    }


    private void GoBackToChildList()
    {
        getActivity().getSupportFragmentManager().beginTransaction().replace(
                R.id.content_holder_parent, new ParentChildList()).addToBackStack(null).commit();
        getFragmentManager().popBackStack(null, getFragmentManager().POP_BACK_STACK_INCLUSIVE);
    }
    @Override
    public void onDestroy() {
        toolbar.setTitle("My Children");
        super.onDestroy();
    }
}
