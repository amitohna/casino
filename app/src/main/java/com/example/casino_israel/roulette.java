package com.example.casino_israel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

public class roulette extends View {
    private Bitmap backgroundImage;
    private Bitmap rouletteWheelImage; // New bitmap for the wheel
    private int result;
    private float currentWheelRotation = 0; // Tracks current rotation of the wheel
    private boolean isSpinning = false;
    private Handler handler = new Handler();

    // Define the destination rectangle for drawing the roulette wheel
    private Rect wheelDestRect;

    public roulette(Context context) {
        super(context);
        // Make sure R.drawable.roulette refers to your roulette table image
        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.roulletes);
        // Load the roulette wheel image
        rouletteWheelImage = BitmapFactory.decodeResource(getResources(), R.drawable.rol90);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Calculate the destination rectangle for the roulette wheel once the size is known
        if (rouletteWheelImage != null) {
            // Adjusted sizing: Make the wheel a bit larger and better centered.
            int wheelSize = (int) (Math.min(getWidth(), getHeight()) * 0.6); // Use the smaller dimension to keep aspect ratio, and 60% of it.
            
            int left = ((getWidth() - wheelSize/2) / 2); // Center horizontally
            int top = (int) (getHeight() * 0.01); // Position it about 10% from the top
            int right = left + wheelSize;
            int bottom = top + wheelSize; // Make it square
            wheelDestRect = new Rect(left, top, right, bottom);
        }
    }

    @Override // It's good practice to use @Override for overridden methods
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Paint paint = new Paint();

        // Draw the background image, scaled to fit the canvas
        if (backgroundImage != null) {
            Rect destRect = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(backgroundImage, null, destRect, paint);
        }

        // Draw the roulette wheel image with rotation
        if (rouletteWheelImage != null && wheelDestRect != null) {
            canvas.save(); // Save the current state of the canvas

            // Calculate the center of the roulette wheel's destination rectangle
            float wheelCenterX = wheelDestRect.centerX();
            float wheelCenterY = wheelDestRect.centerY();

            // Translate to the center, rotate, then translate back
            canvas.translate(wheelCenterX, wheelCenterY);
            canvas.rotate(currentWheelRotation); // Apply current rotation
            canvas.translate(-wheelCenterX, -wheelCenterY);

            // Draw the roulette wheel bitmap
            canvas.drawBitmap(rouletteWheelImage, null, wheelDestRect, paint);

            canvas.restore(); // Restore the canvas to its original state
        }
    }

    private final Runnable spinRunnable = new Runnable() {
        private float degreesToRotate = 360;
        private float rotatedSoFar = 0;

        @Override
        public void run() {
            if (rotatedSoFar < degreesToRotate) {
                float step = 10; // Speed of rotation
                currentWheelRotation += step;
                rotatedSoFar += step;

                if (currentWheelRotation >= 360) {
                    currentWheelRotation -= 360;
                }
                invalidate();
                handler.postDelayed(this, 20); // Delay for animation
            } else {
                isSpinning = false;
                rotatedSoFar = 0;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

               // Check if the touch event is within the bounds of the roulette wheel
               if (!isSpinning && wheelDestRect != null && wheelDestRect.contains((int) touchX, (int) touchY)) {
                   isSpinning = true;
                   handler.post(spinRunnable);
                   return true; // Event handled
               }


        }
        return super.onTouchEvent(event);
    }
}