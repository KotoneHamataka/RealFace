package com.lifeistech.android.realface;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class HomeDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final int score = bundle.getInt("score");

        final EditText nameText = new EditText(getContext());
        nameText.setHint("Name");
        AlertDialog.Builder bld = new AlertDialog.Builder(getActivity(), R.style.Animation_Dialog);
        bld.setTitle("ランキングへの登録");
        bld.setMessage("ランキングに写真と名前を登録しますか?");
        bld.setView(nameText);
        bld.setPositiveButton("YES!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismissAllowingStateLoss();

                saveScore(nameText.getText().toString(), score);

                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        bld.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        return bld.create();
    }

    public void saveScore(String name, int score) {
        User user = new User(name, score, getContext());
        AsyncSaveData asyncTask = new AsyncSaveData();
        asyncTask.execute(user);
    }
}
