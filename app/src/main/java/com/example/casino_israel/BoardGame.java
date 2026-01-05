package com.example.casino_israel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Import TextView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat; // Import DecimalFormat

public class BoardGame extends AppCompatActivity {

    private Button blackJackButton;
    private Button rouletteButton;
    private TextView tvBoardGameWallet; // Declare TextView for wallet
    public boolean game;
    private double walletAmount;

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

        // Get wallet amount from Intent
        Intent intent = getIntent();
        walletAmount = intent.getDoubleExtra("walletAmount", 0.0); // Default to 0.0 if not found

        blackJackButton = findViewById(R.id.button_black_jack);
        rouletteButton = findViewById(R.id.button_roulette);
        tvBoardGameWallet = findViewById(R.id.tvBoardGameWallet); // Initialize tvBoardGameWallet

        // Display wallet amount
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        tvBoardGameWallet.setText("Wallet: $" + decimalFormat.format(walletAmount));

        blackJackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                game=true;
                Intent intent = new Intent(BoardGame.this, ActivityGames.class);
                intent.putExtra("game", game);
                intent.putExtra("walletAmount", walletAmount); // Pass wallet to ActivityGames
                startActivity(intent);
            }
        });

        rouletteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game=false;
                Intent intent = new Intent(BoardGame.this, ActivityGames.class);
                intent.putExtra("game", game);
                intent.putExtra("walletAmount", walletAmount); // Pass wallet to ActivityGames
                startActivity(intent);
            }
        });
    }
}