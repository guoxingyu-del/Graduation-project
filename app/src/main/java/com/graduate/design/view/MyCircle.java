package com.graduate.design.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.graduate.design.R;

public class MyCircle extends View {

    public MyCircle(Context context) {
        super(context);
    }

    public MyCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 绘图
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        //定义画笔
       // Paint paint1 = new Paint();
        Paint paint2 = new Paint();

        //定义画笔颜色
       /* paint1.setColor(getResources().getColor(R.color.gray));
        paint1.setAntiAlias(true);*/
        paint2.setColor(getResources().getColor(R.color.black));
        paint2.setAntiAlias(true);

        //在画布上开始画圆
        canvas.drawCircle(width/2, height/2, 800, paint2);
        //canvas.drawCircle(width/2, height/2, 500, paint1);

    }

}
