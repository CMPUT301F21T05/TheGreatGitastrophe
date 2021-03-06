/**
 * Fragment used in the habits menu
 */
package com.example.cmput_301_project.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.cmput_301_project.FirestoreHandler;
import com.example.cmput_301_project.R;

/**
 * Fragment that allows verification of passwords
 * A {@link Fragment} subclass that helps verify passwords through dialog.
 * Use the {@link VerifyPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyPasswordFragment extends DialogFragment {
    private EditText verifyPasswordEditText;
    OnPasswordVerify passwordVerifier;

    public interface OnPasswordVerify {
        public void onPasswordVerify(boolean verified, Context context);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof OnPasswordVerify) {
            passwordVerifier = (OnPasswordVerify) context;
        } else {
            throw new RuntimeException((context.toString()) + "must implement OnFragementInteractionListener." );
        }
    }

    /**
     * Used to help create a dialog that has a pre-existing habit object to edit
     * @return
     */
    static VerifyPasswordFragment newInstance (){
        Bundle args = new Bundle();
        VerifyPasswordFragment fragment = new VerifyPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Used to fill out habit information in the fragment fields
     * @param savedInstanceState
     * @return
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_verify_password, null);
        verifyPasswordEditText = view.findViewById(R.id.verifyPasswordEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setTitle("Verify Password")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String passwordToVerify = verifyPasswordEditText.getText().toString();
                        if (FirestoreHandler.create().getActiveUserAccount().checkPassword(passwordToVerify)) {
                            passwordVerifier.onPasswordVerify(true, getContext());
                        } else {
                            passwordVerifier.onPasswordVerify(false, getContext());
                        }
                    }
                }).create();
    }
}
