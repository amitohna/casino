package com.example.casino_israel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas; // Import Canvas
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;

public class blackjack extends View {
    public ArrayList<Integer> cards = new ArrayList<Integer>();
    private Bitmap backgroundImage;

    public blackjack(Context context) {
        super(context);
        // Add integers 1 to 10 to the cards ArrayList

        for (int i = 0; i < 4; i++) {
            cards.add(11);
            for (int j = 1; j <= 9; j++) {
                cards.add(i);
            }
        }
        for (int i = 0; i < 16; i++) {
            cards.add(10);
        }
        // Load the background image
        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.dealers);
    }


    @Override // It's good practice to use @Override for overridden methods
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint= new Paint();
        
        // Draw the background image, scaled to fit the canvas
        if (backgroundImage != null) {
            Rect destRect = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(backgroundImage, null, destRect, paint);
        }

        // Original drawing code

    }
}