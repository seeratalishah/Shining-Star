package com.example.shiningstar;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_child_class_list extends Fragment {

    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference classesTb = db.child("classes");
    DatabaseReference childrenTb = db.child("children");
    String currentClass = "";
    MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter();
    RecyclerView recyclerView;

    TextView nochild;

    private boolean HasClasses = false;



    public Fragment_child_class_list() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
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

        nochild = v.findViewById(R.id.nochildtext);

        classesTb.child(currentClass).child("children").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String class_children_string =  dataSnapshot.getValue().toString();

                if(!class_children_string.equals("0")) {
                    HasClasses = true;

                    String[] class_children_arr = class_children_string.split(",");
                    final List<HashMap<String,String>> childrenList = new ArrayList<>();
                    for(final String childInClass : class_children_arr){
                        childrenTb.child(childInClass).addListenerForSingleValueEvent(new ValueEventListener() {
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


                    adapter.setItemClickListener(new MyRecyclerViewAdapter.RecyclerItemClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onItemClick(View v, HashMap<String, String> child) {

                            if (adapter == null) {
                                recyclerView.setVisibility(View.GONE);
                                nochild.setVisibility(View.VISIBLE);

                            }
                            else {
                                recyclerView.setVisibility(View.VISIBLE);
                                nochild.setVisibility(View.GONE);

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

                        }
                    });


                    recyclerView =(androidx.recyclerview.widget.RecyclerView) v.findViewById(R.id.child_class_list);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), layoutManager.getOrientation());
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(adapter);


                }
                else
                {
                    HasClasses = false;

                    View v= inflater.inflate(R.layout.no_child, container, false);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        return v;
    }


    private void AddNewChildToThisClass()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Add new child to this class");
        alert.setMessage("Enter a valid child code of the child to be added to this class");

        final EditText input = new EditText(getContext());
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final String ChildCode = input.getText().toString();
                if(ChildCode.isEmpty())
                    Toast.makeText(getContext(), "Child Code cannot be empty!", Toast.LENGTH_SHORT).show();
                else
                {
                    final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("children").child(ChildCode).exists())
                            {
                                String currChildren = dataSnapshot.child("classes").child(currentClass).child("children").getValue().toString();
                                if(currChildren.equals("0"))
                                {
                                    dataRef.child("classes").child(currentClass).child("children").setValue(ChildCode + ",");
                                    //RefreshClassList();
                                }
                                else if(!isChildAssigned(currChildren, ChildCode)) {
                                    currChildren += ChildCode + ",";
                                    dataRef.child("classes").child(currentClass).child("children").setValue(currChildren);
                                    Toast.makeText(getContext(), "New child with id '" + ChildCode + "' has been added", Toast.LENGTH_SHORT).show();
                                    //RefreshClassList();
                                }
                                else {
                                    Toast.makeText(getContext(), "This child has been Enrolled to this class already!", Toast.LENGTH_LONG).show();
                                }
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


    private boolean isChildAssigned(String childidList, String NewChildid)
    {
        return childidList.contains(NewChildid);
    }

}
