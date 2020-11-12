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

public class StartBreak extends AppCompatActivity {

    CircularProgressBar circularProgressBar;
    TextView timeRemaining;
    databaseHelper myDB;
    String value="";
    String breakLength;
    ModifiedCountDownTimer countDownTimer;
    Long newValue;
    Long newValueM;
    float spareValue;
    Long Tracker;
    Button startButton;
    Button pauseButton;
    Button resetButton;
    Button resumeButton;
    Button cancelButton;
    Button repeatButton;
    SharedPreferences prefs;
    NotificationManager notificationManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_break);
        myDB= new databaseHelper(this);
        startButton= findViewById(R.id.btn_start);
        pauseButton= findViewById(R.id.btn_pause);
        resetButton= findViewById(R.id.btn_ResetTask);
        resumeButton= findViewById(R.id.btn_ResumeTask);
        cancelButton=findViewById(R.id.btn_cancelBreak);
        repeatButton= findViewById(R.id.btn_repeat);
        repeatButton.setVisibility(View.GONE);
        resetButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.GONE);
        prefs= PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        circularProgressBar = findViewById(R.id.circularProgressBar);

        String ProgressBarColour=prefs.getString("Progress_Bar_Colour","blue");
        String  ProgressBarBackgroundColour= prefs.getString("Progress_Bar_Background_Colour","white");
        int intPBC = Color.parseColor(ProgressBarColour);
        int intPBBC= Color.parseColor(ProgressBarBackgroundColour);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        circularProgressBar.setProgressBarColor(intPBC);
        circularProgressBar.setBackgroundProgressBarColor(intPBBC);

        timeRemaining= findViewById(R.id.timeRemaining);
        Bundle data = getIntent().getExtras();
        if (data != null) {
            value = data.getString("key");
        }

        breakLength= myDB.getBreakLength(value);
        timeRemaining.setText(breakLength + ":00");
        newValue= Long.parseLong(breakLength);
        newValueM= (newValue*60*1000);
    }



    public void cancelBreak(View v){
        if (countDownTimer!=null){
            countDownTimer.cancel();
            finish();
        }
        else{
            notificationManager.cancelAll();
            finish();
        }
    }



    public void startTimer(View v) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
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
                repeatButton.setVisibility(View.VISIBLE);
                TimerNotification();

            }
        };
        countDownTimer.start();

    }

    public void repeatTaskBreak(View v){
        Intent A= new Intent(this,StartTask.class);
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
            timeRemaining.setText(breakLength + ":00");
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




    public void TimerNotification() {
        int notification_Id = 240;
        String channel_Id = "Break_Task_Notif";
        CharSequence name = "Break_Task";
        Uri defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel startBreakNotif = new NotificationChannel(channel_Id, name, NotificationManager.IMPORTANCE_HIGH);
            startBreakNotif.setLightColor(Color.GRAY);
            startBreakNotif.enableLights(true);
            startBreakNotif.setDescription("notification for end of first timer");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            startBreakNotif.setSound(defaultRingtone, audioAttributes);
            startBreakNotif.setVibrationPattern(new long[]{0, 500, 1000});

            if (startBreakNotif != null) {
                notificationManager.createNotificationChannel( startBreakNotif );
            }
        }

        Intent NotificationIntent = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        NotificationIntent.setPackage(null);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                NotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channel_Id)
                .setSmallIcon(R.drawable.ic_hourglass)
                .setContentTitle("Break is over")
                .setContentText("Time to get back to work!")
                .setAutoCancel(true)
                .setChannelId(channel_Id)
                .setVibrate(new long[]{0, 500, 1000})
                .setLights(Color.GRAY,300,300)
                .setSound(defaultRingtone)
                .setContentIntent(contentIntent);
        notificationManager.notify(notification_Id, mBuilder.build());
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);

        notificationManager.cancelAll();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }




}