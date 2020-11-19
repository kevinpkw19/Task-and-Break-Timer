package com.TTLabs.TaskBreakTimer;

/**CircularProgressBar by Lopez Mikhael is licensed under a Apache License 2.0. Based on a work at Pedramrn/CircularProgressBar.
 */

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class StartBothTimers extends AppCompatActivity {

    CircularProgressBar circularProgressBar;
    TextView timeRemaining;
    TextView timerName;
    databaseHelper myDB;
    String value="";
    String breakLength;
    String taskLength;
    ModifiedCountDownTimer countDownTimer;
    Long task;
    Long taskM;
    Long breakO;
    Long breakM;
    float spareValue;
    Long Tracker;
    Button startButton;
    Button pauseButton;
    Button resetButton;
    Button resumeButton;
    Button cancelButton;
    Button repeatButton;
    Boolean Timer;
    Boolean autoStartBreakTimer;
    Boolean autoRepeatTimers;
    SharedPreferences prefs;
    NotificationManager notificationManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_both_timers);
        myDB= new databaseHelper(this);
        startButton= findViewById(R.id.btn_start);
        pauseButton= findViewById(R.id.btn_pause);
        resetButton= findViewById(R.id.btn_Reset);
        resumeButton= findViewById(R.id.btn_Resume);
        cancelButton=findViewById(R.id.btn_cancel);
        repeatButton= findViewById(R.id.btn_repeat);
        timeRemaining= findViewById(R.id.timeRemaining);
        timerName= findViewById(R.id.timer_name);
        repeatButton.setVisibility(View.GONE);
        resetButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.GONE);
        Timer=true;
        prefs= PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        circularProgressBar = findViewById(R.id.circularProgressBar);

        String ProgressBarColour=prefs.getString("Progress_Bar_Colour","blue");
        String  ProgressBarBackgroundColour= prefs.getString("Progress_Bar_Background_Colour","white");
        autoStartBreakTimer= prefs.getBoolean("auto_start_break_timer", false);
        autoRepeatTimers=prefs.getBoolean("auto_repeat_timers",false);
        int intPBC = Color.parseColor(ProgressBarColour);
        int intPBBC= Color.parseColor(ProgressBarBackgroundColour);
        circularProgressBar = findViewById(R.id.circularProgressBar);
        circularProgressBar.setProgressBarColor(intPBC);
        circularProgressBar.setBackgroundProgressBarColor(intPBBC);


        Bundle data = getIntent().getExtras();
        if (data != null) {
            value = data.getString("key");
        }

        taskLength=myDB.getTaskLength(value);
        breakLength= myDB.getBreakLength(value);
        timeRemaining.setText(taskLength + ":00");
        timerName.setText("Task Time");
        task= Long.parseLong(taskLength);
        taskM= (task*60*1000);
        breakO= Long.parseLong(breakLength);
        breakM= (breakO*60*1000);
    }



    public void cancelBreak(View v){
        if (countDownTimer!=null){
            countDownTimer.cancel();
        }
        else{
            notificationManager.cancelAll();
        }
        finish();
    }



    public void startTimer(View v) {
        if (Timer){
            notificationManager.cancel(250);
            circularProgressBar.setProgressWithAnimation(100f, taskM);
            startButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
            countDownTimer = new ModifiedCountDownTimer(taskM, 1000) {
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
                    circularProgressBar.setProgressWithAnimation(0,0L);
                    timeRemaining.setText("0:00");
                    pauseButton.setVisibility(View.GONE);
                    startButton.setVisibility(View.VISIBLE);
                    TimerNotification();
                    Timer=false;
                    timerName.setText("Break Time");
                    timeRemaining.setText(breakLength + ":00");

                    if (autoStartBreakTimer){
                        startTimer();
                    }

                }
            };
            countDownTimer.start();
        } else{
            notificationManager.cancel(240);
            circularProgressBar.setProgressWithAnimation(100f, breakM);
            startButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
            countDownTimer = new ModifiedCountDownTimer(breakM, 1000) {
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


    }

    // Overloaded for auto starting on end and repeat timers
    public void startTimer() {
        if (Timer) {
            circularProgressBar.setProgressWithAnimation(100f, taskM);
            startButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);
            countDownTimer = new ModifiedCountDownTimer(taskM, 1000) {
                public void onTick(long millisUntilFinished) {
                    Long minutes = (millisUntilFinished / 60000);
                    String Sminutes = minutes.toString();
                    Long Seconds = (millisUntilFinished / 1000) - (60 * minutes);
                    String Sseconds = Seconds.toString();
                    spareValue = (float) millisUntilFinished;
                    Tracker = millisUntilFinished;

                    if (Seconds < 10) {
                        timeRemaining.setText(Sminutes + ":0" + Sseconds);
                    } else {
                        timeRemaining.setText(Sminutes + ":" + Sseconds);
                    }
                    if (Sminutes == "0" && Sseconds == "0") {
                        countDownTimer.onFinish();

                    }

                }

                public void onFinish() {
                    circularProgressBar.setProgressWithAnimation(0, 0L);
                    timeRemaining.setText("0:00");
                    pauseButton.setVisibility(View.GONE);
                    startButton.setVisibility(View.VISIBLE);
                    TimerNotification();
                    notificationManager.cancel(250);
                    Timer = false;
                    timerName.setText("Break Time");
                    timeRemaining.setText(breakLength + ":00");

                    if (autoRepeatTimers) {
                        startTimer();
                    }

                }
            };
            countDownTimer.start();
        } else{
        circularProgressBar.setProgressWithAnimation(100f, breakM); // =1s
        startButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.VISIBLE);
        countDownTimer = new ModifiedCountDownTimer(breakM, 1000) {
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

                TimerNotification();
                notificationManager.cancel(240);

                if (autoRepeatTimers) {
                    circularProgressBar.setProgressWithAnimation(0, 0L);
                    Timer = true;
                    timerName.setText("Task Time");
                    timeRemaining.setText(breakLength + ":00");
                    startTimer();
                } else{
                    pauseButton.setVisibility(View.GONE);
                    repeatButton.setVisibility(View.VISIBLE);
                }

            }
        };
        countDownTimer.start();
    }}

    public void repeatTimer(View v){
        timerName.setText("Task Time");
        timeRemaining.setText(taskLength + ":00");
        Timer=true;
        circularProgressBar.setProgressWithAnimation(0);
        startButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        repeatButton.setVisibility(View.GONE);
        resetButton.setVisibility(View.GONE);
        pauseButton.setVisibility(View.GONE);
        resumeButton.setVisibility(View.GONE);
        notificationManager.cancel(250);
    }


    public void PauseTimer(View v){
        if(countDownTimer!=null)
            countDownTimer.pause();
        pauseButton.setVisibility(View.GONE);
        resetButton.setVisibility(View.VISIBLE);
        resumeButton.setVisibility(View.VISIBLE);

        if (Timer){
            spareValue=(spareValue/taskM)*100;
        } else{
            spareValue=(spareValue/breakM)*100;
        }
        spareValue= 100-spareValue;
        circularProgressBar.setProgressWithAnimation(spareValue);

    }


    public  void ResetTimer(View v) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            circularProgressBar.setProgressWithAnimation(0);
            pauseButton.setVisibility(View.GONE);
            resetButton.setVisibility(View.GONE);
            startButton.setVisibility(View.VISIBLE);
            resumeButton.setVisibility((View.GONE));

            if (Timer) {
                timeRemaining.setText(taskLength + ":00");
            } else{
                timeRemaining.setText(breakLength + ":00");
            }
            }

        }


    public  void ResumeTimer(View v){
        if(countDownTimer!=null) {
            countDownTimer.resume();
            circularProgressBar.setProgressWithAnimation(100f,Tracker);
            resetButton.setVisibility(View.GONE);
            resumeButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.VISIBLE);

        }}

    public void TimerNotification() {
        int notification_Id;
        String channel_Id;
        String contentTitle;
        String contentText;
        CharSequence name;
        
        if (Timer){
            notification_Id= 240;
            channel_Id = "Task_Notif";
            name = "Task";
            contentTitle= "Task is over";
            contentText= "Time for a break";
            
        } else{
            notification_Id= 250;
            channel_Id = "Break_Notif";
            name = "Break";
            contentTitle= "Break is over";
            contentText= "Time to get back to work";
        }
        Uri defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel timerNotif = new NotificationChannel(channel_Id, name, NotificationManager.IMPORTANCE_HIGH);
            timerNotif.setLightColor(Color.GRAY);
            timerNotif.enableLights(true);
            timerNotif.setDescription("notification for end of timer");
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            timerNotif.setSound(defaultRingtone, audioAttributes);
            timerNotif.setVibrationPattern(new long[]{0, 500, 1000});

            if (timerNotif != null) {
                notificationManager.createNotificationChannel( timerNotif );
            }
        }

        Intent NotificationIntent = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        NotificationIntent.setPackage(null);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                NotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channel_Id)
                .setSmallIcon(R.drawable.ic_hourglass)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//    }




}