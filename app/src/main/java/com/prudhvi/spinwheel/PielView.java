package com.prudhvi.spinwheel;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prudhvi.
 */

public class PielView extends View {
    private RectF mRange = new RectF();
    private RectF mRange1 = new RectF();
    private int mRadius;

    private Paint mArcPaint;
    private Paint mArcPaint_n, draw_ss;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private Paint mTextPaint_n;
    private float mStartAngle = 0;
    private int mCenter;
    private int mPadding;
    private int mTargetIndex;

    private int mRoundOfNumber = 8;
    private boolean isRunning = false;
    Canvas mCanvas;
    private int mEdgeWidth = -1, borderColor = 0;
    private int defaultBackgroundColor = -1;
    private Drawable drawableCenterImage;
    private int textColor = Color.WHITE;
    private List<LuckyItem> mLuckyItemList;
    private PieRotateListener mPieRotateListener;

    private OnSpinWheelClickListener listener;
    private Observer observer;

    boolean isRedraw=false;
    private List<String> paths=new ArrayList<>();

    private int iterationCount=0;
    boolean isLastIteration = false;
    private OnLuckyWheelReachTheTarget mOnLuckyWheelReachTheTarget;
    private OnRotationListener onRotationListener;

    public void updateView() {
        for (int j=0;j<paths.size();j++){
            iterationCount++;
            if(iterationCount==j){
                observer = null;
            }else{
                invalidate();
            }
        }
    }


    public interface PieRotateListener {
        void rotateDone(int index);
    }

    public interface Observer {
        void onDrawCalled();
    }

    public PielView(Context context) {
        super(context);
    }

    public PielView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setObserver(Observer observer) {
        this.observer = observer;
    }

    public void setPieRotateListener(PieRotateListener listener) {
        this.mPieRotateListener = listener;
    }

    public void setBorderWidth(int width) {
        mEdgeWidth = width;
        invalidate();
    }

    public void setBorderColor(int color) {
        borderColor = color;
        invalidate();
    }

    private void init() {
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        //mArcPaint.setFilterBitmap(true);
        // mArcPaint.setAlpha(60);

        mArcPaint_n = new Paint();
        mArcPaint_n.setAntiAlias(true);
        mArcPaint_n.setDither(true);
        mArcPaint_n.setStrokeCap(Paint.Cap.BUTT);

        // mArcPaint_n.setAlpha(20);
        //  mArcPaint_n.setShadowLayer(10, 0, 0, Color.GRAY);

        draw_ss = new Paint();
        draw_ss.setAntiAlias(true);
        draw_ss.setDither(true);
        // draw_ss.setAlpha(40);
        //draw_ss.setShadowLayer(10, 0, 0, Color.GRAY);

        mTextPaint = new Paint();
        mTextPaint_n = new Paint();


        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 25,
                getResources().getDisplayMetrics()));

        Typeface font = ResourcesCompat.getFont(getContext(), R.font.nunito_bold);
        mTextPaint.setTypeface(font);
        mTextPaint.setFakeBoldText(false);
        mTextPaint.setLetterSpacing((float) 0.2);

        mTextPaint_n.setColor(Color.parseColor("#000000"));
        mTextPaint_n.setAlpha(90);
        mTextPaint_n.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                getResources().getDisplayMetrics()));
        mTextPaint_n.setTypeface(font);
        mTextPaint_n.setLetterSpacing((float) 0.40);
        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY) - 30;

        mRange1 = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
    }

    public void setData(List<LuckyItem> luckyItemList) {
        this.mLuckyItemList = luckyItemList;
        invalidate();
    }

    public void setPieBackgroundColor(int color) {
        defaultBackgroundColor = color;
        invalidate();
    }

    public void setPieCenterImage(Drawable drawable) {
        drawableCenterImage = drawable;
        invalidate();
    }

    public void setPieTextColor(int color) {
        textColor = color;
        invalidate();
    }

    private void drawPieBackgroundWithBitmap(Canvas canvas, Bitmap bitmap) {
        canvas.drawBitmap(bitmap, null, new Rect(mPadding / 2, mPadding / 2,
                getMeasuredWidth() - mPadding / 2, getMeasuredHeight() - mPadding / 2), null);
    }

    @SuppressLint("DrawAllocation")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        if (mLuckyItemList == null) {
            return;
        }
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // Calculate the radius and center point of the wheel
        int wheelRadius = Math.min(viewWidth, viewHeight) / 2;
        int centerX = viewWidth / 2;
        int centerY = viewHeight / 2;

        drawBackgroundColor(canvas, defaultBackgroundColor);
        init();
        float tmpAngle = mStartAngle;
        float sweepAngle = 360f / mLuckyItemList.size();

        for (int i = 0; i < mLuckyItemList.size(); i++) {

            if (mLuckyItemList.get(i).c != 0) {

                int bs = mLuckyItemList.get(i).c;
                int alpha = Math.round(255 * 0.15f);
                int semiTransparentColor = ColorUtils.setAlphaComponent(bs, alpha);

                draw_ss.setColor(mLuckyItemList.get(i).c);
                //draw_ss.setStyle(Paint.Style.FILL);
                draw_ss.setShader(new LinearGradient(0, 0, 0, 0, mLuckyItemList.get(i).c, semiTransparentColor, Shader.TileMode.REPEAT));
                canvas.drawArc(mRange, tmpAngle, sweepAngle, true, draw_ss);

                mArcPaint.setStyle(Paint.Style.FILL);
                mArcPaint.setShader(new RadialGradient(getWidth() / 2f, getHeight() / 2f, Math.min(getWidth(), getHeight()) / 2f, Color.parseColor("#ffffff"), mLuckyItemList.get(i).c, Shader.TileMode.CLAMP));

                canvas.drawArc(mRange1, tmpAngle, sweepAngle, true, mArcPaint);

//                float midAngle = tmpAngle + sweepAngle / 2;
//                float shadowStartX = getWidth() / 2f + (float) (mRadius * Math.cos(Math.toRadians(midAngle)));
//                float shadowStartY = getHeight() / 2f + (float) (mRadius * Math.sin(Math.toRadians(midAngle)));
//                float shadowEndX = getWidth() / 2f + (float) (mRadius * Math.cos(Math.toRadians(midAngle + sweepAngle)));
//                float shadowEndY = getHeight() / 2f + (float) (mRadius * Math.sin(Math.toRadians(midAngle + sweepAngle)));
//
//                Path shadowPath = new Path();
//                shadowPath.moveTo(getWidth() / 2f, getHeight() / 2f);
//                shadowPath.lineTo(shadowStartX, shadowStartY);
//                shadowPath.lineTo(shadowEndX, shadowEndY);
//                shadowPath.close();
//                shadowPaint.setColor(mLuckyItemList.get(i).c);
//                canvas.drawArc(mRange1, tmpAngle, sweepAngle, true, shadowPaint);

//                canvas.drawPath(shadowPath, shadowPaint);

            }
            mArcPaint_n.setStyle(Paint.Style.STROKE);

            mArcPaint_n.setStrokeWidth(mEdgeWidth);
            //canvas.drawArc(mRange, tmpAngle,sweepAngle,true,mArcPaint_n);
            float startX = centerX;
            float startY = centerY;
            float endX = (float) (centerX + wheelRadius * Math.cos(Math.toRadians(tmpAngle + sweepAngle / mRadius)));
            float endY = (float) (centerY + wheelRadius * Math.sin(Math.toRadians(tmpAngle + sweepAngle / mRadius)));
            // Draw the line on the canvas
            canvas.drawLine(startX, startY, endX, endY, mArcPaint_n);

            if (mLuckyItemList.get(i).t != null) {
                drawText(canvas, tmpAngle, sweepAngle, mLuckyItemList.get(i).t);
            } else {
                int k = i;
                if (!isRedraw) {
                    ImageDownloader downloader = new ImageDownloader(mBitmap -> {
                        LuckyItem item = mLuckyItemList.get(k);
                        item.bitmap = mBitmap;
                        Log.e("redraw calling", String.valueOf(k));
                        isRedraw = true;
                        paths.add(k+"path");
                        if (observer != null) {
                            observer.onDrawCalled();
                        }
                    });
                    downloader.execute(mLuckyItemList.get(i).path);
                }
                if (mLuckyItemList.get(i).bitmap != null) {
                    drawImage(canvas, tmpAngle, mLuckyItemList.get(i).bitmap);
                } else {
                    drawImage(canvas, tmpAngle, BitmapFactory.decodeResource(getResources(), R.drawable.center_mg));
                }

            }
            if (mLuckyItemList.get(i).st.contains("scratch")) {
                drawTextN(canvas, tmpAngle, sweepAngle, " " + mLuckyItemList.get(i).st);
            } else {
                drawTextN(canvas, tmpAngle, sweepAngle, mLuckyItemList.get(i).st);
            }
            tmpAngle += sweepAngle;
        }
        drawCenterImage(canvas, drawableCenterImage);

        Log.e("calling redraw","Yes");

    }


    private void drawBackgroundColor(Canvas canvas, int color) {
        if (color == -1)
            return;
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(color);
        canvas.drawCircle(mCenter, mCenter, mCenter, mBackgroundPaint);
    }

    /**
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());

        mPadding = getPaddingLeft() == 0 ? 5 : getPaddingLeft();
        mRadius = width - mPadding * 2;

        mCenter = width / 2;

        setMeasuredDimension(width, width);
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param bitmap
     */
    private void drawImage(Canvas canvas, float tmpAngle, Bitmap bitmap) {
        int imgWidth = (canvas.getWidth() / mLuckyItemList.size());
        int desiredHeight = ((int) ((float) imgWidth / bitmap.getWidth() * bitmap.getHeight()));
        if (imgWidth > 100) {
            imgWidth = imgWidth / 2;
            desiredHeight = desiredHeight / 2;
        } else {
            imgWidth = imgWidth - 30;
            desiredHeight = desiredHeight - 30;
        }
        float angle = (float) ((tmpAngle + 360 / mLuckyItemList.size() / 2) * Math.PI / 180);

        int x = (int) (mCenter + mRadius / 2.8 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2.8 * Math.sin(angle));

        Rect rect = new Rect(x - imgWidth, y - imgWidth, x + imgWidth, y + imgWidth);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, imgWidth, desiredHeight, false);
        canvas.drawBitmap(resizedBitmap, null, rect, null);
    }

    private void drawCenterImage(Canvas canvas, Drawable drawable) {
        Bitmap bitmap = LuckyWheelUtils.drawableToBitmap(drawable);
        bitmap = Bitmap.createScaledBitmap(bitmap, 150, 150, false);
        canvas.drawBitmap(bitmap, getMeasuredWidth() / 2f - bitmap.getWidth() / 2f, getMeasuredHeight() / 2f - bitmap.getHeight() / 2f, null);
    }

    /**
     * @param canvas
     * @param tmpAngle
     * @param sweepAngle
     * @param mStr
     */
    private void drawText(Canvas canvas, float tmpAngle, float sweepAngle, String mStr) {
        Path path = new Path();

        path.addArc(mRange, tmpAngle, sweepAngle);

        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius * Math.PI / mLuckyItemList.size() / 2 - textWidth / 2);

        int vOffset = mRadius / 2 / 2;

        canvas.drawTextOnPath(mStr, path, hOffset, vOffset - 50, mTextPaint);
    }

    private void drawTextN(Canvas canvas, float tmpAngle, float sweepAngle, String mStr) {
        Path path = new Path();

        path.addArc(mRange, tmpAngle, sweepAngle);

        float textWidth = mTextPaint_n.measureText(mStr);
        int hOffset = (int) (mRadius * Math.PI / mLuckyItemList.size() / 2 - textWidth / 2);

        int vOffset = mRadius / 2 / 2;

        canvas.drawTextOnPath(mStr, path, hOffset, vOffset + 40, mTextPaint_n);
    }

    /**
     * @return
     */
    private float getAngleOfIndexTarget() {
        // int tempIndex = mTargetIndex == 0 ? 1 : mTargetIndex;
        return (360f / mLuckyItemList.size()) * mTargetIndex;
    }

    private float getAngleOfIndexTarget(int target) {
        return (360f / mLuckyItemList.size()) * target;
    }

    /**
     * @param numberOfRound
     */
    public void setRound(int numberOfRound) {
        mRoundOfNumber = numberOfRound;
    }

    /**
     * @param index
     */
    public void rotateTo(int index) {
        if (isRunning) {
            return;
        }
        mTargetIndex = index;
        setRotation(0);
        float targetAngle;
        if (mTargetIndex == 0) {
            targetAngle = 360 * mRoundOfNumber + 225 - getAngleOfIndexTarget() + (360f / mLuckyItemList.size()) / 2;
        } else {
            targetAngle = 360 * mRoundOfNumber + 225 - getAngleOfIndexTarget() + (360f / mLuckyItemList.size()) / 2;
        }

        animate()
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(mRoundOfNumber * 300L + 1500L)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        isRunning = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isRunning = false;
                        if (mPieRotateListener != null) {
                            mPieRotateListener.rotateDone(mTargetIndex);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                })
                .rotation(targetAngle)
                .start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // Get the x and y coordinates of the touch event
                float x = event.getX();
                float y = event.getY();
                // Calculate the angle of the touch point relative to the center of the spin wheel
                double angle = calculateAngle(x, y);
                // Determine which segment was clicked based on the angle
                int clickedSegment = getClickedSegment(angle);
                // Handle the click event for the clicked segment
                handleClickEvent(clickedSegment);
                return true;
        }
        return false;
    }

    private double calculateAngle(float x, float y) {
        // Calculate the angle in radians using atan2 function
        float dx = x - getWidth() / 2f;
        float dy = y - getHeight() / 2f;
        return Math.atan2(dy, dx);
    }

    private int getClickedSegment(double angle) {
        // Calculate the angle per segment based on the total number of segments
        double anglePerSegment = 2 * Math.PI / mLuckyItemList.size();
        // Adjust the angle to be between 0 and 2π
        double adjustedAngle = (angle + 2 * Math.PI) % (2 * Math.PI);
        // Calculate the segment index based on the adjusted angle
        return (int) (adjustedAngle / anglePerSegment);
    }

    private void handleClickEvent(int clickedSegment) {
        // Handle the click event for the clicked segment
        // You can use the clickedSegment index to access the corresponding data in your custom list
        // Perform any desired actions or update the UI based on the selected segment

        if (listener != null) {

            listener.onSegmentClick(mLuckyItemList.get(clickedSegment));
        }
    }

    public interface OnSpinWheelClickListener {
        void onSegmentClick(LuckyItem segmentIndex);
    }

    public void setOnSpinWheelClickListener(OnSpinWheelClickListener listener) {
        this.listener = listener;
    }

    public String convertImageToBase64(String imageUrl) {

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(input);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] getUrlBytes(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + connection.getResponseCode());
            }
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public void setWheelListener(OnLuckyWheelReachTheTarget onLuckyWheelReachTheTarget) {
        mOnLuckyWheelReachTheTarget = onLuckyWheelReachTheTarget;
    }

    public void setOnRotationListener(OnRotationListener onRotationListener) {
        this.onRotationListener = onRotationListener;
    }

    public void rotateWheelToTarget(int target) {

        float wheelItemCenter = 270 - getAngleOfIndexTarget(target) + (360f / mLuckyItemList.size()) / 2;
        int DEFAULT_ROTATION_TIME = 9000;
        animate().setInterpolator(new DecelerateInterpolator())
                .setDuration(DEFAULT_ROTATION_TIME)
                .rotation((360 * 15) + wheelItemCenter)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (mOnLuckyWheelReachTheTarget != null) {
                            mOnLuckyWheelReachTheTarget.onReachTarget();
                        }
                        if (onRotationListener != null) {
                            onRotationListener.onFinishRotation();
                        }
                        clearAnimation();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    /**
     * Function to rotate to zero angle
     *
     * @param targets target to reach
     */
    public void resetRotationLocationToZeroAngle(final int targets) {
        animate().setDuration(0)
                .rotation(0).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        rotateWheelToTarget(targets);
                        clearAnimation();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }

    public void rotateToNew(int index) {
        if (isRunning) {
            return;
        }
        mTargetIndex = index;
        setRotation(0);

        //  初始值對齊 \項目0的右邊界線/，正上方箭頭對到的是項目0的右邊界線
//        float targetAngle = 360 * mRoundOfNumber - getAngleOfIndexTarget() + (360f / mLuckyItemList.size()) / 2;

        //  初始值對齊 \項目0的中間/，正上方箭頭對到的是項目0
        float targetAngle = 360f * mRoundOfNumber - getAngleOfIndexTarget();

        animate()
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(mRoundOfNumber * 1000)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        isRunning = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        isRunning = false;
                        if (mPieRotateListener != null) {
                            mPieRotateListener.rotateDone(mTargetIndex);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                })
                .rotation(targetAngle)
                .start();
    }
}

