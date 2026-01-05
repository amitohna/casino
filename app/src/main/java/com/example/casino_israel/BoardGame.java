package com.example.casino_israel;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Import TextView
import android.widget.Toast; // Import Toast

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth; // Import FirebaseAuth
import com.google.firebase.auth.FirebaseUser; // Import FirebaseUser
import com.google.firebase.database.DatabaseError; // Import DatabaseError

import java.text.DecimalFormat; // Import DecimalFormat

public class BoardGame extends AppCompatActivity {

    private Button blackJackButton;
    private Button rouletteButton;
    private TextView tvBoardGameWallet; // Declare TextView for wallet
    public boolean game;
    
    private FirebaseAuth mAuth; // Declare FirebaseAuth
    private FbModule fbModule; // Declare FbModule instance
    private String currentUserId; // To store the Firebase User ID
    private double currentWalletAmount = 0.0; // To store the fetched wallet amount

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

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize FbModule
        fbModule = new FbModule(this);

        // Get current user ID (assuming user is already logged in)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            // This case should ideally be handled by MainActivity before reaching BoardGame
            Toast.makeText(this, "User not logged in. Returning to login.", Toast.LENGTH_LONG).show();
            finish(); // Close activity
            return;
        }

        blackJackButton = findViewById(R.id.button_black_jack);
        rouletteButton = findViewById(R.id.button_roulette);
        tvBoardGameWallet = findViewById(R.id.tvBoardGameWallet); // Initialize tvBoardGameWallet

        blackJackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = true;
                Intent intent = new Intent(BoardGame.this, ActivityGames.class);
                intent.putExtra("game", game);
                intent.putExtra("walletAmount", currentWalletAmount); // Pass the updated wallet to ActivityGames
                startActivity(intent);
            }
        });

        rouletteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game = false;
                Intent intent = new Intent(BoardGame.this, ActivityGames.class);
                intent.putExtra("game", game);
                intent.putExtra("walletAmount", currentWalletAmount); // Pass the updated wallet to ActivityGames
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch and display wallet amount every time BoardGame activity resumes
        if (currentUserId != null) {
            fetchAndDisplayWallet(currentUserId);
        } else {
            // If for some reason currentUserId is null (e.g., user logged out after onCreate but before onResume)
            // Re-check Firebase user and potentially finish or redirect
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                currentUserId = currentUser.getUid();
                fetchAndDisplayWallet(currentUserId);
            } else {
                Toast.makeText(this, "User logged out. Returning to login.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void fetchAndDisplayWallet(String userId) {
        fbModule.getPlayerData(userId, new FbModule.PlayerDataCallback() {
            @Override
            public  void onPlayerDataFetched(players player) {
                if (player != null) {
                    currentWalletAmount = player.getWallet();
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    tvBoardGameWallet.setText("Wallet: $" + decimalFormat.format(currentWalletAmount));
                } else {
                    // This scenario should primarily be handled by MainActivity for new users.
                    // If it happens here, it means data was deleted or not created properly.
                    // For robustness, we can set a default and potentially re-create the record.
                    currentWalletAmount = 1000.0;
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    tvBoardGameWallet.setText("Wallet: $" + decimalFormat.format(currentWalletAmount));

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String userName = "";
                        String userEmail = currentUser.getEmail();
                        if (userEmail != null && userEmail.contains("@")) {
                            userName = userEmail.substring(0, userEmail.indexOf("@"));
                        } else if (userEmail != null) {
                            userName = userEmail;
                        } else {
                            userName = "GuestUser";
                        }
                        fbModule.setDetails(currentUser.getUid(), userName, currentWalletAmount);
                    }
                }
            }

            @Override
            public void onPlayerDataError(DatabaseError error) {
                Log.e("BoardGame", "Failed to load wallet: " + error.getMessage());
                Toast.makeText(BoardGame.this, "Error loading wallet.", Toast.LENGTH_SHORT).show();
                tvBoardGameWallet.setText("Wallet: Error"); // Display error state
            }
        });
    }
}