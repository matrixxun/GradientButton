package com.bartbergmans.gradientbutton.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;


import com.bartbergmans.gradientbutton.R;

import static android.graphics.drawable.GradientDrawable.LINEAR_GRADIENT;

/**
 * A powerful button support Gradient and common usage.
 *
 * Created by donghua.xdh on 08/19/2017.
 */
public class FelinPowerfulButton extends AppCompatButton {

    private final static String TAG = FelinPowerfulButton.class.getSimpleName();

    private static final int UNABLE_TEXT_COLOR_DEFAULT = 0xFFA5A6A6;
    private static final int UNABLE_BACKGROUND_COLOR_DEFAULT = 0xFFDFDFDF;
    private static final int FILLED_BACKGROUND_COLOR_DEFAULT = 0xFFE0E0E0;

    public static final int TOP_BOTTOM = 0;
    public static final int TR_BL      = 1;
    public static final int RIGHT_LEFT = 2;
    public static final int BR_TL      = 3;
    public static final int BOTTOM_TOP = 4;
    public static final int BL_TR      = 5;
    public static final int LEFT_RIGHT = 6;
    public static final int TL_BR      = 7;

    public static final int SHAPE_TYPE_RECTANGLE = 0;
    public static final int SHAPE_TYPE_ROUND_RECT = 1;

    public static final int BUTTON_TYPE_RAISED = 0;
    public static final int BUTTON_TYPE_FLAT_COLOR = 1;
    public static final int BUTTON_TYPE_FLAT_BORDERLESS = 2;

    private boolean isFilled;
    private int mStroke;
    private int mStrokeColor;
    private int mButtonType;
    private int mShapeType;
    GradientDrawable.Orientation mGradientOrientation;

    private int mRippleColor;
    private int mFilledBackgroundColor;
    private int mUnableBackgroundColor;

    private int[] mGradient;

    //text color
    private int mNormalTextColor = 0;
    private int mUnableTextColor = 0;
    ColorStateList mTextColorStateList;
    private int[][] states = new int[3][];

    private int mCornerRadius = 0;

    public FelinPowerfulButton(Context context) {
        this(context, null);
    }

    public FelinPowerfulButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.buttonStyle);
    }

    public FelinPowerfulButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDefaultRippleColor(context);

        states[0] = new int[] { android.R.attr.state_enabled, android.R.attr.state_pressed };//pressed
        states[1] = new int[] { android.R.attr.state_enabled }; //normal
        states[2] = new int[] { -android.R.attr.state_enabled}; //unable


        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FelinPowerfulButton);

        mTextColorStateList = getTextColors();
        mNormalTextColor = a.getColor(R.styleable.FelinPowerfulButton_fpb_normal_text_color, mTextColorStateList.getColorForState(states[1], getCurrentTextColor()));
        mUnableTextColor = a.getColor(R.styleable.FelinPowerfulButton_fpb_unable_text_color, UNABLE_TEXT_COLOR_DEFAULT);
        setTextColor();

        mUnableBackgroundColor = a.getColor(R.styleable.FelinPowerfulButton_fpb_unable_background_color, UNABLE_BACKGROUND_COLOR_DEFAULT);

        mButtonType = a.getInt(R.styleable.FelinPowerfulButton_fpb_button_type, BUTTON_TYPE_RAISED);
        mShapeType = a.getInt(R.styleable.FelinPowerfulButton_fpb_shape_type, SHAPE_TYPE_RECTANGLE);

        isFilled = a.getBoolean(R.styleable.FelinPowerfulButton_fpb_filled, false);
        mFilledBackgroundColor = a.getColor(R.styleable.FelinPowerfulButton_fpb_fill_color, FILLED_BACKGROUND_COLOR_DEFAULT);

        mStroke = a.getDimensionPixelSize(R.styleable.FelinPowerfulButton_fpb_stroke, 0);
        mStrokeColor = a.getColor(R.styleable.FelinPowerfulButton_fpb_stroke_color, Color.parseColor("#bdbdbd"));

        mCornerRadius = a.getDimensionPixelSize(R.styleable.FelinPowerfulButton_fpb_corner_radius, dp2px(2));

        /** another way that use code to setStateListAnimator
         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
         StateListAnimator animator = new StateListAnimator();
         animator.addState(new int[]{android.R.attr.state_pressed}, ObjectAnimator.ofFloat(this, "translationZ", dp2px(2), dp2px(10)).setDuration(200));
         animator.addState(new int[]{}, ObjectAnimator.ofFloat(this, "translationZ", dp2px(10), dp2px(2)).setDuration(200));
         setStateListAnimator(animator);
         }
         **/
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            StateListAnimator sla = AnimatorInflater.loadStateListAnimator(getContext(), R.anim.button_state_list_anim_material);
//            setStateListAnimator(sla);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && (mButtonType == BUTTON_TYPE_FLAT_COLOR || mButtonType==BUTTON_TYPE_FLAT_BORDERLESS)) {
            setStateListAnimator(null);
        }

        mGradientOrientation = convertGradientOrientation(a.getInt(R.styleable.FelinPowerfulButton_fpb_orientation, TOP_BOTTOM));


        if(a.hasValue(R.styleable.FelinPowerfulButton_fpb_gradient)) {
            final int id = a.getResourceId(R.styleable.FelinPowerfulButton_fpb_gradient, 0);

            int[] values = getResources().getIntArray(id);

            mGradient = new int[values.length];
            for(int i = 0; i < values.length; i++) {
                mGradient[i] = ContextCompat.getColor(context, values[i]);
            }
        } else {
            mGradient = new int[] {
                    a.getColor(R.styleable.FelinPowerfulButton_fpb_start_color, FILLED_BACKGROUND_COLOR_DEFAULT),
                    a.getColor(R.styleable.FelinPowerfulButton_fpb_end_color, FILLED_BACKGROUND_COLOR_DEFAULT)
            };
        }

        a.recycle();
    }

    private void setDefaultRippleColor(Context context) {
        int[] attrs = new int[] { R.attr.colorControlHighlight};
        TypedArray a = context.obtainStyledAttributes(attrs);
        mRippleColor = a.getColor(0, Color.RED);
        a.recycle();
    }

    private void setTextColor() {
        int[] colors = new int[] {mNormalTextColor, mNormalTextColor,mUnableTextColor,};
        mTextColorStateList = new ColorStateList(states, colors);
        setTextColor(mTextColorStateList);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldHeight, oldHeight);
        setBackgroundDrawable(createBackgroundDrawable(width, height));
        setPadding(dp2px(8),dp2px(4),dp2px(8),dp2px(4));
    }


    private Drawable createBackgroundDrawable(int width, int height) {
        Drawable content = createContentDrawable(width, height);
        return content;
    }

    private Drawable createContentDrawable(int width, int height) {

        Drawable resultDrawable;
        GradientDrawable gradientDrawableNormal = new GradientDrawable();
        GradientDrawable gradientDrawableDisable = new GradientDrawable();
        gradientDrawableDisable.setColor(mUnableBackgroundColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (mButtonType == BUTTON_TYPE_FLAT_BORDERLESS) {
                RippleDrawable rippleDrawable = (RippleDrawable) ContextCompat.getDrawable(getContext(), R.drawable.fpb_btn_borderless_ripple);
                return rippleDrawable;
            } else {
                RippleDrawable rippleDrawable = (RippleDrawable) ContextCompat.getDrawable(getContext(), R.drawable.felin_btn_default_mtrl_shape_);

                StateListDrawable stateListDrawable = new StateListDrawable();
                stateListDrawable.addState(states[1], gradientDrawableNormal);
                stateListDrawable.addState(states[2], gradientDrawableDisable);
                InsetDrawable insetDrawable = new InsetDrawable(stateListDrawable, dp2px(4), dp2px(6), dp2px(4), dp2px(6));
                rippleDrawable.setDrawableByLayerId(R.id.ff, insetDrawable);
                //rippleDrawable.mutate();
                resultDrawable = rippleDrawable;
            }

        }else{
            GradientDrawable gradientDrawablePressed = new GradientDrawable();
            if(isFilled){
                int pressedColor = darkenColor(mFilledBackgroundColor);
                gradientDrawablePressed.setColor(pressedColor);
                gradientDrawablePressed.setShape(GradientDrawable.RECTANGLE);
                //
                if(mShapeType == SHAPE_TYPE_ROUND_RECT){
                    gradientDrawablePressed.setCornerRadius(height / 2);
                }else{
                    gradientDrawablePressed.setCornerRadius(mCornerRadius);
                }
                gradientDrawablePressed.setGradientType(LINEAR_GRADIENT);

                if (mStroke > 0) {
                    gradientDrawablePressed.setStroke(mStroke, mStrokeColor);
                }
                gradientDrawablePressed.setSize(width,height);

            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                int[] colors = new int[mGradient.length];
                for(int i = 0; i < colors.length;i++){
                    colors[i] = darkenColor(mGradient[i]);
                }
                gradientDrawablePressed.setShape(GradientDrawable.RECTANGLE);
                //
                if(mShapeType == SHAPE_TYPE_ROUND_RECT){
                    gradientDrawablePressed.setCornerRadius(height / 2);
                }else{
                    gradientDrawablePressed.setCornerRadius(mCornerRadius);
                }
                gradientDrawablePressed.setOrientation(mGradientOrientation);
                gradientDrawablePressed.setColors(colors);
                gradientDrawablePressed.setGradientType(LINEAR_GRADIENT);

                if (mStroke > 0) {
                    gradientDrawablePressed.setStroke(mStroke, mStrokeColor);
                }
                gradientDrawablePressed.setSize(width,height);
                //
            }else{
                gradientDrawablePressed.setShape(GradientDrawable.RECTANGLE);
                //
                if(mShapeType == SHAPE_TYPE_ROUND_RECT){
                    gradientDrawablePressed.setCornerRadius(height / 2);
                }else{
                    gradientDrawablePressed.setCornerRadius(mCornerRadius);
                }
                gradientDrawablePressed.setGradientType(LINEAR_GRADIENT);

                if (mStroke > 0) {
                    gradientDrawablePressed.setStroke(mStroke, mStrokeColor);
                }
                gradientDrawablePressed.setSize(width,height);
                gradientDrawablePressed.setColor(darkenColor(mGradient[0]));
            }

            if(mButtonType == BUTTON_TYPE_FLAT_BORDERLESS){
                gradientDrawablePressed.setColor(FILLED_BACKGROUND_COLOR_DEFAULT);
            }
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(states[0],gradientDrawablePressed);
            stateListDrawable.addState(states[1],gradientDrawableNormal);
            stateListDrawable.addState(states[2],gradientDrawableDisable);

            resultDrawable = new InsetDrawable(stateListDrawable,dp2px(4),dp2px(6),dp2px(4),dp2px(6));
        }

        gradientDrawableNormal.setShape(GradientDrawable.RECTANGLE);
        gradientDrawableDisable.setShape(GradientDrawable.RECTANGLE);

        if(mShapeType == SHAPE_TYPE_ROUND_RECT){
            gradientDrawableNormal.setCornerRadius(height / 2);
            gradientDrawableDisable.setCornerRadius(height / 2);

        }else{
            gradientDrawableNormal.setCornerRadius(mCornerRadius);
            gradientDrawableDisable.setCornerRadius(mCornerRadius);
        }

//        gradientDrawableNormal.setSize(width, height);
//        gradientDrawableDisable.setSize(width,height);

        if(mButtonType == BUTTON_TYPE_FLAT_BORDERLESS){
            gradientDrawableNormal.setColor(Color.TRANSPARENT);
            return resultDrawable;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            gradientDrawableNormal.setOrientation(mGradientOrientation);
            gradientDrawableNormal.setColors(mGradient);
        }else{
            gradientDrawableNormal.setColor(mGradient[0]);
        }

        gradientDrawableNormal.setGradientType(LINEAR_GRADIENT);


        if (mStroke > 0) {
            gradientDrawableNormal.setStroke(mStroke, mStrokeColor);
            gradientDrawableDisable.setStroke(mStroke, mStrokeColor);
        }
        if (isFilled) {
            gradientDrawableNormal.setColor(mFilledBackgroundColor);
        }
        resultDrawable.mutate();
        return resultDrawable;
    }

    private GradientDrawable.Orientation convertGradientOrientation(int attrOrientation) {
        switch (attrOrientation) {
            default:
            case TOP_BOTTOM:
                return GradientDrawable.Orientation.TOP_BOTTOM;
            case TR_BL:
                return GradientDrawable.Orientation.TR_BL;
            case RIGHT_LEFT:
                return GradientDrawable.Orientation.RIGHT_LEFT;
            case BR_TL:
                return GradientDrawable.Orientation.BR_TL;
            case BOTTOM_TOP:
                return GradientDrawable.Orientation.BOTTOM_TOP;
            case BL_TR:
                return GradientDrawable.Orientation.BL_TR;
            case LEFT_RIGHT:
                return GradientDrawable.Orientation.LEFT_RIGHT;
            case TL_BR:
                return GradientDrawable.Orientation.TL_BR;
        }
    }


    private static int dp2px(float dipValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8F;
        return Color.HSVToColor(hsv);
    }

}