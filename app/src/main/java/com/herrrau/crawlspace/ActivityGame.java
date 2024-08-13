package com.herrrau.crawlspace;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

public class ActivityGame extends AppCompatActivity implements GameView {
    LocalAdapter localController; //mal auf Typ LocalController aendern
    private ArrayList<ImageView> tilesList;
    private ImageView[] imageViewsCharacters;
    private ArrayList<ImageView> imageViewsObjects; //#################
    private ArrayList<Integer> imageViewsObjectsPositions; //#################
    private int tileWidth, tileHeight, characterSize, xOffset, yOffset;
    int colorScreenBackground = Color.BLACK;
    //int colorScreenBackground = Color.rgb(239, 234, 143);;
    int colorLayoutTiles = Color.BLACK; //unused
    int colorLayoutCharacter = Color.TRANSPARENT; //unused
    int colorLayoutObject = Color.TRANSPARENT;
    int colorTextGame = Color.WHITE;
    int colorTextPopup = Color.WHITE;
    int colorBackgroundPopup = Color.BLACK;
    //int colorHighlight = Color.rgb(77,221,85);
    int colorHighlight = Color.rgb(32, 123, 37);
    int currentDepth = 0;
    private int columns, rows, numberOfPlayers;
    ConstraintLayout popupIncluded;
    ConstraintLayout layoutTiles;
    //LinearLayout layoutMessages;
    LinearLayout layoutObject, layoutCharacter;
    TextView textViewTimer, textViewBottom;
    private GestureDetectorCompat gDetectorGame, gDetectorPopup, gDetectorGameActivity;

    boolean modifyColors = true;
    boolean useLimitedVision = false;
    boolean useAnimation = true;
    FactoryTile tileFactory;
    FactoryObject objectFactory;
    private final boolean verbose = false;
    final Handler handler = new Handler();
    Timer timer;
    private static final String DEBUG_TAG = "XXX";
    public static ActivityGame selfreferenceActivityGame;
    public static boolean useBackgroundColor = true;
    public static boolean useFittedBackgroundImage = false;
    protected LinearLayout layoutItems, layoutActions, layoutActionsFight;
    protected TextView textViewCharacter, textViewObject, textViewMessage, textViewFootnote;
    protected ImageView imageViewCharacter, imageViewObject;
    protected Button buttonReturn, buttonExit, buttonAction0, buttonAction1, buttonAction2, buttonAction3, buttonFight0, buttonFight1;
    boolean excursionHasStarted = false;
    int excursionMaxDepth = 0;

    private void print(String s) {
        if (verbose) {
            Log.d(DEBUG_TAG, s);
            //System.out.println(s);
            showFootnote(s);
        }
    }
    // #######################################################################
    // Methods begin
    // #######################################################################

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selfreferenceActivityGame = this;
        System.out.println("AG ON CREATE");
        //initialise attributes
        setContentView(R.layout.activity_game);

        //########################
        popupIncluded = findViewById(R.id.popupIncluded);
        popupIncluded.setVisibility(View.GONE);
        //########################

        textViewBottom = findViewById(R.id.textViewBottom);
        textViewBottom.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewBottom.setTextColor(colorTextGame);
        if (modifyColors) textViewBottom.setBackgroundColor(colorScreenBackground);

        LinearLayout layoutHorizontal = findViewById(R.id.layoutHorizontal);
        if (modifyColors) layoutHorizontal.setBackgroundColor(colorScreenBackground);
        layoutTiles = findViewById(R.id.layoutConstraintTiles);
        textViewTimer = findViewById(R.id.textViewTimer);
        if (modifyColors) textViewTimer.setBackgroundColor(colorScreenBackground);
        if (modifyColors)
            findViewById(R.id.layoutConstraintMaster).setBackgroundColor(Color.TRANSPARENT);
        textViewTimer.setTextColor(colorTextGame);
        //removes keyboard (necessary, but not always sufficient)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //set up GestureDetector
        OnSwipeListener listenerGame = new OnSwipeListenerGame(this);
        gDetectorGame = new GestureDetectorCompat(this, listenerGame);
        gDetectorGame.setOnDoubleTapListener(listenerGame);

        OnSwipeListener listenerPopup = new OnSwipeListenerPopup(this);
        gDetectorPopup = new GestureDetectorCompat(this, listenerPopup);
        gDetectorPopup.setOnDoubleTapListener(listenerPopup);

        OnSwipeListener listenerGameActivity = new OnSwipeListenerUNUSED(this);
        gDetectorGameActivity = new GestureDetectorCompat(this, listenerGameActivity);
        gDetectorGameActivity.setOnDoubleTapListener(listenerGameActivity);


        //prepare factories
        tileFactory = new FactoryTile();
        objectFactory = new FactoryObject();
        //decide on server type
        if (Setup.useMockServerForOnePlayer) {
            localController = new LocalAdapterMock();
        } else {
            localController = new LocalAdapterRemote();
        }
        localController.setView(this);

        // #####################################################################
        //Game starts, potentially
        localController.registerWithServer(Setup.serverIP, Setup.serverPortNumber);
        // #####################################################################


        displayFootnote(0, "on create ");
    }


    // #####################################################################

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("AG START");
        displayFootnote(0, "on start");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("AG RESUME");
        displayFootnote(0, "on resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("AG PAUSE");
        displayFootnote(0, "on pause");
    }

    public void onStop() {
        super.onStop();
        System.out.println("AG STOP");
        displayFootnote(0, "on create");
    }

    public void onRestart() {
        super.onRestart();
        System.out.println("AG RESTART");
        displayFootnote(0, "on restart");
    }

    public void onDestroy() {
        super.onDestroy();
        displayFootnote(0, "on destroy");
        System.out.println("AG DESTROY");
    }

    public void onBackPressed() {
        //
        askAboutLeaving();
    }


    // #######################################################################
    // Setup methods
    // #######################################################################


    public void setupLevel(int columns, int rows, int numberOfPlayers, int depth, boolean useLimitedVision) {
        this.useLimitedVision = useLimitedVision;
        this.currentDepth = depth;
        if (timer != null) timer.cancel();
        runOnUiThread(new Runnable() {
            public void run() {
                setupUserInterface(columns, rows, numberOfPlayers);
                showFootnote("");
            }
        });
        if (depth > 0) excursionHasStarted = true;
        if (depth > excursionMaxDepth) excursionMaxDepth = depth;
        if (Setup.activeCharacter.maxDepth < excursionMaxDepth)
            Setup.activeCharacter.maxDepth = excursionMaxDepth;
    }

    private void setupUserInterface(int cols, int rows, int numberOfPlayers) {
        print("AG sets up UI elements");
        //when level changes, everything is reset
        layoutTiles.removeAllViews();
        //layoutTiles.setVisibility(View.INVISIBLE);

        //set dimensions
        this.columns = cols;
        this.rows = rows;
        this.numberOfPlayers = numberOfPlayers;

        //setup background color or image
        //getWindow().setBackgroundDrawableResource(R.drawable.hintergrund);
        if (modifyColors) getWindow().getDecorView().setBackgroundColor(colorScreenBackground);
        if (modifyColors) layoutTiles.setBackgroundColor(colorLayoutTiles);

        //calculate screen size
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;

        //calculate height of playing area
        int bottomHeight = 200;
        int heightTilesArea = screenHeight - bottomHeight;
        int widthTilesArea;

        // assign width
        widthTilesArea = screenWidth;

        //setup tile dimensions
        tileWidth = widthTilesArea / columns;
        tileHeight = widthTilesArea / columns;

        //if necessary, adjust
        if (heightTilesArea / rows < tileHeight) {
            tileHeight = (heightTilesArea / rows) - 4 + 4; // UGLY
            tileWidth = tileHeight;
        }

        //center layout Tiles //#################
        layoutTiles.setX((screenWidth - tileWidth * columns) / 2);

        //place message layout
        findViewById(R.id.layoutHorizontal).setY(rows * tileHeight + 1);

        //calculate character size
        characterSize = (int) (tileWidth / 1.75);
        xOffset = tileWidth / 2 - characterSize / 2;
        yOffset = tileHeight / 2 - characterSize / 2;

        //initialise list for tiles
        tilesList = new ArrayList<ImageView>(columns * rows);

        //create and place actual tile views
        for (int i = 0; i < columns * rows; i++) {
            ImageView imgViewTile = new ImageView(this);
            layoutTiles.addView(imgViewTile, new ConstraintLayout.LayoutParams(tileWidth, tileHeight));
            //set standard background
            int id = -1;
            if (useLimitedVision) {
                id = tileFactory.getID("unknown_seethrough");
            } else {
                if (false) id = tileFactory.getID("unknown");
                else id = tileFactory.getID("unknown_seethrough");
            }
            imgViewTile.setBackgroundResource(id);
            imgViewTile.setX(i % columns * tileWidth);
            imgViewTile.setY(i / columns * tileHeight);
            int abweichung = 7 - 7;
            imgViewTile.setRotation((float) (Math.random() * abweichung - abweichung / 2) * 2);
            final int j = i;
            tilesList.add(imgViewTile);
        }

        //setup character array
        imageViewsCharacters = new ImageView[numberOfPlayers];
        print("AG sets up characters, " + numberOfPlayers + " in number.");
        //create and position characters
        for (int i = 0; i < imageViewsCharacters.length; i++) {
            imageViewsCharacters[i] = new ImageView(this);
            layoutTiles.addView(imageViewsCharacters[i], new ConstraintLayout.LayoutParams(characterSize, characterSize));
            imageViewsCharacters[i].setX(0); //irrelevant
            imageViewsCharacters[i].setY(0); //irrelevant
            if (i == 0) {
                //imageViewsCharacters[i].setBackgroundColor(Color.GREEN);
                //imageViewsCharacters[i].getBackground().setAlpha(0);
                imageViewsCharacters[i].setBackgroundResource(objectFactory.getID("warrior_white"));
            } else if (i == 1) {
                //imageViewsCharacters[i].setBackgroundColor(Color.RED);
                imageViewsCharacters[i].setBackgroundResource(objectFactory.getID("warrior_red"));
            } else if (i == 2) {
                //imageViewsCharacters[i].setBackgroundColor(Color.BLUE);
                imageViewsCharacters[i].setBackgroundResource(objectFactory.getID("warrior_blue"));
            } else if (i == 3) {
                //imageViewsCharacters[i].setBackgroundColor(Color.YELLOW);
                imageViewsCharacters[i].setBackgroundResource(objectFactory.getID("warrior_yellow"));
            }
        }

        //remove any objects that might be left from last level
        removeAllObjects();

        //create list for objects in this level
        imageViewsObjects = new ArrayList<ImageView>();
        imageViewsObjectsPositions = new ArrayList<Integer>();

        //prepare popups
        setupPopup();

        //reset text views
        textViewTimer.setText(getResources().getString(R.string.footnote_depth) + ": " + currentDepth);
        showFootnote("");

        //set background for tiles
        ImageView background = findViewById(R.id.imageViewBackground);
        //##############################################
        if (useBackgroundColor) background.setBackgroundColor(colorScreenBackground);
        else if (useFittedBackgroundImage)
            background.setBackgroundResource(R.drawable.hintergrund2); //passt ein
        else background.setImageResource(R.drawable.hintergrund2); //passt nicht ein
        //######################################
        android.view.ViewGroup.LayoutParams layoutParams = background.getLayoutParams();
        layoutParams.width = tileWidth * columns;//layoutTiles.getWidth();
        layoutParams.height = rows * tileHeight;
        background.setLayoutParams(layoutParams);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);

        background.setX((screenWidth - tileWidth * columns) / 2);

        layoutTiles.setBackgroundColor(Color.TRANSPARENT);
        //layoutTiles.setBackgroundResource((R.drawable.hintergrund));

        //
        layoutTiles.setMaxHeight(tileHeight * rows);
    }


    // #######################################################################
    // Show stuff - messages from controller, except for popups
    // #######################################################################

    public void displayFootnote(int playerID, String s) {
        showFootnote(s);
    }


    private void showFootnote(String message) {
        if (!Setup.debug) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Snackbar snack = Snackbar.make(layoutTiles, message, Snackbar.LENGTH_SHORT);
                // snack.setDuration(1500);
                // snack.show();
                textViewBottom.setText(message);

                //spaeter null herausnehmen und dafuer sorgen, dass
                //textViewFootnote ganz am Anfang initialisiert wird
                if (textViewFootnote != null) textViewFootnote.setText(message);

                //System.out.println("GA Footnote: "+message);
            }
        });
    }

    public void showTile(int position, String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                print("AG Trying to show tile " + s + " @" + position);
                //in case of old protocol (turm instead of t)
                if (Setup.useOldProtocolForServer) {
                    showFootnote(s);
                }
                //discouraged, still...
                int id = tileFactory.getID(s);
                tilesList.get(position).setBackgroundResource(id);

                //tilesList.get(position).setBackgroundColor(getResources().getColor(android.R.color.transparent));
                //make buttons seethrough/invisible
                //imgButton.getBackground().setAlpha(0);
                //imgViewTile.getDrawable().setAlpha(0);

                boolean useFilterToOverrideAlpha = false;
                if (useFilterToOverrideAlpha) {
                    // color filter (instead of tint)
                    int c = 255 - currentDepth * 10;
                    if (c < 30) c = 60;
                    @ColorInt int color = Color.rgb(c, c, c);
                    tilesList.get(position).getBackground().setColorFilter(color, PorterDuff.Mode.DARKEN);
                }

                boolean useRandomFilter = false;
                if (useRandomFilter) {
                    // color filter (instead of tint)
                    int random = (int) (Math.random()*10);
                    int c = 255 - random * 10;
                    @ColorInt int color = Color.rgb(c, c, c);
                    tilesList.get(position).getBackground().setColorFilter(color, PorterDuff.Mode.DARKEN);
                }

            }
        });
    }

    public void showCharacter(int id, int position, int comesFrom) {
        print("AG Character " + id + " moves.");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //if (Math.random() < 0.25) showSound(0, "step");
                if (useLimitedVision) makeOnlyNeighboursVisible(position);
                print("AG Character " + id + " placed at " + position);
                //not really necessary
                int xOffsetExtra = 0;
                int yOffsetExtra = 0;
                if (id == 0 || id == 3) {
                    xOffsetExtra -= tileWidth / 9;
                } else {
                    xOffsetExtra += tileWidth / 9;
                }
                if (id == 0 || id == 1) {
                    yOffsetExtra -= tileHeight / 9;
                } else {
                    yOffsetExtra += tileHeight / 9;
                }
                //end
                //apply offsets
                float xNew = tilesList.get(position).getX() + xOffset + xOffsetExtra;
                float yNew = tilesList.get(position).getY() + yOffset + yOffsetExtra;

                if (useAnimation) {
                    imageViewsCharacters[id].animate().x(xNew).y(yNew).setDuration(100);
                } else {
                    imageViewsCharacters[id].setX(xNew);
                    imageViewsCharacters[id].setY(yNew);

                }
                //bring moving character to front (over a chest, say)
                imageViewsCharacters[id].bringToFront();
                //bring own character to front, in case another character moves on top of it
                if (id != localController.getPlayerID()) {
                    imageViewsCharacters[localController.getPlayerID()].bringToFront();
                    //WONT WORK YET
                    if (useLimitedVision) {
                        if (!areNeighbours(imageViewsCharacters[localController.getPlayerID()], imageViewsCharacters[id])) {
                            imageViewsCharacters[localController.getPlayerID()].setVisibility(View.INVISIBLE);
                        } else {
                            imageViewsCharacters[localController.getPlayerID()].setVisibility(View.VISIBLE);
                        }
                    }
                }

            }
        });
    }

    public void removeCharacter(int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("AG trying to make " + id + " GONE");
                //localController.tryMessage("AG trying to make "+id+" GONE");
                imageViewsCharacters[id].setVisibility(View.GONE);
            }
        });
    }

    public void removeObject(int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //
                removeObjectSAFELY(position); // safely, because of concurrency??
                // no longer necessary, I think
            }
        });
    }

    //private synchronized void removeObjectSAFELY(int position) {
    private void removeObjectSAFELY(int position) {
        //
        for (int i = 0; i < imageViewsObjects.size(); i++) {
            System.out.println("AG object " + i + "/" + imageViewsObjects.size() + " tried:");
            if (imageViewsObjectsPositions.get(i) == position) {
                layoutTiles.removeView(imageViewsObjects.get(i)); //delete image
                imageViewsObjectsPositions.remove(i); //remove from list
                imageViewsObjects.remove(i);
                System.out.println("AG Object " + i + " removed at " + position);
                System.out.println(" imageViewsObject left: " + imageViewsObjects.size());
                return;
            } else {
                System.out.println("AG object " + i + " NOT removed (is not at " + position + ")");
                System.out.println(" imageViewsObject left: " + imageViewsObjects.size());
            }
        }
    }

    private void removeAllObjects() {
        //private synchronized void removeAllObjects() { // no longer necessary?
        if (imageViewsObjectsPositions != null) { //happens in first run?
            for (int i = 0; i < imageViewsObjects.size(); i++) {
                layoutTiles.removeView(imageViewsObjects.get(i)); //delete image
                imageViewsObjectsPositions.remove(i); //remove from list
                imageViewsObjects.remove(i);
            }
        }
    }

    public void displayLocalTimer(int playerID, int seconds) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (timer != null) timer.cancel();
                timer = new Timer(false);
                startLocalTimer(seconds);
            }
        });
    }

    private void startLocalTimer(int seconds) {
        TimerTask timerTask = new TimerTask() {
            int countdown = seconds;

            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        countdown--;
                        //textViewTimer.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                        textViewTimer.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        textViewTimer.setText(getResources().getString(R.string.footnote_timer) + ": " + countdown + "\n" + getResources().getString(R.string.footnote_depth) + ": " + currentDepth);
                        if (countdown == 0) {
                            timer.cancel();
                        }
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(timerTask, 100, 1000); // every 1 seconds.
    }

    //makes only sense for GameView as CentralAdapter
    public void showObjectTo(int playerID, int position, PlaceableType type) {
        showObject(position, type);
    }

    public void showObject(int position, PlaceableType type) {
        ActivityGame temp = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //remove any previous object? or leave that to Model?
                //System.out.println("AG shows object " + type + " at " + position + " (total before: " + imageViewsObjects.size() + ")");
                ImageView view = new ImageView(temp);
                //translate type to image name
                //not very efficiemt, discouraged; still...
                //int id = objectFactory.getID(type.toString().toLowerCase());
                int id = objectFactory.getID(type.getFileName());
                if (id < 0) System.out.println("AG: image does not exist");
                //view.setBackgroundResource(R.drawable.chest);
                view.setBackgroundResource(id);
                layoutTiles.addView(view, new ConstraintLayout.LayoutParams(characterSize, characterSize));
                view.setX(position % columns * tileWidth + xOffset);
                view.setY(position / columns * tileHeight + yOffset);
                imageViewsObjectsPositions.add(position);
                imageViewsObjects.add(view);
                int playerID = 0;
                // play character OF THIS PLAYER in front of object, others stay in back
                imageViewsCharacters[playerID].bringToFront();

            }
        });
    }

    public void showEffect(int playerID, int position, EffectType type, String argument) {
        if (type == EffectType.EXPLOSION) effectExplosion(position);
    }

    public void showSound(int playerID, String filename) {
        int id = getResources().getIdentifier(filename, "raw", Setup.packageName);
        MediaPlayer mp = MediaPlayer.create(this, id);
        mp.start();
    }

    // #######################################################################
    // Effects
    // #######################################################################

    private void effectExplosion(int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < tilesList.size(); i++) {
                    if (abstand(i, position) < 2) {
                        int abweichung = 7;
                        tilesList.get(i).setRotation((float) (2 * (Math.random() * abweichung - abweichung / 2)));
                    }
                }
            }
        }
        );
    }

    // #######################################################################
    // Leaving
    // #######################################################################

    void askAboutLeaving() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        doLeaveGame();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (currentDepth == 0) {
            builder.setMessage(getString(R.string.message_leaving)).setPositiveButton(getString(R.string.message_leaving_yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.message_leaving_no), dialogClickListener).show();
        } else {
            builder.setMessage(getString(R.string.message_leaving_dangerously)).setPositiveButton(getString(R.string.message_leaving_yes), dialogClickListener)
                    .setNegativeButton(getString(R.string.message_leaving_no), dialogClickListener).show();
        }
    }

    public void doLeaveGame() {
        if (currentDepth == 0 && excursionHasStarted) {
            //Exkursionen um 1 erhöhen
            Setup.activeCharacter.numberOfSoloExcursions++;
            if (Setup.activeCharacter.maxDepth < excursionMaxDepth)
                Setup.activeCharacter.maxDepth = excursionMaxDepth;
            Setup.saveActiveCharacterAs0();
        }
        System.out.println("ACTION LEAVE GAME IN AG");
        localController.sendToServer("Bye?"); //server may have closed down already
        finish(); // returns to previous activity
    }


    // #######################################################################
    // Helper methods
    // #######################################################################

    private void makeOnlyNeighboursVisible(int position) {
        for (int i = 0; i < tilesList.size(); i++) {
            if (areNeighbours(position, i)) {
                //System.out.println("GA are neighbours: "+position+", "+i+" (of "+tilesList.size()+")");
                tilesList.get(i).setVisibility(View.VISIBLE);
                for (int j = 0; j < imageViewsObjects.size(); j++) {
                    if (imageViewsObjectsPositions.get(j) == position) {
                        imageViewsObjects.get(j).setVisibility(View.VISIBLE);
                    }
                }
            } else {
                tilesList.get(i).setVisibility(View.INVISIBLE);
                for (int j = 0; j < imageViewsObjects.size(); j++) {
                    if (imageViewsObjectsPositions.get(j) == position) {
                        // does not work yet
                        //imageViewsObjects.get(j).setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    private boolean areNeighbours(int position, int potentialNeighbour) {
        if (position == potentialNeighbour) return true;
        if (position + columns == potentialNeighbour) return true;
        if (position - columns == potentialNeighbour) return true;
        if (position % columns != 0 && position - 1 == potentialNeighbour) return true;
        if (position % columns != 0 && position - 1 - columns == potentialNeighbour) return true;
        if (position % columns != 0 && position - 1 + columns == potentialNeighbour) return true;
        if (position % columns != columns - 1 && position + 1 == potentialNeighbour) return true;
        if (position % columns != columns - 1 && position + 1 - columns == potentialNeighbour)
            return true;
        if (position % columns != columns - 1 && position + 1 + columns == potentialNeighbour)
            return true;
        return false;
    }

    private boolean areNeighbours(ImageView v1, ImageView v2) {
        return true;
    }

    private double abstand(int i1, int i2) {
        int x1 = i1 % columns;
        int x2 = i2 % columns;
        int y1 = i1 / columns;
        int y2 = i2 / columns;
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private int getNavigationBarHeight() {
        Resources resources = this.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // #######################################################################
    // Event handling
    // #######################################################################

    //@Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gDetectorGame.onTouchEvent(event);
        //this.gDetectorPopup.onTouchEvent(event);
        //this.gDetectorGameActivity.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    // #######################################################################
    // Popups
    // #######################################################################

    int animationDuration;

/*
    void displayPopupOLD(boolean visible) {
        animationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
        if (visible) {
            if (true) {
                ////    scrollViewPopup.setVisibility(View.VISIBLE);
                return;
            }

            //scrollViewPopup.setVisibility(View.VISIBLE);
            //scrollViewPopup.setAlpha(0.98f);

            //scrollViewPopup.setVisibility(View.GONE);

            //          scrollViewPopup.setAlpha(0f);
            //        scrollViewPopup.setVisibility(View.VISIBLE);
            //scrollViewPopup.animate()
////                    .alpha(1f)
            ////                .setDuration(animationDuration)
            ////    .setListener(null);
        } else {
            if (true) {
                //  scrollViewPopup.setVisibility(View.INVISIBLE);
                return;
            }
//            scrollViewPopup.setVisibility(View.INVISIBLE);
//            scrollViewPopup.setAlpha(1f);
//            scrollViewPopup.animate()
//                    .alpha(0f)
//                    .setDuration(animationDuration)
//                    .setListener(null);
            //scrollViewPopup.setVisibility(View.GONE);
        }
    }
*/




    @Override
    public void displayGame(int playerID) {
        if (state==4) { //new!
            localController.tryStart(); //sends message that level has started
            this.displayFootnote(0,"has started");
        }
        state = 0;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                print("AG: show GAME called...");
                state = 0;
                switchVisibilityAndHighlightFavourite();
            }
        });
    }

    public void displayPlaceableType(int playerID, String message, PlaceableType objectType, String objectStats) {
        runOnUiThread(new Runnable() {
            public void run() {
                state = 6;
                //int id = objectFactory.getID(objectType.toString().toLowerCase());
                //imageViewObject.setImageResource(id);
                textViewMessage.setText(message);
                //textViewObject.setText(objectType.getName() + "\n" + objectStats);
                String[] objectData = objectStats.split("#");
                String objectStatsPrepared = objectData[0];
                for (int i = 1; i < CharacterAttributes.values().length; i++) {
                    if (i == 2) {
                        objectStatsPrepared += "/" + objectData[i];
                    } else {
                        objectStatsPrepared += "\n" + CharacterAttributes.values()[i].getName() + ": " + objectData[i];
                    }
                }
                switchVisibilityAndHighlightFavourite();

                int numberOfItems = ItemType.values().length;
                String resultItemNumber = "";
                String resultAmounts = "";
                for (int i = 0; i < numberOfItems; i++) {
                    int number = Integer.parseInt(objectData[i]);
                    if (number > 0) {
                        resultItemNumber += i + "#";
                        resultAmounts += number + "#";
                    }
                }
                String[] itemsNumber = resultItemNumber.split("#");
                String[] itemsAmount = resultAmounts.split("#");

                ActivityGameItemsAdapter adapterItems = new ActivityGameItemsAdapter(selfreferenceActivityGame, itemsNumber, itemsAmount);
                RecyclerView recyclerView = new RecyclerView(selfreferenceActivityGame);
                recyclerView.setAdapter(adapterItems);
                recyclerView.setLayoutManager(new GridLayoutManager(selfreferenceActivityGame, 4) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                });
                layoutItems.removeAllViews();
                ;
                layoutItems.addView(recyclerView);


            }
        });
    }

    @Override
    public void displayStats(int playerID, String characterStats, PlaceableType objectType, String objectStats) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (objectType == null) {
                    state = 1;
                } else {
                    state = 2;
                    //int id = objectFactory.getID(objectType.toString().toLowerCase());
                    int id = objectFactory.getID(objectType.getFileName());
                    imageViewObject.setImageResource(id);
                    textViewObject.setText(objectType.getName() + "\n" + objectStats);
                }
                addItemsAndActionsToPopup(characterStats.split("#"));
                String[] characterData = characterStats.split("#");
                String characterStatsPrepared = characterData[0];
                print("AG uses character stats: " + characterStats);
                for (int i = 1; i < CharacterAttributes.values().length; i++) {
                    if (i == 2) {
                        characterStatsPrepared += "/" + characterData[i];
                    } else {
                        characterStatsPrepared += "\n" + CharacterAttributes.values()[i].getName() + ": " + characterData[i];
                    }
                }
                textViewCharacter.setText(characterStatsPrepared);
                switchVisibilityAndHighlightFavourite();
            }
        });
    }

    @Override
    public void displayOpponent(int playerID, String characterStats, PlaceableType
            opponentType, String opponentStats) {
        runOnUiThread(new Runnable() {
            public void run() {
                print("AG show OPPONENT called...");
                displayStats(playerID, characterStats, opponentType, opponentStats);
                state = 3; // muesste 30 oder 31 sein koennen...
                String[] opponentData = opponentStats.split("#");
                String opponentStatsPrepared = opponentData[0];
                for (int i = 1; i < opponentData.length; i++) {
                    opponentStatsPrepared += "\n" + opponentData[i];
                }
                textViewObject.setText(opponentStatsPrepared);
                addActionsFightToPopup();
                switchVisibilityAndHighlightFavourite();
            }
        });
    }

    @Override
    public void displayMessage(int playerID, String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                switch (state) {
                    default:
                        print("AG show MESSAGE called...");
                        if (true) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                textViewMessage.setText(Html.fromHtml(message, Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                textViewMessage.setText(Html.fromHtml(message));
                            }
                        } else {
                            textViewMessage.setText(message);
                        }
                        state = 4;
                        switchVisibilityAndHighlightFavourite();
                        break;
                }
            }
        });
    }

    @Override
    public void displayGameOver(int playerID, String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                print("AG show LEAVE called...");
                textViewMessage.setText(message);
                state = 5;
                switchVisibilityAndHighlightFavourite();
            }
        });
    }

    protected void addActionsFightToPopup() {
        //wuerde eigtl reichen, das nur einmal zu callen, weil sich hier ja nichts aendert,
        //anders als bei den anderen
        print("AG addActionsFight called");

        if (true) return;

        layoutActionsFight.removeAllViews();
        for (ActionTypeFight a : ActionTypeFight.values()) {
            Button action = new Button(this);
            action.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            action.setText(a.getName());
            layoutActionsFight.addView(action);
            android.view.View.OnClickListener listenerAction = new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    localController.tryActionFight(a);
                }
            };
            action.setOnClickListener(listenerAction);
        }
    }


    protected void addItemsAndActionsToPopup(String[] data) {
        int actionItemButtonWidth = (findViewById(R.id.popupScroll).getWidth() / 2 - 5) / 2;
        //"Glorietta:10:12:1:2:0:4:1:1:1:1"
        int numberOfAttributes = CharacterAttributes.values().length; // name and 5 attributes;
        int numberOfItems = ItemType.values().length;
        int numberOfActions = ActionType.values().length;
        ActivityGame temp = this;
        layoutItems.removeAllViews();

        if (false) {
            for (int i = 0; i < numberOfItems; i++) {
                //calculate number of item type
                int number = Integer.parseInt(data[i + numberOfAttributes]);
                //a button for each item, sending the item number, IF number > 0
                if (number > 0) {
                    int typeNumber = i;
                    Button item = new Button(this);
                    //###########################################
                    item.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    item.setText(ItemType.values()[i].getName() + " (" + number + ")");
                    layoutItems.addView(item);
                    View.OnClickListener listener = new View.OnClickListener() {
                        public void onClick(android.view.View v) {
                            temp.localController.tryItem(typeNumber);
                        }
                    };
                    item.setOnClickListener(listener);
                }
            }
        }
        if (false) {
            ArrayList<Integer> resultItemNumber = new ArrayList(numberOfItems);
            ArrayList<Integer> resultItemAmount = new ArrayList(numberOfItems);
            for (int i = 0; i < numberOfItems; i++) {
                int number = Integer.parseInt(data[numberOfAttributes + i]);
                if (number > 0) {
                    resultItemNumber.add(i);
                    resultItemAmount.add(number);
                }
            }
            ActivityGameItemsAdapter adapterItems = new ActivityGameItemsAdapter(this, resultItemNumber, resultItemAmount);
            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setAdapter(adapterItems);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            layoutItems.addView(recyclerView);

        }


        if (true) {
            String resultItemNumber = "";
            String resultAmounts = "";
            for (int i = 0; i < numberOfItems; i++) {
                int number = Integer.parseInt(data[i + numberOfAttributes]);
                if (number > 0) {
                    resultItemNumber += i + "#";
                    resultAmounts += number + "#";
                }
            }
            String[] itemsNumber = resultItemNumber.split("#");
            String[] itemsAmount = resultAmounts.split("#");
            print("AG # of items: " + itemsNumber.length);

            ActivityGameItemsAdapter adapterItems = new ActivityGameItemsAdapter(this, itemsNumber, itemsAmount);
            RecyclerView recyclerView = new RecyclerView(this);
            recyclerView.setAdapter(adapterItems);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            });
            layoutItems.addView(recyclerView);
        }

        if (true) return;
        layoutActions.removeAllViews();
        int numberOfPrecedingElements = numberOfAttributes + numberOfItems;
        for (int i = 0; i < ActionType.values().length; i++) {
            int number = Integer.parseInt(data[i + numberOfPrecedingElements]);
            if (number > 0) {
                Button action = new Button(this);
                action.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                action.setText(ActionType.values()[i].getName());
                action.setWidth(actionItemButtonWidth);
                layoutActions.addView(action);
                int j = i;
                android.view.View.OnClickListener listenerAction = new android.view.View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View v) {
                        temp.localController.tryAction(ActionType.values()[j]);
                    }
                };
                action.setOnClickListener(listenerAction);
            }
        }
    }


    int state = 0;

    void switchVisibilityAndHighlightFavourite() {
        switch (state) {
            case 0: // game
                buttonReturn.setBackgroundColor(Color.DKGRAY);
                showPopup(false);
                break;
            case 1: //stats without object
                buttonReturn.setBackgroundColor(colorHighlight);
                showPopup(true);
                buttonReturn.setVisibility(View.VISIBLE);
                textViewMessage.setVisibility(View.GONE);
                textViewFootnote.setVisibility(View.VISIBLE);
                textViewFootnote.setText("");
                layoutCharacter.setVisibility(View.VISIBLE);
                layoutObject.setVisibility(View.GONE);
                layoutItems.setVisibility(View.VISIBLE);
                layoutActions.setVisibility(View.VISIBLE);
                layoutActionsFight.setVisibility(View.GONE);
                buttonExit.setVisibility(View.VISIBLE);
                break;
            case 2: // stats with object
                layoutObject.setBackgroundColor(colorHighlight);
                buttonReturn.setBackgroundColor(Color.DKGRAY); //can happen after search
                showPopup(true);
                buttonReturn.setVisibility(View.VISIBLE);
                textViewMessage.setVisibility(View.GONE);
                textViewFootnote.setVisibility(View.VISIBLE);
                textViewFootnote.setText("");
                layoutCharacter.setVisibility(View.VISIBLE);
                layoutItems.setVisibility(View.VISIBLE);
                layoutActions.setVisibility(View.VISIBLE);
                layoutActionsFight.setVisibility(View.GONE);
                buttonExit.setVisibility(View.VISIBLE);
                layoutObject.setVisibility(View.VISIBLE);
                break;
            case 20: // stats with object
                layoutObject.setBackgroundColor(colorHighlight);
                buttonReturn.setBackgroundColor(Color.DKGRAY); //can happen after search
                showPopup(true);
                buttonReturn.setVisibility(View.VISIBLE);
                textViewMessage.setVisibility(View.GONE);
                textViewFootnote.setVisibility(View.VISIBLE);
                textViewFootnote.setText("");
                layoutCharacter.setVisibility(View.VISIBLE);
                layoutItems.setVisibility(View.VISIBLE);
                layoutActions.setVisibility(View.VISIBLE);
                layoutActionsFight.setVisibility(View.GONE);
                buttonExit.setVisibility(View.VISIBLE);
                layoutObject.setVisibility(View.VISIBLE);
                break;
            case 21: // stats with object
                //layoutObject.setBackgroundColor(Color.DKGRAY);
                buttonReturn.setBackgroundColor(colorHighlight); //can happen after search
                showPopup(true);
                buttonReturn.setVisibility(View.VISIBLE);
                textViewMessage.setVisibility(View.GONE);
                textViewFootnote.setVisibility(View.VISIBLE);
                textViewFootnote.setText("");
                layoutCharacter.setVisibility(View.VISIBLE);
                layoutItems.setVisibility(View.VISIBLE);
                layoutActions.setVisibility(View.VISIBLE);
                layoutActionsFight.setVisibility(View.GONE);
                buttonExit.setVisibility(View.VISIBLE);
                layoutObject.setVisibility(View.VISIBLE);
                break;
            case 3: // stats with opponent
                layoutActionsFight.getChildAt(0).setBackgroundColor(colorHighlight);
                layoutActionsFight.getChildAt(1).setBackgroundColor(Color.DKGRAY);
                buttonReturn.setVisibility(View.GONE);
                layoutObject.setBackgroundColor(colorLayoutObject);
                layoutActionsFight.setVisibility(View.VISIBLE);
                layoutActions.setVisibility(View.GONE);
                textViewMessage.setVisibility(View.GONE);
                textViewFootnote.setVisibility(View.VISIBLE);
                //textViewFootnote.setText("");
                layoutItems.setVisibility(View.VISIBLE);
                break;
            case 30: // stats with opponent
                layoutActionsFight.getChildAt(0).setBackgroundColor(colorHighlight);
                layoutActionsFight.getChildAt(1).setBackgroundColor(Color.DKGRAY);
                buttonReturn.setVisibility(View.GONE);
                layoutObject.setBackgroundColor(colorLayoutObject);
                layoutActionsFight.setVisibility(View.VISIBLE);
                layoutActions.setVisibility(View.GONE);
                textViewMessage.setVisibility(View.GONE);
                textViewFootnote.setVisibility(View.VISIBLE);
                //textViewFootnote.setText("");
                layoutItems.setVisibility(View.VISIBLE);
                break;
            case 31: // stats with opponent
                layoutActionsFight.getChildAt(1).setBackgroundColor(Color.LTGRAY);
                layoutActionsFight.getChildAt(0).setBackgroundColor(colorHighlight);
                buttonReturn.setVisibility(View.GONE);
                layoutObject.setBackgroundColor(colorLayoutObject);
                layoutActionsFight.setVisibility(View.VISIBLE);
                layoutActions.setVisibility(View.GONE);
                textViewMessage.setVisibility(View.GONE);
                textViewFootnote.setVisibility(View.VISIBLE);
                //textViewFootnote.setText("");
                layoutItems.setVisibility(View.VISIBLE);
                break;
            case 4: //message
                buttonReturn.setBackgroundColor(colorHighlight);
                showPopup(true);
                buttonReturn.setVisibility(View.VISIBLE);
                textViewMessage.setVisibility(View.VISIBLE);
                textViewFootnote.setVisibility(View.GONE);
                layoutCharacter.setVisibility(View.GONE);
                layoutObject.setVisibility(View.GONE);
                layoutActionsFight.setVisibility(View.GONE);
                layoutActions.setVisibility(View.VISIBLE);
                layoutItems.setVisibility(View.GONE);
                layoutActions.setVisibility(View.GONE);
                buttonExit.setVisibility(View.INVISIBLE); //?
                break;
            case 5: // game over
                buttonExit.setBackgroundColor(colorHighlight);
                showPopup(true);
                buttonReturn.setVisibility(View.INVISIBLE);
                textViewMessage.setVisibility(View.VISIBLE);
                textViewFootnote.setVisibility(View.GONE);
                layoutCharacter.setVisibility(View.GONE);
                layoutObject.setVisibility(View.GONE);
                layoutActionsFight.setVisibility(View.GONE);
                layoutActions.setVisibility(View.GONE);
                layoutItems.setVisibility(View.GONE);
                buttonExit.setVisibility(View.VISIBLE);
                break;
            case 6: // placeable + message
                buttonReturn.setBackgroundColor(colorHighlight);
                layoutObject.setBackgroundColor(colorLayoutObject);
                showPopup(true);
                buttonReturn.setVisibility(View.VISIBLE);
                textViewMessage.setVisibility(View.VISIBLE);
                textViewFootnote.setVisibility(View.GONE);
                layoutCharacter.setVisibility(View.GONE);
                layoutObject.setVisibility(View.GONE);
                layoutActionsFight.setVisibility(View.GONE);
                layoutActions.setVisibility(View.VISIBLE);
                layoutItems.setVisibility(View.VISIBLE);
                layoutActions.setVisibility(View.GONE);
                buttonExit.setVisibility(View.INVISIBLE); //?
                break;
            default:
                //showPopup(false);
                break;
        }
    }


    void doFavouriteAction() {
        switch (state) {
            case (1):
                displayGame(-1);
                break; //showStats without object
            case (2):
                localController.tryInteractWithObject();
                break; //showStats with object
            case (3):
                Consumer<ActionTypeFight> favouriteActionFight = action -> localController.tryActionFight(action);
                favouriteActionFight.accept(ActionTypeFight.FLEE);
                break; //showOpponent
            case (4):
                displayGame(-1);
                break; //showMessage
            case (5):
                askAboutLeaving();
                break; //showLeaveGame
            case (30):
                localController.tryActionFight(ActionTypeFight.FLEE);
                break;
            case (31):
                localController.tryActionFight(ActionTypeFight.FIGHT);
                break;
            case (20):
                displayGame(-1);
                break;
            case (21):
                localController.tryInteractWithObject();
                break;

            default:
                displayGame(-1);
                break;
        }
    }


    private void setupPopup() {
        addGestureDetectionToPopup();


        findViewById(R.id.popupScroll).setBackgroundColor(colorBackgroundPopup);
        layoutActionsFight = findViewById(R.id.popupFightActions);
        layoutActions = findViewById(R.id.popupActions);
        buttonExit = findViewById(R.id.popupButtonExit);
        buttonReturn = findViewById(R.id.popupButtonReturnToGame);
        imageViewCharacter = findViewById(R.id.imageViewCharacter);
        textViewCharacter = findViewById(R.id.textViewCharacter);
        textViewCharacter.setTextColor(colorTextPopup);
        layoutCharacter = findViewById(R.id.popupCharacter);
        imageViewObject = findViewById(R.id.imageViewObject);
        textViewObject = findViewById(R.id.textViewObject);
        textViewObject.setTextColor(colorTextPopup);
        layoutObject = findViewById(R.id.popupObject);
        textViewMessage = findViewById(R.id.popupMessage);
        textViewMessage.setTextColor(colorTextPopup);
        textViewFootnote = findViewById(R.id.popupFootnote);
        textViewFootnote.setText(""); //always visible?
        textViewFootnote.setTextColor(colorTextPopup);
        layoutItems = findViewById(R.id.popupItems);
        layoutActions = findViewById(R.id.popupActions);

        android.view.View.OnClickListener listenerClose = new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                showPopup(false);
                buttonReturn.setBackgroundColor(Color.DKGRAY);
            }
        };
        buttonReturn.setOnClickListener(listenerClose);

        android.view.View.OnClickListener listenerExit = new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                print("AG Button exit");
                buttonExit.setBackgroundColor(Color.DKGRAY);
                askAboutLeaving();
            }
        };
        buttonExit.setOnClickListener(listenerExit);
        buttonExit.setBackgroundColor(Color.DKGRAY);

        android.view.View.OnClickListener listenerObject = new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                layoutObject.setBackgroundColor(Color.YELLOW);
                localController.tryInteractWithObject();
            }
        };
        layoutObject.setOnClickListener(listenerObject);

        layoutCharacter.setBackgroundColor(colorLayoutCharacter);
        layoutObject.setBackgroundColor(colorLayoutObject);


        buttonAction0 = findViewById(R.id.buttonAction0);
        buttonAction0.setText(ActionType.values()[0].getName());
        buttonAction0.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                //buttonExit.setBackgroundColor(Color.DKGRAY);
                localController.tryAction(ActionType.values()[0]);
            }
        });
        buttonAction1 = findViewById(R.id.buttonAction1);
        buttonAction1.setText(ActionType.values()[1].getName());
        buttonAction1.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                //buttonExit.setBackgroundColor(Color.DKGRAY);
                localController.tryAction(ActionType.values()[1]);
            }
        });



        buttonAction2 = findViewById(R.id.buttonAction2);
        buttonAction2.setText(ActionType.values()[2].getName());
        buttonAction2.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                //buttonExit.setBackgroundColor(Color.DKGRAY);
                localController.tryAction(ActionType.values()[2]);
            }
        });
        buttonAction3 = findViewById(R.id.buttonAction3);
        buttonAction3.setText(ActionType.values()[3].getName());
        buttonAction3.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                //buttonExit.setBackgroundColor(Color.DKGRAY);
                localController.tryAction(ActionType.values()[3]);
            }
        });

        if (!Setup.debug) {
            buttonAction0.setVisibility(View.GONE); //dig
            buttonAction1.setVisibility(View.GONE); //search
            buttonAction2.setVisibility(View.GONE); //search
            buttonAction3.setVisibility(View.GONE); //reachable
        }


        buttonFight0 = findViewById(R.id.buttonFight0);
        buttonFight0.setText(ActionTypeFight.values()[0].getName());
        buttonFight0.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                //buttonExit.setBackgroundColor(Color.DKGRAY);
                localController.tryActionFight(ActionTypeFight.values()[0]);
            }
        });
        buttonFight1 = findViewById(R.id.buttonFight1);
        buttonFight1.setText(ActionTypeFight.values()[1].getName());
        buttonFight1.setOnClickListener(new android.view.View.OnClickListener() {
            public void onClick(android.view.View v) {
                //buttonExit.setBackgroundColor(Color.DKGRAY);
                localController.tryActionFight(ActionTypeFight.values()[1]);
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void addGestureDetectionToPopup() {
        ScrollView v = findViewById(R.id.popupScroll);
        GestureDetector.SimpleOnGestureListener listenerExtra = new OnSwipeListenerPopup(this);
        View.OnTouchListener listener = new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(selfreferenceActivityGame, listenerExtra);
            //@Override
            public boolean onTouch(View view, MotionEvent event) {
                if (this.gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return popupIncluded.onTouchEvent(event);
            }
        };
        v.setOnTouchListener(listener);
    }


    private void showPopup(boolean isVisible) {
        //invisible or gone better?
        if (isVisible) {
            popupIncluded.setVisibility(View.VISIBLE);
            layoutTiles.setVisibility(View.INVISIBLE);
            textViewBottom.setVisibility(View.INVISIBLE);
            findViewById(R.id.layoutHorizontal).setVisibility(View.INVISIBLE);
            findViewById(R.id.imageViewBackground).setVisibility(View.INVISIBLE);
        } else {
            state = 0;
            popupIncluded.setVisibility(View.GONE);
            layoutTiles.setVisibility(View.VISIBLE);
            textViewBottom.setVisibility(View.VISIBLE);
            findViewById(R.id.layoutHorizontal).setVisibility(View.VISIBLE);
            findViewById(R.id.imageViewBackground).setVisibility(View.VISIBLE);
        }

    }


}