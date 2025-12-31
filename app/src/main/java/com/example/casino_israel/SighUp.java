package com.example.casino_israel;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SighUp extends Dialog {

    private Button btnYes;
    private Button btnNo;

    public SighUp(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_again_dialog);

        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start LoginResigter activity
                Intent intent = new Intent(getContext(), LoginRegister.class);
                getContext().startActivity(intent);
                dismiss(); // Close the dialog
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "No" button click (e.g., go to main menu, close app)
                Toast.makeText(getContext(), "okay bye", Toast.LENGTH_SHORT).show();
                dismiss(); // Close the dialog
            }
        });
    }
}
