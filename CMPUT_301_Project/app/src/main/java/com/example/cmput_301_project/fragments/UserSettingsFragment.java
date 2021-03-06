/**
 * Fragment to show user settings
 */
package com.example.cmput_301_project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.cmput_301_project.Account;
import com.example.cmput_301_project.FirestoreHandler;
import com.example.cmput_301_project.R;

import java.util.HashMap;

/**
 * Fragment that showcases user settings
 * A simple {@link Fragment} subclass.
 * Use the {@link UserSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSettingsFragment extends Fragment implements View.OnClickListener {
    private FirestoreHandler firestoreHandlerClass = FirestoreHandler.create();
    EditText usernameField;
    public UserSettingsFragment() { /* Required empty public constructor */ }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment UserSettingsFragment.
     */
    public static UserSettingsFragment newInstance(String param1, String param2) {
        UserSettingsFragment fragment = new UserSettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        // Setup save, exit, and edit photo buttons to use onClickListeners
        // Referencing: https://stackoverflow.com/questions/18711433/button-listener-for-button-in-fragment-in-android
        Button saveButton = (Button) view.findViewById(R.id.saveButton);
        Button exitButton = (Button) view.findViewById(R.id.exitButton);
        TextView editProfileTV = (TextView) view.findViewById(R.id.editProfileTV);
        usernameField = (EditText) view.findViewById(R.id.editUsernamePT);
        usernameField.setText(firestoreHandlerClass.getActiveUserAccount().getUserName());
        saveButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);
        editProfileTV.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setCancelable(true);
        // Depending on the view passed to onClick, execute some action
        switch (view.getId()) {
            case R.id.saveButton:
                // If the save button is pressed, save changes and exit the fragment
                HashMap<String, Account> accountData = firestoreHandlerClass.getAccountData();
                String newUsername = usernameField.getText().toString();
                Account updatedAccount = firestoreHandlerClass.getActiveUserAccount();
                if (updatedAccount.getUserName().equals(newUsername)) {
                    getActivity().onBackPressed();
                    break;
                }
                for (Account existingAccount : accountData.values()) {
                    if (existingAccount.getUserName().equals(newUsername)) {
                        builder.setTitle("Invalid Username");
                        builder.setMessage("This username is already taken");
                        builder.setNegativeButton("OK", null);
                        AlertDialog alertBox = builder.create();
                        alertBox.show();
                        return;
                    }
                }
                builder.setTitle("Update Account Info?");
                builder.setMessage("Your old username could get taken");
                // If the user chooses to exit, return to the user profile activity
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    updatedAccount.setUserName(newUsername);
                    firestoreHandlerClass.modifyAccount(updatedAccount);
                    TextView usernameTextView = getActivity().findViewById(R.id.usernameText);
                    Account activeUserAccount = FirestoreHandler.create().getActiveUserAccount();
                    usernameTextView.setText(activeUserAccount.getUserName());
                    getActivity().onBackPressed();
                });
                // If the user chooses to stay on the fragment, simply close the dialog
                builder.setNegativeButton("Go Back", null);
                // Create the alert dialog and display it over the fragment
                AlertDialog confirmUpdatedialog = builder.create();
                confirmUpdatedialog.show();
                break;
            case R.id.exitButton:
                // If the exit button is pressed, simply exit the fragment
                builder.setTitle("Exit Without Saving?");
                builder.setMessage("Any changes you've made will be lost");
                // If the user chooses to exit, return to the user profile activity
                builder.setPositiveButton("Exit", (dialog, which) -> {
                    getActivity().onBackPressed();
                });
                // If the user chooses to stay on the fragment, simply close the dialog
                builder.setNegativeButton("Go Back", null);
                // Create the alert dialog and display it over the fragment
                AlertDialog confirmExitdialog = builder.create();
                confirmExitdialog.show();
                break;
            case R.id.editProfileTV:
                // If the 'edit profile' textView is pressed, allow user to change their profile pic
                // Standard TBD Alert Dialogue
                builder.setCancelable(true);
                builder.setTitle("This Is Not Connected Yet");
                builder.setMessage("This will be added in project part 4");
                builder.setNegativeButton("OK", null);
                // Create the alert dialog and display it over the fragment
                AlertDialog alertBox = builder.create();
                alertBox.show();
                break;
        }
    }
}
