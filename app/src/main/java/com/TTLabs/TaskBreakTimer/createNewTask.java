package com.TTLabs.TaskBreakTimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class createNewTask extends AppCompatActivity {
    databaseHelper myDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task);
        myDB= new databaseHelper(this);


    }

    public  void backToPrevTask(View v){
        finish();
    }



    public void saveTask(View v){
        TextView taskName= findViewById(R.id.taskNameInput);
        TextView taskLength= findViewById(R.id.taskLengthInput);
        TextView breakLength= findViewById(R.id.breakLengthInput);

        try {
            String taskNameInput= taskName.getText().toString();
            Boolean Check= myDB.checkTaskName(taskNameInput);
            if (taskNameInput.equals("")){
                Toast.makeText(this, "Please Fill all fields", Toast.LENGTH_SHORT).show();
            } else if(Check==true) {
                Toast.makeText(this, "A Task with that name already exists. Please Choose another name.", Toast.LENGTH_SHORT).show();
            }
            else{
            int taskLengthinput= Integer.parseInt(taskLength.getText().toString());
            int breakLengthinput=Integer.parseInt(breakLength.getText().toString());

            boolean isInserted= myDB.insertData(taskNameInput,taskLengthinput,breakLengthinput);
            if (isInserted=true)
                Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();
                finish();

            if (isInserted=false)
                Toast.makeText(this, "Error. Send a bug report in the About page", Toast.LENGTH_SHORT).show();

        }}
        catch (Exception e){
            Toast.makeText(this, "Please Fill all fields", Toast.LENGTH_SHORT).show();

        }
    }
}