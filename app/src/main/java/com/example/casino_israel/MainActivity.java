package com.example.casino_israel;

import android.content.Intent;
import android.media.MediaPlayer; // Import MediaPlayer
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Import TextView
import android.widget.Toast; // Import Toast

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError; // Import DatabaseError
import com.example.casino_israel.FbModule; // Import FbModule

import java.text.DecimalFormat; // Import DecimalFormat

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private Button btn2;
    private Button btnSignOut; // Declare logout button
    private FirebaseAuth mAuth;
    private TextView tvUserEmail; // Declare TextView
    private TextView tvWalletAmount; // Declare TextView for wallet
    private FbModule fbModule; // Declare FbModule instance
    private double currentWalletAmount = 0.0; // To store the fetched wallet amount
    private MediaPlayer mediaPlayer; // Declare MediaPlayer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.gamble); // Now using R.drawable.gamble
        if (mediaPlayer != null) {

            mediaPlayer.start(); // Start playing the music
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize FbModule
        fbModule = new FbModule(this);

        // Initialize TextViews and Buttons
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvWalletAmount = findViewById(R.id.tvWalletAmount); // Initialize tvWalletAmount
        btnSignOut = findViewById(R.id.btnSignOut);

        // Check if a user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If no user is logged in, show the SighUp dialog
            SighUp playAgainDialog = new SighUp(MainActivity.this);
            playAgainDialog.show();
            tvUserEmail.setVisibility(View.GONE);
            tvWalletAmount.setVisibility(View.GONE); // Hide wallet info
            btnSignOut.setVisibility(View.GONE);
        } else {
            // If a user is logged in, display their email and show logout button
            tvUserEmail.setText("Logged in as: " + currentUser.getEmail());
            tvUserEmail.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.VISIBLE);

            // Fetch and display wallet amount
            fetchAndDisplayWallet(currentUser.getUid());
        }

        btn = findViewById(R.id.button);
        btn2=findViewById(R.id.score_board_button);
        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Scoreboard.class);
                startActivity(intent);
            }
        });
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid(); // User UID for id
                    String userName = ""; // Default name
                    String userEmail = user.getEmail();

                    if (userEmail != null && userEmail.contains("@")) {
                        userName = userEmail.substring(0, userEmail.indexOf("@"));
                    } else if (userEmail != null) {
                        // If email exists but doesn't contain '@', use the full email as name
                        userName = userEmail;
                    } else {
                        // Fallback if email is null, use a generic name or UID
                        userName = "GuestUser"; // Or use userId, but email is preferred for name
                    }

                    // Use the currentWalletAmount fetched or a default if not fetched yet
                    // For the first time user, default to 1000.0
                    double walletToSave = currentWalletAmount > 0 ? currentWalletAmount : 1000.0;

                    // Call setDetails with correct types and values
                    fbModule.setDetails(userId, userName, walletToSave);

                    Intent intent = new Intent(MainActivity.this, BoardGame.class);
                    intent.putExtra("walletAmount", walletToSave); // Pass wallet amount to BoardGame
                    startActivity(intent);
                } else {
                    // If user is not logged in, prevent starting BoardGame or prompt to login
                    Toast.makeText(MainActivity.this, "Please log in to play.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                // Redirect to LoginRegister activity after logout
                Intent intent = new Intent(MainActivity.this, LoginRegister.class);
                startActivity(intent);
                finish(); // Close MainActivity so user cannot go back to it with back button
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Re-fetch and display wallet amount when the activity resumes
            fetchAndDisplayWallet(currentUser.getUid());
        }

        // Resume music if it was paused
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause music when the activity is paused
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release MediaPlayer resources when the activity is destroyed
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void fetchAndDisplayWallet(String userId) {
        fbModule.getPlayerData(userId, new FbModule.PlayerDataCallback() {
            @Override
            public void onPlayerDataFetched(players player) {
                if (player != null) {
                    currentWalletAmount = player.getWallet();
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    tvWalletAmount.setText("Wallet: $" + decimalFormat.format(currentWalletAmount));
                    tvWalletAmount.setVisibility(View.VISIBLE);
                } else {
                    // Player data not found, possibly a new user, set default wallet
                    currentWalletAmount = 1000.0;
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    tvWalletAmount.setText("Wallet: $" + decimalFormat.format(currentWalletAmount));
                    tvWalletAmount.setVisibility(View.VISIBLE);

                    // Also, create a new record in Firebase for this user with default wallet
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String userName = "";
                        String userEmail = currentUser.getEmail();
                        if (userEmail != null && userEmail.contains("@")) {
                            userName = userEmail.substring(0, userEmail.indexOf("@"));
                        } else if (userEmail != null) {
                            userName = userEmail;
                        }
                        fbModule.setDetails(currentUser.getUid(), userName, currentWalletAmount);
                    }
                }
            }

            @Override
            public void onPlayerDataError(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Failed to load wallet: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                tvWalletAmount.setVisibility(View.GONE); // Hide wallet on error
            }
        });
    }
}