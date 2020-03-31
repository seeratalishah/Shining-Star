package com.example.shiningstar;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParentAddChild extends Fragment {

    private Toolbar toolbar;

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

        final EditText childID = (EditText) v.findViewById(R.id.editText_addChild);
        Button addChildButton = (Button) v.findViewById(R.id.button_addChild);
        addChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(childID.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"Enter Child ID",Toast.LENGTH_SHORT).show();
                } else {
                    addChildIfExixts(childID.getText().toString());
                }
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

    private void addChildIfExixts(final String childID){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        DatabaseReference childrenTb = db.child("children");
        childrenTb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                addChildToParent(childID);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    private void addChildToParent(final String childId){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String uid = auth.getCurrentUser().getUid();
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference parent = db.child("parents").child(user.getUid());
        final DatabaseReference children = db.child("children");
        parent.child("children").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String currentChildren = dataSnapshot.getValue().toString();


                if(currentChildren.equals("0"))
                {
                    parent.child("children").setValue(childId+',');
                    children.child(childId).child("child_name").setValue(0);
                    Toast.makeText(getContext(),"Child added",Toast.LENGTH_SHORT).show();
                    GoBackToChildList();
                }
                else if(!isClassAssigned(currentChildren, childId)) {
                    currentChildren += childId + ",";
                    parent.child("children").setValue(childId+',');
                    children.child(childId).child("child_name").setValue(0);
                    Toast.makeText(getContext(),"Child added",Toast.LENGTH_SHORT).show();
                    GoBackToChildList();
                }
                else {
                    Toast.makeText(getContext(), "You have been assigned to this class already!", Toast.LENGTH_LONG).show();
                }


            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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
