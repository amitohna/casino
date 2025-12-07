package com.example.casino_israel;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class AnimatedSprite {
    protected float x, y; // Current position
    protected float dy;   // Vertical velocity (pixels per frame)
    protected float targetY; // Final Y-position where the sprite should stop
    protected boolean isAnimating; // True if the sprite is currently moving

    public AnimatedSprite(float x, float y, float dy, float targetY) {
        this.x = x;
        this.y = y;
        this.dy = dy;
        this.targetY = targetY;
        this.isAnimating = true; // By default, new sprites start animating
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public boolean isAnimating() { return isAnimating; }

    // Updates the sprite's position. Stops animation if targetY is reached.
    public void move() {
        if (isAnimating) {
            // Move downwards
            if (y < targetY) {
                y += dy;
                // If we've passed or reached the target, snap to it and stop animating
                if (y >= targetY) {
                    y = targetY;
                    isAnimating = false;
                }
            } else { // If for some reason it started below or at target, stop animating immediately
                y = targetY;
                isAnimating = false;
            }
        }
    }

    // Abstract method for drawing the sprite, to be implemented by concrete classes
    public abstract void draw(Canvas canvas, Paint paint);
}
