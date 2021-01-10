package com.example.weightdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.weightdemo.bitmap.CornerBgTextDrawable;
import com.example.weightdemo.bitmap.SpannableStringTextView;

import java.util.ArrayList;
import java.util.List;

public class BitmapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitmap);
        SpannableStringTextView mSpannableStringTextView = findViewById(R.id.mSpannableStringTextView);
        List<Drawable> drawableIdList = new ArrayList<>();
        drawableIdList.add(getTextDrawable());
        mSpannableStringTextView.setTextValue("哈哈哈哈哈", drawableIdList);
    }

    private Drawable getTextDrawable() {
        CornerBgTextDrawable textDrawable = new CornerBgTextDrawable(this, -1, dip2px(this,14));
        textDrawable.setText("这是圆角");
        textDrawable.setPaddingHorizontal(dip2px(this, 10));
        return textDrawable.getBgCornerDrawable();
    }


    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}