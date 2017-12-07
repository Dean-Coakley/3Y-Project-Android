package com.SDH3.VCA;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Cian on 06/12/2017.
 */

public class EventAdd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_view);

        EditText title = (EditText) findViewById(R.id.eventDesc);
        final String eventTitle = title.getText().toString();

        EditText loc = (EditText) findViewById(R.id.eventLocation);
        final String eventLoc = loc.getText().toString();

        Intent date = getIntent();

        int yr[] = new int[0];
        int mn[] = new int[0];
        int dom[] = new int[0];
        Bundle bundle = date.getExtras();
        if (!bundle.isEmpty()) {
            yr = bundle.getIntArray("year");
            mn = bundle.getIntArray("month");
            dom = bundle.getIntArray("dayOfMon");


        }// beginTime.set(2012, 9, 14, 7, 30);


        int yar = yr[0];
        int mon = mn[0];
        int dayo = dom[0];
        long beginTime =0;
        long endTime = 0;

        Spinner hour = (Spinner) findViewById(R.id.chooseStartTimehr);
        ArrayAdapter<CharSequence> hourAdapter = ArrayAdapter.createFromResource(EventAdd.this, R.array.hour, android.R.layout.simple_list_item_1);
        hour.setAdapter(hourAdapter);

        Spinner mins = (Spinner) findViewById(R.id.chooseStartTimemin);
        ArrayAdapter<CharSequence> minAdapter = ArrayAdapter.createFromResource(EventAdd.this, R.array.mins, android.R.layout.simple_list_item_1);
        mins.setAdapter(minAdapter);

        Spinner endHour = (Spinner) findViewById(R.id.chooseEndTimehr);
        ArrayAdapter<CharSequence> endHourAdapter = ArrayAdapter.createFromResource(EventAdd.this, R.array.hour, android.R.layout.simple_list_item_1);
        endHour.setAdapter(endHourAdapter);

        Spinner endMins = (Spinner) findViewById(R.id.chooseEndTimemin);
        ArrayAdapter<CharSequence> endMinAdapter = ArrayAdapter.createFromResource(EventAdd.this, R.array.mins, android.R.layout.simple_list_item_1);
        endMins.setAdapter(endMinAdapter);

        String startHr = hour.getSelectedItem().toString();
        String startMin = mins.getSelectedItem().toString();
        String endHr = endHour.getSelectedItem().toString();
        String endMin = endMins.getSelectedItem().toString();

        int a = Integer.parseInt(startHr);
        int b = Integer.parseInt(startMin);
        int c = Integer.parseInt(endHr);
        int d = Integer.parseInt(endMin);

        Calendar beginT = Calendar.getInstance();
        beginT.set(yar, mon, dayo, a, b);
        Calendar endT = Calendar.getInstance();
        endT.set(yar, mon, dayo, c, d);

        beginTime = beginT.getTimeInMillis();
        endTime = endT.getTimeInMillis();

        final TextView t = (TextView) findViewById(R.id.eventError);
        Button enter = (Button) findViewById(R.id.newEvent);
        final long finalBeginTime = beginTime;
        final long finalEndTime = endTime;
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(eventTitle != null && eventLoc != null && finalBeginTime != 0 && finalEndTime != 0)
                {
                    addEvent(eventTitle, eventLoc, finalBeginTime, finalEndTime);
                }
                else
                {
                    t.setVisibility(View.VISIBLE);
                }

            }
        });


    }

    public void addEvent(String title, String location, long begin, long end) {
        Intent calIntent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (calIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(calIntent);
        }
    }

}