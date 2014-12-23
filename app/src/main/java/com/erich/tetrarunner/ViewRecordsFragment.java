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
 *  View best times and high scores for each level.
 */
public class ViewRecordsFragment extends Fragment
{
    GameRecord _record;
    public void setRecord(GameRecord record) {
        _record = record;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        //Get the mins/secs/hundredths component for the best time
        long bestTime = _record.getBestTime();
        int mins, secs, hundredths;
        mins = (int)Math.floor(bestTime / 60000);
        if (mins > 99)
        {
            mins = 99;
            secs = 59;
            hundredths = 99;
        }
        else {
            secs = (int) Math.floor(bestTime / 1000) % 60;
            hundredths = (int) Math.floor(bestTime / 10) % 100;
        }


        LinearLayout recordLayout = new LinearLayout(getActivity());
        recordLayout.setPadding(5,5,5,5);
        recordLayout.setOrientation(LinearLayout.VERTICAL);

        TextView topFrame = new TextView(getActivity());
        topFrame.setTextSize(24.0f);
        topFrame.setTextColor(0xFF00CCFF);
        topFrame.setBackgroundColor(0xFF000011);
        topFrame.setGravity(Gravity.CENTER);
        topFrame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 20));
        topFrame.setText("Records for " + _record.getGameName());

        TextView midFrame = new TextView(getActivity());
        midFrame.setTextSize(18.0f);
        midFrame.setTextColor(0xFFFFFF88);
        midFrame.setBackgroundColor(0xFF113388);
        //midFrame.setGravity(Gravity.LEFT);
        midFrame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 70));
        midFrame.setText(
                    "TIMES PLAYED: " + _record.numTimesPlayed + "\n\n\n" +
                    "BEST TIME: " + mins +":"+ secs +":"+ hundredths + "\n\n" +
                            "         Set: " + _record.getBestTimeDate() + "\n\n\n" +
                    "MOST TETRA COLLECTED: " + _record.getHighCoins() + "\n\n" +
                            "         Set: " + _record.getHighCoinsDate() + "\n"
        );

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
                ViewRecordsFragment.this.getFragmentManager().popBackStack();
            }
        });


        recordLayout.addView(topFrame);
        recordLayout.addView(midFrame);
        recordLayout.addView(bottomFrame);
        return recordLayout;
    }
}
