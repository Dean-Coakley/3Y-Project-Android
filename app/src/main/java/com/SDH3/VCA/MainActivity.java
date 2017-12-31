package com.SDH3.VCA;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.integreight.onesheeld.sdk.OneSheeldConnectionCallback;
import com.integreight.onesheeld.sdk.OneSheeldDevice;
import com.integreight.onesheeld.sdk.OneSheeldManager;
import com.integreight.onesheeld.sdk.OneSheeldScanningCallback;
import com.integreight.onesheeld.sdk.OneSheeldSdk;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // package name
    public static String PACKAGE_NAME;

    //Layout references
    LinearLayout home_view, calendar_view, weather_view, business_list_view, music_view, memory_game_view;

    //Location
    LocationServicesManager locationServicesManager;

    //Weather var
    TextView cityField, detailsField, currentTemperatureField, humidity_field, weatherIcon, updatedField;
    Typeface weatherFont;


    //Database
    DbManager db;
    UserProfile user;

    // connected to a OneSheeld?
    private boolean connected = false;

    //GUI
    private Button scanButton;
    private Switch toggleLights;
    private Switch toggleHeating;
    private Button disconnectButton;
    private Button getGPS_button;

    //Sheeld
    private OneSheeldManager manager;
    private OneSheeldDevice sheeldDevice;

    //Speech recognition
    private TextView speechText;
    private ImageButton btnSpeak;
    private boolean connectedToSheeld;
    private VoiceManager voiceManager;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 123456789, REQ_CODE_SPEECH_INPUT = 100;

    // Call Permission final variables
    private final String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int PERMISSION_REQUEST = 100;

    //Game variables
    private MemoryGame game;
    private TextView levelText, tooltip, enteredWords, gameWords;
    private EditText wordEntry;
    private Button startGame, nextLevel, enterWord, restartLevel;

    //Twitter variables
    private TwitterLoginButton loginButton;
    private Button tweetButton;

    private OneSheeldScanningCallback scanningCallback = new OneSheeldScanningCallback() {
        @Override
        public void onDeviceFind(OneSheeldDevice device) {
            //cancel further scanning
            manager.cancelScanning();
            //connect to first-found oneSheeld
            device.connect();
        }
    };

    private OneSheeldConnectionCallback connectionCallback = new OneSheeldConnectionCallback() {
        @Override
        public void onConnect(OneSheeldDevice device) {
            sheeldDevice = device;
            connectedToSheeld = true;

            // when a connection is established, enable device-specific buttons
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toggleHeating.setEnabled(true);
                    toggleLights.setEnabled(true);
                    disconnectButton.setEnabled(true);
                }
            });
        }

        public void onDisconnect(OneSheeldDevice device) {
            sheeldDevice = null;
            connectedToSheeld = false;

            //when a disconnect occurs, make sure all device-specific buttons are disabled
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toggleHeating.setEnabled(false);
                    toggleLights.setEnabled(false);
                }
            });
        }

    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(this);
        setContentView(R.layout.activity_main);
        PACKAGE_NAME = getPackageName();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        OneSheeldSdk.init(this);
        OneSheeldSdk.setDebugging(true);
        manager = OneSheeldSdk.getManager();
        manager.setConnectionRetryCount(1);
        manager.setScanningTimeOut(5);
        manager.setAutomaticConnectingRetriesForClassicConnections(true);
        // add callback functions for handling connections / scanning
        manager.addConnectionCallback(connectionCallback);
        manager.addScanningCallback(scanningCallback);

        game = new MemoryGame();

        //Location Permission prompt
        checkLocationPermission();

        // GUI SETUP
        setupGUI();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //location services init
        boolean success = locationServicesInit();
        //Weather
        weatherServicesInit();
        if (success)
            weatherReport();

        //Database
        Intent loginInfo = getIntent();
        String uID = loginInfo.getStringExtra("uID");
        db = new DbManager();
        user = new UserProfile();
        db.initUser(user, uID, this);


        speechText = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                voiceManager.promptSpeechInput();
            }
        });

        voiceManager = new VoiceManager(this, this, REQ_CODE_SPEECH_INPUT);
        connectedToSheeld = false;
        try {
            playMusic();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void weatherServicesInit() {
        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        Button refresh = (Button) findViewById(R.id.refresh_weather);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherReport();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FirebaseAuth.getInstance().signOut();
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        // when a new layout needs to be shown, make all other included layout 'GONE',
        // and make the requested layout 'VISIBLE'
        int id = item.getItemId();

        home_view.setVisibility(View.GONE);
        calendar_view.setVisibility(View.GONE);
        weather_view.setVisibility(View.GONE);
        business_list_view.setVisibility(View.GONE);
        memory_game_view.setVisibility(View.GONE);
        music_view.setVisibility(View.GONE);
        
        if (id == R.id.nav_home)
            home_view.setVisibility(View.VISIBLE);
        else if (id == R.id.nav_weather)
            switchWeatherScene();
        else if (id == R.id.nav_calendar) {
            calendar_view.setVisibility(View.VISIBLE);
        }
        else if (id == R.id.nav_game) {
            memory_game_view.setVisibility(View.VISIBLE);
            game.init();
        } else if (id == R.id.nav_music) {
            music_view.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_to) {
            //prepare the businesses layout
            showBusinesses(DbManager.RESTAURANTS_DB_TAG);
            business_list_view.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_shop) {
            showBusinesses(DbManager.SHOPPING_DB_TAG);
            business_list_view.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_taxi) {
            showBusinesses(DbManager.TAXIS_DB_TAG);
            business_list_view.setVisibility(View.VISIBLE);
        } else if (id == R.id.sign_out) {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setMessage("Logging out..");
            pd.show();
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //cleanly disconnect all devices if app is nearly destruction
    @Override
    protected void onDestroy() {
        manager.disconnectAll();
        manager.cancelConnecting();
        manager.cancelScanning();
        super.onDestroy();
    }

    public boolean checkCallPermission() {
        boolean granted = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) {

                    Toast.makeText(this,
                            R.string.call_per, Toast.LENGTH_LONG).show();

                } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {

                    Toast.makeText(this,
                            R.string.call_per, Toast.LENGTH_LONG).show();

                } else {

                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE},
                            PERMISSION_REQUEST);
                }
            } else
                granted = true;
        }

        return granted;
    }

    public boolean checkLocationPermission() {
        boolean granted = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(this,
                            R.string.loc_req_blue, Toast.LENGTH_LONG).show();

                } else {

                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);

                    // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else
                granted = true;
        }

        return granted;
    }

    public boolean checkBlueTooth() {
        boolean active = false;

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                active = true;
            } else
                Toast.makeText(this, R.string.turn_on_bt, Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, R.string.bt_support, Toast.LENGTH_LONG).show();

        return active;
    }

    // GUI SETUP
    public void setupGUI() {
        // initialise included-layout references
        home_view = (LinearLayout) findViewById(R.id.home_layout);
        calendar_view = (LinearLayout) findViewById(R.id.calendar_layout);
        weather_view = (LinearLayout) findViewById(R.id.weather_id);
        business_list_view = (LinearLayout) findViewById(R.id.business_list_layout);
        memory_game_view = (LinearLayout) findViewById(R.id.game_include_tag);
        music_view = (LinearLayout) findViewById(R.id.music_layout);
        scanButton = (Button) findViewById(R.id.scanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            //cancel all existing scans / connections in progress befroe rescanning
            public void onClick(View v) {
                scan();
            }
        });

        //disconnects all devices
        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.disconnectAll();
                disconnectButton.setEnabled(false);
                toggleHeating.setEnabled(false);
                toggleLights.setEnabled(false);
            }
        });

        // add heating toggling functionality
        toggleHeating = (Switch) findViewById(R.id.toggle_heating);
        toggleHeating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // pin-3 is treated as the "heating" pin
                if (isChecked) sheeldDevice.digitalWrite(3, true);
                else sheeldDevice.digitalWrite(3, false);

            }
        });

        // add lighting toggling functionality
        toggleLights = (Switch) findViewById(R.id.toggle_lights);
        toggleLights.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // pin 4 is treated as the "lighting" pin
                if (isChecked) sheeldDevice.digitalWrite(4, true);
                else sheeldDevice.digitalWrite(4, false);
            }
        });

        // all device-specific buttons are disabled by default
        toggleHeating.setEnabled(false);
        toggleLights.setEnabled(false);
        disconnectButton.setEnabled(false);


        loginButton = (TwitterLoginButton) findViewById(R.id.login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(MainActivity.this,
                        "Could not login to Twitter" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        tweetButton = (Button) findViewById(R.id.tweetButton);
        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TwitterSession session = TwitterCore.getInstance().getSessionManager()
                        .getActiveSession();
                if(session != null) {
                    Location l = locationServicesManager.getLastLocation();
                    if (l != null) {
                        double lon = l.getLongitude();
                        double lat = l.getLatitude();
                    final Intent intent = new ComposerActivity.Builder(MainActivity.this)
                            .session(session)
                            .text(lon + ", " + lat)
                            .hashtags("#geoTag")
                            .createIntent();
                        startActivity(intent);
                    }
                }
                else
                    Toast.makeText(MainActivity.this,
                            "Log in to Twitter first", Toast.LENGTH_SHORT).show();
            }
        });

        //Calendar
        Button addEventBtn = (Button) findViewById(R.id.addEventBtn);
        CalendarView cal = (CalendarView) findViewById(R.id.calendarView);

        final int[] yr = new int[1];
        final int[] mn = new int[1];
        final int[] dom = new int[1];
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                yr[0] = year;
                mn[0] = month;
                dom[0] = dayOfMonth;
            }
        });
        addEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = "";
                String location = "";
                long begin = System.currentTimeMillis();
                addEvent(title, location, begin, begin);

                //My form intent that doesn't seem to work
//                Intent addEvent = new Intent(MainActivity.this, EventAdd.class);
//                Bundle b = new Bundle();
//                b.putIntArray("year", yr);
//                b.putIntArray("month", mn);
//                b.putIntArray("dayOfMon", dom);
//                addEvent.putExtras(b);
//                startActivity(addEvent);
            }
        });
        setUpGame();
    }

    public void setUpGame(){
        //Game related variables
        levelText = (TextView) findViewById(R.id.levelText);
        tooltip = (TextView) findViewById(R.id.tooltip);
        tooltip.setVisibility(View.GONE);
        enteredWords = (TextView) findViewById(R.id.enteredWordsText);
        enteredWords.setVisibility(View.GONE);

        wordEntry = (EditText) findViewById(R.id.wordEntry);
        wordEntry.setVisibility(View.GONE);

        gameWords = (TextView) findViewById(R.id.gameWords);
        gameWords.setVisibility(View.GONE);

        nextLevel = (Button) findViewById(R.id.nextLevel);
        nextLevel.setVisibility(View.GONE);
        nextLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(game.nextLevel()) {
                    levelText.setText(game.getLevel());
                    enteredWords.setText(game.getCorrectWords());
                    gameWords.setText(game.getGameWords());
                    startGame();

                    resetGameVisibility();
                    gameWords.setVisibility(View.VISIBLE);
                    tooltip.setVisibility(View.VISIBLE);
                    tooltip.setText(R.string.memorise_words);
                }
                else
                    Toast.makeText(MainActivity.this,
                            R.string.game_beat, Toast.LENGTH_SHORT).show();
            }
        });

        startGame = (Button) findViewById(R.id.startGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.launchGame();
                levelText.setText(game.getLevel());
                enteredWords.setText(game.getCorrectWords());
                gameWords.setText(game.getGameWords());

                resetGameVisibility();
                gameWords.setVisibility(View.VISIBLE);
                tooltip.setVisibility(View.VISIBLE);
                tooltip.setText(R.string.memorise_words);
                startGame();
            }
        });
        enterWord = (Button) findViewById(R.id.enterWord);
        enterWord.setVisibility(View.GONE);
        enterWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(game.checkWord(wordEntry.getText().toString())) {
                    enteredWords.setText(game.getCorrectWords());
                    wordEntry.setText("");
                }
                else {
                    wordEntry.setText("");
                    Toast.makeText(MainActivity.this,
                            R.string.incorrect_word, Toast.LENGTH_SHORT).show();
                }
                if(game.isWon()) {
                    resetGameVisibility();
                    nextLevel.setVisibility(View.VISIBLE);
                    tooltip.setVisibility(View.VISIBLE);
                    tooltip.setText(R.string.level_beat);
                }
            }
        });
        restartLevel = (Button) findViewById(R.id.restart);
        restartLevel.setVisibility(View.GONE);
        restartLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.restartLevel();
                enteredWords.setText(game.getCorrectWords());
                gameWords.setText(game.getGameWords());
                startGame();

                resetGameVisibility();
                gameWords.setVisibility(View.VISIBLE);
                tooltip.setVisibility(View.VISIBLE);
                tooltip.setText(R.string.memorise_words);
            }
        });
    }

    public void startGame(){
        Runnable myRunnable = new Runnable(){
            public void run(){
                try {
                    Thread.sleep(4000*game.getLevelInt());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        enteredWords.setVisibility(View.VISIBLE);
                        wordEntry.setVisibility(View.VISIBLE);
                        gameWords.setVisibility(View.GONE);
                        enterWord.setVisibility(View.VISIBLE);
                        restartLevel.setVisibility(View.VISIBLE);
                        tooltip.setVisibility(View.VISIBLE);
                        tooltip.setText(R.string.enter_memorised_words);
                    }
                });
            }
        };

        Thread thread = new Thread(myRunnable);
        thread.start();
    }

    public void resetGameVisibility(){
        nextLevel.setVisibility(View.GONE);
        enteredWords.setVisibility(View.GONE);
        wordEntry.setVisibility(View.GONE);
        gameWords.setVisibility(View.GONE);
        enterWord.setVisibility(View.GONE);
        startGame.setVisibility(View.GONE);
        restartLevel.setVisibility(View.GONE);
        tooltip.setVisibility(View.GONE);
    }

    public void showBusinesses(final String businessType) {
        ArrayList<Business> businesses = null;
        TextView layoutTitle = (TextView) findViewById(R.id.business_layout_header);

        switch (businessType) {
            // set the title of the list layout depending on the type of business shown
            case DbManager.SHOPPING_DB_TAG:
                layoutTitle.setText(getString(R.string.shopping));

                // get arraylist of all businesses of the specified type
                businesses = db.getBusinesses(user.getuID(), DbManager.SHOPPING_DB_TAG);
                break;

            case DbManager.TAXIS_DB_TAG:
                layoutTitle.setText(getString(R.string.taxi));
                businesses = db.getBusinesses(user.getuID(), DbManager.TAXIS_DB_TAG);
                break;

            case DbManager.RESTAURANTS_DB_TAG:
                layoutTitle.setText(getString(R.string.take_out));
                businesses = db.getBusinesses(user.getuID(), DbManager.RESTAURANTS_DB_TAG);
                break;
        }

        // get reference to the listView where businesses will be listed
        ListView listContainer = (ListView) findViewById(R.id.business_list_element_container);
        // initialise custom arrayAdapter for mapping businesses to a layout, and pass it to the listview
        BusinessAdapter adapter = new BusinessAdapter(this, businesses, this);
        listContainer.setAdapter(adapter);
    }

    public void scan() {
        if (checkLocationPermission() && checkBlueTooth()) {
            manager.cancelScanning();
            manager.cancelConnecting();
            manager.scan();
        }
    }

    //start location services
    public boolean locationServicesInit() {
        locationServicesManager = new LocationServicesManager(this);
        return locationServicesManager.locationManagerInit();
    }

    //get weather report for current location
    public void weatherReport() {
        cityField = (TextView) findViewById(R.id.city_field);
        updatedField = (TextView) findViewById(R.id.updated_field);
        detailsField = (TextView) findViewById(R.id.details_field);
        currentTemperatureField = (TextView) findViewById(R.id.current_temperature_field);
        humidity_field = (TextView) findViewById(R.id.humidity_field);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);


        Location l = locationServicesManager.getLastLocation();
        WeatherFunction.placeIdTask asyncTask = new WeatherFunction.placeIdTask(new WeatherFunction.AsyncResponse() {
            @SuppressLint("SetTextI18n")
            public void processFinish(String weather_city, String weather_description, String weather_temperature, String weather_humidity, String weather_updatedOn, String icon, String sun_rise) {
                cityField.setText(weather_city);
                updatedField.setText(weather_updatedOn);
                detailsField.setText(weather_description);
                currentTemperatureField.setText(weather_temperature);
                humidity_field.setText(getString(R.string.humid) + weather_humidity);
                weatherIcon.setText(Html.fromHtml(icon));
            }
        });

        //For parsing longitude and latitude (doubles) into Strings
        if (l != null) {
            double lat = l.getLatitude();
            double lon = l.getLongitude();
            String latS = String.valueOf(lat);
            String lonS = String.valueOf(lon);
            asyncTask.execute(latS, lonS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                voiceManager.processResult(resultCode, data);
                break;
            }
        }

        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    public void callNumberButtonOnClick(final String s) {
        //Call Permission Prompt
        checkCallPermission();

        //Take Out Phone call
        CallListener phoneListener = new CallListener();
        final TelephonyManager telephonyM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyM.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + s));

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(callIntent, 100);
            return;
        } else {
            Toast.makeText(this, R.string.call_permission_ungranted, Toast.LENGTH_LONG).show();
        }
    }

    public void switchWeatherScene() {
        weatherReport();
        home_view.setVisibility(View.GONE);
        weather_view.setVisibility(View.VISIBLE);
        business_list_view.setVisibility(View.GONE);
        calendar_view.setVisibility(View.GONE);
        memory_game_view.setVisibility(View.GONE);
    }

    public void openWebpage(String url) {
        Intent page = new Intent(Intent.ACTION_VIEW);
        page.setData(Uri.parse(url));
        startActivity(page);
    }

    public OneSheeldDevice getSheeld() {
        return sheeldDevice;
    }

    public void setSpeechText(String str) {
        speechText.setText(str);
    }

    public boolean getConnectedToSheeld() {
        return connectedToSheeld;
    }

    private ImageButton play;
    private ImageButton stop;
    private Spinner urlS;
    public MediaPlayer mediaPlayer = null;
    private String songName;
    private String songURL;


    public void playMusic() throws FileNotFoundException {

        urlS = (Spinner) findViewById(R.id.music_link);
        ArrayAdapter<CharSequence> musicAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.music, android.R.layout.simple_list_item_1);
        urlS.setAdapter(musicAdapter);

        play = (ImageButton) findViewById(R.id.play_music);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = getResources().getStringArray(R.array.musicUrl)[urlS.getSelectedItemPosition()];

                try {

                    //MediaPlayer will be in a play state but will give a IllegalStateException when asked to be played
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        play.setImageResource(R.drawable.play_button);
                    } else {

                        //Instantiate your MediaPlayer
                        mediaPlayer = new MediaPlayer();
                        //This allows the device to be locked and to continue to play music
                        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            SoundPool sp = new SoundPool.Builder().setAudioAttributes(new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA).build())
                                    .setMaxStreams(1).build();
                        }
                        //Points the MediaPlayer at the link to play
                        mediaPlayer.setDataSource(url);//Url may look like this "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                        //Loads data source set above
                        mediaPlayer.prepare();

                        play.setImageResource(R.drawable.stop_button);


                        //Start Playing your .mp3 file
                        mediaPlayer.start();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addEvent(String title, String location, long begin, long end) {
        Intent calIntent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        if (calIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(calIntent);
        }
    }

    public void notifyUserDataReady(boolean ready){
        if (ready){
            Intent startGeofenceService = new Intent(this, GeoFenceService.class);
            startGeofenceService.putExtra("geofence_latitude", String.valueOf(user.getGeofenceLatitude()));
            startGeofenceService.putExtra("geofence_longitude", String.valueOf(user.getGeofenceLongitude()));
            startGeofenceService.putExtra("geofence_radius", String.valueOf(user.getGeofenceRadius()));
            startService(startGeofenceService);
        }else{
            Toast.makeText(this, "An error occurred while fetching user data",
                    Toast.LENGTH_SHORT).show();
        }
    }
}