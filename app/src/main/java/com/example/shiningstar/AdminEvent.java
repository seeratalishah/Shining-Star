package com.example.shiningstar;


import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.shiningstar.NotificationClass.CHANNEL_1_ID;
import static com.example.shiningstar.NotificationClass.CHANNEL_2_ID;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminEvent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminEvent extends Fragment {

    private NotificationManagerCompat notificationManager;
    EditText titles;
    EditText selectDate,selectTime;
    Button send;

    DatePicker picker;
    private String getdate;

    CalendarView cv;

    Calendar date = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AdminEvent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AdminEvent.
     */
    // TODO: Rename and change types and number of parameters
    public static AdminEvent newInstance(String param1, String param2) {
        AdminEvent fragment = new AdminEvent();
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
        View view = inflater.inflate(R.layout.fragment_admin_event, container, false);


        notificationManager = NotificationManagerCompat.from(getContext());

        titles = view.findViewById(R.id.title);
        selectTime = view.findViewById(R.id.time);
        send = view.findViewById(R.id.send_btn);

        picker=view.findViewById(R.id.datePicker);

        selectTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        selectTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendOnChannel1(v);
            }
        });


        return view;
    }

    public void sendOnChannel1(View v) {
        String title = titles.getText().toString();
        String time = selectTime.getText().toString();

        getdate ="Date : " + picker.getDayOfMonth()+"-"+ (picker.getMonth() + 1)+"-"+picker.getYear()+ "  |  " +
                "Time : " + time;

        Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_shiningicon)
                .setContentTitle(title)
                .setContentText(getdate)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();

        notificationManager.notify(2, notification);
    }

}




