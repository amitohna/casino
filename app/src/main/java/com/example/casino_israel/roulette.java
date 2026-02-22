package com.example.casino_israel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Custom View class for the Roulette game.
 * Handles drawing the table, wheel animation, betting logic, and result processing.
 */
public class roulette extends View {
    // Visual assets
    private Bitmap backgroundImage;
    private Bitmap rouletteWheelImage;
    private Bitmap chipImage;

    // Game state variables
    private float currentWheelRotation = 0;
    private boolean isSpinning = false;
    private Handler handler = new Handler();
    private Random random = new Random();
    private int resulet; // Stores the winning number (0-37, where 37 is 00)

    // Layout and betting structures
    private Rect wheelDestRect; // Area where the spinning wheel is drawn
    private List<PlacedBet> placedBets = new ArrayList<>(); // Stores currently active bets on the table
    private List<BetArea> betAreas = new ArrayList<>(); // Definitions of all clickable boxes on the table
    private String userId;
    private double walletAmount;
    private blackjack.GameUpdateListener gameUpdateListener;
    private Paint textPaint; // Paint for wallet text

    // Set of red numbers for quick win/color lookup
    private static final Set<Integer> RED_NUMBERS = new HashSet<>(Arrays.asList(
            1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36
    ));
    private boolean lost=false;
    private int count=0;

    /**
     * Inner class representing a clickable betting area on the table.
     */
    private static class BetArea {
        RectF bounds; // Physical location on screen
        String name;   // Identifier for logic (e.g., "RED", "1st 12", "14")

        BetArea(RectF bounds, String name) {
            this.bounds = bounds;
            this.name = name;
        }
    }

    /**
     * Inner class representing a chip placed by the player.
     */
    private static class PlacedBet {
        PointF position; // Coordinates for drawing the chip
        BetArea area;    // The specific area the bet belongs to

        PlacedBet(PointF position, BetArea area) {
            this.position = position;
            this.area = area;
        }
    }

    // Constructor: Load images and initialize state
    public roulette(Context context, String userId, double initialWalletAmount, blackjack.GameUpdateListener listener) {
        super(context);
        this.userId = userId;
        this.walletAmount = initialWalletAmount;
        this.gameUpdateListener = listener;
        
        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.roulletes);
        rouletteWheelImage = BitmapFactory.decodeResource(getResources(), R.drawable.rol90);
        chipImage = BitmapFactory.decodeResource(getResources(), R.drawable.chip);
        resulet = random.nextInt(38);

        // Initialize Paint for drawing text
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
    }

    // Called when the view changes size (app start or rotation)
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Calculate the wheel's position based on screen dimensions
        if (rouletteWheelImage != null) {
            int wheelSize = (int) (Math.min(getWidth(), getHeight()) * 0.6);
            int left = ((getWidth() - wheelSize / 2) / 2);
            int top = (int) (getHeight() * 0.01);
            int right = left + wheelSize;
            int bottom = top + wheelSize;
            wheelDestRect = new Rect(left, top, right, bottom);
        }
        // Initialize the hit-boxes for the betting table
        setupBetAreas();
    }

    /**
     * Mathematically defines the layout of the betting grid.
     * Uses percentages of width/height to ensure consistency across devices.
     */
    private void setupBetAreas() {
        betAreas.clear();
        float w = getWidth();
        float h = getHeight();

        // Main grid coordinates (Numbers 1-36)
        float gridTop = h * 0.41f;
        float gridBottom = h * 0.915f;
        float gridLeft = w * 0.54f;
        float gridRight = w * 0.93f;
        
        float cellWidth = (gridRight - gridLeft) / 3f;
        float cellHeight = (gridBottom - gridTop) / 12f;

        // Header bets: 0 and 00
        float headerTop = h * 0.36f;
        betAreas.add(new BetArea(new RectF(gridLeft, headerTop, gridLeft + cellWidth * 1.5f, gridTop), "0"));
        betAreas.add(new BetArea(new RectF(gridLeft + cellWidth * 1.5f, headerTop, gridRight, gridTop), "00"));

        // Loop to create hit-boxes for numbers 1 to 36
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 3; col++) {
                int number = row * 3 + (col + 1);
                float left = gridLeft + col * cellWidth;
                float top = gridTop + row * cellHeight;
                betAreas.add(new BetArea(new RectF(left, top, left + cellWidth, top + cellHeight), String.valueOf(number)));
            }
        }

        // Dozen ranges (1st 12, 2nd 12, 3rd 12)
        float dozenLeft = w * 0.42f;
        float dozenRight = gridLeft;
        betAreas.add(new BetArea(new RectF(dozenLeft, gridTop, dozenRight, gridTop + 4 * cellHeight), "1st 12"));
        betAreas.add(new BetArea(new RectF(dozenLeft, gridTop + 4 * cellHeight, dozenRight, gridTop + 8 * cellHeight), "2nd 12"));
        betAreas.add(new BetArea(new RectF(dozenLeft, gridTop + 8 * cellHeight, dozenRight, gridBottom), "3rd 12"));

        // Far-left outside bets (Even/Odd, Red/Black, High/Low)
        float farLeft = w * 0.32f;
        float farRight = dozenLeft;
        betAreas.add(new BetArea(new RectF(farLeft, gridTop, farRight, gridTop + 2 * cellHeight), "1-18"));
        betAreas.add(new BetArea(new RectF(farLeft, gridTop + 2 * cellHeight, farRight, gridTop + 4 * cellHeight), "EVEN"));
        betAreas.add(new BetArea(new RectF(farLeft, gridTop + 4 * cellHeight, farRight, gridTop + 6 * cellHeight), "RED"));
        betAreas.add(new BetArea(new RectF(farLeft, gridTop + 6 * cellHeight, farRight, gridTop + 8 * cellHeight), "BLACK"));
        betAreas.add(new BetArea(new RectF(farLeft, gridTop + 8 * cellHeight, farRight, gridTop + 10 * cellHeight), "ODD"));
        betAreas.add(new BetArea(new RectF(farLeft, gridTop + 10 * cellHeight, farRight, gridBottom), "19-36"));

        // Column bets at the bottom of the table
        float footerBottom = h * 0.96f;
        betAreas.add(new BetArea(new RectF(gridLeft, gridBottom, gridLeft + cellWidth, footerBottom), "Col 1"));
        betAreas.add(new BetArea(new RectF(gridLeft + cellWidth, gridBottom, gridLeft + 2 * cellWidth, footerBottom), "Col 2"));
        betAreas.add(new BetArea(new RectF(gridLeft + 2 * cellWidth, gridBottom, gridRight, footerBottom), "Col 3"));
    }

    // Rendering method: Logic for drawing everything on screen
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        // 1. Draw static background table
        if (backgroundImage != null) {
            Rect destRect = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(backgroundImage, null, destRect, paint);
        }

        // 2. Draw animated roulette wheel
        if (rouletteWheelImage != null && wheelDestRect != null) {
            canvas.save(); // Isolate rotation transformations
            float wheelCenterX = wheelDestRect.centerX();
            float wheelCenterY = wheelDestRect.centerY();
            canvas.translate(wheelCenterX, wheelCenterY); // Move origin to center
            canvas.rotate(currentWheelRotation);           // Rotate canvas
            canvas.translate(-wheelCenterX, -wheelCenterY); // Move origin back
            canvas.drawBitmap(rouletteWheelImage, null, wheelDestRect, paint);
            canvas.restore(); // Undo rotation for subsequent drawings
        }

        // 3. Draw all chips placed by the player
        if (chipImage != null) {
            int chipSize = getWidth() / 15;
            for (PlacedBet bet : placedBets) {
                // Draw chip centered on the betting box
                RectF chipRect = new RectF(bet.position.x - chipSize / 2, bet.position.y - chipSize / 2, bet.position.x + chipSize / 2, bet.position.y + chipSize / 2);
                canvas.drawBitmap(chipImage, null, chipRect, paint);
            }
        }

        // 4. Draw Wallet Amount
        String walletText = "Wallet: $" + String.format("%.2f", walletAmount);
        canvas.drawText(walletText, 30, 80, textPaint);
    }

    /**
     * Animation runnable that handles the spinning of the wheel.
     * Implements deceleration physics for a realistic stop.
     */
    private final Runnable spinRunnable = new Runnable() {
        private float degreesToRotate = 360 * 3 + random.nextInt(360); // Total rotation (min 3 spins)
        private float rotatedSoFar = 0;

        @Override
        public void run() {
            if (rotatedSoFar < degreesToRotate) {
                // Calculate speed: slows down as it reaches the target
                float step = Math.max(2, (degreesToRotate - rotatedSoFar) / 20f);
                currentWheelRotation += step;
                rotatedSoFar += step;

                if (currentWheelRotation >= 360) {
                    currentWheelRotation -= 360;
                }
                invalidate(); // Trigger redraw
                handler.postDelayed(this, 10); // Recursively loop
            } else {
                // Spin finished: Check bets, show result, and clear board
                boolean won = checkWin(resulet);
                
                // Update wallet: -10*the amount of bet, for a loss
               // if (!won) {
                    walletAmount -= 10.0*placedBets.size();
                    walletAmount+=10.0*count;
               // }



                
                // Notify listener (ActivityGames) to save to Firebase
                if (gameUpdateListener != null) {
                    gameUpdateListener.onWalletUpdated(walletAmount);
                }

                showColoredResult(resulet, won);
                isSpinning = false;
                rotatedSoFar = 0;
                placedBets.clear();
                invalidate();
            }
        }
    };

    /**
     * Logic for calculating wins based on the result number and active chips.
     */
    private boolean checkWin(int resultNumber) {
        for (PlacedBet bet : placedBets) {
            String name = bet.area.name;
            //walletAmount -= 5.0;
            // Check direct number hits
            if (name.equals(String.valueOf(resultNumber)))  {walletAmount+=360; return true; }
            if (name.equals("0") && resultNumber == 0) {walletAmount+=360; return true; }
            if (name.equals("00") && resultNumber == 37) {walletAmount+=360; return true; }
            
            // Skip range checks if result is 0 or 00 (unless specifically bet on)
            if (resultNumber == 0 || resultNumber == 37) continue;

            // Check range and category bets
            if (name.equals("RED") && RED_NUMBERS.contains(resultNumber)) {walletAmount+=20; count++; }
            if (name.equals("BLACK") && !RED_NUMBERS.contains(resultNumber)) {walletAmount+=20; count++; }
            if (name.equals("EVEN") && resultNumber % 2 == 0) {walletAmount+=20; count++; }
            if (name.equals("ODD") && resultNumber % 2 != 0) {walletAmount+=20; count++; }
            if (name.equals("1-18") && resultNumber <= 18) {walletAmount+=20; count++; }
            if (name.equals("19-36") && resultNumber >= 19) {walletAmount+=20; count++; }
            if (name.equals("1st 12") && resultNumber <= 12) {walletAmount+=30; count++;  }
            if (name.equals("2nd 12") && resultNumber >= 13 && resultNumber <= 24) {walletAmount+=30; count++; }
            if (name.equals("3rd 12") && resultNumber >= 25) {walletAmount+=30; count++; }
            if (name.equals("Col 1") && resultNumber % 3 == 1) {walletAmount+=30; count++;; }
            if (name.equals("Col 2") && resultNumber % 3 == 2) {walletAmount+=30; count++; }
            if (name.equals("Col 3") && resultNumber % 3 == 0) {walletAmount+=30; count++; }
            if(count>0)
            {
                count=0;
                return true;
            }
        }
        return false;
    }

    /**
     * Displays a colored popup message with the game result.
     */
    private void showColoredResult(int number, boolean won) {
        String resultText = (number == 37) ? "00" : String.valueOf(number);
        String fullMessage = "Number is: " + resultText + (won ? " - YOU WON!" : " - You lost");
        SpannableString spannable = new SpannableString(fullMessage);

        // Assign color based on the winning number
        int color;
        if (number == 0 || number == 37) {
            color = Color.GREEN;
        } else if (RED_NUMBERS.contains(number)) {
            color = Color.RED;
        } else {
            color = Color.BLACK; // High visibility for black numbers
        }

        spannable.setSpan(new ForegroundColorSpan(color), 0, spannable.length(), 0);
        Toast.makeText(getContext(), spannable, Toast.LENGTH_LONG).show();
    }

    // Input handler for player clicks
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            // 1. Check if user clicked the wheel to spin it
            if (!isSpinning && wheelDestRect != null && wheelDestRect.contains((int) touchX, (int) touchY)) {
                isSpinning = true;
                resulet = random.nextInt(38); // Determine winning number at start of spin
                handler.post(spinRunnable);

                return true;
            }

            // 2. Check if user clicked a betting area to place a chip
            for (BetArea area : betAreas) {
                if (area.bounds.contains(touchX, touchY)) {
                    // Snap chip to the center of the box for a clean appearance
                    placedBets.add(new PlacedBet(new PointF(area.bounds.centerX(), area.bounds.centerY()), area));

                   /* if(!lost)
                    {

                        walletAmount -= 10.0; // Subtract 10 for losing
                       lost=true;
                    }
                    else
                    {
                        lost=false;
                    }*/
                    if (gameUpdateListener != null) {
                        gameUpdateListener.onWalletUpdated(walletAmount);
                    }
                    invalidate();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
