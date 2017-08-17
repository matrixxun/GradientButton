package com.bartbergmans.gradientbutton.views;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

import com.bartbergmans.gradientbutton.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import static android.graphics.drawable.GradientDrawable.LINEAR_GRADIENT;

/**
 * A {@link Button} that has a gradient background or stroke.
 *
 * Created by bartbergmans on 07/01/2017.
 */
public class GradientButton extends AppCompatButton {

    public static final int TOP_BOTTOM = 0;
    public static final int TR_BL      = 1;
    public static final int RIGHT_LEFT = 2;
    public static final int BR_TL      = 3;
    public static final int BOTTOM_TOP = 4;
    public static final int BL_TR      = 5;
    public static final int LEFT_RIGHT = 6;
    public static final int TL_BR      = 7;
    private final static String TAG = GradientButton.class.getSimpleName();
    private boolean isCircular;
    private boolean isFilled;
    private int mStroke;
    private int mRippleColor;
    private int mBackgroundColor;
    @Orientation private int mOrientation;
    private int[] mGradient;

    public GradientButton(Context context) {
        this(context, null);
    }

    public GradientButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.buttonStyle);
    }

    public GradientButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setDefaultRippleColor(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GradientButton);

        isFilled = a.getBoolean(R.styleable.GradientButton_filled, false);
        isCircular = a.getBoolean(R.styleable.GradientButton_circular, false);

        mStroke = a.getDimensionPixelSize(R.styleable.GradientButton_stroke, 0);
        mBackgroundColor = a.getColor(R.styleable.GradientButton_fill_color, Color.TRANSPARENT);

        /** another way that use code to setStateListAnimator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(this, "translationZ", dp2px(2), dp2px(10)).setDuration(200));
            animator.addState(new int[]{}, ObjectAnimator.ofFloat(this, "translationZ", dp2px(10), dp2px(2)).setDuration(200));
            setStateListAnimator(animator);
        }
         **/
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            StateListAnimator sla = AnimatorInflater.loadStateListAnimator(getContext(), R.anim.button_state_list_anim_material);
            setStateListAnimator(sla);
        }

        @Orientation
        int orientation = a.getInt(R.styleable.GradientButton_orientation, TOP_BOTTOM);
        mOrientation = orientation;

        if(a.hasValue(R.styleable.GradientButton_gradient)) {
            final int id = a.getResourceId(R.styleable.GradientButton_gradient, 0);

            int[] values = getResources().getIntArray(id);

            mGradient = new int[values.length];
            for(int i = 0; i < values.length; i++) {
                mGradient[i] = ContextCompat.getColor(context, values[i]);
            }
        } else {
            mGradient = new int[] {
                    a.getColor(R.styleable.GradientButton_start_color, Color.BLUE),
                    a.getColor(R.styleable.GradientButton_end_color, Color.GREEN)
            };
        }

        a.recycle();
    }

    private void setDefaultRippleColor(Context context) {
        int[] attrs = new int[] { R.attr.colorControlHighlight };
        TypedArray a = context.obtainStyledAttributes(attrs);
        mRippleColor = a.getColor(0, Color.RED);
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        setBackground(createBackgroundDrawable(width, height));
    }

    private Drawable createBackgroundDrawable(int width, int height) {
        if (isCircular && height > width) {
            width = height;
        } else if (isCircular && width > height) {
            height = width;
        }

        Drawable content = createContentDrawable(width, height);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "RIPPLE APPLIED, with color: " + mRippleColor);
            Drawable mask = createMaskDrawable(width, height);
            ColorStateList stateList = ColorStateList.valueOf(mRippleColor);
            RippleDrawable rippleDrawable = new RippleDrawable(stateList, content, mask);
            return rippleDrawable;
        } else {
            return content;
        }
    }

    private Drawable createMaskDrawable(int width, int height) {
        float[] outerRadii = new float[8];
        Arrays.fill(outerRadii, height / 2);
        RoundRectShape shape = new RoundRectShape(outerRadii, null, null);
        return new ShapeDrawable(shape);
    }

    private Drawable createContentDrawable(int width, int height) {
//        int radius = height / 2;
//
//        Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setDither(true);
//        paint.setShader(createGradient(width, height));
//
//        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius, paint);
//
//        if(!isFilled) {
//            Paint background = new Paint();
//            background.setAntiAlias(true);
//            background.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//            background.setColor(mBackgroundColor);
//
//            int innerRadius = (height - mStroke) / 2;
//            canvas.drawRoundRect(new RectF(mStroke, mStroke, width - mStroke, height - mStroke),
//                    innerRadius, innerRadius, background);
//        }
//
//        return new BitmapDrawable(getResources(), bitmap);

        InsetDrawable insetDrawable = (InsetDrawable)ContextCompat.getDrawable(getContext(),R.drawable.felin_btn_default_mtrl_shape);
//        Rect rect = new Rect();
//        insetDrawable.getPadding(rect);
//        StrokeInsetDrawable strokeDrawable = new StrokeInsetDrawable(insetDrawable,0);
        GradientDrawable gradientDrawable = (GradientDrawable)insetDrawable.getDrawable();
        gradientDrawable.setOrientation(GradientDrawable.Orientation.BL_TR);
        gradientDrawable = (GradientDrawable)gradientDrawable.mutate();
        gradientDrawable.setColors(mGradient);
        gradientDrawable.setCornerRadius(height/2);
        gradientDrawable.setGradientType(LINEAR_GRADIENT);
//        gradientDrawable.setStroke(dp2px(3), Color.LTGRAY);
        //gradientDrawable.setColor(Color.RED);

        return insetDrawable;
    }

    private LinearGradient createGradient(int width, int height) {
        Shader.TileMode mode = Shader.TileMode.CLAMP;
        switch (mOrientation) {
            default:
            case TOP_BOTTOM:
                return new LinearGradient(width / 2, 0, width / 2, height, mGradient, null, mode);
            case TR_BL:
                return new LinearGradient(width, 0, 0, height, mGradient, null, mode);
            case RIGHT_LEFT:
                return new LinearGradient(width, height / 2, 0, height / 2, mGradient, null, mode);
            case BR_TL:
                return new LinearGradient(width, height, 0, 0, mGradient, null, mode);
            case BOTTOM_TOP:
                return new LinearGradient(width / 2, height, width / 2, 0, mGradient, null, mode);
            case BL_TR:
                return new LinearGradient(0, height, width, 0, mGradient, null, mode);
            case LEFT_RIGHT:
                return new LinearGradient(0, height / 2, width, height / 2, mGradient, null, mode);
            case TL_BR:
                return new LinearGradient(0, 0, width, height, mGradient, null, mode);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TOP_BOTTOM, TR_BL, RIGHT_LEFT, BR_TL, BOTTOM_TOP, BL_TR, LEFT_RIGHT, TL_BR})
    public @interface Orientation {
    }


    private static int dp2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    class StrokeInsetDrawable extends InsetDrawable {
        private Paint strokePaint;
        private static final int WIDTH = 4;

        public StrokeInsetDrawable(@Nullable Drawable drawable, int inset) {
            super(drawable, inset);
            setupStrokePaint();
        }

        public StrokeInsetDrawable(@Nullable Drawable drawable, int insetLeft, int insetTop, int insetRight, int insetBottom) {
            super(drawable, insetLeft, insetTop, insetRight, insetBottom);
            setupStrokePaint();
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            super.draw(canvas);
            float innerRadius = (getBounds().height() - dp2px(WIDTH)) / 2;
            InsetDrawable inset = (InsetDrawable)getDrawable();
            Rect rect = new Rect();
            inset.getPadding(rect);

//            canvas.drawRoundRect(new RectF(getBounds().left+dp2px(4), getBounds().top+dp2px(6),
//                            getBounds().right-dp2px(4), getBounds().bottom-dp2px(6)),
//                    innerRadius, innerRadius, strokePaint);
        }

        private void setupStrokePaint(){
            strokePaint = new Paint();
            strokePaint.setDither(true);
            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setAntiAlias(true);
            strokePaint.setStrokeWidth(dp2px(WIDTH));
            strokePaint.setColor(Color.GRAY);
        }

    }

}