package com.workspike.pedotracker.pedotrackerv1.not_used;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Chamli Priyashan on 9/3/2017.
 */

public class DrawImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint currentPaint;
    public boolean drawRect = false;
    public float left;
    public float top;
    public float right;
    public float bottom;

    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        currentPaint = new Paint();
        currentPaint.setDither(true);
        currentPaint.setColor(0xFF00CC00);  // alpha.r.g.b
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawRect)
        {
            canvas.drawRect(left, top, right, bottom, currentPaint);
        }
    }
}
