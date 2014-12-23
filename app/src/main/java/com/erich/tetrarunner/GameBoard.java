package com.erich.tetrarunner;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Erich on 11/28/2014.
 * Represents the entirety of a level. Contains the name of the board/track, the objects within, etc.
 */
public class GameBoard
{
    final static int BOARD_WIDTH = 5; //Constant value... don't make boards wider than this.
    public enum GameDifficulty { EASY, MODERATE, HARD, EXTREME } //Four difficulty types

    ArrayList<ActorGroup> _board;   //Contains all the objects in this level
    String boardName;               //Name of this board

    GameDifficulty difficulty;
    public GameDifficulty getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(GameDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    int numOfCoins;
    public int getNumOfCoins() {
        return numOfCoins;
    }
    public void setNumOfCoins(int numOfCoins) {
        this.numOfCoins = numOfCoins;
    }

    /**
     *  Constructor. Give it a name and a difficulty; no objects are built here.
     * @param name - String
     * @param diff - GameDifficulty
     */
    public GameBoard(String name, GameDifficulty diff)
    {
        boardName = name;
        difficulty = diff;
        _board = new ArrayList<ActorGroup>(); //Create empty board
    }

    /**
     *  Overloaded constructor to build GameBoard from file.
     * @param context Activity context
     * @param fileName Name of file.
     */
    public GameBoard(Context context, String fileName)
    {
        _board = new ArrayList<ActorGroup>();

        //Read and load the board data from a file, given a file name.
        try{
            InputStream inputStream = context.getAssets().open("levels/"+fileName);
            InputStreamReader fileReader = new InputStreamReader(inputStream);
            BufferedReader bufferedTextReader = new BufferedReader(fileReader);

            Gson gson = new Gson();
            boardName = gson.fromJson(bufferedTextReader.readLine(), String.class);
            difficulty = gson.fromJson(bufferedTextReader.readLine(), GameDifficulty.class);
            numOfCoins = 0;

            String readLine = bufferedTextReader.readLine();
            //Reads data until we hit "end"
            while (!readLine.startsWith("end"))
            {
                String[] upperLayerStrings = readLine.split(",");
                GameActor[] upperLayer = new GameActor[BOARD_WIDTH];

                readLine = bufferedTextReader.readLine();
                String[] lowerLayerStrings = readLine.split(",");
                GameActor[] lowerLayer = new GameActor[BOARD_WIDTH];

                for (int i = 0; i < BOARD_WIDTH; i++)
                {
                    upperLayer[i] = new GameActor(gson.fromJson(upperLayerStrings[i], GameActor.ActorType.class));
                    lowerLayer[i] = new GameActor(gson.fromJson(lowerLayerStrings[i], GameActor.ActorType.class));

                    if (upperLayer[i].getType() == GameActor.ActorType.coin)
                        numOfCoins++;
                }
                ActorGroup group = new ActorGroup(upperLayer, lowerLayer, 0);
                _board.add(group);
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
    }

    /**
     *  Copy constructor. Builds piece by piece a new game board from existing game board.
     * @param copy - GameBoard object to copy.
     */
    public GameBoard(GameBoard copy)
    {
        this.boardName = copy.boardName;
        this.difficulty = copy.difficulty;
        this._board = new ArrayList<ActorGroup>();
        for (ActorGroup ag : copy.getBoard())
        {
            GameActor[] upper = new GameActor[BOARD_WIDTH];
            GameActor[] lower = new GameActor[BOARD_WIDTH];
            for (int i = 0; i < BOARD_WIDTH; i++)
            {
                upper[i] = new GameActor(ag.getUpperLayer()[i].getType());
                lower[i] = new GameActor(ag.getLowerLayer()[i].getType());
            }
            this._board.add(new ActorGroup(upper, lower, 0));
        }
    }

    public ArrayList<ActorGroup> getBoard()
    {
        return _board;
    }

    /**
     * Method used for manually constructing a GameBoard via code
     * @param up    - upper layer
     * @param floor - lower layer
     * @return      - returns the ActorGroup added.
     */
    public ActorGroup addActorGroup(GameActor[] up, GameActor[] floor)
    {
        ActorGroup n = new ActorGroup(up, floor, _board.size() + 1);
        _board.add(n);
        return n;
    }

    /**
     *  Adds a blank ActorGroup to this board.
     * @return - the blank ActorGroup added.
     */
    public ActorGroup addActorGroup()
    {
        ActorGroup n = new ActorGroup(new GameActor[BOARD_WIDTH], new GameActor[BOARD_WIDTH], _board.size() + 1);
        _board.add(n);
        return n;
    }

    public int getSize()
    {
        return _board.size();
    }

    /**
     *  Save this board's data to a file.
     * @param context - Context object to get file directory
     */
    public void writeBoardToFile(Context context)
    {
        Gson gson = new Gson();

        String jsonDataModel = "";

        jsonDataModel += gson.toJson(boardName) + "\r\n";
        jsonDataModel += gson.toJson(difficulty) + "\r\n";

        //Iterate through each game, append data to Json string to be written to file
        for (ActorGroup ag : _board)
        {
            for (GameActor actor : ag.getUpperLayer())
            {
                jsonDataModel = jsonDataModel + gson.toJson(actor.getType(), GameActor.ActorType.class) + ",";
            }
            jsonDataModel += "\r\n";
            for (GameActor actor : ag.getLowerLayer())
            {
                jsonDataModel = jsonDataModel + gson.toJson(actor.getType(), GameActor.ActorType.class) + ",";
            }
            jsonDataModel += "\r\n";
        }
        jsonDataModel += "end\r\n";

        String boardFileName = boardName.replaceAll(" ", "_").toUpperCase();

        try {
            File file = new File(context.getFilesDir(), "level_" + boardFileName + ".txt");
            FileWriter textWriter = new FileWriter(file, false); //false flag overwrites old data
            BufferedWriter bufferedTextWriter = new BufferedWriter(textWriter);
            bufferedTextWriter.write(jsonDataModel);
            bufferedTextWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


}
