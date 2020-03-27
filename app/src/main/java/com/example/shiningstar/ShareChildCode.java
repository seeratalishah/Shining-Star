package com.example.shiningstar;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShareChildCode extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String ChildName;
    private String ChildId;

    private TextView Heading, ChildCode;
    private ImageButton ShareCodeButton;



    public ShareChildCode() {
        // Required empty public constructor
    }


    public static ShareChildCode newInstance(String param1, String param2) {
        ShareChildCode fragment = new ShareChildCode();
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
            ChildId = getArguments().getString(ARG_PARAM1);
            ChildName = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_share_child_code, container, false);

        Heading = (TextView) view.findViewById(R.id.ShareCode_intro);
        String title = "The 'Child Code' for '" + ChildName + "' is given below.";
        Heading.setText(title);

        ChildCode = (TextView) view.findViewById(R.id.ShareCode_childCode);
        ChildCode.setText(ChildId);

        ShareCodeButton = (ImageButton) view.findViewById(R.id.ShareCode_sendButton);
        ShareCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myMessage = "Hello there, \nHere is the ChildCode for the student '" + ChildName + "'. \n" +
                        "ChildCode = '" + ChildId + "'. \n" +
                        "You can use this code to add this child to your list." +
                        "You can also share this with your other family members who has the app 'School Diaries'. \nThank you.";

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, myMessage);
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, "Share via.."));

//                Intent intentsms = new Intent( Intent.ACTION_VIEW, Uri.parse( "sms:" + "" ) );
//                intentsms.putExtra( "sms_body", myMessage );
//                startActivity( intentsms );
            }
        });

        return view;
    }

}
