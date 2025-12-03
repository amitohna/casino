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
import android.widget.Toast;

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
    private int dealercardtotal=0;
    private ArrayList<CardCircle> circlePositions = new ArrayList<>();
    private ArrayList<CardCircle> dealerCirclePositions = new ArrayList<>();
    private boolean initialCardsDealt = false;
    private boolean playerTurnEnded = false;

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

        if (!initialCardsDealt && getWidth() > 0) {
            initialCardsDealt = true;
            // Draw two circles instantly for the player
            for (int i = 0; i < 2; i++) {
                int randomIndex = random.nextInt(cards.size());
                int currentCard = cards.get(randomIndex);
                cards.remove(randomIndex);

                float newCircleX = getWidth() * 0.16f + (circlePositions.size() * 190f);
                float newCircleY = getHeight() * 0.67f;

                circlePositions.add(new CardCircle(new PointF(newCircleX, newCircleY), currentCard));
                CardTotal += currentCard;
                count++;
            }

            // Draw one circle for the dealer
            int dealerRandomIndex = random.nextInt(cards.size());
            int dealerCurrentCard = cards.get(dealerRandomIndex);
            cards.remove(dealerRandomIndex);

            float dealerCircleX = getWidth() * 0.16f + (dealerCirclePositions.size() * 190f);
            float dealerCircleY = getHeight() * 0.25f;

            dealerCirclePositions.add(new CardCircle(new PointF(dealerCircleX, dealerCircleY), dealerCurrentCard));
            dealercardtotal += dealerCurrentCard;
        }

        Paint paint= new Paint();


        // Draw the background image, scaled to fit the canvas
        if (backgroundImage != null) {
            Rect destRect = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(backgroundImage, null, destRect, paint);
        }

        // Draw all the white circles and their numbers for the player
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

        // Draw all the white circles and their numbers for the dealer
        for (CardCircle cardCircle : dealerCirclePositions) {
            paint.setColor(Color.WHITE);
            canvas.drawCircle(cardCircle.position.x, cardCircle.position.y, 90, paint);

            paint.setColor(Color.BLACK);
            paint.setTextSize(50);
            paint.setTextAlign(Paint.Align.CENTER);
            float textY = cardCircle.position.y - ((paint.descent() + paint.ascent()) / 2);
            canvas.drawText(String.valueOf(cardCircle.cardNumber), cardCircle.position.x, textY, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            // Based on the image, defining the approximate area for the "HIT" button
            float hitAreaLeft = getWidth() * 0.25f;
            float hitAreaRight = getWidth() * 0.75f;
            float hitAreaTop = getHeight() * 0.80f;
            float hitAreaBottom = getHeight();

            // Check if the touch is inside the "HIT" button area and it's player's turn
            if (!playerTurnEnded && CardTotal < 21 && count < 5 &&
                    (touchX >= hitAreaLeft && touchX <= hitAreaRight && touchY >= hitAreaTop && touchY <= hitAreaBottom)) {
                
                int randomIndex = random.nextInt(cards.size());
                int currentCard = cards.get(randomIndex);
                cards.remove(randomIndex);

                float newCircleX = getWidth() * 0.16f + (circlePositions.size() * 190f);
                float newCircleY = getHeight() * 0.67f;

                circlePositions.add(new CardCircle(new PointF(newCircleX, newCircleY), currentCard));
                CardTotal += currentCard;
                count++;

                invalidate();
                return true;
            }

            // Area to click to stand and trigger dealer's turn
            float dealerAreaTop = 0;
            float dealerAreaBottom = getHeight() * 0.40f;

            if (touchY >= dealerAreaTop && touchY <= dealerAreaBottom && !playerTurnEnded) {
                playerTurnEnded = true;

                // Dealer's turn logic
                while (dealercardtotal < 17) {
                    int randomIndex = random.nextInt(cards.size());
                    int currentCard = cards.get(randomIndex);
                    cards.remove(randomIndex);

                    float newCircleX = getWidth() * 0.16f + (dealerCirclePositions.size() * 190f);
                    float newCircleY = getHeight() * 0.25f;

                    dealerCirclePositions.add(new CardCircle(new PointF(newCircleX, newCircleY), currentCard));
                    dealercardtotal += currentCard;
                }

                if(playerTurnEnded&&CardTotal>dealercardtotal)
                {
                    Toast.makeText(getContext(), "you won", Toast.LENGTH_SHORT).show();
                }
                else if(playerTurnEnded&&CardTotal<dealercardtotal)
                {
                    Toast.makeText(getContext(), "you lost", Toast.LENGTH_SHORT).show();
                }

                invalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
}