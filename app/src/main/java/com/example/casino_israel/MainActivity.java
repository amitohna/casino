package com.example.casino_israel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView; // Import TextView

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.casino_israel.FbModule; // Import FbModule

public class MainActivity extends AppCompatActivity {
    private Button btn;
    private Button btn2;
    private Button btnSignOut; // Declare logout button
    private FirebaseAuth mAuth;
    private TextView tvUserEmail; // Declare TextView
    private FbModule fbModule; // Declare FbModule instance

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

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize FbModule
        fbModule = new FbModule(this);

        // Initialize TextView and Buttons
        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnSignOut = findViewById(R.id.btnSignOut);

        // Check if a user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // If no user is logged in, show the SighUp dialog
            SighUp playAgainDialog = new SighUp(MainActivity.this);
            playAgainDialog.show();
            tvUserEmail.setVisibility(View.GONE);
            btnSignOut.setVisibility(View.GONE);
        } else {
            // If a user is logged in, display their email and show logout button
            tvUserEmail.setText("Logged in as: " + currentUser.getEmail());
            tvUserEmail.setVisibility(View.VISIBLE);
            btnSignOut.setVisibility(View.VISIBLE);
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
                    String userIdString = user.getUid();
                    // IMPORTANT: The 'id' parameter in FbModule.setDetails expects an int.
                    // Using hashCode() of the Firebase UID as a temporary solution.
                    // This is NOT guaranteed to be unique and can produce negative numbers.
                    // A robust solution would involve storing a unique integer ID for each user
                    // in your database, linked to their Firebase UID, and retrieving it here.
                    int userIdInt = userIdString.hashCode();

                    // IMPORTANT: 'walletamount' currently defaults to 1000.0.
                    // In a real application, this value should be fetched from the database
                    // (e.g., from the user's profile data) when the activity starts or the game begins.
                    double walletamount = 1000.0; // Placeholder for initial wallet amount
                    // TODO: 04/01/2026 there is massives bugs in the app because of this 
                    fbModule.setDetails(userIdInt, "blackjack", walletamount);
                }

                Intent intent = new Intent(MainActivity.this, BoardGame.class);
                startActivity(intent);
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
}