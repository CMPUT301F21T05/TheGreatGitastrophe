package com.example.cmput_301_project.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmput_301_project.Account;
import com.example.cmput_301_project.FirestoreHandler;
import com.example.cmput_301_project.HabitEvent;
import com.example.cmput_301_project.R;
import com.example.cmput_301_project.pages.CameraPage;
import com.example.cmput_301_project.pages.LocationPage;

import java.util.ArrayList;
import java.util.List;

/**
 * An adapter for holding event habit objects, as a recycler view it also comes with a holder to which
 * we assign attributes to various views
 */

public class EventHabitAdapter extends RecyclerView.Adapter<EventHabitAdapter.ItemVH>{
    private static final String TAG="Adapter";
    List<HabitEvent> habitEventList;
    Activity context;
    boolean delMode;

    /**
     * Constructor, Activity and delMode are essential for handling edits and deletions
     * @param fm
     * @param delMode
     */
    public EventHabitAdapter(List<HabitEvent> habitEventList, Activity fm, boolean delMode) {
        this.habitEventList = habitEventList;
        this.context = fm;
        this.delMode = delMode;
    }

    /**
     * Returns whether or not deletion mode is used
     * @return True if delete mode
     */
    public boolean isDelMode() {
        return delMode;
    }

    /**
     * Sets deletion mode
     * @return
     */
    public void setDelMode(boolean delMode) {
        this.delMode = delMode;
    }

    /**
     * Returns a view holder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public EventHabitAdapter.ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_event_custom_list, parent, false);
        return new EventHabitAdapter.ItemVH(view);
    }

    /**
     * Converts an input string to a bitmap and rotates it 90 degrees.
     * @param inputString
     * @return
     */
    private Bitmap stringToRotatedBitmap(String inputString) {
        // String to bitmap from: https://stackoverflow.com/questions/23005948/convert-string-to-bitmap
        try{
            byte [] encodeByte = Base64.decode(inputString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            //Bitmap rotation from: https://stackoverflow.com/questions/9015372/how-to-rotate-a-bitmap-90-degrees/29369579
            Matrix matrix = new Matrix();

            matrix.postRotate(90);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);

            return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        }
        catch(Exception e){
            e.getMessage();
        }
        return null;
    }

    /**
     * Used to translate habit stats to the list item holder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(EventHabitAdapter.ItemVH holder, int position) {
        HabitEvent habitEvent = habitEventList.get(position);
        holder.habitNameView.setText(habitEvent.getTitle());
        holder.commentView.setText(habitEvent.getComment());
        holder.DateView.setText(habitEvent.getDate());
        if (habitEvent.getImage() != null) {
            holder.imageView.setImageBitmap(stringToRotatedBitmap(habitEvent.getImage()));
        }
        boolean isExpanded=habitEventList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE:View.GONE);
    }

    /**
     * Gets count of items
     * @return Size of habit list
     */
    @Override
    public int getItemCount() {
        return habitEventList.size();
    }

    class ItemVH extends RecyclerView.ViewHolder {
        private static final String TAG = "Item";
        TextView habitNameView, DateView;
        EditText commentView;
        LinearLayout expandableLayout;
        Button iconButton;
        Button locationButton;
        Button photoButton;
        Account userAccount;
        ImageView imageView;

        /**
         *
         * Constructor that finds the textviews and buttons needed to assign habit attributes to
         * @param itemView
         */
        public ItemVH(View itemView) {
            super(itemView);
            habitNameView = itemView.findViewById(R.id.habitNameTextView);
            DateView = itemView.findViewById(R.id.habitEventDateTextView);
            commentView = itemView.findViewById(R.id.editTextTextPassword);
            iconButton = itemView.findViewById(R.id.photoIconButton);
            locationButton = itemView.findViewById(R.id.editLocationButton);
            photoButton = itemView.findViewById(R.id.photoIconButton);
            expandableLayout = itemView.findViewById(R.id.expandableHELayout);
            imageView = itemView.findViewById(R.id.imageView);
            userAccount = FirestoreHandler.create().getActiveUserAccount();

            // Give itemView a listener for expansion and deletion
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HabitEvent currentEvent = habitEventList.get(getAdapterPosition());
                    if (isDelMode()) {
                        habitEventList.remove(habitEventList.get(getAdapterPosition()));
                        Bundle extras = context.getIntent().getExtras();
                        ArrayList<HabitEvent> events = new ArrayList<HabitEvent>();
                        userAccount.deleteHabitEvent(currentEvent, extras.getString("habitId"));
                        userAccount.updateFirestore();
                    }
                    else {
                        currentEvent.setExpanded(!currentEvent.isExpanded());
                        commentView.setText(currentEvent.getComment());
                    }
                    notifyDataSetChanged();

                }
            });
            commentView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    HabitEvent habit = habitEventList.get(getAdapterPosition());
                    habit.setComment(charSequence.toString());
                    userAccount.updateFirestore();
                }

                @Override
                public void afterTextChanged(Editable editable) { }
            });
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Edit Habit Location?");
                    builder.setMessage("This will mean updating previously saved location data");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HabitEvent currentEvent = habitEventList.get(getAdapterPosition());
                            Intent locationIntent = new Intent(context.getApplicationContext(), LocationPage.class);
                            locationIntent.putExtra("eventId", currentEvent.getId());
                            locationIntent.putExtra("habitName", currentEvent.getTitle());
                            context.startActivity(locationIntent);
                        }
                    });
                    builder.setNegativeButton("DIMISS", null);
                    // Create the alert dialog and display it over the fragment
                    AlertDialog alertBox = builder.create();
                    alertBox.show();
                }
            });
            photoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setCancelable(true);
                    builder.setTitle("Edit Habit Photo?");
                    builder.setMessage("This will mean updating previously saved photo");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            HabitEvent currentEvent = habitEventList.get(getAdapterPosition());
                            Intent cameraIntent = new Intent(context.getApplicationContext(), CameraPage.class);
                            cameraIntent.putExtra("eventId", currentEvent.getId());
                            cameraIntent.putExtra("habitName", currentEvent.getTitle());
                            context.startActivity(cameraIntent);
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("DIMISS", null);
                    // create the alert dialog and display it over the fragment
                    AlertDialog alertBox = builder.create();
                    alertBox.show();
                }
            });
        }
    }
}
