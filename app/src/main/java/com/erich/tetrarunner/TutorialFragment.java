package com.erich.tetrarunner;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Erich on 12/13/2014.
 *  Displays a series of images that serve as a tutorial for the user.
 */
public class TutorialFragment extends Fragment
{
    ImageView image;
    TextView bottomFrame;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout tutorialLayout = new LinearLayout(getActivity());
        tutorialLayout.setBackgroundColor(0xFF000000);
        tutorialLayout.setOrientation(LinearLayout.VERTICAL);

        image = new ImageView(getActivity());
        image.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 90));
        image.setImageResource(R.drawable.tutorial_a);

        bottomFrame = new TextView(getActivity());
        bottomFrame.setText("NEXT");
        bottomFrame.setTextSize(20.0f);
        bottomFrame.setTextColor(0xFF00CCFF);
        bottomFrame.setBackgroundColor(0xFF000011);
        bottomFrame.setGravity(Gravity.CENTER);
        bottomFrame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 10));
        bottomFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image.setImageResource(R.drawable.tutorial_b);
                bottomFrame.setText("BACK");
                bottomFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TutorialFragment.this.getFragmentManager().popBackStack();
                    }
                });
            }
        });

        tutorialLayout.addView(image);
        tutorialLayout.addView(bottomFrame);

        return tutorialLayout;
    }
}
