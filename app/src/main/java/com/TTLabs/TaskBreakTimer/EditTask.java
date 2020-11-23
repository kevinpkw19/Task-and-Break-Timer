package com.TTLabs.TaskBreakTimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class EditTask extends AppCompatActivity {
    databaseHelper myDB;
    String value="";
    TextView taskNamedisplay;
    TextView taskLengthdisplay;
    TextView breakLengthdisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        myDB= new databaseHelper(this);
        Bundle data = getIntent().getExtras();
        if (data != null) {
            value = data.getString("key");

        }
        taskNamedisplay=findViewById(R.id.taskNameEdit);
        taskLengthdisplay= findViewById(R.id.taskLengthEdit);
        breakLengthdisplay= findViewById(R.id.breakLengthEdit);
        String taskChosen= myDB.getTask(value);
        taskNamedisplay.setText(taskChosen);

    }


    public  void backToPrevTask(View v){
        finish();
    }

    public void updatedatabase(View v){

        try {
            String taskNameInput= taskNamedisplay.getText().toString();
            Boolean Check= myDB.checkTaskName(taskNameInput);
            String IDCheck= myDB.getID(taskNameInput);
            if (taskNameInput.equals("")){
                Toast.makeText(this, "Please Fill all fields", Toast.LENGTH_SHORT).show();
            } else if(Check==true && !IDCheck.equals(value)){
                Toast.makeText(this, "A Task with that name already exists. Please Choose another name.", Toast.LENGTH_SHORT).show();
            }
            else {
            int taskLengthinput= Integer.parseInt(taskLengthdisplay.getText().toString());
            int breakLengthinput=Integer.parseInt(breakLengthdisplay.getText().toString());

            boolean update= myDB.UpdateData(value, taskNameInput,taskLengthinput,breakLengthinput);

            if (update=true)
                Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show();
            finish();

            if (update=false)
                Toast.makeText(this, "Error. Send a bug report in the About page", Toast.LENGTH_SHORT).show();

        }}
        catch (Exception e){
            Toast.makeText(this, "Please Fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void deletedatabase(View v){
        myDB.DeleteData(value);
        Toast.makeText(this, "Task Deleted", Toast.LENGTH_SHORT).show();
        finish();
    }


}