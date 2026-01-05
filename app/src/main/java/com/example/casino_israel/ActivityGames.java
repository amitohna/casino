package com.example.casino_israel;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;

public class ActivityGames extends AppCompatActivity implements blackjack.GameUpdateListener {
    private boolean check;
    private String currentUserId; // To store the Firebase User ID
    private double currentWalletAmount; // To store the current wallet amount
    private FbModule fbModule; // FbModule instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize FbModule
        fbModule = new FbModule(this);

        // Get data from Intent (from BoardGame)
        currentWalletAmount = getIntent().getDoubleExtra("walletAmount", 0.0);
        check = getIntent().getBooleanExtra("game", false);

        // Get current user ID
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            // Handle case where user is not logged in (should not happen if MainActivity enforces login)
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no user
            return;
        }

        if (check == true) {
            // Pass userId, walletAmount, and GameUpdateListener to blackjack
            blackjack blackjackView = new blackjack(this, currentUserId, currentWalletAmount, this);
            setContentView(blackjackView);
        }
        if (check == false) {
            // Pass userId, walletAmount, and GameUpdateListener to roulette (future)
            roulette rouletteView = new roulette(this);
            setContentView(rouletteView);
        }

        // Removed the problematic ViewCompat.setOnApplyWindowInsetsListener block
        // as findViewById(R.id.main) would return null when custom views are set directly.
    }

    @Override
    public void onWalletUpdated(double newWalletAmount) {
        this.currentWalletAmount = newWalletAmount; // Update local wallet amount
        // Update wallet in Firebase
        if (currentUserId != null) {
            // Fetch player's existing name to pass to setDetails
            fbModule.getPlayerData(currentUserId, new FbModule.PlayerDataCallback() {
                @Override
                public void onPlayerDataFetched(players player) {
                    String userName = "";
                    if (player != null) {
                        userName = player.getName(); // Get existing name
                    } else {
                        // Fallback: If player data not found (shouldn't happen if user is logged in),
                        // use email part for name (similar logic as MainActivity)
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null && user.getEmail() != null) {
                            String userEmail = user.getEmail();
                            if (userEmail.contains("@")) {
                                userName = userEmail.substring(0, userEmail.indexOf("@"));
                            } else {
                                userName = userEmail;
                            }
                        }
                    }
                    // Now update Firebase with the new wallet amount and existing name
                    fbModule.setDetails(currentUserId, userName, newWalletAmount);
                    Log.d("ActivityGames", "Wallet updated in Firebase: " + newWalletAmount);
                }

                @Override
                public void onPlayerDataError(DatabaseError error) {
                    Log.e("ActivityGames", "Failed to get player data for wallet update: " + error.getMessage());
                    Toast.makeText(ActivityGames.this, "Error updating wallet in Firebase.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}