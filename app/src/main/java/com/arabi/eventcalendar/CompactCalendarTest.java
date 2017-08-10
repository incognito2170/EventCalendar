package com.arabi.eventcalendar;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class CompactCalendarTest extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String userToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjIsImlzcyI6Imh0dHA6Ly8xODIuMTYwLjEwOS4xMzIvYXBpL2xvZ2luIiwiaWF0IjoxNTAyMzk4MTAxLCJleHAiOjE1MDI0MDE3MDEsIm5iZiI6MTUwMjM5ODEwMSwianRpIjoiSmpaTnlqZ2ZZS2N4MjZsbSJ9.NhY1iMcvzqt7YIYCkpN81T0LFF2u6AJtQw4BHTPykFE";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForDay = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForDisabledDay = new SimpleDateFormat("E", Locale.getDefault());
    private CompactCalendarView compactCalendarView;
    private Boolean shouldHide = true, shouldShowAnimated = false;
    private List<AppointmentListModelClass> pendingJobItem;
    private AppointmentListAdapter adapter;
    private RecyclerView rvPendingWorks;
    private RecyclerView.LayoutManager mLayoutManager;
    private int scrollState;
    private TextView calendarTitle;
    private Date currentDate;
    private List<Event> bookingsFromMap;
    private List<String> disabledDays;
    private TextView textView;
    private AppointmentListModelClass schedule;
    private List<AppointmentListModelClass> allItems = new ArrayList<AppointmentListModelClass>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compact_calendar_view);

        rvPendingWorks = (RecyclerView) findViewById(R.id.rv_pending_works);

        final ImageButton showPreviousMonthBut = (ImageButton) findViewById(R.id.prev_button);
        final ImageButton showNextMonthBut = (ImageButton) findViewById(R.id.next_button);
        calendarTitle = (TextView) findViewById(R.id.calendarTitle);
        textView = (TextView) findViewById(R.id.textView);

        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendarView.invalidate();


        new scheduleAPI(userToken).execute();


        addEvents(3, 7, 2017); //send the actual_date, (actual_month-1), actual_year received from API but without zero infront (e.g: not as 03, but as 3)
        addEvents(4, 8, 2017); //send the actual_date, (actual_month-1), actual_year received from API but without zero infront (e.g: not as 03, but as 3)

        currentDate = new Date();

        bookingsFromMap = compactCalendarView.getEvents(currentDate);

        if (bookingsFromMap.size() != 0) {
            Log.d(TAG, "*********Events list " + bookingsFromMap.toString());
            InitPendingJobRecyclerView(currentDate, 1);
        } else {
            InitPendingJobRecyclerView(currentDate, 0);
        }

        calendarTitle.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        logEventsByMonth(compactCalendarView);


        //set title on calendar scroll
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                calendarTitle.setText(dateFormatForMonth.format(dateClicked));

                Log.d(TAG, "inside onclick " + dateClicked);

                bookingsFromMap = compactCalendarView.getEvents(dateClicked);

                if (bookingsFromMap.size() != 0) {
                    Log.d(TAG, "*********Events list " + bookingsFromMap.toString());
                    InitPendingJobRecyclerView(dateClicked, 1);
                } else {
                    InitPendingJobRecyclerView(dateClicked, 0);
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calendarTitle.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });


        //Handle calendar arrow click
        showPreviousMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showPreviousMonth();
            }
        });

        showNextMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.showNextMonth();
            }
        });


        compactCalendarView.setAnimationListener(new CompactCalendarView.CompactCalendarAnimationListener() {
            @Override
            public void onOpened() {
            }

            @Override
            public void onClosed() {
            }
        });


        // uncomment below to show indicators above small indicator events
        compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        // uncomment below to open onCreate
        //openCalendarOnCreate(v);
    }


    private void openCalendarOnCreate(View v) {
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_content);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                compactCalendarView.showCalendarWithAnimation();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        calendarTitle.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
        // Set to current day on resume to set calendar to latest day
        // toolbar.setTitle(dateFormatForMonth.format(new Date()));
    }


    private void InitPendingJobRecyclerView(final Date dateClickedByUser, int hasEvent) {

        pendingJobItem = new ArrayList<>();
        adapter = new AppointmentListAdapter(this, pendingJobItem);

        mLayoutManager = new LinearLayoutManager(CompactCalendarTest.this, LinearLayoutManager.VERTICAL, false);
        rvPendingWorks.setLayoutManager(mLayoutManager);

        rvPendingWorks.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        System.out.println("The RecyclerView is not scrolling");
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        System.out.println("Scrolling now");
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        System.out.println("Scroll Settling");
                        break;
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dx > 0) {
                    System.out.println("Scrolled Right");

                } else if (dx < 0) {
                    System.out.println("Scrolled Left");

                } else {

                    System.out.println("No Horizontal Scrolled");
                }

                if (dy > 0) {
                    System.out.println("Scrolled Downwards");
                    scrollState = 0;

                    switch (scrollState) {
                        case 0:
                            System.out.println("scrollState = 1");
                            if (!compactCalendarView.isAnimating()) {

                                if (shouldHide) {
                                    compactCalendarView.hideCalendar();
                                    shouldHide = !shouldHide;
                                    shouldShowAnimated = !shouldShowAnimated;
                                    calendarTitle.setText(dateFormatForDay.format(dateClickedByUser));
                                } else {
                                    break;
                                }
                            }
                            break;
                    }
                } else if (dy < 0) {
                    System.out.println("Scrolled Upwards");
                    scrollState = 1;

                    switch (scrollState) {
                        case 1:
                            System.out.println("scrollState = 0");
                            if (!compactCalendarView.isAnimating()) {

                                if (shouldShowAnimated) {
                                    compactCalendarView.showCalendarWithAnimation();
                                    shouldShowAnimated = !shouldShowAnimated;
                                    shouldHide = !shouldHide;
                                    calendarTitle.setText(dateFormatForMonth.format(dateClickedByUser));
                                } else {
                                    break;
                                }


                            }
                            break;
                    }
                } else {

                    System.out.println("No Vertical Scrolled");
                }

            }
        });

        rvPendingWorks.setItemAnimator(new DefaultItemAnimator());
        rvPendingWorks.setAdapter(adapter);


        if (hasEvent == 1) {
            textView.setVisibility(View.GONE);
            LoadPendingWorkList();
        } else {
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void LoadPendingWorkList() {
        int[] profileImage = new int[]{
                R.drawable.patient_profile_pic,
                R.drawable.patient_profile_pic_2,
                R.drawable.patient_profile_pic_3};

        AppointmentListModelClass a = new AppointmentListModelClass("Robin van Persie", null, "10:30 AM","11:00 AM", profileImage[0], null, null, null);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Hakan Calhanoglu", null, "11:00 AM","11:30 AM",profileImage[1], null, null, null);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Gianluigi Buffon", null, "11:30 AM","12:00 PM",profileImage[2], null, null, null);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 4", null, "12:00 PM", "12:30 PM",profileImage[0], null, null, null);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 5", null, "12:30 PM","1:00 PM", profileImage[1], null, null, null);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 6", null, "1:00 PM","1:30 PM",profileImage[2], null, null, null);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 7", null, "1:30 PM","2:00 PM", profileImage[0], null, null, null);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 8", null, "2:00 PM","2:30 PM", profileImage[1], null, null, null);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 9", null, "2:30 PM","3:00 PM",profileImage[2], null, null, null);
        pendingJobItem.add(a);

        Collections.shuffle(pendingJobItem);

        adapter.notifyDataSetChanged();
    }

    private void addEvents(int day, int month, int year) {

        currentCalender.setTime(new Date());

        currentCalender.set(Calendar.DAY_OF_MONTH, day);

        if (month > -1) {
            currentCalender.set(Calendar.MONTH, month);
        }

        if (year > -1) {
            currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
            currentCalender.set(Calendar.YEAR, year);
        }

        setToMidnight(currentCalender);

        long timeInMillis = currentCalender.getTimeInMillis();

        List<Event> events = getEvents(timeInMillis);

        compactCalendarView.addEvents(events);
    }

    private List<Event> getEvents(long timeInMillis) {
        return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
    }

    private List<Event> getDisabledDays(long timeInMillis) {
        return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, Calendar.getInstance().get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, Calendar.getInstance().get(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, Calendar.getInstance().get(Calendar.MILLISECOND));
    }


    private void logEventsByMonth(CompactCalendarView compactCalendarView) {
        currentCalender.setTime(new Date());
        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
        currentCalender.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH));
        List<String> dates = new ArrayList<>();
        for (Event e : compactCalendarView.getEventsForMonth(new Date())) {
            dates.add(dateFormatForDisplaying.format(e.getTimeInMillis()));
        }

        Log.d(TAG, "Events for Aug with simple date formatter: " + dates);
        Log.d(TAG, "Events for Aug month using default local and timezone: " + compactCalendarView.getEventsForMonth(currentCalender.getTime()));
    }


    private class scheduleAPI extends AsyncTask<String, String, String> {
        String userToken;


        private scheduleAPI(String userToken) {
            //set context variables if required
            this.userToken = userToken;
        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Log.d("error", "statusReport: onPreExecute e dhukse!!!");

        }


        @Override
        protected String doInBackground(String... params) {

            Log.d("error", "statusReport: doInBackground e dhukse!!!");

            String resultToDisplay = "";
//*****************************************************************


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://182.160.109.132/api/doctor-schedule/2");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("token", userToken));


// Execute HTTP Post Request
            HttpResponse response = null;
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpclient.execute(httppost);
                resultToDisplay = EntityUtils.toString(response.getEntity());

                Log.v("Util response", resultToDisplay);
            } catch (IOException e) {
                e.printStackTrace();
            }


            //**************************************************************
            return resultToDisplay;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("error", "statusReport: onPostExecute e dhukse!!!");

            try {
                Log.d("jsonData", "+++++++++" + result);
                JSONObject jsonObj = new JSONObject(result);
                JSONObject json = new JSONObject(jsonObj.getString("result"));


                if (json.getString("status").equals("success")) {
                    JSONArray scheduleArray = json.getJSONArray("doctor_time_schedules");

                    Log.d("array", "length of scheduleArray: "+scheduleArray.length());

                    allItems.clear();

                    for (int i = 0; i < scheduleArray.length(); i++) {

                        JSONObject scheduleJson = scheduleArray.getJSONObject(i);
                        Log.d("jsonData", "jsonData#"+ i + "+++++++++" + scheduleJson);


                        schedule = new AppointmentListModelClass();
                        schedule.setDay(scheduleJson.getString("day"));
                        schedule.setStatus(scheduleJson.getString("status"));
                        allItems.add(schedule);

                        if (schedule.getStatus().equals("0")) {
                            disabledDays.add(schedule.getDay());
                        }
                    }

                    for (int j = 0; j < disabledDays.size(); j++) {
                        currentCalender.setTime(new Date());

                        if (disabledDays.get(j).equals("Sun")) {
                            currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                        }else if(disabledDays.get(j).equals("Mon")){
                            currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        }else if(disabledDays.get(j).equals("Tue")){
                            currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                        }else if(disabledDays.get(j).equals("Wed")){
                            currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                        }else if(disabledDays.get(j).equals("Thu")){
                            currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                        }else if(disabledDays.get(j).equals("Fri")){
                            currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                        }else if(disabledDays.get(j).equals("Sat")){
                            currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                        }

                        setToMidnight(currentCalender);

                        long timeInMillis = currentCalender.getTimeInMillis();

                        List<Event> disabledDays = getDisabledDays(timeInMillis);

                        compactCalendarView.addEvents(disabledDays);

                    }

                } else {

                    Toast.makeText(CompactCalendarTest.this, "There is no data to display",
                            Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
