package com.example.casino_israel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class roulette extends View {
    private Bitmap backgroundImage;
    private int result;

    public roulette(Context context) {
        super(context);
        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.rollette);

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
