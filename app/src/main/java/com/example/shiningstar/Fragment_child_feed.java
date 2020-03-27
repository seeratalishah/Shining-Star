package com.example.shiningstar;


import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_child_feed extends Fragment {

    RecyclerView rv;
    MyChildFeedAdapter adapter;
    String childid = "",childName ="", childPic = "";
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference activitiesTb = db.child("activities");
    DatabaseReference childNode;
    Toolbar toolbar;
    ImageView propic;
    Boolean firstFetch = true;

    public static Fragment_child_feed newInstance(String id,String name, String picName) {

        Bundle args = new Bundle();
        args.putString("id",id);
        args.putString("name",name);
        args.putString("picName", picName);
        Fragment_child_feed fragment = new Fragment_child_feed();
        fragment.setArguments(args);
        return fragment;
    }


    public Fragment_child_feed() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        toolbar.getMenu().clear();
        if (getActivity().getClass() == Parent.class) {
            inflater.inflate(R.menu.child_feed_menu_parent, menu);
        } else {
            inflater.inflate(R.menu.child_feed_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int container = (getActivity().getClass()== Staff.class) ? R.id.content_holder_staff : R.id.content_holder_parent;
        switch (item.getItemId()) {
            case R.id.btn_child_profile_edit:
                //Toast.makeText(getContext(),"Edit pressed",Toast.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(container,Fragment_child_profile.newInstance(childid,childName))
                        .addToBackStack(null).commit();
                break;

            case R.id.btn_share_child_code:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(container, ShareChildCode.newInstance(childid,childName))
                        .addToBackStack(null).commit();
                break;

            case R.id.btn_child_shortcut:
                StorageReference pic = FirebaseStorage.getInstance().getReference().child("PostImages").child(childPic);
                Glide.with(getActivity().getApplicationContext()).using(new FirebaseImageLoader())
                        .load(pic)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(new SimpleTarget<Bitmap>(100,100) {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                ShortcutManager shortcutManager = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                    shortcutManager = getActivity().getSystemService(ShortcutManager.class);
                                }
                                Intent intent =  new Intent(getActivity().getApplicationContext(), Parent.class);
                                intent.setAction("SHORTCUT");
                                intent.putExtra("childid",childid);
                                intent.putExtra("childName",childName);
                                intent.putExtra("childPic",childPic);
                                ShortcutInfo shortcut = null;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                    shortcut = new ShortcutInfo.Builder(getActivity().getApplicationContext(), childid)
                                            .setShortLabel(childName)
                                            .setLongLabel("Open "+childName+ " Activity Feed")
                                            .setIcon(Icon.createWithBitmap(resource))
                                            .setIntent(intent)
                                            .build();
                                }
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                                    if(shortcutManager.getDynamicShortcuts().contains(shortcut)) {
                                        Toast.makeText(getContext(), childName+"'s Shortcut Already Exist", Toast.LENGTH_SHORT).show();
                                    } else {
                                        shortcutManager.addDynamicShortcuts(Arrays.asList(shortcut));
                                    }
                                }
                            }
                        });
        }
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_child_feed, container, false);
    }

}
