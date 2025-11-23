package com.example.casino_israel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class BoardGame extends AppCompatActivity {

    private Button blackJackButton;
    private Button rouletteButton;
    public boolean game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_board_game);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        blackJackButton = findViewById(R.id.button_black_jack);
        rouletteButton = findViewById(R.id.button_roulette);

        blackJackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                game=true;
                Intent intent = new Intent(BoardGame.this, ActivityGames.class);
                intent.putExtra("game", game);
                startActivity(intent);
            }
        });

        rouletteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game=false;
                Intent intent = new Intent(BoardGame.this, ActivityGames.class);
                intent.putExtra("game", game);
                startActivity(intent);
            }
        });
    }
}