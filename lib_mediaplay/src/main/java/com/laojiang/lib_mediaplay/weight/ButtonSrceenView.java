package com.laojiang.lib_mediaplay.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 类介绍（必填）：
 * Created by Jiang on 2017/7/19 9:24.
 */

public class ButtonSrceenView extends View{
    public ButtonSrceenView(Context context) {
        super(context);
    }

    public ButtonSrceenView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeWidth(6);
        paint.setStyle(Paint.Style.STROKE);
        Rect rect = new Rect(100,100,100,100);
        canvas.drawRect(rect, paint);
//        canvas.drawLine(2, getHeight() / 2, 10 / 10, getHeight() / 2, paint);
//        canvas.drawLine(getWidth() - 2, getHeight() / 2, getWidth() - 10 / 10, getHeight() / 2, paint);
//        canvas.drawLine(getWidth() / 2, 2, getWidth() / 2, 10 / 10, paint);
//        canvas.drawLine(getWidth() / 2, getHeight() - 2, getWidth() / 2, getHeight() - 10 / 10, paint);
    }
}
