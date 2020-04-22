package com.example.shiningstar;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ChildProfileStaff extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private String childid,childName;
    private TextView tv_childname,tv_dob,tv_parent,tv_num,tv_meds,tv_allergy,tv_notes;
    private ImageView iv_child;
    private Button saveBtn,delBtn;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference childrenTb = db.child("children");
    DatabaseReference child;
    private Uri myUri;
    private static final int CAMERA_INT = 1;
    private static final int GALLERY_INT = 2;
    private Uri CurrImageURI;
    private boolean isImageUploaded = false;
    private ProgressDialog myProgress;



    public ChildProfileStaff() {
        // Required empty public constructor
    }

    
    // TODO: Rename and change types and number of parameters
    public static ChildProfileStaff newInstance(String id,String name) {
        Bundle args = new Bundle();
        args.putString("id",id);
        args.putString("name",name);
        ChildProfileStaff fragment = new ChildProfileStaff();
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
        View v = inflater.inflate(R.layout.fragment_child_profile_staff, container, false);
        setHasOptionsMenu(true);
        tv_childname = (TextView) v.findViewById(R.id.et_childName);
        tv_dob = (TextView) v.findViewById(R.id.et_childDOB);
        tv_parent = (TextView) v.findViewById(R.id.et_childParentName);
        tv_num = (TextView) v.findViewById(R.id.et_childParentNum);
        tv_meds = (TextView) v.findViewById(R.id.et_childMeds);
        tv_allergy = (TextView) v.findViewById(R.id.et_childAllergy);
        tv_notes= (TextView) v.findViewById(R.id.et_childNotes);
        iv_child = (ImageView)v.findViewById(R.id.child_profile_edit_pic) ;
        saveBtn = (Button)v.findViewById(R.id.btn_child_profile_save);
        delBtn = (Button)v.findViewById(R.id.btn_child_profile_delete);

        childid = getArguments().getString("id");
        childName = getArguments().getString("name");
        child = childrenTb.child(childid);

        myProgress = new ProgressDialog(getContext());
        myProgress.setTitle("Updating Profile");

        Toolbar toolbar;
        int toolbar_id = (getActivity().getClass()== Staff.class) ? R.id.toolbar_staff : R.id.toolbar_parent;
        toolbar = (Toolbar) getActivity().findViewById(toolbar_id);
        toolbar.setTitle(childName + "'s Profile");
        setCurrentData();

        return v;
    }

    private void setCurrentData(){
        child.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tv_childname.setText((dataSnapshot.child("name").getValue() != null)? dataSnapshot.child("name").getValue().toString() : "");
                tv_dob.setText((dataSnapshot.child("dob").getValue() != null)? dataSnapshot.child("dob").getValue().toString() : "");
                tv_parent.setText((dataSnapshot.child("parent").getValue() != null)? dataSnapshot.child("parent").getValue().toString() : "");
                tv_num.setText((dataSnapshot.child("parent_num").getValue() != null)? dataSnapshot.child("parent_num").getValue().toString() : "");
                tv_meds.setText((dataSnapshot.child("meds").getValue() != null)? dataSnapshot.child("meds").getValue().toString() : "");
                tv_allergy.setText((dataSnapshot.child("allergy").getValue() != null)? dataSnapshot.child("allergy").getValue().toString() : "");
                tv_notes.setText((dataSnapshot.child("notes").getValue() != null)? dataSnapshot.child("notes").getValue().toString() : "");
                String pic_name = (dataSnapshot.child("pic").getValue() != null)? dataSnapshot.child("pic").getValue().toString() : "";
                if(!pic_name.isEmpty()||pic_name.equalsIgnoreCase("0")) {
                    StorageReference pic = FirebaseStorage.getInstance().getReference().child("PostImages").child(pic_name);
                    Glide.with(getContext()).using(new FirebaseImageLoader())
                            .load(pic)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(iv_child);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}
