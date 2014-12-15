package com.erich.tetrarunner;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Erich on 12/12/2014.
 * Builds stages for the game, loads and stores GameBoard objects into memory.
 */
public class GameData
{
    private static HashMap<String, GameBoard> allBoards;
    private static HashMap<String, GameRecord> allRecords;

    static GameBoard buildGameA(Context context)
    {
        int trackSize = 165;
        GameBoard gameBoard;

        int coinAmount = 0;

        gameBoard = new GameBoard("Tetra Galore", GameBoard.GameDifficulty.EASY);
        Log.i("Building level A", "Building Level start");
        for (int k = 0; k < trackSize; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            for (int i = 0; i < GameBoard.BOARD_WIDTH; i++)
            {
                if ((i+(k%2))%2 == 0 && k > 5 && k < 15 && k%2 == 0)
                    upper[i] = new GameActor(GameActor.ActorType.coin);
                else if ((i+(k%2))%2 == 0 && i > 0 && i < 4 && k > 30 && k < 150 && k%3 == 0)
                    upper[i] = new GameActor(GameActor.ActorType.coin);
                else
                    upper[i] = new GameActor(GameActor.ActorType.empty);
            }

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            for (int i = 0; i < GameBoard.BOARD_WIDTH; i++)
            {
                if (i%2 == 0 && k > 20 && k < 165)
                    lower[i] = new GameActor(GameActor.ActorType.empty);
                else
                    lower[i] = new GameActor(GameActor.ActorType.floor);
            }

            gameBoard.addActorGroup(upper, lower);
        }

        Log.i("Building level A", "Writing to file now.");

        gameBoard.writeBoardToFile(context);
        gameBoard.setNumOfCoins(coinAmount);
        return gameBoard;
    }

    static GameBoard buildGameC(Context context)
    {
        int trackSize = 250;
        GameBoard gameBoard;

        int coinAmount = 0;

        gameBoard = new GameBoard("Easy Breezy", GameBoard.GameDifficulty.EASY);
        Log.i("Building level A", "Building Level start");
        for (int k = 0; k < trackSize; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            for (int i = 0; i < GameBoard.BOARD_WIDTH; i++)
            {
                upper[i] = new GameActor(GameActor.ActorType.empty);
            }

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            for (int i = 0; i < GameBoard.BOARD_WIDTH; i++)
            {
                lower[i] = new GameActor(GameActor.ActorType.floor);
            }

            gameBoard.addActorGroup(upper, lower);
        }

        Log.i("Building level A", "Writing to file now.");

        gameBoard.writeBoardToFile(context);
        gameBoard.setNumOfCoins(coinAmount);
        return gameBoard;
    }

    static GameBoard buildGameB(Context context)
    {
        int trackSize = 600;
        GameBoard gameBoard;

        int coinAmount = 0;

        Log.i("Building level B", "Building Level start");

        gameBoard = new GameBoard("Fun Times", GameBoard.GameDifficulty.MODERATE);
        int k = 0;
        for ( ; k < 30; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            upper[0] = new GameActor(GameActor.ActorType.empty);
            upper[1] = new GameActor(GameActor.ActorType.empty);
            upper[2] = (k<15) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.barrier);
            upper[3] = new GameActor(GameActor.ActorType.empty);
            upper[4] = new GameActor(GameActor.ActorType.empty);

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            lower[0] = new GameActor(GameActor.ActorType.empty);
            lower[1] = (k<10) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[2] = new GameActor(GameActor.ActorType.floor);
            lower[3] = (k<10) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[4] = new GameActor(GameActor.ActorType.empty);

            gameBoard.addActorGroup(upper, lower);
        }

        for ( ; k < 60; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            upper[0] = new GameActor(GameActor.ActorType.empty);
            upper[1] = (k<40) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.barrier);
            upper[2] = (k<35) ? new GameActor(GameActor.ActorType.barrier) : new GameActor(GameActor.ActorType.coin);
            upper[3] = (k<40) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.barrier);
            upper[4] = new GameActor(GameActor.ActorType.empty);

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            lower[0] = (k<35) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[1] = new GameActor(GameActor.ActorType.floor);
            lower[2] = new GameActor(GameActor.ActorType.floor);
            lower[3] = new GameActor(GameActor.ActorType.floor);
            lower[4] =(k<35) ?  new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);

            if (k>=35) coinAmount++;

            gameBoard.addActorGroup(upper, lower);
        }

        for ( ; k < 120; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            upper[0] = (k%10 == 0) ? new GameActor(GameActor.ActorType.coin) : new GameActor(GameActor.ActorType.empty);
            upper[1] = (k%10 == 0) ? new GameActor(GameActor.ActorType.barrier) :
                   ((k+5)%10 == 0) ? new GameActor(GameActor.ActorType.coin) :
                                    new GameActor(GameActor.ActorType.empty);
            upper[2] = new GameActor(GameActor.ActorType.empty);
            upper[3] = (k%10 == 0) ? new GameActor(GameActor.ActorType.barrier) :
                   ((k+5)%10 == 0) ? new GameActor(GameActor.ActorType.coin) :
                                    new GameActor(GameActor.ActorType.empty);
            upper[4] = (k%10 == 0) ? new GameActor(GameActor.ActorType.coin) : new GameActor(GameActor.ActorType.empty);

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            lower[0] = new GameActor(GameActor.ActorType.floor);
            lower[1] = new GameActor(GameActor.ActorType.floor);
            lower[2] = new GameActor(GameActor.ActorType.floor);
            lower[3] = new GameActor(GameActor.ActorType.floor);
            lower[4] = new GameActor(GameActor.ActorType.floor);

            if (k%10 == 0) coinAmount++;
            if ((k+5)%10 == 0) coinAmount+=2;

            gameBoard.addActorGroup(upper, lower);
        }

        Log.i("Building level B", "Second marker");

        for ( ; k < 140; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            upper[0] = new GameActor(GameActor.ActorType.empty);
            upper[1] = new GameActor(GameActor.ActorType.empty);
            upper[2] = new GameActor(GameActor.ActorType.empty);
            upper[3] = new GameActor(GameActor.ActorType.empty);
            upper[4] = new GameActor(GameActor.ActorType.empty);

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            lower[0] = new GameActor(GameActor.ActorType.floor);
            lower[1] = new GameActor(GameActor.ActorType.floor);
            lower[2] = new GameActor(GameActor.ActorType.floor);
            lower[3] = new GameActor(GameActor.ActorType.floor);
            lower[4] = new GameActor(GameActor.ActorType.floor);

            gameBoard.addActorGroup(upper, lower);
        }

        for ( ; k < 250; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            upper[0] = ((k+6)%20==0) ? new GameActor(GameActor.ActorType.coin) : new GameActor(GameActor.ActorType.empty);
            upper[1] = new GameActor(GameActor.ActorType.barrier);
            upper[2] = new GameActor(GameActor.ActorType.empty);
            upper[3] = new GameActor(GameActor.ActorType.barrier);
            upper[4] = ((k+6)%20==0) ? new GameActor(GameActor.ActorType.coin) : new GameActor(GameActor.ActorType.empty);

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            lower[0] = (k%20 == 0 || k%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[1] = new GameActor(GameActor.ActorType.floor);
            lower[2] = new GameActor(GameActor.ActorType.floor);
            lower[3] = new GameActor(GameActor.ActorType.floor);
            lower[4] = (k%20 == 0 || k%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);

            if ((k+6)%20==0) coinAmount+=2;

            gameBoard.addActorGroup(upper, lower);
        }

        for ( ; k < 350; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            upper[0] = new GameActor(GameActor.ActorType.empty);
            upper[1] = new GameActor(GameActor.ActorType.empty);
            upper[2] = new GameActor(GameActor.ActorType.empty);
            upper[3] = new GameActor(GameActor.ActorType.empty);
            upper[4] = new GameActor(GameActor.ActorType.empty);

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            lower[0] = ((k+3)%20 == 0 || (k+3)%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[1] = ((k+3)%20 == 0 || (k+3)%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[2] = ((k+3)%20 == 0 || (k+3)%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[3] = ((k+3)%20 == 0 || (k+3)%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[4] = ((k+3)%20 == 0 || (k+3)%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);

            gameBoard.addActorGroup(upper, lower);
        }

        Log.i("Building level B", "Third marker");

        for ( ; k < 450; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            upper[0] = new GameActor(GameActor.ActorType.empty);
            upper[1] = (k%40 == 0) ? new GameActor(GameActor.ActorType.coin) : new GameActor(GameActor.ActorType.empty);
            upper[2] = new GameActor(GameActor.ActorType.empty);
            upper[3] = ((k+10)%40 == 0) ? new GameActor(GameActor.ActorType.coin) : new GameActor(GameActor.ActorType.empty);
            upper[4] = new GameActor(GameActor.ActorType.empty);

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            lower[0] = new GameActor(GameActor.ActorType.empty);
            lower[1] = ((k+5)%20 == 0 || (k+5)%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[2] = ((k+5)%20 == 0 || (k+5)%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[3] = ((k+5)%20 == 0 || (k+5)%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[4] = new GameActor(GameActor.ActorType.empty);

            if (k%40 == 0) coinAmount++;
            if ((k+5)%40 == 0) coinAmount++;

            gameBoard.addActorGroup(upper, lower);
        }

        for ( ; k < 550; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            upper[0] = new GameActor(GameActor.ActorType.empty);
            upper[1] = new GameActor(GameActor.ActorType.empty);
            upper[2] = (k%20 == 0) ? new GameActor(GameActor.ActorType.coin) : new GameActor(GameActor.ActorType.empty);
            upper[3] = new GameActor(GameActor.ActorType.empty);
            upper[4] = new GameActor(GameActor.ActorType.empty);

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            lower[0] = new GameActor(GameActor.ActorType.empty);
            lower[1] = new GameActor(GameActor.ActorType.empty);
            lower[2] = ((k+5)%20 == 0 || (k+5)%20 == 1) ? new GameActor(GameActor.ActorType.empty) : new GameActor(GameActor.ActorType.floor);
            lower[3] = new GameActor(GameActor.ActorType.empty);
            lower[4] = new GameActor(GameActor.ActorType.empty);

            if (k%20 == 0) coinAmount++;

            gameBoard.addActorGroup(upper, lower);
        }

        for ( ; k < 600; k++)
        {
            GameActor[] upper = new GameActor[GameBoard.BOARD_WIDTH];
            upper[0] = new GameActor(GameActor.ActorType.empty);
            upper[1] = new GameActor(GameActor.ActorType.empty);
            upper[2] = new GameActor(GameActor.ActorType.empty);
            upper[3] = new GameActor(GameActor.ActorType.empty);
            upper[4] = new GameActor(GameActor.ActorType.empty);

            GameActor[] lower = new GameActor[GameBoard.BOARD_WIDTH];
            lower[0] = new GameActor(GameActor.ActorType.empty);
            lower[1] = new GameActor(GameActor.ActorType.floor);
            lower[2] = new GameActor(GameActor.ActorType.floor);
            lower[3] = new GameActor(GameActor.ActorType.floor);
            lower[4] = new GameActor(GameActor.ActorType.empty);

            gameBoard.addActorGroup(upper, lower);
        }

        Log.i("Building level B", "Writing to file now.");

        gameBoard.writeBoardToFile(context);
        gameBoard.setNumOfCoins(coinAmount);
        return gameBoard;
    }

    public static void initialize(Context context)
    {
        allBoards = new HashMap<String, GameBoard>();
        String[] fileList = null;
        try {
            fileList = context.getAssets().list("levels");
        } catch (IOException e)
        {
            Log.i("getAllGames", "Loading levels failed: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        for (String fileName : fileList)
        {
            if (fileName.startsWith("level_"))
            {
                GameBoard gameBoard = new GameBoard(context, fileName);
                allBoards.put(gameBoard.boardName, gameBoard);
            }
        }

        allRecords = new HashMap<String, GameRecord>();
        try{
            File file = new File(context.getFilesDir(), "game_records.txt");
            FileReader textReader = new FileReader(file);
            BufferedReader bufferedTextReader = new BufferedReader(textReader);

            Gson gson = new Gson();
            String readLine = bufferedTextReader.readLine();
            //Reads data until we hit "end"
            while (!readLine.startsWith("end"))
            {
                GameRecord gameRecord = gson.fromJson(readLine, GameRecord.class);
                allRecords.put(gameRecord.getGameName(), gameRecord);
                readLine = bufferedTextReader.readLine();
            }

            bufferedTextReader.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        for (String gameName : allBoards.keySet())
        {
            if (allRecords.get(gameName) == null)
                allRecords.put(gameName, new GameRecord(gameName));
        }
    }

    public static void saveGameRecords(Context context)
    {
        Gson gson = new Gson();

        String jsonDataModel = "";

        for (String gameName : allRecords.keySet())
        {
            jsonDataModel += gson.toJson(allRecords.get(gameName), GameRecord.class) + "\n";
        }
        jsonDataModel += "end\n";

        try {
            File file = new File(context.getFilesDir(), "game_records.txt");
            FileWriter textWriter = new FileWriter(file, false); //false flag overwrites old data
            BufferedWriter bufferedTextWriter = new BufferedWriter(textWriter);
            bufferedTextWriter.write(jsonDataModel);
            bufferedTextWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getGameNames()
    {
        if (allBoards == null) {
            Log.i("getGameBoard", "Attempt to access games before initialized");
            return null;
        }

        ArrayList<String> ret = new ArrayList<String>();
        for (String name : allBoards.keySet())
        {
            ret.add(name);
        }

        return ret;
    }

    public static GameBoard getGameBoard(String gameName)
    {
        if (allBoards == null) {
            Log.i("getGameBoard", "Attempt to access games before initialized");
            return null;
        }

        return allBoards.get(gameName);
    }

    public static GameRecord getGameRecord(String gameName)
    {
        if (allRecords == null) {
            Log.i("getGameRecord", "Attempt to access records before initialized");
            return null;
        }

        return allRecords.get(gameName);
    }
}
