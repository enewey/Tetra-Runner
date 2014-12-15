package com.erich.tetrarunner;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Erich on 12/13/2014.
 */
public class OptionsFragment extends Fragment
{
    static TextView soundFrame;
    static TextView musicFrame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout recordLayout = new LinearLayout(getActivity());
        recordLayout.setPadding(5,5,5,5);
        recordLayout.setOrientation(LinearLayout.VERTICAL);

        TextView topFrame = new TextView(getActivity());
        topFrame.setTextSize(32.0f);
        topFrame.setTextColor(0xFF00CCFF);
        topFrame.setBackgroundColor(0xFF000011);
        topFrame.setGravity(Gravity.CENTER);
        topFrame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 20));
        topFrame.setText("OPTIONS");

        soundFrame = new TextView(getActivity());
        soundFrame.setTextSize(24.0f);
        soundFrame.setTextColor(0xFFFFFF88);
        soundFrame.setBackgroundColor(0xFF113388);
        soundFrame.setGravity(Gravity.CENTER);
        soundFrame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 20));
        soundFrame.setText("SOUND: " + ((MainActivity.soundOff) ? "Off" : "On"));
        soundFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                MainActivity.soundOff = !MainActivity.soundOff;
                OptionsFragment.soundFrame.setText("SOUND: " + ((MainActivity.soundOff) ? "Off" : "On"));
            }
        });

        musicFrame = new TextView(getActivity());
        musicFrame.setTextSize(24.0f);
        musicFrame.setTextColor(0xFFFFFF88);
        musicFrame.setBackgroundColor(0xFF113388);
        musicFrame.setGravity(Gravity.CENTER);
        musicFrame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 20));
        musicFrame.setText("MUSIC: " + ((MainActivity.musicOff) ? "Off" : "On"));
        musicFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                MainActivity.musicOff = !MainActivity.musicOff;
                OptionsFragment.musicFrame.setText("MUSIC: " + ((MainActivity.musicOff) ? "Off" : "On"));
            }
        });

        TextView aboutFrame = new TextView(getActivity());
        aboutFrame.setTextSize(18.0f);
        aboutFrame.setTextColor(0xFFFFFF88);
        aboutFrame.setBackgroundColor(0xFF113388);
        aboutFrame.setGravity(Gravity.CENTER);
        aboutFrame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 30));
        aboutFrame.setText("Tetra Runner\nDeveloped by Erich Newey, 2014\nerich.newey@utah.edu");

        TextView bottomFrame = new TextView(getActivity());
        bottomFrame.setText("BACK");
        bottomFrame.setTextSize(20.0f);
        bottomFrame.setTextColor(0xFF00CCFF);
        bottomFrame.setBackgroundColor(0xFF000011);
        bottomFrame.setGravity(Gravity.CENTER);
        bottomFrame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 10));
        bottomFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OptionsFragment.this.getFragmentManager().popBackStack();
            }
        });


        recordLayout.addView(topFrame);
        recordLayout.addView(soundFrame);
        recordLayout.addView(musicFrame);
        recordLayout.addView(aboutFrame);
        recordLayout.addView(bottomFrame);
        return recordLayout;
    }
}
