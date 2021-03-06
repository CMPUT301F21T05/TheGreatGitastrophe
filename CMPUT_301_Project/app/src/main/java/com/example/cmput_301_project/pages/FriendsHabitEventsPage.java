package com.example.cmput_301_project.pages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmput_301_project.FirestoreHandler;
import com.example.cmput_301_project.HabitEvent;
import com.example.cmput_301_project.adapters.ProfileHabitAdapter;
import com.example.cmput_301_project.R;

import java.util.ArrayList;

/**
 *  Page for seeing a friend's Publicly viewable habit events
 */
public class FriendsHabitEventsPage extends AppCompatActivity {
    private ProfileHabitAdapter todoHabitAdapter;
    private ProfileHabitAdapter completedHabitAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_habit_events_page);

        ArrayList<HabitEvent> incompleteHabits = new ArrayList<>();
        ArrayList<HabitEvent> completeHabits = new ArrayList<>();

        FirestoreHandler firestoreHandler = FirestoreHandler.create();
        // Convert the incomplete/complete habits to corresponding ArrayLists
        for (String friendId: firestoreHandler.getActiveUserAccount().getFriendList()) {
            firestoreHandler.getAccountData().get(friendId).getTodayHabitEvents(incompleteHabits, completeHabits, false, true);
        }

        todoHabitAdapter = new ProfileHabitAdapter(incompleteHabits,this);
        completedHabitAdapter = new ProfileHabitAdapter(completeHabits,this);

        // Set up the recyclerView and adapter for incomplete habits
        RecyclerView incompleteHabitsView = findViewById(R.id.incompleteRV);
        incompleteHabitsView.setAdapter(todoHabitAdapter);
        incompleteHabitsView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the recyclerView and adapter for complete habits
        RecyclerView completeHabitsView = findViewById(R.id.completeRV);
        completeHabitsView.setAdapter(completedHabitAdapter);
        completeHabitsView.setLayoutManager(new LinearLayoutManager(this));

        // This just adds dividing lines between the values in both recycler views
        // from: https://stackoverflow.com/questions/24618829/how-to-add-dividers-and-spaces-between-items-in-recyclerview
        DividerItemDecoration incompleteRVDivider = new DividerItemDecoration(incompleteHabitsView.getContext(), LinearLayoutManager.VERTICAL);
        incompleteHabitsView.addItemDecoration(incompleteRVDivider);
        DividerItemDecoration completeRVDivider = new DividerItemDecoration(completeHabitsView.getContext(), LinearLayoutManager.VERTICAL);
        completeHabitsView.addItemDecoration(completeRVDivider);
    }
}
