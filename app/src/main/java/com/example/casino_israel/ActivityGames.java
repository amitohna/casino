package com.example.casino_israel;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActivityGames extends AppCompatActivity {
private boolean check;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntent();
        check=getIntent().getBooleanExtra("game",false);
        // TODO: 23/11/2025 check for roulette and check if work (with israel) 
       if( check==true)
       {
           blackjack blackjack = new blackjack(this);

           setContentView(blackjack);
       }
       
       
    }
}