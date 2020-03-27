package com.example.shiningstar;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_activities_grid extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Fragment_activities_grid() {
        // Required empty public constructor
    }

    public static Fragment_activities_grid newInstance(String param1, String param2) {
        Fragment_activities_grid fragment = new Fragment_activities_grid();
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
        View view =  inflater.inflate(R.layout.fragment_activities_grid, container, false);

        // Registering all buttons
        ImageButton PhotoButton = (ImageButton) view.findViewById(R.id.imgBtn_photo);
        PhotoButton.setOnClickListener((View.OnClickListener) this);

        ImageButton ActivitiesButton = (ImageButton) view.findViewById(R.id.imgBtn_Activities);
        ActivitiesButton.setOnClickListener((View.OnClickListener) this);

        ImageButton NoteButton = (ImageButton) view.findViewById(R.id.imgBtn_Note);
        NoteButton.setOnClickListener((View.OnClickListener) this);

        ImageButton FoodButton = (ImageButton) view.findViewById(R.id.imgBtn_Food);
        FoodButton.setOnClickListener((View.OnClickListener) this);

        ImageButton NapButton = (ImageButton) view.findViewById(R.id.imgBtn_Nap);
        NapButton.setOnClickListener((View.OnClickListener) this);

        ImageButton MedsButton = (ImageButton) view.findViewById(R.id.imgBtn_Meds);
        MedsButton.setOnClickListener((View.OnClickListener) this);



        return view;
    }


    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgBtn_photo:
                Fragment PhotoFrag = Fragment_activities_child_list.newInstance("Photo");
                getActivity().getSupportFragmentManager().beginTransaction().add(
                        R.id.content_holder_staff, PhotoFrag).addToBackStack("TagList").commit();
                break;
            case R.id.imgBtn_Activities:
                Fragment ActFrag = Fragment_activities_child_list.newInstance("customActivity");
                getActivity().getSupportFragmentManager().beginTransaction().add(
                        R.id.content_holder_staff, ActFrag).addToBackStack("TagList").commit();
                break;
            case R.id.imgBtn_Note:
                Fragment NoteFrag = Fragment_activities_child_list.newInstance("Note");
                getActivity().getSupportFragmentManager().beginTransaction().add(
                        R.id.content_holder_staff, NoteFrag).addToBackStack("TagList").commit();
                break;
            case R.id.imgBtn_Food:
                Fragment FoodFrag = Fragment_activities_child_list.newInstance("Food");
                getActivity().getSupportFragmentManager().beginTransaction().add(
                        R.id.content_holder_staff, FoodFrag).addToBackStack("TagList").commit();
                break;
            case R.id.imgBtn_Nap:
                Fragment NapFrag = Fragment_activities_child_list.newInstance("Nap");
                getActivity().getSupportFragmentManager().beginTransaction().add(
                        R.id.content_holder_staff, NapFrag).addToBackStack("TagList").commit();
                break;
            case R.id.imgBtn_Meds:
                Fragment MedsFrag = Fragment_activities_child_list.newInstance("Meds");
                getActivity().getSupportFragmentManager().beginTransaction().add(
                        R.id.content_holder_staff, MedsFrag).addToBackStack("TagList").commit();
                break;
        }
    }

}
