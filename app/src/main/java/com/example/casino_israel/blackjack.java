package com.example.casino_israel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas; // Import Canvas for drawing graphics
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Color; // Import Color for setting drawing colors
import android.os.Handler; // Import Handler for animation loop
import android.os.Message; // Import Message for Handler
import android.view.MotionEvent; // Import MotionEvent for handling touch input
import android.view.View;
import android.widget.Toast; // Import Toast for displaying brief messages

import androidx.annotation.NonNull; // For Handler.Callback

import java.util.Random;
import java.util.ArrayList;

// The main class for our Blackjack game, extending Android's View to allow custom drawing
public class blackjack extends View {

    // Inner class to hold information about each card circle displayed on the screen
    private class CardCircle {
        PointF position; // The x,y coordinates where the center of the card circle is drawn
        int cardNumber;   // The numerical value of the card

        // Constructor for CardCircle
        CardCircle(PointF position, int cardNumber) {
            this.position = position;
            this.cardNumber = cardNumber;
        }

        // Getter for the card number
        public int getCardNumber() {
            return cardNumber;
        }
    }

    // ArrayList to hold all possible card values (e.g., 2-10, J,Q,K as 10, A as 11)
    public ArrayList<Integer> cards = new ArrayList<Integer>();
    private Bitmap backgroundImage; // The image used for the game's background
    private Bitmap winnerChipBitmap; // The image for the falling winner chip
    private Random random = new Random(); // Random number generator for drawing cards
    private int count=0; // Tracks the number of cards the player has drawn
    private int CardTotal=0; // The sum of the player's card numbers
    private int dealercardtotal=0; // The sum of the dealer's card numbers
    private ArrayList<CardCircle> circlePositions = new ArrayList<>(); // List of card circles for the player
    private ArrayList<CardCircle> dealerCirclePositions = new ArrayList<>(); // List of card circles for the dealer
    private ArrayList<FallingCardChip> winnerChips = new ArrayList<>(); // List of falling chips for the winner animation
    private boolean initialCardsDealt = false; // Flag to ensure initial cards are dealt only once
    private boolean playerTurnEnded = false; // Flag to indicate if the player has finished their turn
    private boolean winner; // Flag to store if the player won or lost

    // Animation related fields (only for winner chip now)
    private Handler handler; // Handles messages from the animation thread to update UI
    private AnimationThread animationThread; // Thread for continuous animation updates
    private final int ANIMATION_INTERVAL = 50; // Milliseconds between animation updates
    private final float WINNER_CHIP_DROP_SPEED = 30f; // Pixels per animation frame for winner chip
    private Paint drawingPaint; // Reused Paint object for drawing performance
    private boolean side = true; // True for down, false for up


    // Constructor for the blackjack custom View
    public blackjack(Context context) {
        super(context);

        // Populate the 'cards' ArrayList with standard Blackjack card values
        for (int i = 0; i < 4; i++) { // Four suits
            cards.add(11); // Aces
            for (int j = 2; j <= 9; j++) {
                cards.add(j); // Cards 2 through 9
            }
        }
        for (int i = 0; i < 16; i++) { // Sixteen 10-value cards (10, J, Q, K) 
            cards.add(10);
        }

        // Load the background image from resources
        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.dealers2);
        // Load the winner chip image from resources (assuming R.drawable.chip exists)
        winnerChipBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.chip);

        // Initialize Paint object once for drawing
        drawingPaint = new Paint();

        // Initialize Handler for animation updates from the animation thread
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                boolean chipsStillAnimating = false;

                // Move winner chips if they are animating
                for (FallingCardChip chip : winnerChips) {
                    if (chip.isAnimating()) {
                        chip.move(side);
                        chipsStillAnimating = true;
                    }
                }

                // Request redraw only if any chip is still animating
                if (chipsStillAnimating) {
                    invalidate();
                }
                return true; // Message handled
            }
        });

        // Start the animation thread
        animationThread = new AnimationThread();
        animationThread.start();
    }

    // Private inner class for the animation thread
    private class AnimationThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(ANIMATION_INTERVAL); // Pause for a short duration
                    handler.sendEmptyMessage(0); // Tell the handler to update and redraw
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override // It's good practice to use @Override for overridden methods
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // This block ensures initial cards are dealt only after the view has been laid out (has width and height)
        if (!initialCardsDealt && getWidth() > 0) {
            initialCardsDealt = true;
            
            // Deal two initial cards for the player (no falling animation)
            for (int i = 0; i < 2; i++) {
                int randomIndex = random.nextInt(cards.size()); // Get a random index
                int currentCard = cards.get(randomIndex); // Get the card number
                cards.remove(randomIndex); // Remove the card from the deck

                // Calculate final position for player's new card circle
                float finalX = getWidth() * 0.16f + (circlePositions.size() * 190f);
                float finalY = getHeight() * 0.67f;

                // Add the new card to the player's hand (no animation)
                circlePositions.add(new CardCircle(new PointF(finalX, finalY), currentCard));
                CardTotal += currentCard; // Update player's total score
                count++; // Increment player's card count
            }

            // Deal one initial card for the dealer (no falling animation)
            int dealerRandomIndex = random.nextInt(cards.size());
            int dealerCurrentCard = cards.get(dealerRandomIndex);
            cards.remove(dealerRandomIndex);

            // Calculate final position for dealer's new card circle
            float dealerFinalX = getWidth() * 0.16f + (dealerCirclePositions.size() * 190f);
            float dealerFinalY = getHeight() * 0.25f;

            // Add the new card to the dealer's hand (no animation)
            dealerCirclePositions.add(new CardCircle(new PointF(dealerFinalX, dealerFinalY), dealerCurrentCard));
            dealercardtotal += dealerCurrentCard; // Update dealer's total score
        }

        // Draw the background image, scaled to fit the canvas
        if (backgroundImage != null) {
            Rect destRect = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(backgroundImage, null, destRect, drawingPaint);
        }

        // Draw all the white circles and their numbers for the player
        for (CardCircle cardCircle : circlePositions) {
            drawingPaint.setColor(Color.WHITE); // Set color to white for the circle
            canvas.drawCircle(cardCircle.position.x, cardCircle.position.y, 90, drawingPaint); // Draw a circle with radius 90

            // Draw the card number in the center of the circle
            drawingPaint.setColor(Color.BLACK); // Set color to black for the text
            drawingPaint.setTextSize(50); // Set text size
            drawingPaint.setTextAlign(Paint.Align.CENTER); // Center the text horizontally
            // Adjust Y position to center the text vertically
            float textY = cardCircle.position.y - ((drawingPaint.descent() + drawingPaint.ascent()) / 2);
            canvas.drawText(String.valueOf(cardCircle.cardNumber), cardCircle.position.x, textY, drawingPaint);
        }

        // Draw all the black circles and their numbers for the dealer
        for (CardCircle cardCircle : dealerCirclePositions) {
            drawingPaint.setColor(Color.BLACK);
            canvas.drawCircle(cardCircle.position.x, cardCircle.position.y, 90, drawingPaint);

            drawingPaint.setColor(Color.WHITE);
            drawingPaint.setTextSize(50);
            drawingPaint.setTextAlign(Paint.Align.CENTER);
            float textY = cardCircle.position.y - ((drawingPaint.descent() + drawingPaint.ascent()) / 2);
            canvas.drawText(String.valueOf(cardCircle.cardNumber), cardCircle.position.x, textY, drawingPaint);
        }

        // Draw all the falling winner chips
        for (FallingCardChip chip : winnerChips) {
            chip.draw(canvas, drawingPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Only respond to a touch down event
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX(); // Get X coordinate of touch
            float touchY = event.getY(); // Get Y coordinate of touch

            // Area for the "BLACKJACK" title to go back
            float titleAreaTop = 0;
            float titleAreaBottom = getHeight() * 0.15f;
            float titleAreaLeft = getWidth() * 0.20f;
            float titleAreaRight = getWidth() * 0.80f;

            if (touchY >= titleAreaTop && touchY <= titleAreaBottom &&
                    touchX >= titleAreaLeft && touchX <= titleAreaRight) {
                Context context = getContext();
                if (context instanceof Activity) {
                    ((Activity) context).finish(); // Close the current activity
                } 
                return true;
            }

            // Define the approximate area for the "HIT" button on the screen
            float hitAreaLeft = getWidth() * 0.25f;
            float hitAreaRight = getWidth() * 0.75f;
            float hitAreaTop = getHeight() * 0.80f;
            float hitAreaBottom = getHeight();

            // Check if the touch is inside the "HIT" button area AND it's player's turn, and conditions allow hitting
            if (!playerTurnEnded && CardTotal < 21 && count < 5 &&
                    (touchX >= hitAreaLeft && touchX <= hitAreaRight && touchY >= hitAreaTop && touchY <= hitAreaBottom)) {
                
                int randomIndex = random.nextInt(cards.size()); // Get a random card index
                int currentCard = cards.get(randomIndex); // Get the card number
                cards.remove(randomIndex); // Remove the card from the deck

                // Calculate final position for player's new card circle
                float finalX = getWidth() * 0.16f + (circlePositions.size() * 190f);
                float finalY = getHeight() * 0.67f;

                // Add the new card to the player's hand (no animation)
                circlePositions.add(new CardCircle(new PointF(finalX, finalY), currentCard));
                CardTotal += currentCard; // Update player's total
                count++; // Increment player's card count

                invalidate(); // Request a redraw to show the new circle
                return true; // Indicate that we've handled the event
            }

            // Define the area to click to stand and trigger the dealer's turn
            float dealerAreaTop = getHeight() * 0.15f; // Start below the title area
            float dealerAreaBottom = getHeight() * 0.40f; // Top 40% of the screen

            // Check if the touch is within the dealer's area AND it's still the player's turn
            if (touchY >= dealerAreaTop && touchY <= dealerAreaBottom && !playerTurnEnded) {
                playerTurnEnded = true; // End the player's turn

                // Dealer's turn logic: draw cards until total is 17 or more
                while (dealercardtotal < 17) {
                    int randomIndex = random.nextInt(cards.size());
                    int dealerCurrentCard = cards.get(randomIndex);
                    cards.remove(randomIndex);

                    // Calculate final position for dealer's new card circle
                    float dealerFinalX = getWidth() * 0.16f + (dealerCirclePositions.size() * 190f);
                    float dealerFinalY = getHeight() * 0.25f;

                    // Add the new card to the dealer's hand (no animation)
                    dealerCirclePositions.add(new CardCircle(new PointF(dealerFinalX, dealerFinalY), dealerCurrentCard));
                    dealercardtotal += dealerCurrentCard;
                }

                // Determine winner and display Toast message
                if(playerTurnEnded && CardTotal <= 21 && (CardTotal > dealercardtotal || dealercardtotal > 21)) {
                    Toast.makeText(getContext(), "you won", Toast.LENGTH_LONG).show();
                    winner=true;

                    // Trigger winner chip animation
                    float centerX = getWidth() / 2f;
                    float centerY = getHeight()*1.15f; // Target for downward animation
                    winnerChips.add(new FallingCardChip(winnerChipBitmap, new PointF(centerX, 0f), 0, WINNER_CHIP_DROP_SPEED, centerY));

                } else if (playerTurnEnded && (CardTotal < dealercardtotal || CardTotal > 21)) {
                    Toast.makeText(getContext(), "you lost", Toast.LENGTH_LONG).show();
                    winner=false;
                    side=false;
                    float centerX = getWidth() / 2f;
                    float centerY = 0f; // Target for upward animation (top of the screen)
                    winnerChips.add(new FallingCardChip(winnerChipBitmap, new PointF(centerX, getHeight()), 0, WINNER_CHIP_DROP_SPEED, centerY));


                } else if (CardTotal == dealercardtotal && playerTurnEnded && CardTotal <= 21 && dealercardtotal <= 21) {
                    Toast.makeText(getContext(), "tie", Toast.LENGTH_LONG).show();
                }

                invalidate(); // Request a redraw to show the dealer's new cards and winner chip if any
                return true; // Indicate that we've handled the event
            }
        }
        return super.onTouchEvent(event); // Let the superclass handle other touch events
    }
}
