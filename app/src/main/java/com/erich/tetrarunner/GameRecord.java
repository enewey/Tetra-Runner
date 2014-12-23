package com.erich.tetrarunner;

import java.util.Date;

/**
 * Created by Erich on 12/13/2014.
 * Represents the user's various records (high score, best time, etc) for a specific level
 */
public class GameRecord
{
    String gameName; //Name of the game associated with this GameRecord
    public String getGameName() {
        return gameName;
    }

    int numTimesPlayed; //Number of times the user has attempted this level
    public int getNumTimesPlayed() {
        return numTimesPlayed;
    }
    public void incrementNumTimesPlayed(){
        this.numTimesPlayed++;
    }

    int highCoins;  //Highest number of tetra this user has collected (coins == tetra)
    public int getHighCoins() {
        return highCoins;
    }
    public void setHighCoins(int highCoins) {
        this.highCoins = highCoins;
        this.highCoinsDate = new Date(System.currentTimeMillis());
    }

    Date highCoinsDate; //Date/time the Tetra high score was set
    public String getHighCoinsDate() {
        return chopDate(highCoinsDate.toString());
    }

    long bestTime;  //Fastest time for completing this level
    public long getBestTime() {
        return bestTime;
    }
    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
        this.bestTimeDate = new Date(System.currentTimeMillis());
    }

    Date bestTimeDate;  //Date/time the fastest time was set
    public String getBestTimeDate() {
        return chopDate(bestTimeDate.toString());
    }

    /**
     *  Helper method for chopping up the Date.toString() method into a specific format.
     * @param date - Date.toString() String
     * @return - String with the desired format (MM/DD/YYYY)
     */
    private String chopDate(String date)
    {
        String[] choppedDate = date.split(" ");
        return "" + choppedDate[1] + "/" + choppedDate[2] + "/" + choppedDate[5];
    }

    /**
     *  Constructor to create a GameRecord with dummy initial values
     * @param gameName - Name of the game board.
     */
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
