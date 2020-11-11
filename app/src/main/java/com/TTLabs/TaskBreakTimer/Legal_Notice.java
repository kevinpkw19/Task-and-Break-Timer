package com.TTLabs.TaskBreakTimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class Legal_Notice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_legal_notice);

        TextView link = findViewById(R.id.textView9);
        link.setMovementMethod(LinkMovementMethod.getInstance());
        TextView link2 = findViewById(R.id.textView6);
        link2.setMovementMethod(LinkMovementMethod.getInstance());
        TextView link3 = findViewById(R.id.textView7);
        link3.setMovementMethod(LinkMovementMethod.getInstance());
        TextView link4= findViewById(R.id.textView10);
        link4.setMovementMethod(LinkMovementMethod.getInstance());
        TextView link5= findViewById(R.id.textView11);
        link5.setMovementMethod(LinkMovementMethod.getInstance());

    }

}