package com.example.casino_israel;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FbModule {
    private Context context;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    public FbModule(Context context) {
        this.context = context;


        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("play");

    }
}
