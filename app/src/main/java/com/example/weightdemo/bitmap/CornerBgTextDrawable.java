package com.example.weightdemo.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class CornerBgTextDrawable {
    private int width = -1;
    private int height;
    private Context mContext;
    private String textValue = "";
    private Paint mPaint;
    private int bgColor = Color.GRAY;
    private int textColor = Color.RED;
    private float radius = 15;
    private float paddingHorizontal = 10;

    public CornerBgTextDrawable(Context mContext) {
        this(mContext, 0, 0);
    }

    public CornerBgTextDrawable(Context mContext, int width, int height) {
        this.width = width;
        this.height = height;
        this.mContext = mContext;
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(15);
        mPaint.setColor(textColor);
    }

    public Bitmap getBgCornerBitmap() {
        float measureText = mPaint.measureText(textValue);
        if (getWidth() == -1) {
            setWidth((int) (measureText + (getPaddingHorizontal() * 2)));
        }
        Rect rect = new Rect();
        mPaint.getTextBounds(textValue, 0, textValue.length(), rect);

        Bitmap bitmap = createBitmap();
        Canvas canvas = new Canvas();
        canvas.drawColor(Color.TRANSPARENT);
        canvas.setBitmap(bitmap);
        //背景
        mPaint.setColor(bgColor);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), radius, radius, mPaint);
        //文字
        mPaint.setColor(textColor);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        canvas.drawText(textValue, (getWidth() - measureText) / 2, (getHeight() - fontMetrics.top - fontMetrics.bottom) / 2, mPaint);
        return bitmap;
    }

    public Drawable getBgCornerDrawable() {
        return new BitmapDrawable(mContext.getResources(), getBgCornerBitmap());
    }


    private Bitmap createBitmap() {
        return Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setTextSize(float textSize) {
        mPaint.setTextSize(textSize);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }


    public void setText(String textValue) {
        this.textValue = textValue;
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPaddingHorizontal(float paddingHorizontal) {
        this.paddingHorizontal = paddingHorizontal;
    }

    public float getPaddingHorizontal() {
        return paddingHorizontal;
    }
}
