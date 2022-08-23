package com.android.blog.android_view.demo.v4;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.android.blog.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HelpWordView extends View {

    private Context mContext;

    private int rx = 10, ry = 10;
    private int mWidth;
    private int mHeight;
    private float mOffsetX;//X轴偏移量
    private float mOffsetY;//Y轴偏移量
    private int mScreenWidth;

    private int mTextColor;//字体颜色
    private int mPlaceColor;//放置区背景颜色
    private int mSelectColor;//选择区背景颜色
    private float mTextSize;//字体大小
    private float mTextMarginLeft;//距离左边间隔
    private float mTextMarginTop;//距离上面间隔

    private ArrayList<Word> mData;
    private ArrayList<Word> mShuffleData;
    private ArrayList<RectF> mSelectRectF;
    private ArrayList<RectF> mPlaceRectF;
    private ArrayList<String> mPlaceData;

    private Paint mTextPaint;
    private Paint mShapePaint;
    private Paint mPlacePaint;
    private Paint mSelectPaint;
    private Paint mTextMeasurePaint;

    private onResultCallback mOnResultCallback;

    private static final String TAG = "HelpWordView";


    public HelpWordView(Context context) {
        super(context);
    }

    public HelpWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HelpWordView);
        mTextSize = typedArray.getDimension(R.styleable.HelpWordView_text_size, 35f);
        mTextMarginLeft = typedArray.getDimension(R.styleable.HelpWordView_text_marginLeft, 50f);
        mTextMarginTop = typedArray.getDimension(R.styleable.HelpWordView_text_marginTop, 30f);
        mTextColor = typedArray.getColor(R.styleable.HelpWordView_text_color, Color.BLACK);
        mPlaceColor = typedArray.getColor(R.styleable.HelpWordView_place_background_color, Color.rgb(240, 240, 240));
        mSelectColor = typedArray.getColor(R.styleable.HelpWordView_select_background_color, Color.WHITE);
        typedArray.recycle();//回收
        init();
    }

    public ArrayList<Word> getData() {
        return mData;
    }

    public void setData(ArrayList<Word> mData) {
        this.mData = mData;
        this.mShuffleData = new ArrayList<>();
        this.mShuffleData.addAll(mData);
        Collections.shuffle(mShuffleData);
        invalidate();
    }

    public void setOnResultCallback(onResultCallback mOnResultCallback) {
        this.mOnResultCallback = mOnResultCallback;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float tempWidth = 0;//存储宽度的临时变量
        float totalHeight = getTextHeight(mTextMeasurePaint) * 3;//view所占的屏幕高度
        if (null != mShuffleData) {
            for (Word word : mShuffleData) {
                tempWidth += getTextWidth(word.getWord(), mTextMeasurePaint) + mTextMarginLeft;//累加计算出当前文字的宽度
                if (tempWidth >= mScreenWidth) {//如果累加的宽度大于屏幕的宽度则换行
                    totalHeight += getTextHeight(mTextMeasurePaint) + mTextMarginTop;
                    tempWidth = getTextWidth(word.getWord(), mTextMeasurePaint) + mTextMarginLeft;
                }
            }
        }
        Log.e(TAG, "widthMeasureSpec: " + getMode(widthMeasureSpec));
        Log.e(TAG, "heightMeasureSpec: " + getMode(heightMeasureSpec));
        totalHeight += totalHeight;
//        setMeasuredDimension(mScreenWidth, (int) totalHeight);
    }

    private String getMode(int spec) {
        switch (MeasureSpec.getMode(spec)) {
            case MeasureSpec.AT_MOST:
                return "AT_MOST";
            case MeasureSpec.EXACTLY:
                return "EXACTLY";
            case MeasureSpec.UNSPECIFIED:
                return "UNSPECIFIED";
        }
        return "UNKNOWN";
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPlace(canvas);
        drawSelect(canvas);
    }

    //画放置区的文字
    private void drawPlace(Canvas canvas) {
        mPlaceRectF = new ArrayList<>();
        RectF rectF = new RectF(0, 0, mWidth, mHeight / 2);
        canvas.drawRect(rectF, mPlacePaint);

        float startX = 0;
        float startY = getTextHeight(mTextMeasurePaint);
        float tempX = 0;

        for (String str : mPlaceData) {

            if (tempX + getTextWidth(str, mTextMeasurePaint) + mTextMarginLeft + mOffsetX >= mScreenWidth) {
                tempX = 0;
                startX = 0;
                startY += getTextHeight(mTextMeasurePaint) + mTextMarginTop;
            }

            tempX += getTextWidth(str, mTextMeasurePaint) + mTextMarginLeft;
            RectF position = new RectF(startX, startY - getTextHeight(mTextMeasurePaint), tempX - mTextMarginLeft, startY);
            mPlaceRectF.add(position);

            if (tempX >= mScreenWidth) {
                tempX = 0;
                startY += getTextHeight(mTextMeasurePaint) + mTextMarginTop;
            }
            startX = tempX;
        }

        ArrayList<ArrayList<RectF>> total = new ArrayList<>();
        ArrayList<RectF> temp = new ArrayList<>();
        for (int i = 0; i < mPlaceRectF.size(); i++) {
            if (i > 0) {
                if (mPlaceRectF.get(i).bottom == mPlaceRectF.get(i - 1).bottom) {
                    temp.add(mPlaceRectF.get(i));
                } else {
                    total.add(temp);
                    temp = new ArrayList<>();
                    temp.add(mPlaceRectF.get(i));
                }
            } else {
                temp.add(mPlaceRectF.get(i));
            }
        }
        if (temp.size() > 0) {
            total.add(temp);
        }

        if (total.size() > 0) {
            mPlaceRectF = new ArrayList<>();

            for (int i = 0; i < total.size(); i++) {
                for (RectF f : total.get(i)) {
                    f.offset(mOffsetX, mOffsetY);
                    mPlaceRectF.add(f);
                }
            }
        }

        for (int i = 0; i < mPlaceRectF.size(); i++) {
            float textWidth = getTextWidth(mPlaceData.get(i), mTextPaint);
            float textHeight = getTextHeight(mTextPaint);
            float textX = (float) (mPlaceRectF.get(i).left + textWidth);
            float textY = (float) (mPlaceRectF.get(i).bottom - textHeight / 2.0);
            canvas.drawRoundRect(mPlaceRectF.get(i), rx, ry, mSelectPaint);
            canvas.drawText(mPlaceData.get(i), textX, textY, mTextPaint);
            canvas.drawRoundRect(mPlaceRectF.get(i), rx, ry, mShapePaint);
        }

    }

    //画选择区的文字
    private void drawSelect(Canvas canvas) {
        mSelectRectF = new ArrayList<>();
        //画选择区的矩形
        RectF rectF = new RectF(0, mHeight / 2, mWidth, mHeight);
        canvas.drawRect(rectF, mSelectPaint);

        float startX = 0;
        float startY = getTextHeight(mTextMeasurePaint) + mHeight / 2;
        float tempX = 0;
        for (Word word : mShuffleData) {
            if (tempX + getTextWidth(word.getWord(), mTextMeasurePaint) + mTextMarginLeft >= mScreenWidth) {
                tempX = 0;
                startX = 0;
                startY += getTextHeight(mTextMeasurePaint) + mTextMarginTop;
            }

            tempX += getTextWidth(word.getWord(), mTextMeasurePaint) + mTextMarginLeft;

            RectF position = new RectF(startX, startY - getTextHeight(mTextMeasurePaint), tempX - mTextMarginLeft, startY);
            mSelectRectF.add(position);

            if (tempX >= mScreenWidth) {
                tempX = 0;
                startY += getTextHeight(mTextMeasurePaint) + mTextMarginTop;
            }
            startX = tempX;
        }

        ArrayList<ArrayList<RectF>> total = new ArrayList<>();
        ArrayList<RectF> temp = new ArrayList<>();
        for (int i = 0; i < mSelectRectF.size(); i++) {
            if (i > 0) {
                if (mSelectRectF.get(i).bottom == mSelectRectF.get(i - 1).bottom) {
                    temp.add(mSelectRectF.get(i));
                } else {
                    total.add(temp);
                    temp = new ArrayList<>();
                    temp.add(mSelectRectF.get(i));
                }
            } else {
                temp.add(mSelectRectF.get(i));
            }
        }

        if (temp.size() > 0) {
            total.add(temp);
        }

        if (total.size() > 0) {
            mSelectRectF = new ArrayList<>();
            float lastTop = total.get(total.size() - 1).get(total.get(total.size() - 1).size() - 1).top;
            float lastBottom = total.get(total.size() - 1).get(total.get(total.size() - 1).size() - 1).bottom;
            float freeY = mHeight - lastTop - (lastBottom - lastTop) / 2;

            int maxIndex = isBest(total);

            float freeX = mWidth - total.get(maxIndex).get(total.get(maxIndex).size() - 1).right;

            mOffsetX = (float) (freeX / (total.get(maxIndex).size() - 1.0));
            mOffsetY = (float) (freeY / (total.size() + 1 + 0.0));

            if (total.size() == 1) {
                mOffsetY = freeY / 3;
            }
            for (int i = 0; i < total.size(); i++) {

                for (RectF f : total.get(i)) {
                    f.offset(mOffsetX, mOffsetY);
                    mSelectRectF.add(f);
                }
            }
        }

        for (int i = 0; i < mSelectRectF.size(); i++) {
            if (!mShuffleData.get(i).isHide()) {
                float textWidth = getTextWidth(mShuffleData.get(i).getWord(), mTextPaint);
                float textHeight = getTextHeight(mTextPaint);
                float textX = (float) (mSelectRectF.get(i).left + textWidth);
                float textY = (float) (mSelectRectF.get(i).bottom - textHeight / 2.0);
                canvas.drawText(mShuffleData.get(i).getWord(), textX, textY, mTextPaint);
            }
            canvas.drawRoundRect(mSelectRectF.get(i), rx, ry, mShapePaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < mSelectRectF.size(); i++) {
                    if (mSelectRectF.get(i).contains(event.getX(), event.getY())) {
                        if (!mShuffleData.get(i).isHide()) {
                            mPlaceData.add(mShuffleData.get(i).getWord());
                            mShuffleData.get(i).setHide(!mShuffleData.get(i).isHide());
                        }
                        invalidate();
                        break;
                    }
                }

                for (int i = 0; i < mPlaceRectF.size(); i++) {
                    if (mPlaceRectF.get(i).contains(event.getX(), event.getY())) {
                        setShow(mPlaceData.get(i));
                        mPlaceRectF.remove(i);
                        mPlaceData.remove(i);
                        invalidate();
                        break;
                    }
                }
                compare();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    private void init() {

        mScreenWidth = getScreenWidth();

        if (null == mTextPaint) {
            mTextPaint = new Paint();
            mTextPaint.setTextSize(mTextSize);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setColor(mTextColor);
            mTextPaint.setTextAlign(Paint.Align.CENTER);
        }

        if (null == mTextMeasurePaint) {
            mTextMeasurePaint = new Paint();
            mTextMeasurePaint.setAntiAlias(true);
            mTextMeasurePaint.setTextSize((float) (mTextSize * 2));
            mTextMeasurePaint.setColor(mTextColor);
        }

        if (null == mPlacePaint) {
            mPlacePaint = new Paint();
            mPlacePaint.setColor(mPlaceColor);
            mPlacePaint.setAntiAlias(true);
            mPlacePaint.setStyle(Paint.Style.FILL);
        }

        if (null == mSelectPaint) {
            mSelectPaint = new Paint();
            mSelectPaint.setAntiAlias(true);
            mSelectPaint.setColor(mSelectColor);
            mSelectPaint.setStyle(Paint.Style.FILL);
        }

        if (null == mShapePaint) {
            mShapePaint = new Paint();
            mShapePaint.setAntiAlias(true);
            mShapePaint.setStyle(Paint.Style.STROKE);
            mShapePaint.setStrokeWidth(2);
            mShapePaint.setColor(Color.GRAY);
        }

        if (null == mSelectRectF) {
            mSelectRectF = new ArrayList<>();
        }

        if (null == mPlaceRectF) {
            mPlaceRectF = new ArrayList<>();
        }

        if (null == mPlaceData) {
            mPlaceData = new ArrayList<>();
        }
    }

    //比较用户选择的字符串顺序
    private void compare() {
        if (null == mData) {
            return;
        }
        if (null == mPlaceData) {
            return;
        }
        if (null != mOnResultCallback) {
            int count = 0;
            for (int i = 0; i < mPlaceData.size(); i++) {
                if (!mPlaceData.get(i).equals(mData.get(i).getWord())) {
                    count++;
                }
            }
            mOnResultCallback.error(count);
            if (mPlaceData.size() == mData.size()) {
                if (count == 0) {
                    mOnResultCallback.right();
                }
            }
        }
    }

    private int isBest(ArrayList<ArrayList<RectF>> lists) {

        ArrayList<Right> sortList = new ArrayList<>();

        for (int i = 0; i < lists.size(); i++) {
            sortList.add(new Right(i, lists.get(i).get(lists.get(i).size() - 1).right));
        }

        Collections.sort(sortList, new Comparator<Right>() {

            /**
             * 返回负数表示：o1 小于o2，
             * 返回0 表示：o1和o2相等，
             * 返回正数表示：o1大于o2*/
            @Override
            public int compare(Right o1, Right o2) {
                if (o1.value >= o2.value) {
                    return 1;
                }
                return -1;
            }
        });

        for (int i = sortList.size() - 1; i >= 0; i--) {
            ArrayList<RectF> rectFS = lists.get(sortList.get(i).index);
            if ((mWidth - rectFS.get(rectFS.size() - 1).right) / (rectFS.size() - 1.0) + rectFS.get(rectFS.size() - 1).right > mWidth) {
                continue;
            }
            return sortList.get(i).index;
        }
        return 0;
    }

    //显示字符串
    private void setShow(String text) {
        for (Word word : mShuffleData) {
            if (word.getWord().equals(text)) {
                word.setHide(!word.isHide());
            }
        }
    }

    //获取文字的高
    private int getTextHeight(Paint paint) {

        Rect rect = new Rect();
        paint.getTextBounds("测试", 0, "测试".length(), rect);
        return rect.height();
    }

    //获取文字的宽
    private float getTextWidth(String text, Paint paint) {
        return paint.measureText(text);
    }

    //获取屏幕的宽度
    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    public interface onResultCallback {

        void right();

        void error(int count);
    }

    private class Right {
        int index;
        float value;

        public Right(int index, float value) {
            this.index = index;
            this.value = value;
        }
    }

    public static class Word {

        private String word;
        private boolean hide;

        public Word(String word) {
            this.word = word;
        }

        public Word(String word, boolean hide) {
            this.word = word;
            this.hide = hide;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public boolean isHide() {
            return hide;
        }

        public void setHide(boolean hide) {
            this.hide = hide;
        }
    }
}
