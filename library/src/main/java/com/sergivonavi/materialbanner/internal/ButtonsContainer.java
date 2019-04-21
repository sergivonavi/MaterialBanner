/*
 * Copyright 2019 Sergey Ivanov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sergivonavi.materialbanner.internal;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.sergivonavi.materialbanner.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.DimenRes;
import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;
import androidx.core.view.ViewCompat;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class ButtonsContainer extends ViewGroup {

    @IntDef({HORIZONTAL, VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    private @interface OrientationMode {}

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private MaterialButton mLeftButton;
    private MaterialButton mRightButton;

    private int mButtonMarginEnd;
    private int mButtonMarginBottom;

    private int mOrientation;

    public ButtonsContainer(Context context) {
        this(context, null);
    }

    public ButtonsContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ButtonsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mButtonMarginEnd = getDimen(R.dimen.mb_button_margin_end);
        mButtonMarginBottom = getDimen(R.dimen.mb_button_margin_bottom);

        MarginLayoutParams layoutParams = new MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutParams.setMarginEnd(mButtonMarginEnd);
        } else {
            layoutParams.rightMargin = mButtonMarginEnd;
        }
        layoutParams.bottomMargin = mButtonMarginBottom;

        mLeftButton = new MaterialButton(context, null, R.attr.borderlessButtonStyle);
        mLeftButton.setId(R.id.mb_button_left);
        mLeftButton.setSingleLine(true);
        mLeftButton.setMaxLines(1);
        mLeftButton.setMinWidth(0);
        mLeftButton.setMinimumWidth(0);
        mLeftButton.setLayoutParams(layoutParams);
        mLeftButton.setVisibility(GONE);

        mRightButton = new MaterialButton(context, null, R.attr.borderlessButtonStyle);
        mRightButton.setId(R.id.mb_button_right);
        mRightButton.setSingleLine(true);
        mRightButton.setMaxLines(1);
        mRightButton.setMinWidth(0);
        mRightButton.setMinimumWidth(0);
        mRightButton.setLayoutParams(layoutParams);
        mRightButton.setVisibility(GONE);

        addView(mLeftButton);
        addView(mRightButton);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthUsed = 0;

        if (mLeftButton.getVisibility() != GONE) {
            measureChildWithMargins(mLeftButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
            widthUsed += mLeftButton.getMeasuredWidth() + mButtonMarginEnd;
        }

        if (mRightButton.getVisibility() != GONE) {
            measureChildWithMargins(mRightButton, widthMeasureSpec, 0, heightMeasureSpec, 0);
            widthUsed += mRightButton.getMeasuredWidth() + mButtonMarginEnd;
        }

        // Allow orientation change only when the both buttons are not hidden
        if (mLeftButton.getVisibility() != GONE && mRightButton.getVisibility() != GONE) {
            if (widthUsed > MeasureSpec.getSize(widthMeasureSpec)) {
                mOrientation = VERTICAL;
            } else {
                mOrientation = HORIZONTAL;
            }
        }

        if (mOrientation == VERTICAL) {
            measureVertical();
        } else {
            measureHorizontal();
        }
    }

    /**
     * Measures the children when the orientation of this view is set to {@link #VERTICAL}.
     */
    private void measureVertical() {
        int widthUsed = 0;
        int heightUsed = 0;

        if (mLeftButton.getVisibility() != GONE) {
            widthUsed = mLeftButton.getMeasuredWidth() + mButtonMarginEnd;
            heightUsed += mLeftButton.getMeasuredHeight() + mButtonMarginBottom;
        }

        if (mRightButton.getVisibility() != GONE) {
            widthUsed = Math.max(widthUsed, mRightButton.getMeasuredWidth() + mButtonMarginEnd);
            heightUsed += mRightButton.getMeasuredHeight() + mButtonMarginBottom;
        }

        setMeasuredDimension(widthUsed, heightUsed);
    }

    /**
     * Measures the children when the orientation of this view is set to {@link #HORIZONTAL}.
     */
    private void measureHorizontal() {
        int widthUsed = 0;
        int heightUsed = 0;

        if (mLeftButton.getVisibility() != GONE) {
            widthUsed += mLeftButton.getMeasuredWidth() + mButtonMarginEnd;
            heightUsed = mLeftButton.getMeasuredHeight() + mButtonMarginBottom;
        }

        if (mRightButton.getVisibility() != GONE) {
            widthUsed += mRightButton.getMeasuredWidth() + mButtonMarginEnd;
            heightUsed = Math.max(heightUsed,
                    mRightButton.getMeasuredHeight() + mButtonMarginBottom);
        }

        setMeasuredDimension(widthUsed, heightUsed);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mOrientation == VERTICAL) {
            layoutVertical();
        } else {
            layoutHorizontal();
        }
    }

    /**
     * Position the children during a layout pass if the orientation of this view is set to
     * {@link #VERTICAL}.
     */
    private void layoutVertical() {
        int top = 0;
        int lBtnRight = getMeasuredWidth() - mButtonMarginEnd;
        int lBtnLeft = lBtnRight - mLeftButton.getMeasuredWidth();
        int rBtnRight = getMeasuredWidth() - mButtonMarginEnd;
        int rBtnLeft = rBtnRight - mRightButton.getMeasuredWidth();

        if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            lBtnLeft = mButtonMarginEnd;
            lBtnRight = lBtnLeft + mLeftButton.getMeasuredWidth();
            rBtnLeft = mButtonMarginEnd;
            rBtnRight = rBtnLeft + mRightButton.getMeasuredWidth();
        }

        if (mRightButton.getVisibility() != GONE) {
            mRightButton.layout(rBtnLeft, top, rBtnRight, mRightButton.getMeasuredHeight());
            top += mRightButton.getMeasuredHeight() + mButtonMarginBottom;
        }

        if (mLeftButton.getVisibility() != GONE) {
            mLeftButton.layout(lBtnLeft, top, lBtnRight, top + mLeftButton.getMeasuredHeight());
        }
    }

    /**
     * Position the children during a layout pass if the orientation of this view is set to
     * {@link #HORIZONTAL}.
     */
    private void layoutHorizontal() {
        int lBtnRight = mLeftButton.getMeasuredWidth();
        int lBtnLeft = 0;
        int rBtnRight = getMeasuredWidth() - mButtonMarginEnd;
        int rBtnLeft = rBtnRight - mRightButton.getMeasuredWidth();

        if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
            rBtnLeft = mButtonMarginEnd;
            rBtnRight = rBtnLeft + mRightButton.getMeasuredWidth();
            lBtnRight = getMeasuredWidth();
            lBtnLeft = lBtnRight - mLeftButton.getMeasuredWidth();
        }

        if (mLeftButton.getVisibility() != GONE) {
            mLeftButton.layout(lBtnLeft, 0, lBtnRight, mLeftButton.getMeasuredHeight());
        }

        if (mRightButton.getVisibility() != GONE) {
            mRightButton.layout(rBtnLeft, 0, rBtnRight, mRightButton.getMeasuredHeight());
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MarginLayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    /**
     * Returns the baseline of the left button if it's not hidden or the baseline of the right
     * button. If both buttons hidden returns -1.
     */
    @Override
    public int getBaseline() {
        if (mLeftButton.getVisibility() != GONE && mLeftButton.getText() != null) {
            return mLeftButton.getBaseline();
        } else if (mRightButton.getVisibility() != GONE && mRightButton.getText() != null) {
            return mRightButton.getBaseline();
        }
        return -1;
    }

    /**
     * Should the layout be a column or a row.
     *
     * @param orientation {@link #HORIZONTAL} or {@link #VERTICAL}. Default value is
     *                    {@link #HORIZONTAL}.
     */
    public void setOrientation(@OrientationMode int orientation) {
        if (mOrientation != orientation) {
            mOrientation = orientation;
            requestLayout();
        }
    }

    /**
     * Returns the current orientation.
     *
     * @return either {@link #HORIZONTAL} or {@link #VERTICAL}
     */
    @OrientationMode
    public int getOrientation() {
        return mOrientation;
    }

    public MaterialButton getLeftButton() {
        return mLeftButton;
    }

    public MaterialButton getRightButton() {
        return mRightButton;
    }

    private int getDimen(@DimenRes int dimenId) {
        return getContext().getResources().getDimensionPixelSize(dimenId);
    }
}
