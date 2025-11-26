package com.example.casino_israel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas; // Import Canvas
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Color; // Import Color
import android.view.MotionEvent; // Import MotionEvent
import android.view.View;
import java.util.Random;


import java.util.ArrayList;
import java.util.Random;

public class blackjack extends View {
    public ArrayList<Integer> cards = new ArrayList<Integer>();
    private Bitmap backgroundImage;
    private Random random = new Random(); // Add this line
    private int currentCard;
    private ArrayList<PointF> circlePositions = new ArrayList<>();

    public blackjack(Context context) {
        super(context);


        // Add integers 1 to 10 to the cards ArrayList
        for (int i = 1; i <= 10; i++) {
            cards.add(i);
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

        // Draw all the white circles
        paint.setColor(Color.WHITE);
        for (PointF position : circlePositions) {
            canvas.drawCircle(position.x, position.y, 90, paint); // Draw a circle with radius 30
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            // Based on the image, defining the approximate area for the "HIT" button
            float hitAreaLeft = getWidth() * 0.25f; // Made wider
            float hitAreaRight = getWidth() * 0.75f; // Made wider
            float hitAreaTop = getHeight() * 0.80f; // Made taller
            float hitAreaBottom = getHeight(); // Made taller, extends to the bottom

            // Check if the touch is inside the "HIT" button area
            if (touchX >= hitAreaLeft && touchX <= hitAreaRight && touchY >= hitAreaTop && touchY <= hitAreaBottom) {
                // Calculate the position for the new circle, next to the previous one
                float newCircleX = getWidth() * 0.26f + (circlePositions.size() * 190f); // 70 = diameter (60) + spacing (10)
                float newCircleY = getHeight() * 0.67f;
                
                circlePositions.add(new PointF(newCircleX, newCircleY));
                
                invalidate(); // Request a redraw to show the new circle

              /*  // Generate a new random card
                int randomIndex = random.nextInt(cards.size());
                currentCard = cards.get(randomIndex);
                cards.remove(randomIndex);*/
                // TODO: 26/11/2025 add the random value to each chip

            }
            return true; // Indicate that we've handled the event
        }
        return super.onTouchEvent(event);
    }
}