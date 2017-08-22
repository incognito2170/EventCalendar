package com.arabi.eventcalendar;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.view.MotionEvent;
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
    private static final String userId = "31";
    private static final String userToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOjMxLCJpc3MiOiJodHRwOi8vMTgyLjE2MC4xMDkuMTMyL2FwaS9sb2dpbiIsImlhdCI6MTUwMzIwMDQ3NiwiZXhwIjoxNTM0NzM2NDc2LCJuYmYiOjE1MDMyMDA0NzYsImp0aSI6IlZHMEtZT1l4b2VXcWFUcmMifQ.za30R-yIYA9noJIf_AedjzE9ssnyKaM6fV2Dlm7Hs7U";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForDay = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
    private SimpleDateFormat dateFormatForAppointmentDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private CompactCalendarView compactCalendarView;
    private Boolean shouldHide = true, shouldShowAnimated = false;
    private RecyclerView rvPendingWorks;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView calendarTitle;
    private Date currentDate;
    private Date selectedDate;
    private List<Event> bookingsFromMap, bookingsFromMap1;
    private TextView textView;
    private String dayInString, monthInString, yearInString;
    private AppointmentListModelClass schedule, appointments, appointmentDay;
    private List<AppointmentListModelClass> allItems = new ArrayList<AppointmentListModelClass>();
    private AppointmentListAdapter adapter;
    private ImageButton showPreviousMonthBut;
    private ImageButton showNextMonthBut;
    private Boolean hasAppointmentDays = false;
    private Boolean fromDisabledDays = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compact_calendar_view);

        rvPendingWorks = (RecyclerView) findViewById(R.id.rv_pending_works);

        showPreviousMonthBut = (ImageButton) findViewById(R.id.prev_button);
        showNextMonthBut = (ImageButton) findViewById(R.id.next_button);



        textView = (TextView) findViewById(R.id.textView);


        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendarView.invalidate();

        calendarTitle = (TextView) findViewById(R.id.calendarTitle);
        calendarTitle.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        InitPendingJobRecyclerView();

        new scheduleAPI(userId, userToken).execute();
        new appointmentsAPI(userId, userToken).execute();


        currentDate = new Date();

        bookingsFromMap = compactCalendarView.getEvents(currentDate);
        if (bookingsFromMap.size() > 0) {

            new daywiseAppointmentsAPI(userId, userToken, dateFormatForAppointmentDay.format(currentDate), currentDate);
            Log.d("dateFormat", dateFormatForAppointmentDay.format(currentDate));
            Log.d("bookingsSize", "bookingsSize: "+bookingsFromMap.size());

        } else{
            textView.setVisibility(View.VISIBLE);
        }


        //Handle 'calendar date clicks' and 'custom toolbar text on month scroll'
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                calendarTitle.setText(dateFormatForMonth.format(dateClicked));

                selectedDate = dateClicked;

                bookingsFromMap1 = compactCalendarView.getEvents(selectedDate);

                if (bookingsFromMap1.size() > 0) {
                    new daywiseAppointmentsAPI(userId, userToken, dateFormatForAppointmentDay.format(selectedDate), selectedDate);
                    Log.d("dateFormat", dateFormatForAppointmentDay.format(selectedDate));
                    Log.d("bookingsSize", "bookingsSize: "+bookingsFromMap1.size());

                } else{
                    textView.setVisibility(View.VISIBLE);
                    Log.d("bookingsSize", "bookingsSize: "+bookingsFromMap1.size());
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


        compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);


//        addEvents(3, 7, 2017); //send the actual_date, (actual_month-1), actual_year received from API but without zero infront (e.g: not as 03, but as 3)
//        addEvents(4, 8, 2017); //send the actual_date, (actual_month-1), actual_year received from API but without zero infront (e.g: not as 03, but as 3)


    }


    @Override
    public void onResume() {
        super.onResume();
        calendarTitle.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
    }


    private void InitPendingJobRecyclerView() {

        adapter = new AppointmentListAdapter(CompactCalendarTest.this, allItems);
        rvPendingWorks.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager llm = new LinearLayoutManager(CompactCalendarTest.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvPendingWorks.setLayoutManager(llm);
        rvPendingWorks.setAdapter(adapter);

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


                    if (!compactCalendarView.isAnimating()) {
                        if (shouldHide) {
                            compactCalendarView.hideCalendar();
                            shouldHide = !shouldHide;
                            shouldShowAnimated = !shouldShowAnimated;
                            showNextMonthBut.setVisibility(View.GONE);
                            showPreviousMonthBut.setVisibility(View.GONE);
                            calendarTitle.setText(dateFormatForDay.format(selectedDate));
                        }
                    }

                } else if (dy < 0) {
                    System.out.println("Scrolled Upwards");

                    if (!compactCalendarView.isAnimating()) {

                        if (shouldShowAnimated) {
                            compactCalendarView.showCalendarWithAnimation();
                            shouldShowAnimated = !shouldShowAnimated;
                            shouldHide = !shouldHide;
                            showNextMonthBut.setVisibility(View.VISIBLE);
                            showPreviousMonthBut.setVisibility(View.VISIBLE);
                            calendarTitle.setText(dateFormatForMonth.format(selectedDate));
                        }
                    }
                } else {
                    System.out.println("No Vertical Scrolled");
                }

            }
        });
    }



    private void LoadPendingWorkList() {
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


        long timeInMillis = currentCalender.getTimeInMillis();

        List<Event> events = getEvents(timeInMillis);

        compactCalendarView.addEvents(events);
    }

    private List<Event> getEvents(long timeInMillis) {
        return Arrays.asList(new Event(Color.argb(255, 0, 255, 0), timeInMillis, "Event at " + new Date(timeInMillis)));
    }

    private List<Event> getDisabledDays(long timeInMillis) {
        return Arrays.asList(new Event(Color.argb(255, 255, 0, 0), timeInMillis, "Event at " + new Date(timeInMillis)));
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
            HttpPost httppost = new HttpPost("http://182.160.109.132/api/doctor-schedule/"+userId);
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

            try {
                Log.d("jsonDataSchedule", "+++++++++" + result);
                JSONObject jsonObj = new JSONObject(result);
                JSONObject json = new JSONObject(jsonObj.getString("result"));


                if (json.getString("status").equals("success")) {
                    JSONArray scheduleArray = json.getJSONArray("doctor_time_schedules");

                    Log.d("array", "length of scheduleArray: " + scheduleArray.length());


                    if(scheduleArray.length()!=0) {

                        for (int i = 0; i < scheduleArray.length(); i++) {

                            JSONObject scheduleJson = scheduleArray.getJSONObject(i);
                            Log.d("jsonData", "jsonData#" + i + "+++++++++" + scheduleJson);


                            AppointmentListModelClass schedule = new AppointmentListModelClass();
                            schedule.setDay(scheduleJson.getString("day"));
                            schedule.setStatus(scheduleJson.getString("status"));


                            if(schedule.getStatus().equals("0")) {

                                fromDisabledDays = true;

                                currentCalender.setTime(new Date());

                                currentCalender.set(Calendar.WEEK_OF_YEAR, Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));

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


                                    long timeInMillis = currentCalender.getTimeInMillis();

                                    List<Event> disabledDaysEvent = getDisabledDays(timeInMillis);

                                    compactCalendarView.addEvents(disabledDaysEvent);

                                    currentCalender.add(Calendar.WEEK_OF_YEAR, 1);
                                }
                            }
                        }
                    }

                }
            } catch(JSONException e){
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

            try {
                Log.d("jsonData", "+++++++++" + result);
                JSONObject jsonObj = new JSONObject(result);
                JSONObject json = new JSONObject(jsonObj.getString("result"));


                if (json.getString("status").equals("success")) {
                    JSONArray scheduleArray = json.getJSONArray("appointments");

                    Log.d("array", "length of appointmentsArray: " + scheduleArray.length());


                    if(scheduleArray.length()!=0) {

                        for (int i = 0; i < scheduleArray.length(); i++) {

                            JSONObject scheduleJson = scheduleArray.getJSONObject(i);
                            Log.d("jsonData", "jsonData#" + i + "+++++++++" + scheduleJson);


                            AppointmentListModelClass appointments = new AppointmentListModelClass();
                            appointments.setDay(scheduleJson.getString("appointment_date"));
                            appointments.setStatus(scheduleJson.getString("status"));


                            if(appointments.getStatus().equals("approved")) {
                                dayInString = appointments.getDay().substring(8,10);
                                monthInString = appointments.getDay().substring(5,7);
                                yearInString = appointments.getDay().substring(0,4);

                                Log.d("requestedDate", yearInString+"-"+monthInString+"-"+dayInString);

                                addEvents(Integer.parseInt(dayInString), Integer.parseInt(monthInString)-1, Integer.parseInt(yearInString));
                            }
                        }


                    }

                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }




    private class daywiseAppointmentsAPI extends AsyncTask<String, String, String> {
        String userId, userToken, dateClickedByUser;
        Date selectedDate;


        private daywiseAppointmentsAPI(String userId, String userToken, String dateClickedByUser, Date selectedDate) {
            this.userId = userId;
            this.userToken = userToken;
            this.dateClickedByUser = dateClickedByUser;
            this.selectedDate= selectedDate;
        }


        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Log.d("hey", "statusReport: onPreExecute e dhukse!!!");

        }


        @Override
        protected String doInBackground(String... params) {

            Log.d("hey", "statusReport: doInBackground e dhukse!!!");

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

            Log.d("hey", "statusReport: onPostExecute e dhukse!!!");

            try {
                Log.d("jsonData", "+++++++++" + result);
                JSONObject jsonObj = new JSONObject(result);
                JSONObject json = new JSONObject(jsonObj.getString("result"));


                if (json.getString("status").equals("success")) {
                    allItems.clear();

                    JSONArray scheduleArray = json.getJSONArray("appointments");

                    Log.d("array", "length of appointmentsArray: " + scheduleArray.length());



                    if(scheduleArray.length() != 0) {

                        Log.d("checker", ""+scheduleArray.length());

                        textView.setVisibility(View.GONE);


                        for (int i = 0; i < scheduleArray.length(); i++) {

                            JSONObject scheduleJson = scheduleArray.getJSONObject(i);
                            Log.d("appointmentDetails", "jsonData#" + i + "+++++++++" + scheduleJson);
                            JSONObject patientData = scheduleJson.getJSONObject("patient");
                            JSONObject patientProfileData = patientData.getJSONObject("profile");


                            AppointmentListModelClass appointmentDay = new AppointmentListModelClass();
                            appointmentDay.setDay(scheduleJson.getString("appointment_date"));
                            appointmentDay.setStartTime(scheduleJson.getString("appointment_start_time"));
                            appointmentDay.setEndTime(scheduleJson.getString("appointment_end_time"));
                            appointmentDay.setStatus(scheduleJson.getString("status"));
                            appointmentDay.setPatientAvatar(patientData.getString("avatar"));
                            appointmentDay.setPatientFirstName(patientProfileData.getString("first_name"));
                            appointmentDay.setPatientLastName(patientProfileData.getString("last_name"));
                            appointmentDay.setReason(patientProfileData.getString("main_problem"));
                            allItems.add(appointmentDay);

                            Log.d("profilePicURL", appointmentDay.getPatientAvatar());

                        }




                        if(allItems.size() > 0) {
                            adapter.notifyDataSetChanged();
                        }



                    } else{

                        textView.setVisibility(View.VISIBLE);
                    }

                }
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
}
