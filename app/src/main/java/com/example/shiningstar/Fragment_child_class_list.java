package com.example.shiningstar;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_child_class_list extends Fragment {

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference classes = db.child("classes");
    DatabaseReference children = db.child("children");
    String currentClass = "";
    MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter();
    RecyclerView recyclerView;
    private final int PICK_IMAGE_REQUEST = 71;

    private ImageButton childImage;
    private Button btnChoose, btnUpload;
    private Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;

    private ProgressDialog progressDialog;

    private boolean HasClasses = false;

    public Fragment_child_class_list() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View v= inflater.inflate(R.layout.fragment_child_class_list, container, false);
        currentClass = ClassFragment.classid;

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.Button_AddChildToClass);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewChildToThisClass();
            }
        });

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String uid = firebaseAuth.getCurrentUser().getUid();


        classes.child(currentClass).child("children").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String class_children_string =  dataSnapshot.child("child_ids").getValue().toString();
                if(!class_children_string.equals("0")) {

                    String[] class_children_arr = class_children_string.split(",");
                    final List<HashMap<String,String>> childrenList = new ArrayList<>();
                    for(final String childInClass : class_children_arr){
                        children.child(childInClass).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                HashMap<String, String> childInfo = new HashMap<String, String>();
                                childInfo.put("name",name);
                                childInfo.put("id",childInClass);
                                childInfo.put("pic",dataSnapshot.child("pic").getValue().toString());
                                childrenList.add(childInfo);
                                adapter.setChildren(childrenList);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }

                }
                else
                {
                    AddNewChildToThisClass();
                }



                adapter.setItemClickListener(new MyRecyclerViewAdapter.RecyclerItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onItemClick(View v, HashMap<String, String> child) {
                        System.out.println("on list click: "+child.toString());

                        ImageView myPic = (ImageView) v.findViewById(R.id.child_list_pic);
                        myPic.setTransitionName("ProPicShared");
                        Fragment Myfrag = Fragment_child_feed.newInstance(child.get("id"),child.get("name"), child.get("pic"));

                        // Exit transition
                        Fade exitFade = new Fade();
                        exitFade.setDuration(500);
                        //setExitTransition(exitFade);

                        TransitionSet MoveImage = new TransitionSet();
                        MoveImage.addTransition(new ChangeTransform()).addTransition(new ChangeImageTransform());
                        MoveImage.addTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
                        MoveImage.setDuration(800);
                        //MoveImage.setStartDelay(100);
                        Myfrag.setSharedElementEnterTransition(MoveImage);
                        Fragment previous = getActivity().getSupportFragmentManager().findFragmentById(R.id.content_holder_staff);
                        previous.setExitTransition(new Explode().setDuration(700));
                        Myfrag.setEnterTransition(new Slide().setDuration(500).setStartDelay(700));
                        Myfrag.setExitTransition(new Explode().setDuration(700).setStartDelay(300));

                        // Transition for entire fragment
//                        Fade enterFade = new Fade();
//                        enterFade.setStartDelay(300);
//                        enterFade.setDuration(300);
//                        Myfrag.setEnterTransition(enterFade);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .addSharedElement(myPic, myPic.getTransitionName())
                                .replace(R.id.content_holder_staff, Myfrag)
                                .addToBackStack(null)
                                .commit();
                    }
                });
                recyclerView =(androidx.recyclerview.widget.RecyclerView) v.findViewById(R.id.child_class_list);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());
                recyclerView.addItemDecoration(dividerItemDecoration);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return v;
    }


    private void AddNewChildToThisClass()
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = firebaseAuth.getCurrentUser().getUid();

        LayoutInflater factory = LayoutInflater.from(getActivity());


        final View childaddview = factory.inflate(R.layout.children_data_layout, null);

        final EditText ChildCodes = (EditText) childaddview.findViewById(R.id.childid);
        final EditText ChildName = (EditText) childaddview.findViewById(R.id.childname);
        childImage = (ImageButton) childaddview.findViewById(R.id.childimage);
        btnChoose = (Button) childaddview.findViewById(R.id.btnChoose);
        btnUpload = (Button) childaddview.findViewById(R.id.btnUpload);


        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Add new child");
        alert.setMessage("Enter a valid child code assigned to you");


        childImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();

            }
        });


        alert.setView(childaddview);


        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                final String ChildCode = ChildCodes.getText().toString();
                final String Childname = ChildName.getText().toString();


                if(ChildCode.isEmpty())
                    Toast.makeText(getContext(), "Child Code cannot be empty!", Toast.LENGTH_SHORT).show();
                else
                {
                    final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                                String currChildren = dataSnapshot.child("classes").child(currentClass).child("children").getValue().toString();
                                if(currChildren.equals("0"))
                                {
                                    dataRef.child("classes").child(currentClass).child("children").setValue(ChildCode + ",");
                                    dataRef.child("classes").child(ChildCode).child("class_name").setValue(Childname);

                                }
                                else if(!isChildAssigned(currChildren, ChildCode)) {
                                    currChildren += ChildCode + ",";
                                    dataRef.child("classes").child(currentClass).child("children").setValue(currChildren);
                                    dataRef.child("classes").child(ChildCode).child("class_name").setValue(Childname);
                                    Toast.makeText(getContext(), "New child has been added", Toast.LENGTH_SHORT).show();
                                    //RefreshClassList();
                                }
                            else
                            {
                                Toast.makeText(getContext(), "Child code you have entered is wrong.", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();

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
                childImage.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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




    private boolean isChildAssigned(String childidList, String NewChildid)
    {
        return childidList.contains(NewChildid);
    }

}
