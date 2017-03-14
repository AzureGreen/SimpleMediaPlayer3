package com.example.mediaplayer3;

import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        
        // ��ʱ��
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent =new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(mainIntent);
                WelcomeActivity.this.finish();
            }
        };
        // 2����Զ���ת��ҳ��
        timer.schedule(timerTask, 1500);
        
    }
}
