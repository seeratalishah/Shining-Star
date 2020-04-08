package com.example.shiningstar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_staff_class_list.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_staff_class_list#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_staff_class_list extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ListView ClassListView;
    private List<String> AllClassesList = new ArrayList<>();
    private boolean HasClasses = false;

    private Toolbar toolbar;

    private ImageButton addClass;


    private OnFragmentInteractionListener mListener;

    public Fragment_staff_class_list() {
        // Required empty public constructor
    }

    public static Fragment_staff_class_list newInstance(String param1, String param2) {
        Fragment_staff_class_list fragment = new Fragment_staff_class_list();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_staff_class_list, container, false);
        setHasOptionsMenu(true);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar_staff_class_list);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        addClass = view.findViewById(R.id.action_addClass);

        CollapsingToolbarLayout myCollaps = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        myCollaps.setExpandedTitleTextAppearance(R.style.MyToolbarTheme);
        myCollaps.setCollapsedTitleTextAppearance(R.style.MyToolbarTheme);


        Toolbar toolbar =(Toolbar) getActivity().findViewById(R.id.toolbar_staff);
        toolbar.setTitle("Classes List");
        ClassListView = (ListView) view.findViewById(R.id.class_list_staff);

        GetClassList();
        ClassListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(HasClasses) {
                    String currClassId = AllClassesList.get(position);
                    Fragment classFrag = ClassFragment.newInstance(currClassId);
                    getFragmentManager().beginTransaction().replace(
                            R.id.content_holder_staff, classFrag)
                            .addToBackStack(null).commit();
                }
            }
        });



        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewClass();
            }
        });


        return view;
    }


    private void GetClassList()
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = firebaseAuth.getCurrentUser().getUid();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String AllClasses = dataSnapshot.child("staff").child(uid).child("class_ids").getValue().toString();
                if(!AllClasses.equals("0")) {
                    HasClasses = true;
                    String[] myList = AllClasses.split(",");
                    SetClassIdsList(myList);

                    List<String> ClassNameList = new ArrayList<String>();
                    List<String> ClassTimingsList = new ArrayList<String>();
                    for (String currClassID : myList) {
                        ClassNameList.add(dataSnapshot.child("classes").child(currClassID).child("class_name").getValue().toString());
                        ClassTimingsList.add(dataSnapshot.child("classes").child(currClassID).child("class_room").getValue().toString());
                    }
                    String[] ClassName = new String[ClassNameList.size()];
                    ClassNameList.toArray(ClassName);
                    String[] ClassTimings = new String[ClassTimingsList.size()];
                    ClassTimingsList.toArray(ClassTimings);
                    SetListVIew(ClassName, ClassTimings);
                }
                else
                {
                    HasClasses = false;
                    String[] noclasses = {"You are not assigned to any class yet"};
                    ListAdapter myListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, noclasses);
                    ClassListView.setAdapter(myListAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void SetClassIdsList(String[] ClassIds)
    {
        AllClassesList.clear();
        Collections.addAll(AllClassesList, ClassIds);
    }


    private void SetListVIew(String[] ClassNames, String[] ClassTimings)
    {
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
        for(int i = 0; i < ClassNames.length; i ++ )
        {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("one", ClassNames[i]);
            map.put("two", ClassTimings[i]);
            mylist.add(map);
        }
        // Keys used in Hashmap
        String[] from1 = {"one", "two"};
        // Ids of views in listview_layout
        int[] to1 = { R.id.ListItem_one, R.id.ListItem_two};

        SimpleAdapter adapter1 = new SimpleAdapter(getContext(), mylist, R.layout.custom_listview_frame, from1, to1);
        ClassListView.setAdapter(adapter1);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.staff_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_addClass) {
            AddNewClass();
        }
        return super.onOptionsItemSelected(item);
    }


    private void AddNewClass()
    {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final String uid = firebaseAuth.getCurrentUser().getUid();

        LayoutInflater factory = LayoutInflater.from(getActivity());


        final View textEntryView = factory.inflate(R.layout.class_data_layout, null);

        final EditText id = (EditText) textEntryView.findViewById(R.id.classid);
        final EditText name = (EditText) textEntryView.findViewById(R.id.classname);
        final EditText room = (EditText) textEntryView.findViewById(R.id.classroom);

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Add new class");
        alert.setMessage("Enter class details");



        alert.setView(textEntryView);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                final String Classcode = id.getText().toString();
                final String Classname = name.getText().toString();
                final String Classroom = room.getText().toString();

                if(Classcode.isEmpty())
                    Toast.makeText(getContext(), "Class Code cannot be empty!", Toast.LENGTH_SHORT).show();
                else
                {
                    final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
                    dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String currClasses = dataSnapshot.child("staff").child(uid).child("class_ids").getValue().toString();

                                if(currClasses.equals("0"))
                                {
                                    dataRef.child("staff").child(uid).child("class_ids").setValue(Classcode + ",");
                                    dataRef.child("classes").child(Classcode).child("class_name").setValue(Classname);
                                    dataRef.child("classes").child(Classcode).child("class_room").setValue(Classroom);

                                    dataRef.child("classes").child(Classcode).child("checked_in").setValue(0);
                                    dataRef.child("classes").child(Classcode).child("checked_out").setValue(0);
                                    dataRef.child("classes").child(Classcode).child("absent").setValue(0);

                                    dataRef.child("classes").child(Classcode).child("children").setValue(0);

                                    RefreshClassList();
                                }
                                else if(!isClassAssigned(currClasses, Classcode)) {
                                    currClasses += Classcode + ",";
                                    dataRef.child("staff").child(uid).child("class_ids").setValue(currClasses);
                                    dataRef.child("classes").child(Classcode).child("class_name").setValue(Classname);
                                    dataRef.child("classes").child(Classcode).child("class_room").setValue(Classroom);

                                    dataRef.child("classes").child(Classcode).child("checked_in").setValue(0);
                                    dataRef.child("classes").child(Classcode).child("checked_out").setValue(0);
                                    dataRef.child("classes").child(Classcode).child("absent").setValue(0);


                                   dataRef.child("classes").child(Classcode).child("children").setValue(0);

                                    Toast.makeText(getContext(), "Your new class has been added", Toast.LENGTH_SHORT).show();
                                    RefreshClassList();
                                }
                                else {
                                    Toast.makeText(getContext(), "You have been assigned to this class already!", Toast.LENGTH_LONG).show();
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


    private boolean isClassAssigned(String classList, String NewClass)
    {
        return classList.contains(NewClass);
    }

    private void RefreshClassList()
    {
        getActivity().getSupportFragmentManager().beginTransaction().replace(
                R.id.content_holder_staff, new Fragment_staff_class_list()).commit();
        //getFragmentManager().popBackStack(null, getFragmentManager().POP_BACK_STACK_INCLUSIVE);
    }





    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
