package com.example.casino_israel;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FbModule {

    FirebaseDatabase database;
    Context context;
    ArrayList<players> myRecords;

    // Interface for asynchronous callback when player data is fetched
    public interface PlayerDataCallback {
        void onPlayerDataFetched(players player);
        void onPlayerDataError(DatabaseError error);
    }

    public FbModule(Context context) {
        // Use the default instance. The URL is usually configured in google-services.json
        database = FirebaseDatabase.getInstance();
        this.context = context;

        // Read records from Firebase, order them by 'wallet' from highest to lowest,
        // and limit to the last 8 items. Using 'wallet' as per your players class.
        Query myQuery = database.getReference("records").orderByChild("wallet").limitToLast(8);
        myQuery.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // FIX: Check if Scoreboard.records is initialized to prevent NullPointerException.
                // It's only guaranteed to be initialized when Scoreboard activity is active.
                if (Scoreboard.records != null) {
                    Scoreboard.records.clear();  // Clear the array list for new data
                    for(DataSnapshot userSnapshot : snapshot.getChildren())
                    {
                         players currentMyRecord = userSnapshot.getValue(players.class);
                        // Add a null check for currentMyRecord in case of data inconsistencies
                        if (currentMyRecord != null) {
                            // Add to the beginning of the list to get highest scores first
                            Scoreboard.records.add(0, currentMyRecord);
                        }
                    }

                    // If the current context is a Scoreboard activity, notify it to refresh its UI.
                    if (context instanceof Scoreboard) {
                        ((Scoreboard) context).dataChange(); // Assuming Scoreboard has a public dataChange method
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors during data fetching
                // You might want to log this error or show a Toast.
            }
        });

    }

    public void setDetails(String id, String name, double wallet)
    {
        // Write a message to the database for a specific user ID
        DatabaseReference myRef = database.getReference("records/" + id);
        players rec = new players(id, name, wallet);
        myRef.setValue(rec);
    }

    public void getPlayerData(String userId, final PlayerDataCallback callback) {
        DatabaseReference playerRef = database.getReference("records/").child(userId);
        playerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // If player data exists, convert it to a 'players' object
                    players player = snapshot.getValue(players.class);
                    callback.onPlayerDataFetched(player);
                } else {
                    // If no data found for the player, return null
                    callback.onPlayerDataFetched(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors during data fetching
                callback.onPlayerDataError(error);
            }
        });
    }
}