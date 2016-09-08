package com.anenn.selecteditemview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import java.util.List;

/**
 * Created by Anenn on 8/31/16.
 *
 * 条件筛选动画
 */
public class SelectedItemView extends View {

  private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#222222");        // 默认的文本的字体颜色
  private static final int SELECTED_TEXT_COLOR = Color.parseColor("#FFFFFF");       // 选中的文本的字体颜色
  private static final int SELECTED_BG = Color.parseColor("#ED5296");               // 选中的背景色
  private static final int DEFAULT_BG = Color.parseColor("#EAEAEA");                // 默认的背景色
  private static final int DEFAULT_TEXT_SIZE = 12;                                  // 默认文本字体大小
  private static final int DEFAULT_MARGIN = 10;                                     // 默认 Item 之间的间隙

  private List<String> mSelectItemList;
  private int mDefaultBg = DEFAULT_BG;
  private int mSelectedBg = SELECTED_BG;
  private int mDefaultTextColor = DEFAULT_TEXT_COLOR;
  private int mSelectedTextColor = SELECTED_TEXT_COLOR;
  private int mTextSize = DEFAULT_TEXT_SIZE;
  private int mItemMargin = DEFAULT_MARGIN;

  private int mCurrentIndex = 0;                             // 当前选中的 Item 的索引位置,默认选中第一项
  private int mLastIndex = 0;                                // 上一次选中的 Item 的索引位置
  private float mViewWidth;                                  // 当前 View 的宽度
  private float mViewHeight;                                 // 当前 View 的高度
  private float mItemWidth;                                  // 每个 Item 的宽度
  private float mItemHeight;                                 // 每个 Item 的高度

  private RectF mRectF;                                      // 矩形, 用于绘制每个 Item 的边界
  private Paint mPaint;
  private float mProgress;                                   // 当前动画的进度
  private ValueAnimator mProgressAnimator;
  private float mStartX;                                     // 存放 DOWN 事件的点击位置的 X 坐标
  private float mStartY;                                     // 存放 DOWN 事件的点击位置的 Y 坐标

  private OnItemSelectedListener mOnItemSelectedListener;    // 事件回调

  public SelectedItemView(Context context) {
    this(context, null);
  }

  public SelectedItemView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public SelectedItemView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    initAttributes(context, attrs, defStyleAttr);
  }

  private void initAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
    final TypedArray typedArray = context.getTheme()
        .obtainStyledAttributes(attrs, R.styleable.SelectedItemView, defStyleAttr, 0);

    mDefaultBg = typedArray.getColor(R.styleable.SelectedItemView_default_bg, DEFAULT_BG);
    mSelectedBg = typedArray.getColor(R.styleable.SelectedItemView_selected_bg, SELECTED_BG);
    mDefaultTextColor =
        typedArray.getColor(R.styleable.SelectedItemView_default_text_color, DEFAULT_TEXT_COLOR);
    mSelectedTextColor =
        typedArray.getColor(R.styleable.SelectedItemView_selected_text_color, SELECTED_TEXT_COLOR);
    mTextSize = typedArray.getDimensionPixelSize(R.styleable.SelectedItemView_text_size,
        (int) DimenUtil.dp2sp(context, DEFAULT_TEXT_SIZE));
    mItemMargin = typedArray.getDimensionPixelOffset(R.styleable.SelectedItemView_item_margin,
        DEFAULT_MARGIN);

    typedArray.recycle();

    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    mRectF = new RectF();
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    // 获取除去内边距后的可用区域宽高
    mViewWidth = w - getPaddingLeft() - getPaddingRight();
    mViewHeight = h - getPaddingTop() - getPaddingBottom();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    // 获取每个 Item 的可用宽高
    mItemWidth =
        (mViewWidth - (mSelectItemList.size() - 1) * mItemMargin) * 1.0f / mSelectItemList.size();
    mItemHeight = mViewHeight;

    if (mSelectItemList != null && mSelectItemList.size() > 0) {

      // 绘制 Item 的默认背景
      mPaint.setColor(mDefaultBg);
      for (int i = 0, size = mSelectItemList.size(); i < size; i++) {
        mRectF.set(getPaddingLeft() + (mItemWidth + mItemMargin) * i, getPaddingTop(),
            getPaddingLeft() + mItemWidth * (i + 1) + mItemMargin * i,
            getHeight() - getPaddingBottom());
        canvas.drawRoundRect(mRectF, mItemHeight / 2.0f, mItemHeight / 2.0f, mPaint);
      }

      // 绘制 Item 的选中背景, 由于这里可能会出现动画, 所以需要考虑动画进度
      mPaint.setColor(mSelectedBg);
      mRectF.set(getPaddingLeft()
              + (mItemWidth + mItemMargin) * mLastIndex
              + (mCurrentIndex - mLastIndex) * mProgress * (mItemWidth + mItemMargin), getPaddingTop(),
          getPaddingLeft()
              + mItemWidth * (mLastIndex + 1)
              + mItemMargin * mLastIndex
              + (mCurrentIndex - mLastIndex) * mProgress * (mItemWidth + mItemMargin),
          getHeight() - getPaddingBottom());
      canvas.drawRoundRect(mRectF, mItemHeight / 2.0f, mItemHeight / 2.0f, mPaint);

      // 绘制默认文本信息
      mPaint.setTextSize(mTextSize);
      mPaint.setColor(mDefaultTextColor);
      Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
      for (int i = 0, size = mSelectItemList.size(); i < size; i++) {
        String text = mSelectItemList.get(i);
        float length = mPaint.measureText(text);

        canvas.drawText(text,
            getPaddingLeft() + (mItemWidth + mItemMargin) * i + (mItemWidth - length) / 2f,
            getPaddingTop() + mItemHeight / 2.0f
                - (fontMetrics.ascent + fontMetrics.descent) / 2.0f, mPaint);
      }

      // 绘制选中文本信息
      mPaint.setTextSize(mTextSize);
      mPaint.setColor(mSelectedTextColor);
      String text = mSelectItemList.get(mCurrentIndex);
      float length = mPaint.measureText(text);
      canvas.drawText(mSelectItemList.get(mCurrentIndex), getPaddingLeft()
              + (mItemWidth + mItemMargin) * mCurrentIndex
              + (mItemWidth - length) / 2f,
          getPaddingTop() + mItemHeight / 2.0f - (fontMetrics.ascent + fontMetrics.descent) / 2.0f,
          mPaint);
    }
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    if (mProgressAnimator != null && mProgressAnimator.isRunning()) {
      return true;
    }

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mStartX = event.getX();
        mStartY = event.getY();
        return true;
      case MotionEvent.ACTION_UP:
        float tempX = event.getX();
        float tempY = event.getY();

        // 去除边界情况
        if (tempX < 0 || tempX > getWidth() && tempY < 0 || tempY > getHeight()) {
          return true;
        }

        // 去除手指滑动情况
        if (Math.abs(mStartX - tempX) < 10 && Math.abs(mStartY - tempY) < 10) {
          for (int i = 0, size = mSelectItemList.size(); i < size; i++) {
            RectF rect =
                new RectF(getPaddingLeft() + (mItemWidth + mItemMargin) * i, getPaddingTop(),
                    getPaddingLeft() + mItemWidth * (i + 1) + mItemMargin * i, mItemHeight);
            if (rect.contains(tempX, tempY)) {
              mCurrentIndex = i;
              startAnimator();
              break;
            }
          }
        }
        return true;
      default:
        return super.onTouchEvent(event);
    }
  }

  private void startAnimator() {
    if (mProgressAnimator == null) {
      mProgressAnimator = ValueAnimator.ofFloat(0, 1);
      mProgressAnimator.setDuration(500);
      mProgressAnimator.setInterpolator(new OvershootInterpolator(1.0f));
      mProgressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
        @Override public void onAnimationUpdate(ValueAnimator animation) {
          mProgress = (float) animation.getAnimatedValue();
          invalidate();
        }
      });
      mProgressAnimator.addListener(new AnimatorListenerAdapter() {
        @Override public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          mLastIndex = mCurrentIndex;
          if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(mCurrentIndex);
          }
        }
      });
    }

    if (!mProgressAnimator.isRunning()) mProgressAnimator.start();
  }

  public List<String> getSelectItemList() {
    return mSelectItemList;
  }

  public void setSelectItemList(List<String> selectItemList) {
    this.mSelectItemList = selectItemList;
    invalidate();
  }

  public void setOnItemSelectedListener(OnItemSelectedListener mOnItemSelectedListener) {
    this.mOnItemSelectedListener = mOnItemSelectedListener;
  }

  public interface OnItemSelectedListener {
    void onItemSelected(int position);
  }
}
