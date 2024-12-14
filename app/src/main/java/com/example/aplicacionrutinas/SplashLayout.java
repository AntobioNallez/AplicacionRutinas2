package com.example.aplicacionrutinas;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashLayout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_layout);
        //getSupportActionBar().hide(); Falla no se porque

        ImageView imageView = findViewById(R.id.logoImg);

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360f);
        objectAnimator.setDuration(2000);
        objectAnimator.setRepeatCount(0);
        objectAnimator.start();

        final Intent i = new Intent(SplashLayout.this, MainActivity.class);
        new Handler().postDelayed(() -> {
            startActivity(i);
            finish();
        }, 2500);
    }
}