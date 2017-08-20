package com.arabi.eventcalendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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

public class MainActivity extends ActionBarActivity {
    @BindView(R.id.rv_pending_works)
    RecyclerView rvPendingWorks;
    Unbinder unbinder;

    private List<AppointmentListModelClass> pendingJobItem = new ArrayList<>();
    private AppointmentListAdapter adapter;
    CalendarDay currentDay, eventDay;
    MaterialCalendarView calendar;
    Calendar startingCal, cal;
    int scrollState;
    DayViewDecorator currentDayDecorator;
    Boolean isSelected = true, isCurrentDayDecoratorAdded = false;
    String currentDayInString, selectedDayInString, dateInString, currentDateInString, selectedDateInString;

    private Toolbar toolbar;

    private static final String TAG = "MainActivity";
    private static final String userId = "31";
    private static final String patientId = "33";
    private static final String userToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjMxLCJpc3MiOiJodHRwOi8vMTgyLjE2MC4xMDkuMTMyL2FwaS9sb2dpbiIsImlhdCI6MTUwMzIwMDQ3NiwiZXhwIjoxNTM0NzM2NDc2LCJuYmYiOjE1MDMyMDA0NzYsImp0aSI6IlZHMEtZT1l4b2VXcWFUcmMifQ.za30R-yIYA9noJIf_AedjzE9ssnyKaM6fV2Dlm7Hs7U";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForDay = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForAppointmentDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Boolean shouldHide = true, shouldShowAnimated = false;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView calendarTitle;
    private Date currentDate;
    private Date selectedDate;
    private TextView textView, textView1;
    private String dayInString, monthInString, yearInString;
    private AppointmentListModelClass schedule, appointments, appointmentDay;
    private List<AppointmentListModelClass> allItems = new ArrayList<AppointmentListModelClass>();
    private ImageButton showPreviousMonthBut;
    private ImageButton showNextMonthBut;
    private Boolean hasAppointmentDays = false;
    private Boolean fromDisabledDays = false;
    private int titleToggle = 0;
    public SharedPreferences sp;
    public SharedPreferences.Editor editor;
    private long startTimeInMillis=0;
    private long endTimeInMillis=0;
    private String startTime="10:00:00";
    private String endTime="10:30:00";
    private RelativeLayout rl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("Appointment Calendar");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        calendar = (MaterialCalendarView) findViewById(R.id.calendarView);

        textView = (TextView) findViewById(R.id.textView);
        textView1 = (TextView) findViewById(R.id.textView1);

        rl = (RelativeLayout) findViewById(R.id.mainLayout);

        currentDay = CalendarDay.today();
        calendar.setSelectedDate(currentDay);

        currentDayInString = convertInString(currentDay);
        selectedDateInString = convertInString1(currentDay);

//        Toast.makeText(this, "Current date is: " + currentDayInString, Toast.LENGTH_SHORT).show();


        currentDayDecorator = new DayViewDecorator() {   //Decorate the current day only
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                Calendar cal1 = day.getCalendar();
                Calendar cal2 = Calendar.getInstance();

                return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                        && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                        && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.selector));
            }
        };


        new scheduleAPI(userId, userToken).execute();
        new appointmentsAPI(userId, userToken).execute();

        InitPendingJobRecyclerView();

        currentDate = new Date();
        new daywiseAppointmentsAPI(userId, userToken, dateFormatForAppointmentDay.format(currentDate)).execute();
        Log.d("formattedDate", dateFormatForAppointmentDay.format(currentDate)+" "+currentDate);


        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                currentDayInString = convertInString(calendar.getSelectedDate());

                isSelected = calendar.getSelectedDate().equals(currentDay);

                if (!isSelected && !isCurrentDayDecoratorAdded) {
                    calendar.addDecorator(currentDayDecorator);
                    isCurrentDayDecoratorAdded = true;
                } else if (isSelected && isCurrentDayDecoratorAdded) {
                    calendar.removeDecorator(currentDayDecorator);
                    isCurrentDayDecoratorAdded = false;
                }


                selectedDateInString = convertInString1(date);
                new daywiseAppointmentsAPI(userId, userToken, selectedDateInString).execute();
                Log.d("formattedDate", selectedDateInString);

//                calendar.state().edit()
//                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setMinimumDate(CalendarDay.from(calendar.getSelectedDate().getYear(), calendar.getSelectedDate().getMonth(), (calendar.getSelectedDate().getDay()-6)))
//                        .setMaximumDate(CalendarDay.from(calendar.getSelectedDate().getYear(), calendar.getSelectedDate().getMonth(), (calendar.getSelectedDate().getDay()+6)))
//                        .setCalendarDisplayMode(CalendarMode.WEEKS)
//                        .commit();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {


            if (titleToggle == 0) {
                item.setIcon(R.drawable.ic_calendar_view);

                toolbar.setTitle(currentDayInString);

                calendar.animate()
                        .translationX(calendar.getWidth())
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                calendar.setVisibility(View.GONE);
                            }
                        });

                titleToggle = 1;
            } else {
                item.setIcon(R.drawable.ic_calendar_hide);

                toolbar.setTitle("Appointment Calendar");

                calendar.animate()
                        .translationX(0)
                        .alpha(1.0f)
                        .setDuration(20)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                calendar.setVisibility(View.VISIBLE);
                            }
                        });

                titleToggle = 0;
            }
            return true;
        } else if(id == R.id.action_add){
            new createAppointmentAPI(userId, userToken, patientId, selectedDateInString, startTime, endTime).execute();
        }

        return super.onOptionsItemSelected(item);
    }


    public class EventDecorator implements DayViewDecorator {

        int color;
        CalendarDay dates;


        public EventDecorator(int color, CalendarDay dates) {
            this.color = color;
            this.dates = dates;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates != null && day.equals(dates);
        }

        @Override
        public void decorate(DayViewFacade view) {

            view.addSpan(new DotSpan(8, color));
            view.setDaysDisabled(false);

        }
    }

    public class DisabledDayDecorator implements DayViewDecorator {

        CalendarDay dates;


        public DisabledDayDecorator(CalendarDay dates) {
            this.dates = dates;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates != null && day.equals(dates);
        }

        @Override
        public void decorate(DayViewFacade view) {

            view.setDaysDisabled(true);

        }
    }


    String convertInString(CalendarDay selectedDay) {

        if (selectedDay.getDay() < 10) {
            dayInString = "0" + selectedDay.getDay();
        } else {
            dayInString = String.valueOf(selectedDay.getDay());
        }

        switch (selectedDay.getMonth() + 1) {
            case 1:
                monthInString = "January";
                break;
            case 2:
                monthInString = "February";
                break;
            case 3:
                monthInString = "March";
                break;
            case 4:
                monthInString = "April";
                break;
            case 5:
                monthInString = "May";
                break;
            case 6:
                monthInString = "June";
                break;
            case 7:
                monthInString = "July";
                break;
            case 8:
                monthInString = "August";
                break;
            case 9:
                monthInString = "September";
                break;
            case 10:
                monthInString = "October";
                break;
            case 11:
                monthInString = "November";
                break;
            case 12:
                monthInString = "December";
                break;
        }

        yearInString = String.valueOf(selectedDay.getYear());

        dateInString = dayInString + " " + monthInString + " " + yearInString;

        return dateInString;
    }


    String convertInString1(CalendarDay selectedDay) {

        if (selectedDay.getDay() < 10) {
            dayInString = "0" + selectedDay.getDay();
        } else {
            dayInString = String.valueOf(selectedDay.getDay());
        }

        if ((selectedDay.getMonth() + 1)<10) {
            monthInString = "0" + (selectedDay.getMonth()+1);
        } else {
            monthInString = String.valueOf(selectedDay.getMonth()+1);
        }

        yearInString = String.valueOf(selectedDay.getYear());

        dateInString = yearInString + "-" + monthInString + "-" + dayInString;

        return dateInString;
    }


//    private void LoadPendingWorkList() {
//        int[] profileImage = new int[]{
//                R.drawable.patient_profile_pic,
//                R.drawable.patient_profile_pic_2,
//                R.drawable.patient_profile_pic_3};
//
//        AppointmentListModelClass a = new AppointmentListModelClass("Robin van Persie", null, null, "10:30 AM", "11:00 AM", profileImage[0], null, null, null, null);
//        pendingJobItem.add(a);
//
//        a = new AppointmentListModelClass("Hakan Calhanoglu", null, null, "11:00 AM", "11:30 AM", profileImage[1], null, null, null, null);
//        pendingJobItem.add(a);
//
//        a = new AppointmentListModelClass("Gianluigi Buffon", null, null, "11:30 AM", "12:00 PM", profileImage[2], null, null, null, null);
//        pendingJobItem.add(a);
//
//        a = new AppointmentListModelClass("Patient 4", null, null, "12:00 PM", "12:30 PM", profileImage[0], null, null, null, null);
//        pendingJobItem.add(a);
//
//        a = new AppointmentListModelClass("Patient 5", null, null, "12:30 PM", "1:00 PM", profileImage[1], null, null, null, null);
//        pendingJobItem.add(a);
//
//        a = new AppointmentListModelClass("Patient 6", null, null, "1:00 PM", "1:30 PM", profileImage[2], null, null, null, null);
//        pendingJobItem.add(a);
//
//        a = new AppointmentListModelClass("Patient 7", null, null, "1:30 PM", "2:00 PM", profileImage[0], null, null, null, null);
//        pendingJobItem.add(a);
//
//        a = new AppointmentListModelClass("Patient 8", null, null, "2:00 PM", "2:30 PM", profileImage[1], null, null, null, null);
//        pendingJobItem.add(a);
//
//        a = new AppointmentListModelClass("Patient 9", null, null, "2:30 PM", "3:00 PM", profileImage[2], null, null, null, null);
//        pendingJobItem.add(a);
//
//        Collections.shuffle(pendingJobItem);
//
//        adapter.notifyDataSetChanged();
//    }

    private void InitPendingJobRecyclerView() {

        rvPendingWorks.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        rvPendingWorks.setLayoutManager(mLayoutManager);


    }


    private class scheduleAPI extends AsyncTask<String, String, String> {
        String userId, userToken;


        private scheduleAPI(String userId, String userToken) {
            this.userId = userId;
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
            HttpPost httppost = new HttpPost("http://182.160.109.132/api/doctor-schedule/" + userId);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("token", userToken));


//// Execute HTTP Post Request
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

            ArrayList listDecor = new ArrayList();

            try {
                Log.d("jsonDataSchedule", "+++++++++" + result);
                JSONObject jsonObj = new JSONObject(result);
                JSONObject json = new JSONObject(jsonObj.getString("result"));


                if (json.getString("status").equals("success")) {
                    JSONArray scheduleArray = json.getJSONArray("doctor_time_schedules");

                    Log.d("array", "length of scheduleArray: " + scheduleArray.length());


                    if (scheduleArray.length() != 0) {

                        for (int i = 0; i < scheduleArray.length(); i++) {

                            JSONObject scheduleJson = scheduleArray.getJSONObject(i);
                            Log.d("jsonData", "jsonData#" + i + "+++++++++" + scheduleJson);


                            AppointmentListModelClass schedule = new AppointmentListModelClass();
                            schedule.setDay(scheduleJson.getString("day"));
                            schedule.setStatus(scheduleJson.getString("status"));


                            if (schedule.getStatus().equals("0")) {

                                fromDisabledDays = true;

                                currentCalender.setTime(new Date());

                                currentCalender.set(Calendar.WEEK_OF_YEAR, 1);

                                int remainingWeeks = 52 - currentCalender.get(Calendar.WEEK_OF_YEAR);
                                Log.d("dayOfWeek", "Remaining weeks in 2017: " + remainingWeeks);

                                for (int j = 0; j <= remainingWeeks; j++) {
                                    if (schedule.getDay().equals("Sun")) {
                                        currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                                    } else if (schedule.getDay().equals("Mon")) {
                                        currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                                    } else if (schedule.getDay().equals("Tue")) {
                                        currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                                    } else if (schedule.getDay().equals("Wed")) {
                                        currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
                                    } else if (schedule.getDay().equals("Thu")) {
                                        currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
                                    } else if (schedule.getDay().equals("Fri")) {
                                        currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
                                    } else if (schedule.getDay().equals("Sat")) {
                                        currentCalender.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                                    }


                                    eventDay = CalendarDay.from(currentCalender);
                                    listDecor.add((new DisabledDayDecorator(eventDay)));

                                    currentCalender.add(Calendar.WEEK_OF_YEAR, 1);

                                }

                                calendar.addDecorators(listDecor);
                            }
                        }
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class appointmentsAPI extends AsyncTask<String, String, String> {
        String userId, userToken;


        private appointmentsAPI(String userId, String userToken) {
            this.userId = userId;
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
            HttpPost httppost = new HttpPost("http://182.160.109.132/api/appointmentsDoctor");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("doctor_id", userId));
            nameValuePairs.add(new BasicNameValuePair("token", userToken));


//// Execute HTTP Post Request
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

            ArrayList listDecor = new ArrayList();

            try {
                Log.d("jsonData", "+++++++++" + result);
                JSONObject jsonObj = new JSONObject(result);
                JSONObject json = new JSONObject(jsonObj.getString("result"));


                if (json.getString("status").equals("success")) {
                    JSONArray scheduleArray = json.getJSONArray("appointments");

                    Log.d("array", "length of appointmentsArray: " + scheduleArray.length());


                    if (scheduleArray.length() != 0) {

                        for (int i = 0; i < scheduleArray.length(); i++) {

                            JSONObject scheduleJson = scheduleArray.getJSONObject(i);
                            Log.d("jsonData", "jsonData#" + i + "+++++++++" + scheduleJson);


                            AppointmentListModelClass appointments = new AppointmentListModelClass();
                            appointments.setDay(scheduleJson.getString("appointment_date"));
                            appointments.setStatus(scheduleJson.getString("status"));


                            if (appointments.getStatus().equals("approved")) {

                                cal = new GregorianCalendar();
                                String dateStr = appointments.getDay();
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = format.parse(dateStr);
                                cal.setTime(date);
                                eventDay = CalendarDay.from(cal);
                                listDecor.add((new EventDecorator(Color.argb(255, 0, 255, 0), eventDay)));

                            }
                        }


                        calendar.addDecorators(listDecor);

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }




    private class daywiseAppointmentsAPI extends AsyncTask<String, String, String> {
        String userId, userToken, dateClickedByUser;


        private daywiseAppointmentsAPI(String userId, String userToken, String dateClickedByUser) {
            this.userId = userId;
            this.userToken = userToken;
            this.dateClickedByUser = dateClickedByUser;
        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Log.d("heyo", "statusReport: onPreExecute e dhukse!!!");

            rvPendingWorks.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            textView1.setVisibility(View.VISIBLE);

        }


        @Override
        protected String doInBackground(String... params) {

            Log.d("heyo", "statusReport: doInBackground e dhukse!!!");

            String resultToDisplay = "";
//*****************************************************************


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://182.160.109.132/api/appointmentListDateWise");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
            nameValuePairs.add(new BasicNameValuePair("doctor_id", userId));
            nameValuePairs.add(new BasicNameValuePair("token", userToken));
            nameValuePairs.add(new BasicNameValuePair("appointment_date", dateClickedByUser));


//// Execute HTTP Post Request
            HttpResponse response = null;
            try {
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                response = httpclient.execute(httppost);
                resultToDisplay = EntityUtils.toString(response.getEntity());

                Log.v("Latest util response", resultToDisplay);
            } catch (IOException e) {
                e.printStackTrace();
            }


            //**************************************************************
            return resultToDisplay;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("heyo", "statusReport: onPostExecute e dhukse!!!");

            try {
                Log.d("jsonData", "+++++++++" + result);
                JSONObject jsonObj = new JSONObject(result);
                JSONObject json = new JSONObject(jsonObj.getString("result"));


                if (json.getString("status").equals("success")) {
                    textView1.setVisibility(View.GONE);

                    pendingJobItem.clear();

                    JSONArray scheduleArray = json.getJSONArray("appointments");

                    Log.d("array", "length of dateWisesArray: " + scheduleArray.length());



                    if(scheduleArray.length() != 0) {

                        Log.d("checker", ""+scheduleArray.length());

                        for (int i = 0; i < scheduleArray.length(); i++) {

                            JSONObject scheduleJson = scheduleArray.getJSONObject(i);
                            Log.d("appointmentDetails", "jsonData#" + i + "+++++++++" + scheduleJson);
                            JSONObject patientData = scheduleJson.getJSONObject("patient");
                            JSONObject patientProfileData = patientData.getJSONObject("profile");


                            AppointmentListModelClass appointmentDay = new AppointmentListModelClass();
                            appointmentDay.setDay(scheduleJson.getString("appointment_date"));
                            appointmentDay.setStartTime(scheduleJson.getString("appointment_start_time").substring(0,5));
                            appointmentDay.setEndTime(scheduleJson.getString("appointment_end_time").substring(0,5));
                            appointmentDay.setStatus(scheduleJson.getString("status"));
                            appointmentDay.setPatientAvatar(patientData.getString("avatar"));
                            appointmentDay.setPatientFirstName(patientProfileData.getString("first_name"));
                            appointmentDay.setPatientLastName(patientProfileData.getString("last_name"));
                            appointmentDay.setReason(patientProfileData.getString("main_problem"));
                            pendingJobItem.add(appointmentDay);
                        }


                        adapter = new AppointmentListAdapter(MainActivity.this, pendingJobItem);
                        rvPendingWorks.setItemAnimator(new DefaultItemAnimator());
                        rvPendingWorks.setAdapter(adapter);

                        rvPendingWorks.setVisibility(View.VISIBLE);


                    } else{

                        textView.setVisibility(View.VISIBLE);
                    }

                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }





    private class createAppointmentAPI extends AsyncTask<String, String, String> {
        String userId, userToken, patientId, selectedDateInString, startTime, endTime;


        private createAppointmentAPI(String userId, String userToken, String patientId, String selectedDateInString, String startTime, String endTime) {
            this.userId = userId;
            this.userToken = userToken;
            this.patientId = patientId;
            this.selectedDateInString = selectedDateInString;
            this.startTime = startTime;
            this.endTime = endTime;
        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Log.d("error", "statusReport: onPreExecute e dhukse!!!");

            rl.setClickable(false);

        }


        @Override
        protected String doInBackground(String... params) {

            Log.d("error", "statusReport: doInBackground e dhukse!!!");

            String resultToDisplay = "";
//*****************************************************************


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://182.160.109.132/api/appointmentDoctorStore");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
            nameValuePairs.add(new BasicNameValuePair("doctor_id", userId));
            nameValuePairs.add(new BasicNameValuePair("token", userToken));
            nameValuePairs.add(new BasicNameValuePair("patient_id", patientId));
            nameValuePairs.add(new BasicNameValuePair("appointment_date", selectedDateInString));
            nameValuePairs.add(new BasicNameValuePair("appointment_start_time", startTime));
            nameValuePairs.add(new BasicNameValuePair("appointment_end_time", endTime));


//// Execute HTTP Post Request
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

            Toast.makeText(MainActivity.this, "New appointment created", Toast.LENGTH_SHORT).show();
            rl.setClickable(true);
        }
    }
}

