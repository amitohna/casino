package com.example.casino_israel;

import android.app.Notification;
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

    // Inner class to hold information about each card circle
    private class CardCircle {
        PointF position;
        int cardNumber;

        CardCircle(PointF position, int cardNumber) {
            this.position = position;
            this.cardNumber = cardNumber;
        }
        public int getCardNumber() {
            return cardNumber;
        }
    }

    public ArrayList<Integer> cards = new ArrayList<Integer>();
    private Bitmap backgroundImage;
    private Random random = new Random(); // Add this line
    private int count=0;
    private int CardTotal=0;
    private ArrayList<CardCircle> circlePositions = new ArrayList<>();

    public blackjack(Context context) {
        super(context);


        // Add integers 1 to 10 to the cards ArrayList
        for (int i = 0; i < 4; i++) {
            cards.add(11);
            for (int j = 2; j <= 9; j++) {
                cards.add(j);
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

        // Draw all the white circles and their numbers
        for (CardCircle cardCircle : circlePositions) {
            paint.setColor(Color.WHITE);
            canvas.drawCircle(cardCircle.position.x, cardCircle.position.y, 90, paint); // Draw a circle with radius 90

            // Draw the card number in the center of the circle
            paint.setColor(Color.BLACK);
            paint.setTextSize(50); // Set text size
            paint.setTextAlign(Paint.Align.CENTER);
            // Adjust Y position to center the text vertically
            float textY = cardCircle.position.y - ((paint.descent() + paint.ascent()) / 2);
            canvas.drawText(String.valueOf(cardCircle.cardNumber), cardCircle.position.x, textY, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN&&count<5&&CardTotal<21) {
            float touchX = event.getX();
            float touchY = event.getY();

            // Based on the image, defining the approximate area for the "HIT" button
            float hitAreaLeft = getWidth() * 0.25f; // Made wider
            float hitAreaRight = getWidth() * 0.75f; // Made wider
            float hitAreaTop = getHeight() * 0.80f; // Made taller
            float hitAreaBottom = getHeight(); // Made taller, extends to the bottom

            // Check if the touch is inside the "HIT" button area
            if (touchX >= hitAreaLeft && touchX <= hitAreaRight && touchY >= hitAreaTop && touchY <= hitAreaBottom) {
                // Generate a new random card
                int randomIndex = random.nextInt(cards.size()); // Get a random index from the cards list
                int currentCard = cards.get(randomIndex); // Get the card number at the random index
                cards.remove(randomIndex);

                // Calculate the position for the new circle, next to the previous one
                float newCircleX = getWidth() * 0.16f + (circlePositions.size() * 190f); // this is the x position of the circle we start from 0.16% of the screen and every time we add a circle we add 190f to the x position
                float newCircleY = getHeight() * 0.67f; // this is the y position of the circle we start from 0.67% of the screen

                circlePositions.add(new CardCircle(new PointF(newCircleX, newCircleY), currentCard));
                  CardTotal=CardTotal+currentCard;

                invalidate(); // Request a redraw to show the new circle
                count++;
            }
            return true; // Indicate that we've handled the event
        }
        return super.onTouchEvent(event);
    }
}