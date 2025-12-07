package com.example.casino_israel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class FallingCardChip extends AnimatedSprite {
    private Bitmap chipBitmap; // The image of the chip
    private int cardNumber;    // The number to display on the chip
    private final float CHIP_DIAMETER = 180; // Diameter of the chip (2 * radius)

    public FallingCardChip(Bitmap chipBitmap, PointF initialPosition, int cardNumber, float dy, float targetY) {
        // Start the chip off-screen above its initial X position, with given vertical speed and target Y
        super(initialPosition.x, initialPosition.y - 180.0f, dy, targetY);
        // Scale the provided bitmap to match the desired chip size
        this.chipBitmap = Bitmap.createScaledBitmap(chipBitmap, (int)CHIP_DIAMETER, (int)CHIP_DIAMETER, true);
        this.cardNumber = cardNumber;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        // Draw the chip bitmap centered at the current (x, y) position
        if (chipBitmap != null) {
            canvas.drawBitmap(chipBitmap, x - chipBitmap.getWidth() / 2, y - chipBitmap.getHeight() / 2, paint);
        }

        // Draw the card number in the center of the chip
        paint.setColor(Color.WHITE); // Assuming white text on the chip
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        // Adjust Y position to center the text vertically on the chip
        float textY = y - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(String.valueOf(cardNumber), x, textY, paint);
    }

    public int getCardNumber() {
        return cardNumber;
    }
}
