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
        database = FirebaseDatabase.getInstance("https://casino-finalproject-default-rtdb.firebaseio.com/");
        database = FirebaseDatabase.getInstance();
        this.context = context;
        this.myRecords = new ArrayList<>(); // Initialize myRecords

        // read the records from the Firebase and order them by the record from highest to lowest
        // limit to only 8 items
        Query myQuery = database.getReference("records").orderByChild("score").limitToLast(8);
        myQuery.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              /*  myRecords.clear();  // clear the array list
                for(DataSnapshot userSnapshot : snapshot.getChildren())
                {
                    players currentMyRecord =userSnapshot.getValue(players.class);
                    myRecords.add(0, currentMyRecord);
                }*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setDetails(String id, String name, double wallet)
    {
        // Write a message to the database
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
                    players player = snapshot.getValue(players.class);
                    callback.onPlayerDataFetched(player);
                } else {
                    callback.onPlayerDataFetched(null); // Player not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onPlayerDataError(error);
            }
        });
    }
}