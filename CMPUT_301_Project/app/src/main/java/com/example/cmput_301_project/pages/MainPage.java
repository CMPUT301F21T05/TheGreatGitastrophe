/**
 * Main home page activity
 */
package com.example.cmput_301_project.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmput_301_project.Account;
import com.example.cmput_301_project.FirestoreHandler;
import com.example.cmput_301_project.Habit;
import com.example.cmput_301_project.R;

/**
 * Activity that serves as a main hub and overview of the User's progression of their own habits
 */
public class MainPage extends AppCompatActivity {
    // Represents text that changes depending on menu item clicked
    TextView textView;
    // Textview that represents the % of progress bar filled out
    TextView progressText;
    // Amount of circular progress bar filled out
    private int progress = 0;
    // References progress bar object
    ProgressBar progressBar;
    int progressMaxCounter = 0, progressCurrentCounter = 0;
    int[] progressRate = new int[2];

    Account userAccount;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        // Initialize circular progress bar
        progressBar = findViewById(R.id.progress_bar);
        // Initialize updatebutton and friendbutton
        Button updateButton = findViewById(R.id.updateButton);
        Button friendButton = findViewById(R.id.friendbutton);
        textView = findViewById(R.id.btName);
        progressText = findViewById(R.id.prg_value);
        userAccount = FirestoreHandler.create().getActiveUserAccount();

        // Percent completed habits per month graphic update
        for (Habit habit : userAccount.getHabitTable()) {
            progressRate = userAccount.getHabitCompletionRateInLastThirtyDays(habit.getId());
            progressCurrentCounter += progressRate[1];
            progressMaxCounter += progressRate[0];
        }
        if (progressMaxCounter != 0) {
            progress = 100 * progressCurrentCounter / progressMaxCounter;
        } else {
            progress = 0;
        }
        // Progress bar library bug fix (not our fault)
        if (progress < 100) {
            updateProgress(progress + 1);
        } else {
            updateProgress(progress - 1);
        }
        updateProgress(progress);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchToTodayHabitsPage = new Intent(MainPage.this, TodayHabitsPage.class);
                updateProgress(progress);
                startActivity(switchToTodayHabitsPage);
            }
        });
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { }
        });

        friendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Switch to Friends Page
                Intent switchToAddFriendsPage = new Intent(MainPage.this, MyFriendsPage.class);
                startActivity(switchToAddFriendsPage);
            }
        });
    }

    /**
     * Method to set the progress value i.e % of progress
     * bar filled out */
    private void updateProgress(int progress)
    {
        progressBar.setProgress(progress);
        progressText.setText(progress + "%");
    }
}
