package com.laojiang.lib_mediaplay.weight;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;

import com.laojiang.lib_mediaplay.R;

/**
 * 类介绍（必填）：
 * Created by Jiang on 2017/7/20 15:34.
 */

public class PlayerSeekBar extends SeekBar {
    private Context context;
    private int widthSize;

    public PlayerSeekBar(Context context) {
        super(context);
        this.context = context;
    }

    public PlayerSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        int paddingBottom = getPaddingBottom();
        int paddingRight = getPaddingRight();


    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int scrollBarSize = super.getScrollBarSize();

        Log.i("seekbar的大小==",scrollBarSize+"");
        int paddingTop = getPaddingTop();
        int paddingLeft = getPaddingLeft();
        int paddingBottom = getPaddingBottom();
        int paddingRight = getPaddingRight();
        int width  = super.getWidth();
        Paint paint = new Paint();
        paint.setColor(context.getColor(R.color.bg_seekbar));
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth( super.getHeight()-paddingTop-paddingBottom);
        float startX =  super.getLeft()-paddingLeft;
        float startY = super.getTop();
        float endX = startX+widthSize-paddingRight;
        canvas.drawLine(startX,startY,endX,startY,paint);
    }
}
