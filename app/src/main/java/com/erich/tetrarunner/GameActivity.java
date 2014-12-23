package com.erich.tetrarunner;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Erich on 12/8/2014.
 * Main game activity. Contains the game logic and OpenGL calls.
 */
public class GameActivity extends Activity implements GLSurfaceView.Renderer
{
    //Enum used to keep track of game state
    public enum GameState { START, PLAYING, WIN, LOSE }
    static GameState _gameState;

    static final int POSITION_ATTRIBUTE_ID = 0; //Attribute pointer for shaders
    static final int TEXTURE_ATTRIBUTE_ID = 1;  // "
    static final int NORMAL_ATTRIBUTE_ID = 2;   // "
    static final int MAX_FRAMES_PER_SECOND = 60;    //Not actually used, it turns out
    static final int MAX_NODES_LOADED = 40;     //How many rows of objects can be on screen
    static final float GRAVITY = -9.8f;         //Gravitational constant

    static final float TURN_POWER = 20.0f;      //Sideways acceleration
    static final float JUMP_POWER = 4.0f;       //Vertical velocity of jumps
    static final float MAX_SIDE_VELOCITY = 4.0f;    //Upper limit on sideways velocity
    static final float MAX_VELOCITY = 15.0f;    //Top speed

    static final float SHIP_WIDTH = 0.75f;      //For object collision
    static final float SHIP_LENGTH = 0.75f;

    //Locations for uniform variables in shaders
    static int _mvmLoc, _viewLoc, _projLoc;
    static int _ambientLoc, _diffuseLoc, _specularLoc, _emissiveLoc;
    static int _shineLoc;
    static int _lightPosLoc;
    static int _normalMatrixLoc;
    static int _textureSamplerLoc;
    static int _wordsMvmLoc, _wordsProjLoc;

    //Shaders themselves
    static int _program = -1;
    static int _wordsProgram = -1;

    static int[] _textures = new int[1];    //Loaded textures

    static GameBoard _gameBoard;    //Contains all objects on track (squares, tetra, floor, etc.)
    static String _gameName;        //Name of the board (used for keeping records etc)

    //Variables used for keeping track of time in-game, and for calculating distance/velocity
    static long _time;
    static long _timeStarted;
    static long _gameTime;


    boolean noMusic, noSound;   //Options set by user
    SoundPool soundPool;        //Used to play sounds in-game
    int[] soundIds;             //Contains loaded sounds
    MediaPlayer backgroundMusic;    //Used to play the background music in-game

    //Variables to calculate movement between frames
    float _position, _velocity, _acceleration, _sideVelocity, _sideAcceleration, _verticalVelocity;
    float _shipPositionX, _shipPositionY, _positionOffset;
    boolean _onGround, _stabilize, _falling;

    //Screen size
    int _width, _height;

    //Matrices used to keep geometry within same coordinate space
    float[] _projectionMatrix;
    static float[] _wordsProjectionMatrix;
    float[] _modelViewMatrix;

    Node[] _shipNodes;  //Contains geometry for the player's ship

    //Initial values for camera and the lookAt function.
    float[] eye = { 0.0f, 0.5f, 3.0f };
    float[] at = { 0.0f, 0.0f, 0.0f };

    float theta = 0.0f; //Theta constantly incremented during game play to rotate objects

    int _trackSize;     //Length of the in-game track

    HashMap<Integer, PointF> _activePointers;   //Data structure to maintain multiple screen touches
    int _countDown;         //Used to countdown at the beginning of the game
    int _coinsCollected;    //Keeps track of tetrahedron collected

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //Retrieve the level data from the Intent data used to start activity (from MainActivity)
        _gameName = getIntent().getExtras().getString("gameName");
        noMusic = getIntent().getExtras().getBoolean("musicOff");
        noSound = getIntent().getExtras().getBoolean("soundOff");

        //Initialize the structure for touch inputs
        _activePointers = new HashMap<Integer, PointF>();

        //Create the OpenGL window
        GLSurfaceView surfaceView = new GLSurfaceView(this);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        surfaceView.setRenderer(this);

        //Load sounds
        if (!noSound) {
            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
            soundIds = new int[10];
            soundIds[0] = soundPool.load(this, R.raw.bump_noise, 1);
            soundIds[1] = soundPool.load(this, R.raw.coin_noise, 1);
            soundIds[2] = soundPool.load(this, R.raw.countdown_noise, 1);
        }

        //Load and start playing music
        if (!noMusic) {
            backgroundMusic = MediaPlayer.create(GameActivity.this, R.raw.saturday);
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(100,100);
            backgroundMusic.start();
        }

        _gameState = GameState.START;   //Start the game
        _countDown = 3000;              //Set countdown for 3 seconds

        setContentView(surfaceView);
    }

    /**
     *  onDestroy simply stops the music and saves any records that were made.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (!noMusic) backgroundMusic.stop();
        GameData.saveGameRecords(this);
    }

    /**
     *  Similar to onDestroy; stops the music and saves any game records made.
     *  (State persistence not yet implemented...)
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        if (!noMusic) backgroundMusic.stop();
        GameData.saveGameRecords(this);
    }

    /**
     *  Initializes the OpenGL shaders and game data.
     * @param gl10 - unused
     * @param eglConfig - unused
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig)
    {
        //Read shaders from files
        String vertexShaderSource = "";
        try {
            InputStream inputStream = getAssets().open("vertex-shader");
            InputStreamReader fileReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String read;
            while ((read = bufferedReader.readLine()) != null)
                vertexShaderSource += read + "\n";

        } catch (FileNotFoundException e)
        { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

        String fragmentShaderSource = "";
        try {
            InputStream inputStream = getAssets().open("fragment-shader");
            InputStreamReader fileReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String read;
            while ((read = bufferedReader.readLine()) != null)
                fragmentShaderSource += read + "\n";

        } catch (FileNotFoundException e)
        { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

        String texVertexShaderSource = "";
        try {
            InputStream inputStream = getAssets().open("tex_vertex-shader");
            InputStreamReader fileReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String read;
            while ((read = bufferedReader.readLine()) != null)
                texVertexShaderSource += read + "\n";

        } catch (FileNotFoundException e)
        { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

        String texFragmentShaderSource = "";
        try {
            InputStream inputStream = getAssets().open("tex_fragment-shader");
            InputStreamReader fileReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String read;
            while ((read = bufferedReader.readLine()) != null)
                texFragmentShaderSource += read + "\n";

        } catch (FileNotFoundException e)
        { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }

        //Make sure these are valid
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderSource);
        GLES20.glCompileShader(vertexShader);
        String vertexShaderCompileLog = GLES20.glGetShaderInfoLog(vertexShader);
        Log.i("Vertex Shader Compile", vertexShaderCompileLog + "\n");

        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderSource);
        GLES20.glCompileShader(fragmentShader);
        String fragmentShaderCompileLog = GLES20.glGetShaderInfoLog(fragmentShader);
        Log.i("fragment Shader Compile", fragmentShaderCompileLog + "\n");

        int texVertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(texVertexShader, texVertexShaderSource);
        GLES20.glCompileShader(texVertexShader);
        vertexShaderCompileLog = GLES20.glGetShaderInfoLog(texVertexShader);
        Log.i("Texture Vertex Shader Compile", vertexShaderCompileLog + "\n");

        int texFragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(texFragmentShader, texFragmentShaderSource);
        GLES20.glCompileShader(texFragmentShader);
        fragmentShaderCompileLog = GLES20.glGetShaderInfoLog(texFragmentShader);
        Log.i("Texture fragment Shader Compile", fragmentShaderCompileLog + "\n");

        //This program used to draw ship and track; includes lighting equations
        _program = GLES20.glCreateProgram();
        GLES20.glAttachShader(_program, vertexShader);
        GLES20.glAttachShader(_program, fragmentShader);

        //This program used to draw fonts on screen
        _wordsProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(_wordsProgram, texVertexShader);
        GLES20.glAttachShader(_wordsProgram, texFragmentShader);

        //Bind attribute locations
        GLES20.glBindAttribLocation(_program, POSITION_ATTRIBUTE_ID, "position");
        GLES20.glBindAttribLocation(_program, NORMAL_ATTRIBUTE_ID, "normal");
        GLES20.glBindAttribLocation(_wordsProgram, POSITION_ATTRIBUTE_ID, "position");
        GLES20.glBindAttribLocation(_wordsProgram, TEXTURE_ATTRIBUTE_ID, "texture");

        //Link the programs, so we can easily swap between them at little cost
        GLES20.glLinkProgram(_program);
        String programLinkLog = GLES20.glGetProgramInfoLog(_program);
        Log.i("Program Link", programLinkLog + "\n");

        GLES20.glLinkProgram(_wordsProgram);
        programLinkLog = GLES20.glGetProgramInfoLog(_wordsProgram);
        Log.i("Program Link", programLinkLog + "\n");

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f); //Clear color is Black

        //Enable the vertex attribute arrays
        GLES20.glEnableVertexAttribArray(POSITION_ATTRIBUTE_ID);
        GLES20.glEnableVertexAttribArray(NORMAL_ATTRIBUTE_ID);
        GLES20.glEnableVertexAttribArray(TEXTURE_ATTRIBUTE_ID);

        //Uniform locations
        _viewLoc = GLES20.glGetUniformLocation(_program, "viewMatrix");
        _projLoc = GLES20.glGetUniformLocation(_program, "projectionMatrix");
        _ambientLoc = GLES20.glGetUniformLocation(_program, "ambientProduct");
        _diffuseLoc = GLES20.glGetUniformLocation(_program, "diffuseProduct");
        _specularLoc = GLES20.glGetUniformLocation(_program, "specularProduct");
        _emissiveLoc = GLES20.glGetUniformLocation(_program, "emissive");
        _shineLoc = GLES20.glGetUniformLocation(_program, "shine");
        _lightPosLoc = GLES20.glGetUniformLocation(_program, "lightPosition");
        _mvmLoc = GLES20.glGetUniformLocation(_program, "modelViewMatrix");
        _normalMatrixLoc = GLES20.glGetUniformLocation(_program, "normalMatrix");
        _wordsMvmLoc = GLES20.glGetUniformLocation(_wordsProgram, "modelViewMatrix");
        _wordsProjLoc = GLES20.glGetUniformLocation(_wordsProgram, "projectionMatrix");
        _textureSamplerLoc = GLES20.glGetUniformLocation(_wordsProgram, "textureSampler");

        //Load the font bitmap into textures
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.nums);
        GLES20.glGenTextures(1, _textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textures[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle(); //always recycle!

        //Initialize arrays
        _modelViewMatrix = new float[16];
        _shipNodes = new Node[3];

        initializeBoard(); //Method that initializes the game board
        _positionOffset = 3.0f; //Constant value... may move declaration to above later.

        //Initialize starting values
        _position = 0.0f;
        _velocity = 1.0f;
        _sideVelocity = 0.0f;
        _verticalVelocity = 0.0f;
        _acceleration = 5.0f;
        _shipPositionX = 0.0f;
        _shipPositionY = 0.0f;
        _onGround = true;
        _coinsCollected = 0;
        _timeStarted = System.currentTimeMillis();
        _time = _timeStarted;

        //Play the countdown noise! (Game is starting!)
        playNoise(Noises.COUNTDOWN);
    }

    /**
     *  Override to adjust the perspective matrix.
     * @param gl10 -- unused
     * @param width -- Screen width
     * @param height - Screen height
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height)
    {
        //Create the viewport
        GLES20.glViewport(0, 0, width, height);
        _width = width;
        _height = height;

        float ratio = (float)width/height;

        //Initialize and create the perspective matrices
        _projectionMatrix = new float[16];
        Matrix.perspectiveM(_projectionMatrix, 0, 90.0f, ratio, 0.01f, 30.0f);
        _wordsProjectionMatrix = _projectionMatrix.clone(); //Used for drawing text

        //Move things back a bit
        Matrix.translateM(_projectionMatrix, 0, 0.0f, 0.0f, -3.0f);
        Matrix.translateM(_wordsProjectionMatrix, 0, 0.0f, 0.0f, -1.0f);
    }

    /**
     *  Handles multiple touches
     * @param event - the touch event
     * @return - true; we got this
     */
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int pIndex = event.getActionIndex();
        int pId = event.getPointerId(pIndex);
        int maskedAction = event.getActionMasked();

        //We only really care about actions down and actions up;
        // if down, store the pointer. if up, remove it.
        switch(maskedAction)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                PointF p = new PointF();
                p.x = event.getX(pIndex);
                p.y = event.getY(pIndex);
                _activePointers.put(pId, p);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
               _activePointers.remove(pId);
        }

        return true; //return true to tell Android the event was handled.
    }

    /**
     *  Helper method used to handle touch events when necessary
     */
    private void handleButtonPress()
    {
        //Button press will be determined by gamestate
        switch (_gameState)
        {
            case START:
                break;
            case PLAYING:
                boolean leftPressed = false; //Three different possible inputs during game
                boolean rightPressed = false;
                boolean jumpPressed = false;

                //Reset these at start of getting inputs
                _sideAcceleration = 0.0f;
                _stabilize = true;

                int removalKey = -1; //Initial value; flags key to be removed from pointers
                for (Integer key : _activePointers.keySet()) //Iterate through all inputs
                {
                    PointF p = _activePointers.get(key);
                    if (p == null)
                        continue;   //If pointer is no longer there, move on

                    float percentX = (p.x * 100.0f) / _width;
                    float percentY = (p.y * 100.0f) / _height;

                    //Pressing right or left will cancel the other direction
                    if (percentX < 20) {
                        leftPressed = true;
                        rightPressed = false;
                    } else if (percentX > 80) {
                        rightPressed = true;
                        leftPressed = false;
                    } else if (percentY > 50) {
                        jumpPressed = true;
                        removalKey = key;
                    }
                }

                if (removalKey != -1) //If changed, then we remove this key
                    _activePointers.remove(removalKey);

                //Handle different inputs
                if (leftPressed) { //Turn Left
                    _sideAcceleration = -TURN_POWER;
                    _stabilize = false;
                }

                if (rightPressed) { //Turn Right
                    _sideAcceleration = TURN_POWER;
                    _stabilize = false;
                }

                if (jumpPressed) {  //Jump
                    if (_onGround) {
                        _verticalVelocity = JUMP_POWER;
                        _onGround = false;
                    }
                }
                break;
            case WIN:
            case LOSE:
                leftPressed = false;
                rightPressed = false;
                for (Integer key : _activePointers.keySet())
                {
                    PointF p = _activePointers.get(key);
                    float percentX = (p.x * 100.0f) / _width;
                    //Pressing right or left will cancel the other direction
                    if (percentX < 50) {
                        leftPressed = true;
                    } else {
                        rightPressed = true;
                    }
                }
                if (leftPressed)    //Again
                    restartTrack();
                else if (rightPressed)  //Back
                    finish();
                break;
        }

    }

    /**
     *  Helper method to calculate how far ship has moved in time between frame draws.
     *  Based on global velocity and acceleration values
     */
    private void calculateMovement()
    {
        //Calculate factor based on time elapsed since last call of this method
        //This keeps ship moving at a steady rate, regardless of rendering or processing time
        float factor  = updateTime() / 1000.0f;

        //First, calculate velocity(s)
        _velocity += (_acceleration * factor);
        if (_velocity > MAX_VELOCITY) _velocity = MAX_VELOCITY;

        //Calculate sideways movement
        //  If turning the opposite direction of movement, or if turning stopped,
        //  then the ship is "stabilizing", meaning it needs to stop moving quickly
        //  to make turning more responsive.
        if ((_sideVelocity > 0 && _sideAcceleration < 0) ||
            (_sideVelocity < 0 && _sideAcceleration > 0) ||
            (_stabilize))
        {
            _sideVelocity -= (_sideVelocity / 2);
            if (Math.abs(_sideVelocity) < 0.2)
                _sideVelocity = 0.0f;
        }
        else
            _sideVelocity += (_sideAcceleration * factor); //Otherwise, just move ship horizontally

        //Cap sideways velocity
        if (_sideVelocity > MAX_SIDE_VELOCITY) _sideVelocity = MAX_SIDE_VELOCITY;
        if (_sideVelocity < -MAX_SIDE_VELOCITY) _sideVelocity = -MAX_SIDE_VELOCITY;

        //Move ship down by gravitational constant.
        _verticalVelocity += (GRAVITY * factor);

        //Now we detect collision with objects
        collisionDetection(factor);

        //If low frame rate, position will skip over squares at high velocity
        float tempPosition = _position;
        float tempVelocity = _velocity * factor;
        while (tempVelocity > 1.0f)
        {
            tempVelocity -= 1.0f;
            _position = tempPosition;
            _position += tempVelocity;
            collisionDetection(factor);
        }
        _position = tempPosition;

        //Forward motion
        _position += (_velocity * factor); //Z position of ship... think "where is ship on the track"

        //Horizontal Movement
        _shipPositionX += (_sideVelocity * factor);

        //Vertical movement
        //Falling flag set in collisionDetection method
        _shipPositionY += (_verticalVelocity * factor);
        if (_shipPositionY < 0.0f && !_falling)
        {
            _shipPositionY = 0.0f;
            _verticalVelocity = 0.0f;
            _onGround = true;
        }
        else if (_falling) {
            if (_onGround) {
                _verticalVelocity = -1.0f; //Make falling a little more punishing as well.
                _onGround = false;
            }
            if (_shipPositionY < -3.0f) //If fallen too far, we are dead.
            {
                death();
            }
        }

        //Rotates objects that need to rotate.
        theta += 1.0f;
    }

    /**
     *  Method for collision detection.
     * @param factor - Factor is time elapsed between frames in ms divided by 1000.0f.
     */
    private void collisionDetection(float factor)
    {
        //Calculate necessary offset for x-coordinate
        float coordCorrection = (0.5f * (_shipPositionX) / Math.abs((_shipPositionX)));

        //Get actual x-coordinate
        int realCoordinate = (int)(_shipPositionX + coordCorrection);
        int realLeftCoordinate = (int)(realCoordinate - 1.0f);
        int realRightCoordinate = (int)(realCoordinate + 1.0f);

        //Horizontal ship padding. Used for offsetting horizontal collision to make it feel better.
        float horizontalShipPad = SHIP_WIDTH / 4.0f;
        horizontalShipPad *= Math.abs(_sideVelocity) > 0 ? ((_sideVelocity) / Math.abs((_sideVelocity))) : 0;
        //Calculate potential coordinates, based on horizontal velocity (ie where the ship COULD end up)
        int potentialRealCoordinate = (int)((_shipPositionX + horizontalShipPad + ((_sideVelocity + (_sideAcceleration * factor)) * factor)) + coordCorrection);

        //Calculate where the wings sit and where they will sit based on velocity/acceleration
        int leftWingCoordinate = (int)((_shipPositionX - (SHIP_WIDTH / 3.0f) + coordCorrection));
        float potentialLeftWingCoordinate = (int)((_shipPositionX - (SHIP_WIDTH / 3.0f) + ((_sideVelocity + (_sideAcceleration * factor)) * factor)) + coordCorrection);
        int rightWingCoordinate = (int)((_shipPositionX + (SHIP_WIDTH / 3.0f) + coordCorrection));
        float potentialRightWingCoordinate = (int)((_shipPositionX + (SHIP_WIDTH / 3.0f) + ((_sideVelocity + (_sideAcceleration * factor)) * factor)) + coordCorrection);

        //Position of the ship along the track (ie Z-coordinate)
        int realPosition = (int)(_position + _positionOffset);
        float forwardShipPad = SHIP_LENGTH / 2.0f; //Offset collision of ship's nose
        int potentialRealPosition = (int)((_position + _positionOffset) + forwardShipPad + ((_velocity + (_acceleration * factor)) * factor));

        //Condition for when we need to check the objects that are positioned diagonally in front of the ship.
        boolean checkCorners;
        checkCorners = (potentialRealPosition != realPosition && (
                        potentialRealCoordinate != realCoordinate ||
                        (leftWingCoordinate != realCoordinate ^ rightWingCoordinate != realCoordinate)
                       ));

        //Get current and all adjacent Actors relative to the ship
        GameActor currentUpperSquare = null;
        GameActor forwardUpperSquare = null;
        GameActor leftUpperSquare    = null;
        GameActor rightUpperSquare   = null;
        GameActor currentLowerSquare = null;
        GameActor leftForwardUpperSquare    = null;
        GameActor rightForwardUpperSquare   = null;
        if (realCoordinate >= -2 && realCoordinate <= 2) {
            currentUpperSquare = (realPosition >= _trackSize) ? null : _gameBoard.getBoard().get(realPosition).getUpperLayer()[realCoordinate + 2];
            currentLowerSquare = (realPosition >= _trackSize) ? null : _gameBoard.getBoard().get(realPosition).getLowerLayer()[realCoordinate + 2];
            forwardUpperSquare = (realPosition >= _trackSize-1) ? null : _gameBoard.getBoard().get(realPosition + 1).getUpperLayer()[realCoordinate + 2];
            if (realLeftCoordinate >= -2) {
                leftUpperSquare = (realPosition >= _trackSize) ? null : _gameBoard.getBoard().get(realPosition).getUpperLayer()[realLeftCoordinate + 2];
                leftForwardUpperSquare = (realPosition >= _trackSize-1) ? null : _gameBoard.getBoard().get(realPosition + 1).getUpperLayer()[realLeftCoordinate + 2];
            }
            if (realRightCoordinate <= 2) {
                rightUpperSquare = (realPosition >= _trackSize) ? null : _gameBoard.getBoard().get(realPosition).getUpperLayer()[realRightCoordinate + 2];
                rightForwardUpperSquare = (realPosition >= _trackSize-1) ? null : _gameBoard.getBoard().get(realPosition + 1).getUpperLayer()[realRightCoordinate + 2];
            }
        }

        //Coin case
        if (currentUpperSquare != null && _shipPositionY >= 0.0f)
        {
            GameActor.ActorType type = currentUpperSquare.getType();
            if (type == GameActor.ActorType.coin)
            {
                //TODO: Collect a coin
                playNoise(Noises.COIN);
                _coinsCollected++;
                _gameBoard.getBoard().get((int)(_position + _positionOffset)).getUpperLayer()[realCoordinate+2] = new GameActor(GameActor.ActorType.empty);
            }
        }

        //Barrier cases

        //Block directly in front
        if (forwardUpperSquare != null && _shipPositionY >= -0.1f)
        {
            GameActor.ActorType type = forwardUpperSquare.getType();
            if (type == GameActor.ActorType.barrier
                    && potentialRealPosition == realPosition + 1) //If the ship could potentially be in the block...
            {
                //Then stop moving.
                if (_velocity > 4.0f)
                    playNoise(Noises.BUMP);
                _velocity /= -5.0f;
            }
        }
        //Block directly to left
        if (leftUpperSquare != null && _shipPositionY >= 0.0f)
        {
            GameActor.ActorType type = leftUpperSquare.getType();
            if (type == GameActor.ActorType.barrier
                    && (potentialRealCoordinate == realLeftCoordinate || potentialLeftWingCoordinate == realLeftCoordinate))
            {
                if (_sideVelocity < 0.0f) {
                    _sideVelocity = 0.0f;
                    potentialRealCoordinate = realCoordinate;
                }
            }
        }
        //Block directly to right
        if (rightUpperSquare != null && _shipPositionY >= 0.0f)
        {
            GameActor.ActorType type = rightUpperSquare.getType();
            if (type == GameActor.ActorType.barrier
                    && (potentialRealCoordinate == realRightCoordinate || potentialRightWingCoordinate == realRightCoordinate))
            {
                if (_sideVelocity > 0.0f) {
                    _sideVelocity = 0.0f;
                    potentialRealCoordinate = realCoordinate;
                }
            }
        }

        //Corner cases; blocks diagonally in front
        if (checkCorners)
        {
            //Diagonal left
            if (leftForwardUpperSquare != null && _shipPositionY >= 0.0f)
            {
                GameActor.ActorType type = leftForwardUpperSquare.getType();
                if (type == GameActor.ActorType.barrier && potentialRealPosition == realPosition + 1)
                {
                    if (potentialLeftWingCoordinate == realLeftCoordinate) {
                        if (_sideVelocity < 0.0f) {
                            _sideVelocity = 0.0f;
                        }
                        if (potentialRealCoordinate == realLeftCoordinate || leftWingCoordinate == realLeftCoordinate)
                        {
                            //Collide with the block
                            if (_velocity > 4.0f)
                                playNoise(Noises.BUMP);
                            _velocity /= -5.0f;
                        }
                    }
                }
            }
            //Diagonal right
            if (rightForwardUpperSquare != null && _shipPositionY >= 0.0f)
            {
                GameActor.ActorType type = rightForwardUpperSquare.getType();
                if (type == GameActor.ActorType.barrier && potentialRealPosition == realPosition + 1)
                {
                    if (potentialRightWingCoordinate == realRightCoordinate) {
                        if (_sideVelocity > 0.0f) {
                            _sideVelocity = 0.0f;
                        }
                        if (potentialRealCoordinate == realRightCoordinate || rightWingCoordinate == realRightCoordinate)
                        {
                            //Collide with the block
                            if (_velocity > 4.0f)
                                playNoise(Noises.BUMP);
                            _velocity /= -5.0f;
                        }
                    }
                }
            }
        }

        //Hole case
        if (currentLowerSquare != null)
        {
            GameActor.ActorType type = currentLowerSquare.getType();
            if (type == GameActor.ActorType.floor && _shipPositionY >= -0.2f) //Check ship Y to give a little leeway to the player
            {
                _falling = false;
            }
            else if (type == GameActor.ActorType.empty || _shipPositionY < -0.2f) {
                _falling = true;
            }
        }
        else
            _falling = true;
    }

    /**
     *  Method called when the player dies.
     */
    private void death()
    {
        _gameState = GameState.LOSE;
        clearButtonPresses();
    }

    /**
     *  Used to re-initialize track; for when game is restarted.
     */
    private void restartTrack()
    {
        initializeBoard();
        _coinsCollected = 0;
        _position = 0.0f;
        _velocity = 0.0f;
        _verticalVelocity = 0.0f;
        _shipPositionX = 0.0f;
        _shipPositionY = 0.0f;
        _sideVelocity = 0.0f;
        _falling = false;
        _onGround = true;
        _countDown = 3000;
        _timeStarted = System.currentTimeMillis();
        _time = _timeStarted;
        _gameState = GameState.START;
        playNoise(Noises.COUNTDOWN);
    }

    /**
     *  Updates the time tracker based on the System.currentTimeMillis() call
     * @return number of milliseconds that have elapsed since last call.
     */
    private float updateTime()
    {
        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - _time;
        _time = currentTime;
        _gameTime = _time - _timeStarted;

        if (_gameState == GameState.START)
            _timeStarted = currentTime;

        return timeElapsed;
    }

    /**
     *  Simple method to clear all touch inputs.
     */
    private void clearButtonPresses()
    {
        _activePointers.clear();
    }

    /**
     *   Contains the game loop; needs to be made way more efficient.
     * @param gl10 -- unused
     */
    @Override
    public void onDrawFrame(GL10 gl10)
    {
        handleButtonPress(); // Handle touch inputs

        //Main game loop
        switch (_gameState)
        {
            case START:
                _countDown -= updateTime(); //Update the countdown
                if (_countDown <= 0)
                {
                    GameRecord record = GameData.getGameRecord(_gameBoard.boardName);
                    if (record != null)
                    {
                        record.incrementNumTimesPlayed(); //Increment each time game is started
                    }
                    _gameState = GameState.PLAYING;
                }
                break;
            case PLAYING:
                if (_position + _positionOffset > _trackSize) { //Reach the end of the track
                    _gameState = GameState.WIN;
                    clearButtonPresses();
                }
                else
                    calculateMovement();
                break;
            case WIN:
                GameRecord record = GameData.getGameRecord(_gameBoard.boardName);
                if (record != null)
                {
                    //Check for high scores
                    if (record.getHighCoins() < _coinsCollected)
                        record.setHighCoins(_coinsCollected);
                    if (record.getBestTime() > _gameTime)
                        record.setBestTime(_gameTime);
                }
                break;
            case LOSE:
                break;
        }

        //Set program and specific flags
        GLES20.glUseProgram(_program);
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Matrix.setLookAtM(_modelViewMatrix, 0, eye[0], eye[1], eye[2], at[0], at[1], at[2], 0.0f, 1.0f, 0.0f);
        GLES20.glUniformMatrix4fv(_viewLoc, 1, false, _modelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(_projLoc, 1, false, _projectionMatrix, 0);

        float[] lightPosition =
        {
                -_shipPositionX, -_shipPositionY + 2.0f, (_position % 10) - 20.0f, 1.0f,
                -_shipPositionX, -_shipPositionY + 2.0f, (_position % 10) - 10.0f, 1.0f,
                -_shipPositionX, -_shipPositionY + 2.0f, (_position % 10)        , 1.0f,
                -_shipPositionX, -_shipPositionY + 2.0f, (_position % 10) + 10.0f, 1.0f,
        };
        GLES20.glUniform4fv(_lightPosLoc, 4, lightPosition, 0);

        float[] bodyModel = _modelViewMatrix.clone();
        Matrix.translateM(_modelViewMatrix, 0, -_shipPositionX, -_shipPositionY, 0.0f);

        float[] shipAmbient = {0.1f, 0.1f, 0.3f, 1.0f};
        float[] shipDiffuse = {0.2f, 0.3f, 0.5f, 1.0f};
        float[] shipSpecular = {0.6f, 0.6f, 0.6f, 1.0f};

        //Move and draw ship
        Matrix.translateM(bodyModel, 0, 0.0f, 0.0f, 4.0f);
        Matrix.rotateM(bodyModel, 0, (3.0f * -_sideVelocity), 0.0f, 0.0f, 1.0f);
        Matrix.rotateM(bodyModel, 0, (2.0f * _verticalVelocity), 1.0f, 0.0f, 0.0f);
        //Body of ship
        float[] instanceMatrix = bodyModel.clone();
        Matrix.translateM(instanceMatrix, 0, 0.0f, -0.05f, 0.3f);
        Matrix.scaleM(instanceMatrix, 0, 0.15f, 0.05f, 0.35f);
        Node shape = new Node(instanceMatrix);
        shape.setColor(shipAmbient, shipDiffuse, shipSpecular);
        shape.setPoints(GeometryBuilder.getCube());
        shape.setShine(2.0f);
        _shipNodes[0] = shape;
        //Right Wing
        instanceMatrix = bodyModel.clone();
        Matrix.translateM(instanceMatrix, 0, 0.25f, 0.0f, 0.0f);
        Matrix.scaleM(instanceMatrix, 0, 0.35f, 0.15f, 1.0f);
        shape = new Node(instanceMatrix);
        shape.setColor(shipAmbient, shipDiffuse, shipSpecular);
        shape.setPoints(GeometryBuilder.getRightTetrahedron());
        shape.setShine(2.0f);
        _shipNodes[1] = shape;
        //Left Wing
        instanceMatrix = bodyModel.clone();
        Matrix.translateM(instanceMatrix, 0, -0.25f, 0.0f, 0.0f);
        Matrix.scaleM(instanceMatrix, 0, 0.35f, 0.15f, 1.0f);
        Matrix.rotateM(instanceMatrix, 0, 90, 0.0f, 0.0f, 1.0f);
        shape = new Node(instanceMatrix);
        shape.setColor(shipAmbient, shipDiffuse, shipSpecular);
        shape.setPoints(GeometryBuilder.getRightTetrahedron());
        shape.setShine(2.0f);
        _shipNodes[2] = shape;

        traversal(_position); //Traversal method renders all objects on screen

        //Render the ship last
        for (int i = 0; i < 3; i++)
            _shipNodes[i].render();

        //Now render the text (for score and time)
        GLES20.glUseProgram(_wordsProgram);
        drawText(_timeStarted, _time);

    }

    /**
     *  Method for drawing text on screen.
     * @param timeStart - time game was started
     * @param currTime  - current in-game time
     */
    private void drawText(long timeStart, long currTime)
    {
        //Calculate specific components of the time.
        int mins = (int)Math.floor((currTime - timeStart) / 60000) % 60;
        int secs = (int)Math.floor((currTime - timeStart) / 1000) % 60;
        int hundredths = (int)Math.floor((currTime - timeStart) / 10) % 100;

        //Convert values to strings, with leading zeroes when necessary
        String sCoins = (_coinsCollected < 100) ? (_coinsCollected < 10) ? "00" + _coinsCollected : "0" + _coinsCollected : ""+_coinsCollected;
        String sMins = (mins < 10) ? "0" + mins : ""+mins;
        String sSecs = (secs < 10) ? "0" + secs : ""+secs;
        String sHuns = (hundredths < 10) ? "0" + hundredths : ""+hundredths;

        //display[] contains all the numeral text at top of screen, based on time components and tetra collected
        NumberGraphic[] display = new NumberGraphic[10];
        display[0] = new NumberGraphic(-0.45f, 0.9f, ':', 0.05f); //Triangle figure
        display[1] = new NumberGraphic(-0.4f, 0.9f, sCoins.charAt(0), 0.05f);
        display[2] = new NumberGraphic(-0.35f, 0.9f, sCoins.charAt(1), 0.05f);
        display[3] = new NumberGraphic(-0.3f, 0.9f, sCoins.charAt(2), 0.05f);

        display[4] = new NumberGraphic(0.1f, 0.9f, sMins.charAt(0), 0.05f); //Minutes
        display[5] = new NumberGraphic(0.15f, 0.9f, sMins.charAt(1), 0.05f);

        display[6] = new NumberGraphic(0.225f, 0.9f, sSecs.charAt(0), 0.05f); // Seconds
        display[7] = new NumberGraphic(0.275f, 0.9f, sSecs.charAt(1), 0.05f);

        display[8] = new NumberGraphic(0.35f, 0.9f, sHuns.charAt(0), 0.05f); //Hundredths
        display[9] = new NumberGraphic(0.40f, 0.9f, sHuns.charAt(1), 0.05f);

        //Render the numbers
        for (int i = 0; i < 10; i++)
        {
            display[i].render();
        }

        //The text for "TIME:"
        StringGraphic timeGraphic = new StringGraphic(0.0f, 0.9f, 0, 0.05f);
        timeGraphic.render();

        //Render the countdown if game is starting
        if (_gameState == GameState.START)
        {
            NumberGraphic count = new NumberGraphic(0.0f, 0.0f, (char)((_countDown / 1000)+49), 0.25f);
            count.render();
        }

        //Render the GAME OVER screen, plus AGAIN and BACK if lose
        if (_gameState == GameState.LOSE)
        {
            StringGraphic[] loseScreen = new StringGraphic[3];
            loseScreen[0] = new StringGraphic(0.0f, 0.0f, 1, 0.125f);
            loseScreen[1] = new StringGraphic(-0.3f, -0.3f, 3, 0.05f);
            loseScreen[2] = new StringGraphic(0.3f, -0.3f, 4, 0.05f);

            for (int i=0; i<3; i++)
                loseScreen[i].render();
        }

        //Render the COMPLETE screen, plus AGAIN and BACK if win
        if (_gameState == GameState.WIN)
        {
            StringGraphic[] winScreen = new StringGraphic[3];
            winScreen[0] = new StringGraphic(0.0f, 0.0f, 2, 0.125f);
            winScreen[1] = new StringGraphic(-0.3f, -0.3f, 3, 0.05f);
            winScreen[2] = new StringGraphic(0.3f, -0.3f, 4, 0.05f);

            for (int i=0; i<3; i++)
                winScreen[i].render();
        }
    }

    /**
     *  Get the normal matrix of a given matrix
     * @param mvm   - the input matrix
     * @return      - a normal matrix of the input matrix
     */
    public static float[] getNormalMatrix(float[] mvm)
    {
        float[] normalMatrix = new float[mvm.length];
        Matrix.setIdentityM(normalMatrix, 0);
        Matrix.invertM(normalMatrix, 0, mvm, 0);
        float[] normalMatrixTransposed = new float[mvm.length];
        Matrix.transposeM(normalMatrixTransposed, 0, normalMatrix, 0);
        return normalMatrixTransposed;
    }

    /**
     *  Method for initializing the game board
     */
    private void initializeBoard()
    {
        _gameBoard = new GameBoard(GameData.getGameBoard(_gameName));
        _trackSize = _gameBoard.getSize();
    }

    /**
     *  Recursive method; will call itself MAX_NODES_LOADED times
     *  The track in-game is organized into rows (ActorGroups); this method renders one ActorGroup.
     *  After it renders an ActorGroup, the method moves up the track one row and calls itself.
     *
     *  Traverse through each object on screen and render it using a model matrix
     *      (Not the most efficient way to do things; very quick and dirty.)
     * @param position - where the player is at on the game board
     */
    private void traversal(float position)
    {
        int pos = (int)position; //Get truncated value of position
        if (pos > _gameBoard.getSize() - 1 || pos - _position > MAX_NODES_LOADED)
            return; //Return if position exceeds board length or max number of nodes

        //First render the upper layer of objects
        for (int i = 0; i < GameBoard.BOARD_WIDTH; i++)
        {
            GameActor actor = _gameBoard.getBoard().get(pos).getUpperLayer()[i];

            if (actor.getType() == GameActor.ActorType.empty)
                continue;

            float[] instanceMatrix;
            //Traversal
            instanceMatrix = _modelViewMatrix.clone();
            Matrix.scaleM(instanceMatrix, 0, 1.0f, 1.0f, 1.0f);
            Matrix.translateM(instanceMatrix, 0, (-2.0f + i), 0.0f, (6.0f - pos + _position));

            if (actor.getType() == GameActor.ActorType.coin)
            {
                Matrix.rotateM(instanceMatrix, 0, theta*8.0f, 0.0f, 1.0f, 0.0f);
                Matrix.rotateM(instanceMatrix, 0, theta*2.0f, 1.0f, 0.0f, 0.0f);
                Matrix.scaleM(instanceMatrix, 0, 0.5f, 0.5f, 0.5f);
            }

            Node shape = new Node(instanceMatrix);
            shape.setPoints(actor.getPoints());
            shape.setColor(actor.ambient, actor.diffuse, actor.specular);
            shape.setShine(actor.shine);
            shape.render();
        }

        //Then the lower layer of objects
        for (int i = 0; i < GameBoard.BOARD_WIDTH; i++)
        {
            GameActor actor = _gameBoard.getBoard().get(pos).getLowerLayer()[i];

            if (actor.getType() == GameActor.ActorType.empty)
                continue;

            float[] instanceMatrix;
            //Traversal
            instanceMatrix = _modelViewMatrix.clone();
            Matrix.scaleM(instanceMatrix, 0, 1.0f, 1.0f, 1.0f);
            Matrix.translateM(instanceMatrix, 0, (-2.0f + i), -0.5f, (6.0f - pos + _position));

            if (actor.getType() == GameActor.ActorType.pit)
            {
                Matrix.scaleM(instanceMatrix, 0, 1.0f, 1.0f, 1.0f);
                Matrix.translateM(instanceMatrix, 0, 0.0f, -1.0f, 0.0f);
            }

            Node shape = new Node(instanceMatrix);
            shape.setPoints(actor.getPoints());
            shape.setColor(actor.ambient, actor.diffuse, actor.specular);
            shape.setShine(actor.shine);
            shape.render();
        }

        //Render the next row of objects up the track
        position += 1.0f;
        traversal(position);
    }

    private enum Noises { BUMP, COUNTDOWN, COIN }

    /**
     *  Used to play a loaded sound. Sounds loaded in the onCreate method.
     * @param type  - Noises enum to designate the sound played
     */
    private void playNoise(Noises type)
    {
        if (noSound)
            return;

        switch (type)
        {
            case BUMP:
                soundPool.play(soundIds[0], 1, 1, 1, 0, 1.0f);
                break;
            case COIN:
                soundPool.play(soundIds[1], 1, 1, 1, 0, 1.0f);
                break;
            case COUNTDOWN:
                soundPool.play(soundIds[2], 1, 1, 1, 0, 1.0f);
                break;
        }
    }
}
