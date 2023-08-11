package com.prudhvi.spinwheel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


import java.util.List;

/**
 * Created by prudhvi
 */

public class LuckyWheelView extends RelativeLayout implements PielView.PieRotateListener, PielView.Observer, PielView.OnSpinWheelClickListener {
    private int mBackgroundColor;
    private int mTextColor;
    private Drawable mCenterImage;
    private Drawable mCursorImage;

    private PielView pielView;
    private ImageView ivCursorView;

    private ProgressBar progressBar;

    private int mBorderColor;
    private int mEdgeWidth;

    private int target = -1;
    private boolean isRotate = false;

    private LuckyRoundItemSelectedListener mLuckyRoundItemSelectedListener;
    private OnPressListener onPressListener;

    @Override
    public void rotateDone(int index) {
        if (mLuckyRoundItemSelectedListener != null) {
            mLuckyRoundItemSelectedListener.LuckyRoundItemSelected(index);
        }
    }

    @Override
    public void onDrawCalled() {
        pielView.updateView();
    }

    @Override
    public void onSegmentClick(LuckyItem segmentIndex) {
        if (onPressListener!=null){
            onPressListener.OnClickSegment(segmentIndex);
        }
    }



    public interface LuckyRoundItemSelectedListener {
        void LuckyRoundItemSelected(int index);
    }
 public interface OnPressListener {
        void OnClickSegment(LuckyItem index);
    }

    public void setLuckyRoundItemSelectedListener(LuckyRoundItemSelectedListener listener) {
        this.mLuckyRoundItemSelectedListener = listener;
    }
  public void setItemSelectedListener(OnPressListener listener) {
        this.onPressListener = listener;
    }

    public LuckyWheelView(Context context) {
        super(context);
        init(context, null);
    }

    public LuckyWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * @param ctx
     * @param attrs
     */
    private void init(Context ctx, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = ctx.obtainStyledAttributes(attrs, R.styleable.LuckyWheelView);
            mBackgroundColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwBackgroundColor, 0xffcc0000);
            mTextColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwTextColor, Color.WHITE);
            mCursorImage = typedArray.getDrawable(R.styleable.LuckyWheelView_lkwCursor);
            mCenterImage = typedArray.getDrawable(R.styleable.LuckyWheelView_lkwCenterImage);
            mEdgeWidth = typedArray.getInt(R.styleable.LuckyWheelView_lkwEdgeWidth, 10);
            mBorderColor = typedArray.getColor(R.styleable.LuckyWheelView_lkwEdgeColor, 0);

            typedArray.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.lucky_wheel_layout, this, false);

        pielView = (PielView) frameLayout.findViewById(R.id.pieView);
        ivCursorView = (ImageView) frameLayout.findViewById(R.id.cursorView);
        progressBar=(ProgressBar) frameLayout.findViewById(R.id.progress_vv);

        pielView.setPieRotateListener(this);
        pielView.setPieBackgroundColor(mBackgroundColor);
        pielView.setPieCenterImage(mCenterImage);
        pielView.setPieTextColor(mTextColor);
        pielView.setObserver(this);
        pielView.setOnSpinWheelClickListener(this);


       // pielView.setWheelStrokeColor(wheelStrokeColor);

        pielView.setBorderColor(mBorderColor);
        pielView.setBorderWidth(mEdgeWidth);

        ivCursorView.setImageDrawable(mCursorImage);

        addView(frameLayout);
    }

    public void setBorderColor(int color) {
        pielView.setBorderColor(color);
    }

//    public void setWheelStrokeColor(int color) {
//        pielView.setWheelStrokeColor(color);
//    }

    public void setBorderWidth(int borderWidth) {
        pielView.setBorderWidth(borderWidth);
    }

    public void setLuckyWheelBackgrouldColor(int color) {
        pielView.setPieBackgroundColor(color);
    }

    public void setLuckyWheelCursorImage(int drawable) {
        ivCursorView.setBackgroundResource(drawable);
    }

    public void setLuckyWheelCenterImage(Drawable drawable) {
        pielView.setPieCenterImage(drawable);
    }

    public void setLuckyWheelTextColor(int color) {
        pielView.setPieTextColor(color);
    }

    /**
     * @param data
     */
    public void setData(List<LuckyItem> data) {
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            pielView.setData(data);
        },0);

    }

    /**
     * @param numberOfRound
     */
    public void setRound(int numberOfRound) {
        pielView.setRound(numberOfRound);
    }

    public void startLuckyWheelWithTargetIndex(int index) {
        pielView.rotateTo(index);
    }

    public void setTarget(int target) {
        this.target = target;
    }

    /**
     * Function to rotate wheel to degree
     *
     * @param number Number to rotate
     */
    public void rotateWheelTo(int number) {
        isRotate = true;
       // int tempIndex = number == 0 ? 1 : number;
        pielView.resetRotationLocationToZeroAngle(number);
    }

    public void setLuckyWheelReachTheTarget(OnLuckyWheelReachTheTarget onLuckyWheelReachTheTarget) {
        pielView.setWheelListener(onLuckyWheelReachTheTarget);
    }
}
