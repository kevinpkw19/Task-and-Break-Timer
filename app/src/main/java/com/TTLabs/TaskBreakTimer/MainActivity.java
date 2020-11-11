package com.TTLabs.TaskBreakTimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity{

    databaseHelper myDB;
    String taskChosen;
    String IDchosen;
    ArrayAdapter<String> taskListerAdapter;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String customName= prefs.getString("user_name","");


        Button btnNewTask= findViewById(R.id.btn_newTask);
        Button btnStartTask=findViewById(R.id.btn_startTask);
        Button btnEditTask=findViewById(R.id.btn_editTask);
        TextView taskLengthDisplay= findViewById(R.id.taskLengthDisplay);
        TextView breakLengthDisplay= findViewById(R.id.breakLengthDisplay);
        TextView welcomeGreeting= findViewById(R.id.textview_first);
        TextView userHint= findViewById(R.id.userHint);
        Spinner taskLister= findViewById(R.id.taskLister);
        welcomeGreeting.setText("Welcome Back " + customName);
        myDB= new databaseHelper(this);
        List<String> all_tasks=myDB.getAllTasks();

        taskListerAdapter= new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,all_tasks);
        taskLister.setAdapter(taskListerAdapter);

        taskLister.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            taskChosen=all_tasks.get(position);
            String tasklength=myDB.getTaskLength(taskChosen);
            taskLengthDisplay.setText(tasklength);
            String breakLength= myDB.getBreakLength(taskChosen);
            breakLengthDisplay.setText(breakLength);
            IDchosen= myDB.getID(taskChosen);


            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
            if (all_tasks.isEmpty()){
                btnEditTask.setVisibility(View.GONE);
                btnStartTask.setVisibility(View.GONE);
            } else{
                btnEditTask.setVisibility(View.VISIBLE);
                btnStartTask.setVisibility(View.VISIBLE);
                userHint.setVisibility(View.GONE);
            }

        }



    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatemen

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        if (id == R.id.action_About){
            startActivity(new Intent(this, About.class));
            return true;

        }

        if (id == R.id.action_Legal_Notice){
            startActivity(new Intent(this, Legal_Notice.class));
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void launchcreateNewTask(View v){
        startActivity(new Intent(this, createNewTask.class));
    }

    public void launchEditTask(View v){
        Intent EditTask= new Intent(this,EditTask.class);

        EditTask.putExtra("key",IDchosen);
        startActivity(EditTask);


    }

    public void launchStartTask(View v){
        Intent StartTask= new Intent(this,StartTask.class);
        StartTask.putExtra("key",taskChosen);
        startActivity(StartTask);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onResume(){
        super.onResume();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

    }





}