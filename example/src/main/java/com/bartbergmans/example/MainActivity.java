package com.bartbergmans.example;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.gradient_1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked Button 1");
            }
        });

        final Button button1 = (Button) findViewById(R.id.button1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                button1.setEnabled(false);
            }
        },5000);

//
//        Button button2 = (Button) findViewById(R.id.button2);
//        button2.setClickable(false);
    }
}
