package com.arabi.eventcalendar;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.rv_pending_works)
    RecyclerView rvPendingWorks;
    Unbinder unbinder;

    private List<AppointmentListModelClass> pendingJobItem;
    private AppointmentListAdapter adapter;
    CalendarDay currentDay, eventCalendar;
    MaterialCalendarView calendar, calendar1;
    GridLayoutManager layoutManager;
    int visibleItemCount;
    int totalItemCount;
    int pastVisibleItems;
    Calendar cal;
    int scrollState;
    DayViewDecorator currentDayDecorator;
    ArrayList daysOfWeek;
    Boolean isSelected=true, isCurrentDayDecoratorAdded = false;
    String currentDayInString, selectedDayInString, dayInString, monthInString, yearInString, dateInString;
    HashSet<CalendarDay> calendarDayList;
    RecyclerView.OnScrollListener mListener;

    RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendar1 = (MaterialCalendarView) findViewById(R.id.calendarView1);

        currentDay = CalendarDay.today();
        calendar.setSelectedDate(currentDay);
        calendar1.setSelectedDate(currentDay);

        cal = Calendar.getInstance();   //takes the current date as the starting date for event marking
        addEventDecoration(cal);   //passes the current date as the starting date in the range of dates to be set events on

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


        currentDayInString = convertInString(currentDay);

        centerTitle();

        Toast.makeText(this, "Current date is: "+currentDayInString, Toast.LENGTH_SHORT).show();

        unbinder = ButterKnife.bind(this);

        InitPendingJobRecyclerView();



        calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                isSelected = calendar.getSelectedDate().equals(currentDay);

                if(!isSelected && !isCurrentDayDecoratorAdded){
                    calendar.addDecorator(currentDayDecorator);
                    isCurrentDayDecoratorAdded = true;
                }
                else if(isSelected && isCurrentDayDecoratorAdded){
                    calendar.removeDecorator(currentDayDecorator);
                    isCurrentDayDecoratorAdded = false;
                }
                else{
                    selectedDayInString = convertInString(date);
                    Toast.makeText(getApplicationContext(), "Selected date is: " +selectedDayInString , Toast.LENGTH_SHORT).show();
                }



//                calendar.state().edit()
//                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setMinimumDate(CalendarDay.from(calendar.getSelectedDate().getYear(), calendar.getSelectedDate().getMonth(), (calendar.getSelectedDate().getDay()-6)))
//                        .setMaximumDate(CalendarDay.from(calendar.getSelectedDate().getYear(), calendar.getSelectedDate().getMonth(), (calendar.getSelectedDate().getDay()+6)))
//                        .setCalendarDisplayMode(CalendarMode.WEEKS)
//                        .commit();

                InitPendingJobRecyclerView();
            }
            });


        calendar1.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                isSelected = calendar1.getSelectedDate().equals(currentDay);

                if(!isSelected && !isCurrentDayDecoratorAdded){
                    calendar.addDecorator(currentDayDecorator);
                    isCurrentDayDecoratorAdded = true;
                }
                else if(isSelected && isCurrentDayDecoratorAdded){
                    calendar.removeDecorator(currentDayDecorator);
                    isCurrentDayDecoratorAdded = false;
                }
                else{
                    selectedDayInString = convertInString(date);
                    Toast.makeText(getApplicationContext(), "Selected date is: " +selectedDayInString , Toast.LENGTH_SHORT).show();
                }



//                calendar.state().edit()
//                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setMinimumDate(CalendarDay.from(calendar.getSelectedDate().getYear(), calendar.getSelectedDate().getMonth(), (calendar.getSelectedDate().getDay()-6)))
//                        .setMaximumDate(CalendarDay.from(calendar.getSelectedDate().getYear(), calendar.getSelectedDate().getMonth(), (calendar.getSelectedDate().getDay()+6)))
//                        .setCalendarDisplayMode(CalendarMode.WEEKS)
//                        .commit();

                InitPendingJobRecyclerView();
            }
        });
    }


    public void addEventDecoration(Calendar cal){

        ArrayList listDecor = new ArrayList();

        for (int i = 0; i<10; i++) {
            CalendarDay day = CalendarDay.from(cal);
            listDecor.add((new EventDecorator(Color.parseColor("#FF4081"), day)));
            cal.add(Calendar.DATE, 2);   //here, '2' represents how many days interval to increment, applicable for testing on a range of dates
        }

        calendar.addDecorators(listDecor);
        calendar1.addDecorators(listDecor);
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

        }
    }


    String convertInString(CalendarDay selectedDay){

        if(selectedDay.getDay()<10){
            dayInString = "0"+selectedDay.getDay();
        }
        else{
            dayInString = String.valueOf(selectedDay.getDay());
        }

        if(selectedDay.getMonth()<10){
            monthInString = "0"+selectedDay.getMonth();
        }
        else{
            monthInString = String.valueOf(selectedDay.getMonth());
        }

        yearInString = String.valueOf(selectedDay.getYear());

        dateInString = dayInString+"-"+monthInString+"-"+yearInString;

        return dateInString;
    }


    private void LoadPendingWorkList() {
        int[] profileImage = new int[]{
                R.drawable.patient_profile_pic,
                R.drawable.patient_profile_pic_2,
                R.drawable.patient_profile_pic_3};

        AppointmentListModelClass a = new AppointmentListModelClass("Robin van Persie","10:30 AM","11:00 AM", profileImage[0]);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Hakan Calhanoglu","11:00 AM","11:30 AM",profileImage[1]);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Gianluigi Buffon","11:30 AM","12:00 PM",profileImage[2]);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 4","12:00 PM","12:30 PM",profileImage[0]);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 5","12:30 PM","1:00 PM", profileImage[1]);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 6","1:00 PM","1:30 PM",profileImage[2]);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 7","1:30 PM","2:00 PM", profileImage[0]);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 8","2:00 PM","2:30 PM", profileImage[1]);
        pendingJobItem.add(a);

        a = new AppointmentListModelClass("Patient 9","2:30 PM","3:00 PM",profileImage[2]);
        pendingJobItem.add(a);

        Collections.shuffle(pendingJobItem);

        adapter.notifyDataSetChanged();
    }

    private void InitPendingJobRecyclerView() {

        pendingJobItem = new ArrayList<>();
        adapter = new AppointmentListAdapter(this, pendingJobItem);

        mLayoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        rvPendingWorks.setLayoutManager(mLayoutManager);

        rvPendingWorks.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    System.out.println("The RecyclerView is not scrolling");
                    switch (scrollState) {
                        case 1:
                            System.out.println("scrollState = 1");
                            calendar.setVisibility(View.GONE);
                            calendar1.setVisibility(View.VISIBLE);
                            break;
                        case 0:
                            System.out.println("scrollState = 0");
                            calendar1.setVisibility(View.GONE);
                            calendar.setVisibility(View.VISIBLE);
                            break;
                    }
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
        public void onScrolled(RecyclerView recyclerView, int dx, int dy){
            if(dx>0)
            {
                System.out.println("Scrolled Right");

            }
            else if(dx < 0)
            {
                System.out.println("Scrolled Left");

            }
            else {

                System.out.println("No Horizontal Scrolled");
            }

            if(dy>0)
            {
                System.out.println("Scrolled Downwards");
                scrollState = 1;
            }
            else if(dy < 0)
            {
                System.out.println("Scrolled Upwards");
                scrollState = 0;

            }
            else {

                System.out.println("No Vertical Scrolled");
            }

        }
        });

        rvPendingWorks.setItemAnimator(new DefaultItemAnimator());
        rvPendingWorks.setAdapter(adapter);

        LoadPendingWorkList();

    }




    private void centerTitle() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }
}

