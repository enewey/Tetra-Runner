package com.erich.tetrarunner;

import java.util.Date;

/**
 * Created by Erich on 12/13/2014.
 */
public class GameRecord
{
    String gameName;
    public String getGameName() {
        return gameName;
    }

    int numTimesPlayed;
    public int getNumTimesPlayed() {
        return numTimesPlayed;
    }
    public void incrementNumTimesPlayed(){
        this.numTimesPlayed++;
    }

    int highCoins;
    public int getHighCoins() {
        return highCoins;
    }
    public void setHighCoins(int highCoins) {
        this.highCoins = highCoins;
        this.highCoinsDate = new Date(System.currentTimeMillis());
    }

    Date highCoinsDate;
    public String getHighCoinsDate() {
        return chopDate(highCoinsDate.toString());
    }

    long bestTime;
    public long getBestTime() {
        return bestTime;
    }
    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
        this.bestTimeDate = new Date(System.currentTimeMillis());
    }

    Date bestTimeDate;
    public String getBestTimeDate() {
        return chopDate(bestTimeDate.toString());
    }

    private String chopDate(String date)
    {
        String[] choppedDate = date.split(" ");
        return "" + choppedDate[1] + "/" + choppedDate[2] + "/" + choppedDate[5];
    }

    public GameRecord(String gameName)
    {
        this.gameName = gameName;
        numTimesPlayed = 0;
        highCoins = 0;
        highCoinsDate = new Date(System.currentTimeMillis());
        bestTime = System.currentTimeMillis();
        bestTimeDate = new Date(System.currentTimeMillis());
    }
}
