package com.example.michael.ui.activities;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.michael.ui.R;

/**
 * Created by Michael on 2015-05-11.
 */
public class GameStateDialog extends DialogFragment implements View.OnClickListener {
    private Button okButton;
    Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_game_state, null);
        okButton = (Button) view.findViewById(R.id.okButton);
        okButton.setOnClickListener(this);
        setCancelable(false);
        getDialog().setTitle("The game has finished!");
        return view;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        communicator.onDialogMessage();
    }

    interface Communicator{
        void onDialogMessage();
    }

}
