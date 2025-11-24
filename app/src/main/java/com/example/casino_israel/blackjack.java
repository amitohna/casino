package com.example.casino_israel;

import android.content.Context;
import android.graphics.Canvas; // Import Canvas
import android.graphics.Paint;
import android.view.View;

public class blackjack extends View {
    public blackjack(Context context) {
        super(context);
    }

    @Override // It's good practice to use @Override for overridden methods
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint= new Paint();
        canvas.drawCircle(200,200,100,paint);
    }
}
