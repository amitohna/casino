package com.example.casino_israel;

import android.content.Context;
import android.graphics.Canvas; // Import Canvas
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;

public class blackjack extends View {
    public ArrayList<Integer> cards = new ArrayList<Integer>();

    public blackjack(Context context) {
        super(context);
        // Add integers 1 to 10 to the cards ArrayList
        for (int i = 1; i <= 10; i++) {
            cards.add(i);
        }
    }


    @Override // It's good practice to use @Override for overridden methods
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint= new Paint();
        canvas.drawCircle(200,200,100,paint);
    }
}
