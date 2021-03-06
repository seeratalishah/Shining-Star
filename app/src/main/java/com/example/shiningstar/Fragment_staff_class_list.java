package com.example.shiningstar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

        CollapsingToolbarLayout myCollaps = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        myCollaps.setExpandedTitleTextAppearance(R.style.MyToolbarTheme);
        myCollaps.setCollapsedTitleTextAppearance(R.style.MyToolbarTheme);

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
                            .addToBackStack("a").commit();
                }
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
                    String[] noclasses = {"There are no rooms yet"};
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
        return super.onOptionsItemSelected(item);
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }

}
