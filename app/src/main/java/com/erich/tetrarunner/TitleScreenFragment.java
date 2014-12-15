package com.erich.tetrarunner;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Erich on 12/11/2014.
 *
 */
public class TitleScreenFragment extends Fragment
{
    public final static int START_GAME_ID =    0;
    public final static int RECORDS_ID =       1;
    public final static int OPTIONS_ID =       2;
    public final static int HOW_TO_PLAY_ID =   3;
    public final static int EXIT_ID =          4;

    OnSelectionListener _onSelectionListener;
    public interface OnSelectionListener
    {
        public void OnSelection(int selection);
    }
    public void setOnSelectionListener(OnSelectionListener osl)
    {
        this._onSelectionListener = osl;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout titleLayout = new LinearLayout(getActivity());
        titleLayout.setBackgroundColor(0xFF000000);
        titleLayout.setOrientation(LinearLayout.VERTICAL);

        ImageView title = new ImageView(getActivity());
        title.setBackgroundColor(0xFF000011);
        title.setImageResource(R.drawable.logo);
        title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 40));

        TextView start = new TextView(getActivity());
        start.setTextSize(20.0f);
        start.setTextColor(0xFFFFFF88);
        start.setBackgroundColor(0xFF113388);
        start.setText("Start Playing");
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (_onSelectionListener != null)
                    _onSelectionListener.OnSelection(START_GAME_ID);
            }
        });
        start.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 8));
        start.setGravity(Gravity.CENTER);

        TextView records = new TextView(getActivity());
        records.setTextSize(20.0f);
        records.setTextColor(0xFFFFFF88);
        records.setBackgroundColor(0xFF113388);
        records.setText("View Records");
        records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (_onSelectionListener != null)
                    _onSelectionListener.OnSelection(RECORDS_ID);
            }
        });
        records.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 8));
        records.setGravity(Gravity.CENTER);

        TextView options = new TextView(getActivity());
        options.setTextSize(20.0f);
        options.setTextColor(0xFFFFFF88);
        options.setBackgroundColor(0xFF113388);
        options.setText("Options");
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (_onSelectionListener != null)
                    _onSelectionListener.OnSelection(OPTIONS_ID);
            }
        });
        options.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 8));
        options.setGravity(Gravity.CENTER);

        TextView howToPlay = new TextView(getActivity());
        howToPlay.setTextSize(20.0f);
        howToPlay.setTextColor(0xFFFFFF88);
        howToPlay.setBackgroundColor(0xFF113388);
        howToPlay.setText("How To Play");
        howToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (_onSelectionListener != null)
                    _onSelectionListener.OnSelection(HOW_TO_PLAY_ID);
            }
        });
        howToPlay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 8));
        howToPlay.setGravity(Gravity.CENTER);

        TextView exit = new TextView(getActivity());
        exit.setTextSize(20.0f);
        exit.setTextColor(0xFFFFFF88);
        exit.setBackgroundColor(0xFF113388);
        exit.setText("Exit");
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (_onSelectionListener != null)
                    _onSelectionListener.OnSelection(EXIT_ID);
            }
        });
        exit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 8));
        exit.setGravity(Gravity.CENTER);

        FrameLayout paddingLayout = new FrameLayout(getActivity());
        paddingLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 20));
        paddingLayout.setBackgroundColor(0xFF000011);

        titleLayout.addView(title);
        titleLayout.addView(start);
        titleLayout.addView(records);
        titleLayout.addView(options);
        titleLayout.addView(howToPlay);
        titleLayout.addView(exit);
        titleLayout.addView(paddingLayout);
        return titleLayout;
    }
}
