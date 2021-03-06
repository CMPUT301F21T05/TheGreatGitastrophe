package com.example.cmput_301_project.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmput_301_project.Account;
import com.example.cmput_301_project.FirestoreHandler;
import com.example.cmput_301_project.adapters.PendingFriendsAdapter;
import com.example.cmput_301_project.R;

import java.util.ArrayList;
import java.util.List;

/**
 *  Activity that handles the process of adding friends to an account, and fetches Account data related to pending and actual friends
 */
public class AddFriendsPage extends AppCompatActivity {

    private ListView pendingFriendsView;
    private Button addFriendButton;
    private EditText friendNameField;
    private static ArrayList<String> pendingFriendsList;
    private static PendingFriendsAdapter pendingFriendsAdapter;
    FirestoreHandler firestoreHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_page);
        // Find our components here
        addFriendButton = (Button) findViewById(R.id.addFriendButton);

        pendingFriendsView = (ListView) findViewById(R.id.pendingFriendList);

        friendNameField = (EditText) findViewById(R.id.friendNameField);

        pendingFriendsList = new ArrayList<>();

        firestoreHandler = FirestoreHandler.create();
        List<String> pendingIdList = firestoreHandler.getActiveUserAccount().getFriendPendingList();
        for (String id : pendingIdList) {
            pendingFriendsList.add(firestoreHandler.getAccountData().get(id).getUserName());
        }

        pendingFriendsAdapter = new PendingFriendsAdapter(this, R.layout.add_friend_custom_list, pendingFriendsList);
        pendingFriendsView.setAdapter(pendingFriendsAdapter);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendName = friendNameField.getText().toString();
                int confirmAdd = -1;
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(true);
                if (!friendName.equals(firestoreHandler.getActiveUserAccount().getUserName())) {
                    if (friendName.length() > 0) {
                        for (Account friendAccount : firestoreHandler.getAccountData().values()) {
                            if (friendAccount.getUserName().equals(friendName)) {
                                confirmAdd = friendAccount.addPendingFriend(firestoreHandler.getActiveUserId());
                                friendAccount.updateFirestore();
                                break;
                            }
                        }
                    }

                    if (confirmAdd == 0) {
                        // Standard TBD Alert Dialogue
                        builder.setTitle("Sent Friend Request");
                        builder.setMessage("You asked " + friendName + " to be your friend.");
                    } else if (confirmAdd == -1) {
                        // Standard TBD Alert Dialogue
                        builder.setTitle("User Not Found");
                    } else if (confirmAdd == 1) {
                        builder.setTitle("Already Friends");
                    } else if (confirmAdd == 2) {
                        builder.setTitle("Already Sent Request");
                    }
                } else {
                    builder.setTitle("Cannot Friend Yourself");
                }
                builder.setNegativeButton("OK", null);
                // Create the alert dialog and display it over the fragment
                AlertDialog alertBox = builder.create();
                alertBox.show();
            }
        });
    }

    /**
     * Returns the ToDo array list
     * @return Array list containing ToDo events
     */
    public static ArrayList<String> getPendingList(){
        return pendingFriendsList;
    }

    /**
     * Returns the ToDo adapter
     * @return Adapter for ToDo list
     */
    public static PendingFriendsAdapter getPendingListAdapter(){
        return pendingFriendsAdapter;
    }
}
