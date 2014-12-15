package com.erich.tetrarunner;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

/**
 * Created by Erich on 12/3/2014.
 *
 */
public class MainActivity extends Activity implements TitleScreenFragment.OnSelectionListener, LevelSelectFragment.OnItemSelectedListener
{
    final static int FRAME_ID = 10;
    static boolean inRecordsSelect;
    static boolean soundOff, musicOff;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GameData.initialize(this);

        FrameLayout frame = new FrameLayout(this);
        frame.setId(FRAME_ID);

        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction addTransaction = fragManager.beginTransaction();
        TitleScreenFragment tf = new TitleScreenFragment();
        tf.setOnSelectionListener(this);

        addTransaction.replace(FRAME_ID, tf);
        addTransaction.commit();

        inRecordsSelect = false;
        soundOff = false;
        musicOff = false;

        setContentView(frame);
    }

    @Override
    public void OnSelection(int selection)
    {
        switch (selection)
        {
            case TitleScreenFragment.START_GAME_ID:
                inRecordsSelect = false;
                activateLevelSelectFragment();
                break;
            case TitleScreenFragment.RECORDS_ID:
                inRecordsSelect = true;
                activateLevelSelectFragment();
                break;
            case TitleScreenFragment.OPTIONS_ID:
                activateOptionsFragment();
                break;
            case TitleScreenFragment.HOW_TO_PLAY_ID:
                activateTutorialFragment();
                break;
            case TitleScreenFragment.EXIT_ID:
                finish();
                break;
        }
    }

    private void activateLevelSelectFragment()
    {
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction addTransaction = fragManager.beginTransaction();
        addTransaction.addToBackStack(null);
        LevelSelectFragment levelSelectFragment = new LevelSelectFragment();
        levelSelectFragment.setOnItemSelectedListener(this); //Activity set as listener for gameListFragment
        addTransaction.replace(FRAME_ID, levelSelectFragment);
        addTransaction.commit();
    }

    private void activateViewRecordsFragment(GameRecord record)
    {
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction addTransaction = fragManager.beginTransaction();
        addTransaction.addToBackStack(null);
        ViewRecordsFragment viewRecordsFragment = new ViewRecordsFragment();
        viewRecordsFragment.setRecord(record);
        addTransaction.replace(FRAME_ID, viewRecordsFragment);
        addTransaction.commit();
    }

    private void activateOptionsFragment()
    {
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction addTransaction = fragManager.beginTransaction();
        addTransaction.addToBackStack(null);
        OptionsFragment optionsFragment = new OptionsFragment();
        addTransaction.replace(FRAME_ID, optionsFragment);
        addTransaction.commit();
    }

    private void activateTutorialFragment()
    {
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction addTransaction = fragManager.beginTransaction();
        addTransaction.addToBackStack(null);
        TutorialFragment tutorialFragment = new TutorialFragment();
        addTransaction.replace(FRAME_ID, tutorialFragment);
        addTransaction.commit();
    }

    @Override
    public void onItemSelected(LevelSelectFragment gameListFragment, String gameName)
    {
        if (inRecordsSelect)
        {
            activateViewRecordsFragment(GameData.getGameRecord(gameName));
        }
        else
        {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("gameName", gameName);
            intent.putExtra("soundOff", soundOff);
            intent.putExtra("musicOff", musicOff);
            startActivity(intent);
        }
    }
}
