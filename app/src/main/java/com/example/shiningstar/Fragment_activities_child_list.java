package com.example.shiningstar;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_activities_child_list extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private Button NextButton;
    private String CurrentClassId, ActivityType;
    private ListView ChildNameLS;
    private String[] AllChildIds;

    public Fragment_activities_child_list() {
        // Required empty public constructor
    }

    public static Fragment_activities_child_list newInstance(String param1) {
        Fragment_activities_child_list fragment = new Fragment_activities_child_list();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ActivityType = getArguments().getString(ARG_PARAM1);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_activities_child_list, container, false);

        CurrentClassId = ClassFragment.classid;
        ChildNameLS = (ListView) view.findViewById(R.id.currclass_childList);
        getCheckinStudents();

        NextButton = (Button) view.findViewById(R.id.button_Next);

        NextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taggedchild = GetSelectedChild();

                if(!taggedchild.equals("error"))
                    LaunchIt(taggedchild);
            }
        });

        return view;
    }


    private String GetSelectedChild()
    {
        String TaggedChild = "";
        int itemCount = ChildNameLS.getChildCount();
        for(int i = 0; i < itemCount; i++)
        {
            CheckedTextView c = (CheckedTextView) ChildNameLS.getChildAt(i);
            if(c.isChecked())
            {
                String child = c.getText().toString();
                String childId = AllChildIds[i];
                TaggedChild += childId  + "/" + child + ",";
            }
        }
        if(TaggedChild.equals("")) {
            Toast.makeText(getContext(), "No child selected", Toast.LENGTH_SHORT).show();
            return "error";
        }
        else
            return TaggedChild;
    }


    private void getCheckinStudents()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String AllChildIds = dataSnapshot.child("classes").child(CurrentClassId).child("children").getValue().toString();
                String[] AllChildIdsArray = AllChildIds.split(",");
                setChildIdsList(AllChildIdsArray);
                List<String> AllChildNamesList = new ArrayList<String>();

                if(dataSnapshot.hasChild("children"))
                {
                    for(String id : AllChildIdsArray)
                    {
                        String name = dataSnapshot.child("children").child(id).child("name").getValue().toString();
                        AllChildNamesList.add(name);
                    }
                    setListView(AllChildNamesList);
                }
                else{

                    String[] noclasses = {"There are no children yet"};
                    ListAdapter myListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, noclasses);
                    ChildNameLS.setAdapter(myListAdapter);
                    NextButton.setVisibility(View.GONE);

                }





            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void setChildIdsList(String[] AllIds)
    {
        AllChildIds = AllIds;
    }

    private void setListView(List<String> NamesList)
    {
        ArrayAdapter adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_checked, NamesList);
        ChildNameLS.setAdapter(adapter1);
        ChildNameLS.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }


    private void LaunchIt(String taggedChildren)
    {
        Fragment MyFragment;
        switch (ActivityType)
        {
            case "customActivity":
                MyFragment = Fragment_child_custom_activity.newInstance(taggedChildren);
                break;
            case "Note":
                MyFragment = Fragment_note_activity.newInstance(taggedChildren);
                break;
            case "Food":
                MyFragment = Fragment_food_activity.newInstance(taggedChildren);
                break;
            case "Nap":
                MyFragment = Fragment_nap_activity.newInstance(taggedChildren);
                break;
            case "Meds":
                MyFragment = Fragment_meds_activity.newInstance(taggedChildren);
                break;
            case "Photo":
                MyFragment = Fragment_photo_activity.newInstance(taggedChildren);
                break;
            default:
                MyFragment = new Fragment_child_custom_activity();
                break;
        }
        getActivity().getSupportFragmentManager().beginTransaction().add(
                R.id.content_holder_staff, MyFragment).addToBackStack("ActFrag").commit();
    }

}
