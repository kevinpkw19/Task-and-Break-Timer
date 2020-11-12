package com.TTLabs.TaskBreakTimer;

// Now redundant but kept in case bugs are found
/**CircularProgressBar by Lopez Mikhael is licensed under a Apache License 2.0. Based on a work at Pedramrn/CircularProgressBar.
 */

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class StartTask extends AppCompatActivity {
    CircularProgressBar circularProgressBar;
    TextView timeRemaining;
    databaseHelper myDB;
    String value="";
    String tasklength;
    ModifiedCountDownTimer countDownTimer;
    Long newValue;
    Long newValueM;
    float spareValue;
    Long Tracker;
    Button startButton;
    Button pauseButton;
    Button resetButton;
    Button resumeButton;
    SharedPreferences prefs;
    NotificationManager notificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_task);
        myDB = new databaseHelper(this);
        startButton = findViewById(R.id.btn_start);
        pauseButton = findViewById(R.id.btn_pause);
        resetButton = findViewById(R.id.btn_ResetTask);
        resumeButton = findViewById(R.id.btn_ResumeTask);
        resetButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.GONE);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        notificationManager.cancelAll();

        String ProgressBarColour = prefs.getString("Progress_Bar_Colour", "blue");
        String ProgressBarBackgroundColour = prefs.getString("Progress_Bar_Background_Colour", "white");
        int intPBC = Color.parseColor(ProgressBarColour);
        int intPBBC = Color.parseColor(ProgressBarBackgroundColour);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        circularProgressBar.setProgressBarColor(intPBC);
        circularProgressBar.setBackgroundProgressBarColor(intPBBC);

        timeRemaining = findViewById(R.id.timeRemaining);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
        }

        tasklength = myDB.getTaskLength(value);
        timeRemaining.setText(tasklength + ":00");
        newValue = Long.parseLong(tasklength);
        newValueM = (newValue * 60 * 1000);
    }

    public void cancelTask(View v){
        if (countDownTimer!=null){
            countDownTimer.cancel();
            finish();
        }
        else{finish();}

    }


    //start timer function
    public void startTimer(View v) {
        circularProgressBar.setProgressWithAnimation(100f, newValueM); // =1s
        startButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.VISIBLE);
        countDownTimer = new ModifiedCountDownTimer(newValueM, 1000) {
            public void onTick(long millisUntilFinished) {
                Long minutes= (millisUntilFinished/60000);
                String Sminutes= minutes.toString();
                Long Seconds= (millisUntilFinished/1000)-(60*minutes);
                String Sseconds= Seconds.toString();
                spareValue= (float)millisUntilFinished;
                Tracker= millisUntilFinished;


                if (Seconds<10){
                    timeRemaining.setText(Sminutes+ ":0"+ Sseconds);
                } else{
                timeRemaining.setText(Sminutes+ ":"+ Sseconds);
                }
                if (Sminutes=="0" && Sseconds=="0"){
                    countDownTimer.onFinish();

                }

            }
            public void onFinish() {
                timeRemaining.setText("0:00");
                pauseButton.setVisibility(View.GONE);
                TimerNotification();
                launchStartBreak();

            }
        };
        countDownTimer.start();

    }

    public void launchStartBreak(){
        Intent A= new Intent(this,StartBreak.class);
        A.putExtra("key",value);
        startActivity(A);
        finish();

    }


    public void PauseTimer(View v){
        if(countDownTimer!=null)
            countDownTimer.pause();
        pauseButton.setVisibility(View.GONE);
        resetButton.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.VISIBLE);
        spareValue=(spareValue/newValueM)*100;
        spareValue= 100-spareValue;
        circularProgressBar.setProgressWithAnimation(spareValue);

    }


    public  void ResetTask(View v) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            circularProgressBar.setProgressWithAnimation(0);
            timeRemaining.setText(tasklength + ":00");
            pauseButton.setVisibility(View.GONE);
            resetButton.setVisibility(View.GONE);
            startButton.setVisibility(View.VISIBLE);
            resumeButton.setVisibility((View.GONE));
        }
    }

    public  void ResumeTask(View v){
        if(countDownTimer!=null) {
            countDownTimer.resume();
            circularProgressBar.setProgressWithAnimation(100f,Tracker);
            resetButton.setVisibility(View.GONE);
            resumeButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);



        }}

        public void TimerNotification(){
            int NOTIFICATION_ID = 240;
            String CHANNEL_ID = "Start_Task_Notif";
            CharSequence name = "Start_Task";
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel startTaskNotif = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
                startTaskNotif.setLightColor(Color.GRAY);
                startTaskNotif.enableLights(true);
                startTaskNotif.setDescription("notification for end of first timer");
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build();
                startTaskNotif.setSound(uri, audioAttributes);
                startTaskNotif.setVibrationPattern(new long[]{0, 500, 1000});

                if (startTaskNotif != null) {
                    notificationManager.createNotificationChannel( startTaskNotif );
                }
            }

            Intent NotificationIntent= this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
            NotificationIntent.setPackage(null);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    NotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hourglass)
                    .setContentTitle("Task is complete")
                    .setContentText("Time for a break!")
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0, 500, 1000})
                    .setLights(Color.GRAY,300,300)
                    .setSound(uri)
                    .setContentIntent(contentIntent);

            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }


    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    @Override
    protected void onResume(){
        super.onResume();
        notificationManager.cancelAll();
    }



}