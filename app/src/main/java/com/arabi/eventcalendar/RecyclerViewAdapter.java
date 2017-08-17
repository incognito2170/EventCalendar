package com.arabi.eventcalendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.BenMcLelland.PetCrazy.activities.PetProfileActivity;
import com.BenMcLelland.PetCrazy.entities.PetInformation;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolders> {

    private List<PetInformation> itemList;
    private Context context;
    public PetInformation nature;
    public int pet_id;
    public CardView.LayoutParams layoutParams;


    public RecyclerViewAdapter(Context context, List<PetInformation> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_list, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }


    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, final int position) {
        nature = itemList.get(position);


//        DisplayImageOptions builder = new DisplayImageOptions.Builder()
//                .cacheOnDisk(true)
//                .build();
        String imageUrl = nature.getPet_image();

        Glide.with(context)
                .load(imageUrl)
                .override(layoutParams.width, layoutParams.height)
                .centerCrop()
                .placeholder(R.color.transparent)
                .error(R.color.transparent)
                .crossFade()
                .into(holder.petImage);

        Log.d("Testsize", "size: " + holder.imageWidth);

//        ImageLoader imageLoader = ImageLoader.getInstance();
//
//        imageLoader.displayImage(imageUrl,
//                holder.petImage, builder);


        holder.petName.setText(nature.getPetName());
        holder.petBreed.setText(nature.getPetBreed());
        String petBornTime = itemList.get(position).getPetAge();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");

        try {

            Date oldDate = dateFormat.parse(petBornTime);
            System.out.println(oldDate);

            Date currentDate = new Date();

            long diff = currentDate.getTime() - oldDate.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long extraSeconds = seconds % 60;
            long hours = minutes / 60;
            long extraMinutes = minutes % 60;
            long days = hours / 24;
            long extraHours = hours % 24;
            long months = days / 30;
            long extraDays = days % 30;
            long years = months / 12;
            long extraMonths = months % 12;

            if (oldDate.before(currentDate)) {
                Log.d("Difference", " years: " + years + " months: " + extraMonths
                        + " days: " + extraDays + " hours: " + extraHours + " minutes: " + extraMinutes + " seconds: " + extraSeconds);
            }


            String age = years + " years " + extraMonths + " months " + extraDays + " days";
            holder.petAge.setText(age);
            holder.petImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, PetProfileActivity.class);
                    Log.d("CurrentPetClick", "click hoise================================= " + nature.getPetName() + " " + nature.getPetID());
                    intent.putExtra("Pet_Selected", itemList.get(position));
                    context.startActivity(intent);

                }

            });
        } catch (ParseException e) {
            e.printStackTrace();
            holder.petAge.setText("N/A");
            holder.petImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("CurrentPetClick", "Test Data click pet=================================");
                    Intent intent = new Intent(context, PetProfileActivity.class);
                    intent.putExtra("Pet_Selected", itemList.get(position));
                    context.startActivity(intent);

                }

            });
        }


    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    class RecyclerViewHolders extends RecyclerView.ViewHolder {

        public TextView petName, petBreed, petAge;
        public View petPhoto;
        public ImageView petImage;
        int imageWidth;

        public RecyclerViewHolders(View itemView) {
            super(itemView);

            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                itemView.getBackground().setAlpha(0);
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            }

            petName = (TextView) itemView.findViewById(R.id.tv_species);
            petBreed = (TextView) itemView.findViewById(R.id.tv_area);
            petAge = (TextView) itemView.findViewById(R.id.tv_population);
            petPhoto = itemView.findViewById(R.id.grid_view_layout);
            petImage = (ImageView) itemView.findViewById(R.id.petImage);

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);
            int width = size.x;

            layoutParams = (CardView.LayoutParams) petPhoto.getLayoutParams();

            layoutParams.width = (width) / 2;
            imageWidth = width / 2;
            layoutParams.height = (width) / 2;

        }

    }
}
