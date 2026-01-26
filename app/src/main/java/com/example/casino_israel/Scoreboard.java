package com.example.casino_israel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Scoreboard extends AppCompatActivity{

    private Button btnAddRecordToDB;
    private AdapterRecords adapter;
    public static ArrayList<players> records;

    FbModule fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        initialization();
        fb = new FbModule(this);
    }

    private void initialization() {
        // initialize



        RecyclerView recyclerView = findViewById(R.id.tvScore);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // to be vertical

        records = new ArrayList<>();
        adapter = new AdapterRecords(this, records);
        recyclerView.setAdapter(adapter);
    }

    public void dataChange() {
        // update the RecyclerView after change in the arraylist
        adapter.notifyDataSetChanged();
    }


}