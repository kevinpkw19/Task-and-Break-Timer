package com.TTLabs.TaskBreakTimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView link = findViewById(R.id.textView4);
        link.setMovementMethod(LinkMovementMethod.getInstance());
    }
}


