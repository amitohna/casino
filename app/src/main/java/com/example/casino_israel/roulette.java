package com.example.casino_israel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class roulette extends View {
    private Bitmap backgroundImage;
    private Bitmap rouletteWheelImage;
    private Bitmap chipImage;
    private float currentWheelRotation = 0;
    private boolean isSpinning = false;
    private Handler handler = new Handler();
    private Random random = new Random();
    private int resulet;

    private Rect wheelDestRect;
    private List<PointF> placedChips = new ArrayList<>();
    private List<BetArea> betAreas = new ArrayList<>();

    private static class BetArea {
        RectF bounds;
        String name;

        BetArea(RectF bounds, String name) {
            this.bounds = bounds;
            this.name = name;
        }
    }

    public roulette(Context context) {
        super(context);
        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.roulletes);
        rouletteWheelImage = BitmapFactory.decodeResource(getResources(), R.drawable.rol90);
        chipImage = BitmapFactory.decodeResource(getResources(), R.drawable.chip);
        resulet = random.nextInt(38);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (rouletteWheelImage != null) {
            int wheelSize = (int) (Math.min(getWidth(), getHeight()) * 0.6);
            int left = ((getWidth() - wheelSize / 2) / 2);
            int top = (int) (getHeight() * 0.01);
            int right = left + wheelSize;
            int bottom = top + wheelSize;
            wheelDestRect = new Rect(left, top, right, bottom);
        }

        setupBetAreas();
    }

    private void setupBetAreas() {
        betAreas.clear();
        float w = getWidth();
        float h = getHeight();

        // Refined coordinates for pixel-perfect alignment based on user feedback
        float gridTop = h * 0.41f;    // Shifted down from 0.40f
        float gridBottom = h * 0.915f; // Shifted down from 0.90f
        float gridLeft = w * 0.54f;   // Shifted right from 0.52f
        float gridRight = w * 0.93f;  // Pulled in from 0.94f
        
        float cellWidth = (gridRight - gridLeft) / 3f;
        float cellHeight = (gridBottom - gridTop) / 12f;

        // 0 and 00 at the top
        float headerTop = h * 0.36f; // Shifted down from 0.35f
        betAreas.add(new BetArea(new RectF(gridLeft, headerTop, gridLeft + cellWidth * 1.5f, gridTop), "0"));
        betAreas.add(new BetArea(new RectF(gridLeft + cellWidth * 1.5f, headerTop, gridRight, gridTop), "00"));

        // Numbers 1-36 Grid
        for (int row = 0; row < 12; row++) {
            for (int col = 0; col < 3; col++) {
                float left = gridLeft + col * cellWidth;
                float top = gridTop + row * cellHeight;
                betAreas.add(new BetArea(new RectF(left, top, left + cellWidth, top + cellHeight), "Number"));
            }
        }

        // Dozen Bets (1st 12, 2nd 12, 3rd 12)
        float dozenLeft = w * 0.42f;
        float dozenRight = gridLeft;
        betAreas.add(new BetArea(new RectF(dozenLeft, gridTop, dozenRight, gridTop + 4 * cellHeight), "1st 12"));
        betAreas.add(new BetArea(new RectF(dozenLeft, gridTop + 4 * cellHeight, dozenRight, gridTop + 8 * cellHeight), "2nd 12"));
        betAreas.add(new BetArea(new RectF(dozenLeft, gridTop + 8 * cellHeight, dozenRight, gridBottom), "3rd 12"));

        // Outside Bets (Far Left)
        float farLeft = w * 0.32f;
        float farRight = dozenLeft;
        betAreas.add(new BetArea(new RectF(farLeft, gridTop, farRight, gridTop + 2 * cellHeight), "1-18"));
        betAreas.add(new BetArea(new RectF(farLeft, gridTop + 2 * cellHeight, farRight, gridTop + 4 * cellHeight), "EVEN"));
        betAreas.add(new BetArea(new RectF(farLeft, gridTop + 4 * cellHeight, farRight, gridTop + 6 * cellHeight), "RED"));
        betAreas.add(new BetArea(new RectF(farLeft, gridTop + 6 * cellHeight, farRight, gridTop + 8 * cellHeight), "BLACK"));
        betAreas.add(new BetArea(new RectF(farLeft, gridTop + 8 * cellHeight, farRight, gridTop + 10 * cellHeight), "ODD"));
        betAreas.add(new BetArea(new RectF(farLeft, gridTop + 10 * cellHeight, farRight, gridBottom), "19-36"));

        // Bottom Column Bets (2 to 1)
        float footerBottom = h * 0.96f; // Adjusted for slightly lower grid
        betAreas.add(new BetArea(new RectF(gridLeft, gridBottom, gridLeft + cellWidth, footerBottom), "2-1"));
        betAreas.add(new BetArea(new RectF(gridLeft + cellWidth, gridBottom, gridLeft + 2 * cellWidth, footerBottom), "2-1"));
        betAreas.add(new BetArea(new RectF(gridLeft + 2 * cellWidth, gridBottom, gridRight, footerBottom), "2-1"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();

        if (backgroundImage != null) {
            Rect destRect = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(backgroundImage, null, destRect, paint);
        }

        if (rouletteWheelImage != null && wheelDestRect != null) {
            canvas.save();
            float wheelCenterX = wheelDestRect.centerX();
            float wheelCenterY = wheelDestRect.centerY();
            canvas.translate(wheelCenterX, wheelCenterY);
            canvas.rotate(currentWheelRotation);
            canvas.translate(-wheelCenterX, -wheelCenterY);
            canvas.drawBitmap(rouletteWheelImage, null, wheelDestRect, paint);
            canvas.restore();
        }

        // Draw placed chips
        if (chipImage != null) {
            int chipSize = getWidth() / 15;
            for (PointF pos : placedChips) {
                RectF chipRect = new RectF(pos.x - chipSize / 2, pos.y - chipSize / 2, pos.x + chipSize / 2, pos.y + chipSize / 2);
                canvas.drawBitmap(chipImage, null, chipRect, paint);
            }
        }
    }

    private final Runnable spinRunnable = new Runnable() {
        private float degreesToRotate = 360 * 3 + random.nextInt(360);
        private float rotatedSoFar = 0;

        @Override
        public void run() {
            if (rotatedSoFar < degreesToRotate) {
                float step = Math.max(2, (degreesToRotate - rotatedSoFar) / 20f);
                currentWheelRotation += step;
                rotatedSoFar += step;

                if (currentWheelRotation >= 360) {
                    currentWheelRotation -= 360;
                }
                invalidate();
                handler.postDelayed(this, 10);
            } else {
                Toast.makeText(getContext(), "Number is: " + resulet, Toast.LENGTH_LONG).show();
                isSpinning = false;
                rotatedSoFar = 0;
                
                // Clear chips when spinning ends
                placedChips.clear();
                invalidate();
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            if (!isSpinning && wheelDestRect != null && wheelDestRect.contains((int) touchX, (int) touchY)) {
                isSpinning = true;
                resulet = random.nextInt(38);
                handler.post(spinRunnable);
                return true;
            }

            // Check betting areas
            for (BetArea area : betAreas) {
                if (area.bounds.contains(touchX, touchY)) {
                    // Snap the chip to the center of the betting area
                    placedChips.add(new PointF(area.bounds.centerX(), area.bounds.centerY()));
                    invalidate();
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
