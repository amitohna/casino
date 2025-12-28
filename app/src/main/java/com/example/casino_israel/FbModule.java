package com.example.casino_israel;

import android.content.Context;

import androidx.annotation.NonNull;

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
    public FbModule(Context context) {
        //database = FirebaseDatabase.getInstance("https://fbrecordst-default-rtdb.firebaseio.com");
        database = FirebaseDatabase.getInstance();
        this.context = context;
        this.myRecords = myRecords;

        // read the records from the Firebase and order them by the record from highest to lowest
        // limit to only 8 items
        Query myQuery = database.getReference("records").orderByChild("score").limitToLast(8);
        myQuery.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myRecords.clear();  // clear the array list
                for(DataSnapshot userSnapshot : snapshot.getChildren())
                {
                    players currentMyRecord =userSnapshot.getValue(players.class);
                    myRecords.add(0, currentMyRecord);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void setDetails(int id, String type, double wallet)
    {
        // Write a message to the database
        //DatabaseReference myRef = database.getReference("records").push(); // push adds new node with unique value

      //  DatabaseReference myRef = database.getReference("records/" + FirebaseAuth.getInstance().getUid());

        players rec = new players(id, type, (int)wallet);
       // myRef.setValue(rec);
    }
}
