package com.arabi.eventcalendar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Arabi on 08/09/2017.
 */

public class AppointmentListAdapter extends RecyclerView.Adapter<AppointmentListAdapter.ShowsViewHolder> {

    public Context context;
    private List<AppointmentListModelClass> pendingJobItems;
    private String changeMessage = "";

    public AppointmentListAdapter(Context context, List<AppointmentListModelClass> pendingJobItems) {
        this.context = context;
        this.pendingJobItems = pendingJobItems;
    }

    @Override
    public ShowsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.appointment_list_item, parent, false);
        return new ShowsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ShowsViewHolder holder, int position) {

        AppointmentListModelClass stuffsPendingJobs = pendingJobItems.get(position);

        holder.tvServiceName.setText(stuffsPendingJobs.getPatientName());
        holder.tvStartTime.setText(stuffsPendingJobs.getStartTime());
        holder.tvEndTime.setText(stuffsPendingJobs.getEndTime());
        holder.pop_up_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.pop_up_icon);
                //inflating menu from xml resource
                popup.inflate(R.menu.pop_up_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.changeAppointment:
                                //handle menuItem1 click
                                Toast.makeText(context,"Change Appointment clicked",Toast.LENGTH_SHORT).show();
                                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                                alertDialog.setTitle("Change Appointment Date");
                                alertDialog.setIcon(R.mipmap.ic_launcher);
                                alertDialog.setMessage("The patient will be asked to schedule another appointment");
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                                break;
                            case R.id.cancelAppointment:
                                //handle menuItem2 click
                                Toast.makeText(context,"Cancel Appointment clicked",Toast.LENGTH_SHORT).show();
                                AlertDialog alertDialog1 = new AlertDialog.Builder(context).create();
                                alertDialog1.setTitle("Cancel Appointment");
                                alertDialog1.setIcon(R.mipmap.ic_launcher);
                                alertDialog1.setMessage("The patient will be notified about your cancellation of the appointment");
                                alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog1.show();
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        Glide.with(context).load(stuffsPendingJobs.getProfileImage()).into(holder.imageStuffProfile);

    }

    @Override
    public int getItemCount() {
        return pendingJobItems.size();
    }

    public class ShowsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.image_stuff_profile)
        CircularImageView imageStuffProfile;
        @BindView(R.id.tv_service_name)
        TextView tvServiceName;
        @BindView(R.id.tv_start_time)
        TextView tvStartTime;
        @BindView(R.id.tv_end_time)
        TextView tvEndTime;
        @BindView(R.id.pop_up_icon)
        ImageView pop_up_icon;

        public ShowsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            imageStuffProfile.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.image_stuff_profile:
                    Intent testIntent = new Intent(context, PatientProfile.class);
                    context.startActivity(testIntent);
                    break;
            }
        }
    }
}
