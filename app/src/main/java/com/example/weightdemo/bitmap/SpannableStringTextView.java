package com.example.weightdemo.bitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;


import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;


@SuppressLint("AppCompatCustomView")
public class SpannableStringTextView extends TextView {
    private boolean selfSpannableString = false;//是否达到富文本绘制的条件
    private int selfViewWidth = 0;//view的实际宽度
    private String mTextValue;//文本内容
    private List<Drawable> drawableIdList;//文本后面的图片
    private int spacingWidth;//不同内容间的间距

    public SpannableStringTextView(Context context) {
        super(context);
    }

    public SpannableStringTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpannableStringTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param mTextValue     需要绘制的文本内容
     * @param drawableIdList 需要绘制的图片，绘制顺序是数据的顺序
     */
    public void setTextValue(String mTextValue, List<Drawable> drawableIdList) {
        spacingWidth = dip2px(4);
        this.mTextValue = mTextValue;
        this.drawableIdList = drawableIdList;
        postInvalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        selfSpannableString = false;
        selfViewWidth = 0;
        //1、获得MeasureSpec的mode
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        //2、获得MeasureSpec的specSize
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {//准确的值
            selfSpannableString = true;
            selfViewWidth = specSize;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (selfSpannableString && selfViewWidth > 0) {
            int strwid = 0;
            int drawablesWidth = 0;
            List<Drawable> tempList = new ArrayList<>();
            if (drawableIdList != null && drawableIdList.size() > 0) {
                for (int i = 0; i < drawableIdList.size(); i++) {
                    if (i == 0) {
                        drawablesWidth += getDrawableWidth(tempList, drawableIdList.get(i), true);
                        continue;
                    }
                    drawablesWidth += getDrawableWidth(tempList, drawableIdList.get(i), true) + spacingWidth;
                }

            }
            String newTextValue = mTextValue;
            if (!TextUtils.isEmpty(mTextValue)) {

                strwid = (int) getPaint().measureText(mTextValue);


                if (strwid + drawablesWidth > selfViewWidth) {
                    int endStringWidth = (int) getPaint().measureText("...");
                    strwid = selfViewWidth - drawablesWidth - endStringWidth;
                    newTextValue = getSubStringWidth(newTextValue, strwid) + "...";

                }

                TextPaint mTextPaint = getPaint();
                Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
                int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
                canvas.drawText(newTextValue, getPaddingLeft(), baseline, mTextPaint);//绘制指定文本
            }
            int addEndWith = 0;
            if (!TextUtils.isEmpty(newTextValue) && newTextValue.endsWith("...")) {
                addEndWith = spacingWidth + 6;//+3避免特殊的字符串组合造成文本和图片紧挨着
            }
            int drawBitmapLeft = strwid + spacingWidth + addEndWith;
            for (int i = 0; i < tempList.size(); i++) {//循环绘制指定的图片
                Bitmap mBitmap = drawable2Bitmap(tempList.get(i));
                if (mBitmap != null) {
                    int mTop = (getMeasuredHeight() - tempList.get(i).getIntrinsicHeight()) - dip2px(1);
                    canvas.drawBitmap(mBitmap, drawBitmapLeft, mTop, null);
                    drawBitmapLeft += tempList.get(i).getIntrinsicWidth() + spacingWidth;
                }
            }
            return;
        }
        super.onDraw(canvas);
    }

    /**
     * 获取对应资源图片的宽度
     *
     * @param tempList
     * @param mDrawable
     * @param matchHeight
     * @return
     */
    private int getDrawableWidth(List<Drawable> tempList, Drawable mDrawable, boolean matchHeight) {

        Drawable drawable = mDrawable;
        if (drawable == null) {
            return 0;
        }
        int ow = drawable.getIntrinsicWidth();
        int oh = drawable.getIntrinsicHeight();
        if (matchHeight) {
            int th = getPaint().getFontMetricsInt(null) - dip2px(2);
            int tw = th * ow / oh;
            drawable.setBounds(new Rect(0, 0, tw, th));
        } else {
            drawable.setBounds(new Rect(0, 0, ow, oh));
        }
        tempList.add(drawable);
        return drawable.getIntrinsicWidth();
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        }
//        else if (drawable instanceof CountableAnimationDrawable) {
//            Bitmap bitmap = Bitmap
//                    .createBitmap(
//                            drawable.getIntrinsicWidth(),
//                            drawable.getIntrinsicHeight(),
//                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                                    : Bitmap.Config.RGB_565);
//            Canvas canvas = new Canvas(bitmap);
//            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
//                    drawable.getIntrinsicHeight());
//            drawable.draw(canvas);
//            return bitmap;
//        }
        else {
            return null;
        }
    }

    /**
     * 根据宽度获取指定长度字符串
     *
     * @param str
     * @param width
     * @return
     */
    public String getSubStringWidth(String str, int width) {
        if (TextUtils.isEmpty(str) || width <= 0) {
            return "";
        }
        //字符串长度
        int length = str.length();
        Paint paint = getPaint();
        //根据宽度得到字符数量
        int measurennums = paint.breakText(str, true, width, null);
        //字符数量和长度比较
        if (measurennums > length) {
            measurennums = length;
        }
        return str.substring(0, measurennums);
    }

    public BitmapDrawable getBgCornerDrawable(int height, int width, String mTextValue) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        return new BitmapDrawable(getContext().getResources(),bitmap);
    }


    /**
     * dip换算成像素px
     *
     * @param dipValue dp或者dip的值
     * @return px
     */
    public int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}