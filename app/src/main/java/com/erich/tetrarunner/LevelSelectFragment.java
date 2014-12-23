package com.erich.tetrarunner;

import android.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Erich on 12/11/2014.
 * Fragment used to select a level.
 * Acts as a ListAdapter.
 */
public class LevelSelectFragment extends Fragment implements ListAdapter
{
    ArrayList<String> _gameNames;

    public interface OnItemSelectedListener
    {
        public void onItemSelected(LevelSelectFragment gameListFragment, String gameName);
    }
    OnItemSelectedListener _onItemSelectedListener;
    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener)
    {
        _onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        LinearLayout selectLayout = new LinearLayout(getActivity());
        selectLayout.setOrientation(LinearLayout.VERTICAL);

        TextView topFrame = new TextView(getActivity());
        topFrame.setText("SELECT A LEVEL");
        topFrame.setTextSize(32.0f);
        topFrame.setTextColor(0xFF00CCFF);
        topFrame.setBackgroundColor(0xFF000011);
        topFrame.setGravity(Gravity.CENTER);
        topFrame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 10));

        ListView gameList = new ListView(getActivity());
        gameList.setAdapter(this);
        _gameNames = null;

        gameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = _gameNames.get(i);
                if (_onItemSelectedListener != null)
                    _onItemSelectedListener.onItemSelected(LevelSelectFragment.this, name);
            }
        });
        gameList.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 80));

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
                LevelSelectFragment.this.getFragmentManager().popBackStack();
            }
        });

        selectLayout.addView(topFrame);
        selectLayout.addView(gameList);
        selectLayout.addView(bottomFrame);

        return selectLayout;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int i) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return GameData.getGameNames().size();
    }

    @Override
    public Object getItem(int i)
    {
        if (_gameNames == null)
            _gameNames = GameData.getGameNames();

        return GameData.getGameBoard(_gameNames.get(i));
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     *  Overridden method returns a TextView that displays information about a level.
     */
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup)
    {
        GameBoard gameBoard = (GameBoard)getItem(i);

        TextView gameText;
        if (convertView != null)
            gameText = (TextView) convertView;
        else
            gameText = new TextView(getActivity());

        gameText.setTextSize(20.0f);
        gameText.setTextColor(0xFFFFFF88);
        gameText.setGravity(Gravity.CENTER_VERTICAL);
        gameText.setBackgroundColor(0xFF113388);

        String gameString = "";
        gameString += "\n          " + gameBoard.boardName + "\n\n" + "Difficulty: " + gameBoard.difficulty.toString() + "\n";

        gameText.setText(gameString);
        return gameText;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return getCount() <= 0;
    }
}
