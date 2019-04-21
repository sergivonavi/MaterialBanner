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

package com.sergivonavi.materialbanner;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.google.android.material.button.MaterialButton;
import com.sergivonavi.materialbanner.internal.ButtonsContainer;
import com.sergivonavi.materialbanner.internal.MessageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.TextViewCompat;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.widget.RelativeLayout.ALIGN_BASELINE;
import static android.widget.RelativeLayout.ALIGN_PARENT_END;
import static android.widget.RelativeLayout.ALIGN_PARENT_LEFT;
import static android.widget.RelativeLayout.ALIGN_PARENT_RIGHT;
import static android.widget.RelativeLayout.ALIGN_PARENT_START;
import static android.widget.RelativeLayout.BELOW;
import static android.widget.RelativeLayout.END_OF;
import static android.widget.RelativeLayout.LEFT_OF;
import static android.widget.RelativeLayout.RIGHT_OF;
import static android.widget.RelativeLayout.START_OF;
import static android.widget.RelativeLayout.TRUE;

/**
 * A banner displays an important, succinct message, and provides actions for users to address
 * (or dismiss the banner). It requires a user action to be dismissed.
 * <p>
 * Banners should be displayed at the top of the screen, below a top app bar. They are persistent
 * and nonmodal, allowing the user to either ignore them or interact with them at any time.
 * </p>
 * <p>
 * Banners can contain up to two action buttons which are set via {@link #setLeftButton} and
 * {@link #setRightButton} methods.
 * </p>
 * <p>
 * To be notified when a banner has been shown or dismissed, you can provide a
 * {@link BannerInterface.OnShowListener} and {@link BannerInterface.OnDismissListener} via
 * {@link #setOnShowListener(OnShowListener)} and {@link #setOnDismissListener(OnDismissListener)}.
 * </p>
 * <h3>Design Guides</h3>
 * <p>For the style and usage guidelines read the
 * <a href="https://material.io/design/components/banners.html" target="_top">Banners - Material Design</a>.
 * </p>
 */
public class Banner extends ViewGroup implements BannerInterface {
    private static final String TAG = "Banner";
    private static final boolean DEBUG = false;

    @IntDef(value = {VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface Visibility {}

    private static final int LAYOUT_UNDEFINED = -1;
    private static final int LAYOUT_SINGLE_LINE = 0;
    private static final int LAYOUT_MULTILINE = 1;

    private RelativeLayout mContentContainer;
    private AppCompatImageView mIconView;
    private MessageView mMessageView;
    private ButtonsContainer mButtonsContainer;
    private MaterialButton mLeftButton;
    private MaterialButton mRightButton;
    private View mLine;

    private Drawable mIcon;
    private String mMessageText;
    private String mLeftButtonText;
    private String mRightButtonText;

    private int mContainerPaddingTopOneLine;
    private int mContainerPaddingTopMultiline;

    private int mIconSize;
    private int mIconMarginStart;

    private int mMessageMarginStart;
    private int mMessageMarginEndSingleLine;
    private int mMessageMarginEndMultiline;
    private int mMessageMarginBottomMultiline;
    private int mMessageMarginBottomWithIcon;

    private int mLineHeight;

    /**
     * Banner's bottom margin.
     */
    private int mMarginBottom;

    /**
     * Indicates that the device is at least a 10-inch tablet.
     */
    private boolean mWideLayout;

    /**
     * The layout type: {@link #LAYOUT_UNDEFINED} {@link #LAYOUT_SINGLE_LINE} or
     * {@link #LAYOUT_MULTILINE}.
     */
    private int mLayoutType = LAYOUT_UNDEFINED;

    private static final int ANIM_DURATION_DISMISS = 160;
    private static final int ANIM_DURATION_SHOW = 180;

    private boolean mIsAnimating;

    private BannerInterface.OnClickListener mLeftButtonListener;
    private BannerInterface.OnClickListener mRightButtonListener;
    private BannerInterface.OnDismissListener mOnDismissListener;
    private BannerInterface.OnShowListener mOnShowListener;

    public Banner(Context context) {
        this(context, null);
    }

    public Banner(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.bannerStyle);
    }

    public Banner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadDimens(context);
        initViewGroup(context);
        retrieveAttrs(context, attrs, defStyleAttr);
    }

    private void loadDimens(Context context) {
        mWideLayout = context.getResources().getBoolean(R.bool.mb_wide_layout);

        mIconSize = getDimen(R.dimen.mb_icon_size);
        mIconMarginStart = getDimen(R.dimen.mb_icon_margin_start);

        mMessageMarginStart = getDimen(R.dimen.mb_message_margin_start);
        mMessageMarginEndSingleLine = getDimen(R.dimen.mb_message_margin_end_singleline);
        mMessageMarginEndMultiline = getDimen(R.dimen.mb_message_margin_end_multiline);
        mMessageMarginBottomMultiline = getDimen(R.dimen.mb_message_margin_bottom_multiline);
        mMessageMarginBottomWithIcon = getDimen(R.dimen.mb_message_margin_bottom_with_icon);

        mLineHeight = getDimen(R.dimen.mb_line_height);

        mContainerPaddingTopOneLine = getDimen(R.dimen.mb_container_padding_top_singleline);
        mContainerPaddingTopMultiline = getDimen(R.dimen.mb_container_padding_top_multiline);
    }

    private void initViewGroup(Context context) {
        // CONTENT CONTAINER
        LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);

        mContentContainer = new RelativeLayout(context);
        mContentContainer.setId(R.id.mb_container_content);
        mContentContainer.setLayoutParams(layoutParams);

        // ICON VIEW
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                mIconSize, mIconSize);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.setMarginStart(mIconMarginStart);
            relativeLayoutParams.addRule(ALIGN_PARENT_START, TRUE);
        } else {
            relativeLayoutParams.leftMargin = mIconMarginStart;
            relativeLayoutParams.addRule(ALIGN_PARENT_LEFT, TRUE);
        }

        mIconView = new AppCompatImageView(context);
        mIconView.setId(R.id.mb_icon);
        mIconView.setLayoutParams(relativeLayoutParams);
        mIconView.setVisibility(GONE);

        // MESSAGE VIEW
        relativeLayoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.setMarginStart(mMessageMarginStart);
            relativeLayoutParams.addRule(ALIGN_PARENT_START, TRUE);
        } else {
            relativeLayoutParams.leftMargin = mMessageMarginStart;
            relativeLayoutParams.addRule(ALIGN_PARENT_LEFT, TRUE);
        }

        mMessageView = new MessageView(context);
        mMessageView.setId(R.id.mb_message);
        mMessageView.setLayoutParams(relativeLayoutParams);

        // BUTTONS CONTAINER
        relativeLayoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.addRule(ALIGN_PARENT_END, TRUE);
        } else {
            relativeLayoutParams.addRule(ALIGN_PARENT_RIGHT, TRUE);
        }

        mButtonsContainer = new ButtonsContainer(context);
        mButtonsContainer.setId(R.id.mb_container_buttons);
        mButtonsContainer.setLayoutParams(relativeLayoutParams);

        mLeftButton = mButtonsContainer.getLeftButton();
        mRightButton = mButtonsContainer.getRightButton();

        // LINE
        layoutParams = new LayoutParams(MATCH_PARENT, mLineHeight);

        mLine = new View(context);
        mLine.setId(R.id.mb_line);
        mLine.setLayoutParams(layoutParams);

        addView(mContentContainer);
        addView(mLine);

        mContentContainer.addView(mIconView);
        mContentContainer.addView(mMessageView);
        mContentContainer.addView(mButtonsContainer);
    }

    private void retrieveAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Banner, defStyleAttr,
                R.style.Widget_Material_Banner);

        if (a.hasValue(R.styleable.Banner_icon)) {
            setIcon(a.getDrawable(R.styleable.Banner_icon));
        }
        if (a.hasValue(R.styleable.Banner_iconTint)) {
            setIconTintColorInternal(a.getColor(R.styleable.Banner_iconTint, Color.BLACK));
        }
        if (a.hasValue(R.styleable.Banner_messageText)) {
            setMessage(a.getString(R.styleable.Banner_messageText));
        }
        if (a.hasValue(R.styleable.Banner_buttonLeftText)) {
            setLeftButton(a.getString(R.styleable.Banner_buttonLeftText), null);
        }
        if (a.hasValue(R.styleable.Banner_buttonRightText)) {
            setRightButton(a.getString(R.styleable.Banner_buttonRightText), null);
        }

        if (a.hasValue(R.styleable.Banner_messageTextAppearance)) {
            mMessageView.setTextAppearance(context,
                    a.getResourceId(R.styleable.Banner_messageTextAppearance,
                            R.style.TextAppearance_Banner_Message));
        }
        if (a.hasValue(R.styleable.Banner_buttonsTextAppearance)) {
            int textAppearance = a.getResourceId(R.styleable.Banner_buttonsTextAppearance,
                    R.style.TextAppearance_Banner_Button);
            mLeftButton.setTextAppearance(context, textAppearance);
            mRightButton.setTextAppearance(context, textAppearance);
        }

        if (a.hasValue(R.styleable.Banner_messageTextColor)) {
            mMessageView.setTextColor(a.getColor(R.styleable.Banner_messageTextColor, Color.BLACK));
        }
        if (a.hasValue(R.styleable.Banner_buttonsTextColor)) {
            mLeftButton.setTextColor(a.getColor(R.styleable.Banner_buttonsTextColor, Color.BLACK));
            mRightButton.setTextColor(a.getColor(R.styleable.Banner_buttonsTextColor, Color.BLACK));
        }
        if (a.hasValue(R.styleable.Banner_buttonsRippleColor)) {
            mLeftButton.setRippleColor(ColorStateList.valueOf(
                    a.getColor(R.styleable.Banner_buttonsRippleColor, Color.BLACK)));
            mRightButton.setRippleColor(ColorStateList.valueOf(
                    a.getColor(R.styleable.Banner_buttonsRippleColor, Color.BLACK)));
        }
        if (a.hasValue(R.styleable.Banner_backgroundColor)) {
            setBackgroundColor(a.getColor(R.styleable.Banner_backgroundColor, 0));
        }
        if (a.hasValue(R.styleable.Banner_lineColor)) {
            mLine.setBackgroundColor(a.getColor(R.styleable.Banner_lineColor, Color.BLACK));
        }
        if (a.hasValue(R.styleable.Banner_lineOpacity)) {
            mLine.setAlpha(a.getFloat(R.styleable.Banner_lineOpacity, 0.12f));
        }

        int contentPaddingStart = a.getDimensionPixelSize(R.styleable.Banner_contentPaddingStart,
                0);
        int contentPaddingEnd = a.getDimensionPixelSize(R.styleable.Banner_contentPaddingEnd, 0);
        setContainerPadding(contentPaddingStart, -1, contentPaddingEnd);

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (DEBUG) {
            Log.e("Banner onMeasure w", MeasureSpec.toString(widthMeasureSpec));
            Log.e("Banner onMeasure h", MeasureSpec.toString(heightMeasureSpec));
        }

        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int widthUsed = getContainerHorizontalPadding();
        int heightUsed = 0;

        // Measure the message view
        measureChild(mMessageView, widthMeasureSpec, heightMeasureSpec);
        // Adding the start margin and possible single line end margin
        int messageViewWidth =
                mMessageView.getMeasuredWidth() + mMessageMarginStart + mMessageMarginEndSingleLine;

        // Measure the icon
        int iconViewWidth = 0;
        if (mIcon != null) {
            measureChild(mIconView, widthMeasureSpec, heightMeasureSpec);
            iconViewWidth = mIconView.getMeasuredWidth() + mIconMarginStart;
        }

        measureChild(mButtonsContainer, widthMeasureSpec, heightMeasureSpec);
        int buttonsWidth = mButtonsContainer.getMeasuredWidth();

        // Update the layout params
        if (widthSpecSize - widthUsed - iconViewWidth - buttonsWidth >= messageViewWidth) {
            // The message view fits in one line with the icon and the both buttons
            onSingleLine();
        } else {
            // Doesn't fit
            onMultiline();
        }

        measureChild(mContentContainer, widthMeasureSpec, heightMeasureSpec);
        measureChild(mLine, widthMeasureSpec, heightMeasureSpec);

        widthUsed = mContentContainer.getMeasuredWidth();
        heightUsed = mContentContainer.getMeasuredHeight() + mLine.getMeasuredHeight();

        setMeasuredDimension(widthUsed, heightUsed);
    }

    private void onSingleLine() {
        if (mLayoutType == LAYOUT_SINGLE_LINE) {
            // Skip unnecessary layout params changes. The views already have the correct ones.
            return;
        }

        setContainerPadding(-1, mContainerPaddingTopOneLine, -1);

        RelativeLayout.LayoutParams messageLayoutParams = (RelativeLayout.LayoutParams) mMessageView
                .getLayoutParams();
        RelativeLayout.LayoutParams buttonsContainerLayoutParams =
                (RelativeLayout.LayoutParams) mButtonsContainer
                .getLayoutParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            messageLayoutParams.addRule(START_OF, mButtonsContainer.getId());
            messageLayoutParams.setMarginEnd(mMessageMarginEndSingleLine);
        } else {
            messageLayoutParams.addRule(LEFT_OF, mButtonsContainer.getId());
            messageLayoutParams.rightMargin = mMessageMarginEndSingleLine;
        }
        messageLayoutParams.addRule(ALIGN_BASELINE, mButtonsContainer.getId());
        messageLayoutParams.bottomMargin = 0;
        mMessageView.setLayoutParams(messageLayoutParams);

        buttonsContainerLayoutParams.addRule(ALIGN_BASELINE, 0);
        buttonsContainerLayoutParams.addRule(BELOW, 0);
        mButtonsContainer.setLayoutParams(buttonsContainerLayoutParams);

        mLayoutType = LAYOUT_SINGLE_LINE;
    }

    private void onMultiline() {
        if (mLayoutType == LAYOUT_MULTILINE) {
            // Skip unnecessary layout params changes. The views already have the correct ones.
            return;
        }

        setContainerPadding(-1, mContainerPaddingTopMultiline, -1);

        RelativeLayout.LayoutParams messageLayoutParams = (RelativeLayout.LayoutParams) mMessageView
                .getLayoutParams();
        RelativeLayout.LayoutParams buttonsContainerLayoutParams =
                (RelativeLayout.LayoutParams) mButtonsContainer
                .getLayoutParams();

        if (mWideLayout) {
            if (mButtonsContainer.getMeasuredWidth()
                    > (getMeasuredWidth() - getContainerHorizontalPadding()) / 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    messageLayoutParams.addRule(START_OF, 0);
                } else {
                    messageLayoutParams.addRule(LEFT_OF, 0);
                }
                messageLayoutParams.bottomMargin = mIcon
                        == null ? mMessageMarginBottomMultiline : mMessageMarginBottomWithIcon;

                buttonsContainerLayoutParams.addRule(BELOW, mMessageView.getId());
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    messageLayoutParams.addRule(START_OF, mButtonsContainer.getId());
                } else {
                    messageLayoutParams.addRule(LEFT_OF, mButtonsContainer.getId());
                }

                buttonsContainerLayoutParams.addRule(ALIGN_BASELINE, mMessageView.getId());
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                messageLayoutParams.addRule(START_OF, 0);
            } else {
                messageLayoutParams.addRule(LEFT_OF, 0);
            }
            messageLayoutParams.bottomMargin =
                    mIcon == null ? mMessageMarginBottomMultiline : mMessageMarginBottomWithIcon;

            buttonsContainerLayoutParams.addRule(BELOW, mMessageView.getId());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            messageLayoutParams.setMarginEnd(mMessageMarginEndMultiline);
        } else {
            messageLayoutParams.rightMargin = mMessageMarginEndMultiline;
        }
        messageLayoutParams.addRule(ALIGN_BASELINE, 0);

        mMessageView.setLayoutParams(messageLayoutParams);
        mButtonsContainer.setLayoutParams(buttonsContainerLayoutParams);

        mLayoutType = LAYOUT_MULTILINE;
    }

    private void updateParamsOnIconChanged() {
        RelativeLayout.LayoutParams messageLayoutParams = (RelativeLayout.LayoutParams) mMessageView
                .getLayoutParams();
        int parentStart = mIcon == null ? TRUE : 0;
        int toEndOfId = mIcon == null ? 0 : mIconView.getId();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            messageLayoutParams.addRule(ALIGN_PARENT_START, parentStart);
            messageLayoutParams.addRule(END_OF, toEndOfId);
        } else {
            messageLayoutParams.addRule(ALIGN_PARENT_LEFT, parentStart);
            messageLayoutParams.addRule(RIGHT_OF, toEndOfId);
        }
        mMessageView.setLayoutParams(messageLayoutParams);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int y = mContentContainer.getMeasuredHeight();
        mContentContainer.layout(0, 0, mContentContainer.getMeasuredWidth(), y);
        mLine.layout(0, y, mLine.getMeasuredWidth(), y + mLine.getMeasuredHeight());
    }

    /**
     * Sets the icon to display in the banner.
     *
     * @param icon The drawable to use as the icon or null if you don't want an icon
     */
    public void setIcon(@Nullable Drawable icon) {
        mIcon = icon;
        if (mIcon != null) {
            mIconView.setVisibility(VISIBLE);
            mIconView.setImageDrawable(icon);
        } else {
            mIconView.setVisibility(GONE);
        }

        updateParamsOnIconChanged();
    }

    /**
     * Sets the icon to display in the banner.
     *
     * @param iconId The resourceId of the drawable to use as the icon
     */
    public void setIcon(@DrawableRes int iconId) {
        setIcon(ContextCompat.getDrawable(getContext(), iconId));
    }

    /**
     * Sets the message to display in the banner.
     *
     * @param text The text to display in the banner
     */
    public void setMessage(String text) {
        mMessageText = text;
        mMessageView.setText(text);
    }

    /**
     * Sets the message to display in the banner using the given resource id.
     *
     * @param textId The resource id of the text to display
     */
    public void setMessage(@StringRes int textId) {
        setMessage(getContext().getString(textId));
    }

    /**
     * Sets a listener to be invoked when the left button of the banner is pressed.
     * <p>
     * Usually used for the dismissive action.
     * </p>
     *
     * @param text     The text to display in the left button
     * @param listener The {@link BannerInterface.OnClickListener} to use
     * @see #setLeftButton(int, BannerInterface.OnClickListener)
     */
    public void setLeftButton(String text, @Nullable BannerInterface.OnClickListener listener) {
        mLeftButtonText = text;
        if (mLeftButtonText != null) {
            mLeftButton.setVisibility(VISIBLE);
            mLeftButton.setText(text);
            setLeftButtonListener(listener);
        } else {
            mLeftButton.setVisibility(GONE);
        }
    }

    /**
     * Sets a listener to be invoked when the left button of the banner is pressed.
     * <p>
     * Usually used for the dismissive action.
     * </p>
     *
     * @param textId   The resource id of the text to display in the left button
     * @param listener The {@link BannerInterface.OnClickListener} to use
     * @see #setLeftButton(String, BannerInterface.OnClickListener)
     */
    public void setLeftButton(@StringRes int textId,
                              @Nullable BannerInterface.OnClickListener listener) {
        setLeftButton(getContext().getString(textId), listener);
    }

    /**
     * Sets a listener to be invoked when the left button of the banner is pressed.
     * <p>
     * Usually used for the dismissive action.
     * </p>
     *
     * @param listener The {@link BannerInterface.OnClickListener} to use
     */
    public void setLeftButtonListener(@Nullable BannerInterface.OnClickListener listener) {
        mLeftButtonListener = listener;
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLeftButtonListener != null) {
                    mLeftButtonListener.onClick(Banner.this);
                }
            }
        });
    }

    /**
     * Sets a listener to be invoked when the right button of the banner is pressed.
     * <p>
     * Usually used for the confirming action.
     * </p>
     *
     * @param text     The text to display in the right button
     * @param listener The {@link BannerInterface.OnClickListener} to use
     * @see #setRightButton(int, BannerInterface.OnClickListener)
     */
    public void setRightButton(String text, @Nullable BannerInterface.OnClickListener listener) {
        mRightButtonText = text;
        if (mRightButtonText != null) {
            mRightButton.setVisibility(VISIBLE);
            mRightButton.setText(text);
            setRightButtonListener(listener);
        } else {
            mRightButton.setVisibility(GONE);
        }
    }

    /**
     * Sets a listener to be invoked when the right button of the banner is pressed.
     * <p>
     * Usually used for the confirming action.
     * </p>
     *
     * @param textId   The resource id of the text to display in the right button
     * @param listener The {@link BannerInterface.OnClickListener} to use
     * @see #setRightButton(String, BannerInterface.OnClickListener)
     */
    public void setRightButton(@StringRes int textId,
                               @Nullable BannerInterface.OnClickListener listener) {
        setRightButton(getContext().getString(textId), listener);
    }

    /**
     * Sets a listener to be invoked when the right button of the banner is pressed.
     * <p>
     * Usually used for the confirming action.
     * </p>
     *
     * @param listener The {@link BannerInterface.OnClickListener} to use
     */
    public void setRightButtonListener(@Nullable BannerInterface.OnClickListener listener) {
        mRightButtonListener = listener;
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRightButtonListener != null) {
                    mRightButtonListener.onClick(Banner.this);
                }
            }
        });
    }

    /**
     * Sets a listener to be invoked when the banner is dismissed.
     *
     * @param listener The {@link BannerInterface.OnDismissListener} to use
     */
    public void setOnDismissListener(@Nullable BannerInterface.OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    /**
     * Sets a listener to be invoked when the banner is shown.
     *
     * @param listener The {@link BannerInterface.OnShowListener} to use
     */
    public void setOnShowListener(@Nullable BannerInterface.OnShowListener listener) {
        mOnShowListener = listener;
    }

    /**
     * Applies a tint to the icon.
     *
     * @param colorId the resource id of the color
     */
    public void setIconTintColor(@ColorRes int colorId) {
        setIconTintColorInternal(ContextCompat.getColor(getContext(), colorId));
    }

    private void setIconTintColorInternal(@ColorInt int color) {
        ImageViewCompat.setImageTintList(mIconView, ColorStateList.valueOf(color));
    }

    /**
     * Sets the text appearance of a message from the specified style resource.
     *
     * @param resId The resource identifier of the style to apply.
     */
    public void setMessageTextAppearance(@StyleRes int resId) {
        TextViewCompat.setTextAppearance(mMessageView, resId);
    }

    /**
     * Sets the text color of a message.
     *
     * @param colorId the resource id of the color
     */
    public void setMessageTextColor(@ColorRes int colorId) {
        mMessageView.setTextColor(ContextCompat.getColor(getContext(), colorId));
    }

    /**
     * Sets the text appearance of buttons' text from the specified style resource.
     *
     * @param resId The resource identifier of the style to apply.
     */
    public void setButtonsTextAppearance(@StyleRes int resId) {
        TextViewCompat.setTextAppearance(mLeftButton, resId);
        TextViewCompat.setTextAppearance(mRightButton, resId);
    }

    /**
     * Sets the text color of both buttons.
     *
     * @param colorId the resource id of the color
     */
    public void setButtonsTextColor(@ColorRes int colorId) {
        mLeftButton.setTextColor(ContextCompat.getColor(getContext(), colorId));
        mRightButton.setTextColor(ContextCompat.getColor(getContext(), colorId));
    }

    /**
     * Sets the ripple color for both buttons.
     *
     * @param colorId the resource id of the color
     */
    public void setButtonsRippleColor(@ColorRes int colorId) {
        mLeftButton.setRippleColorResource(colorId);
        mRightButton.setRippleColorResource(colorId);
    }

    /**
     * Sets the line color.
     *
     * @param colorId the resource id of the color
     */
    public void setLineColor(@ColorRes int colorId) {
        mLine.setBackgroundColor(ContextCompat.getColor(getContext(), colorId));
    }

    /**
     * Sets the opacity of the line to a value from 0 to 1, where 0 means the line is
     * completely transparent and 1 means the line is completely opaque.
     *
     * @param lineOpacity the opacity of the line
     */
    public void setLineOpacity(@FloatRange(from = 0.0, to = 1.0) float lineOpacity) {
        mLine.setAlpha(lineOpacity);
    }

    /**
     * Sets a content start padding.
     *
     * @param dimenId the resource id of the dimension
     * @see #setContentPaddingStartPx(int)
     */
    public void setContentPaddingStart(@DimenRes int dimenId) {
        setContentPaddingStartPx(getDimen(dimenId));
    }

    /**
     * Sets a content start padding.
     *
     * @param dimenPx the padding in pixels
     * @see #setContentPaddingStart(int)
     */
    public void setContentPaddingStartPx(@Dimension int dimenPx) {
        setContainerPadding(dimenPx, -1, -1);
    }

    /**
     * Sets a content end padding.
     *
     * @param dimenId the resource id of the dimension
     * @see #setContentPaddingEndPx(int)
     */
    public void setContentPaddingEnd(@DimenRes int dimenId) {
        setContentPaddingEndPx(getDimen(dimenId));
    }

    /**
     * Sets a content end padding.
     *
     * @param dimenPx the padding in pixels
     * @see #setContentPaddingEnd(int)
     */
    public void setContentPaddingEndPx(@Dimension int dimenPx) {
        setContainerPadding(-1, -1, dimenPx);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Note:</strong> this will not trigger {@link BannerInterface.OnShowListener} and
     * {@link BannerInterface.OnDismissListener} callbacks. If you want them use
     * {@link #setBannerVisibility(int)} instead.
     * </p>
     */
    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    /**
     * Sets the visibility state of this banner.
     * <p>
     * This will trigger {@link BannerInterface.OnShowListener} callback if visibility set to
     * {@link #VISIBLE} or {@link BannerInterface.OnDismissListener} callback if set to
     * {@link #GONE}.
     * </p>
     * <p>
     * If visibility set to {@link #INVISIBLE} none of these callbacks will be triggered.
     * </p>
     *
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     * @see #setVisibility(int)
     */
    public void setBannerVisibility(@Visibility int visibility) {
        if (visibility == VISIBLE) {
            dispatchOnShow();
        } else if (visibility == GONE) {
            dispatchOnDismiss();
        }
        setVisibility(visibility);
    }

    /**
     * Shows the {@link Banner} with the animation.
     * <p>
     * Call {@link #setVisibility(int) Banner.setVisibility(VISIBLE)} to immediately show the
     * banner without animation.
     * </p>
     *
     * @see #setBannerVisibility(int)
     */
    public void show() {
        show(0);
    }

    /**
     * Shows the {@link Banner} with the animation after the specified delay in milliseconds.
     * <p>
     * Note that the delay should always be non-negative. Any negative delay will be clamped to 0
     * on N and above.
     * </p>
     * <p>
     * Call {@link #setVisibility(int) Banner.setVisibility(VISIBLE)} to immediately show the
     * banner without animation.
     * </p>
     *
     * @param delay The amount of time, in milliseconds, to delay starting the banner animation
     * @see #show()
     * @see #setBannerVisibility(int)
     */
    public void show(long delay) {
        // Other variants return getMeasuredHeight lesser than actual height.
        // See https://stackoverflow.com/a/29684471/1216542

        int widthSpec = MeasureSpec.makeMeasureSpec(((ViewGroup) getParent()).getWidth(),
                MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        measure(widthSpec, heightSpec);

        final int fromY = -getMeasuredHeight();
        final MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
        mMarginBottom = layoutParams.bottomMargin;

        // Animate the banner
        ObjectAnimator bannerAnimator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, fromY, 0);
        // Animate the banner's bottom margin to move other views
        layoutParams.bottomMargin = fromY;
        ValueAnimator marginAnimator = ValueAnimator.ofInt(layoutParams.bottomMargin,
                mMarginBottom);
        marginAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.bottomMargin = (Integer) valueAnimator.getAnimatedValue();
                requestLayout();
            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(bannerAnimator, marginAnimator);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setStartDelay(delay);
        animatorSet.setDuration(ANIM_DURATION_SHOW);
        animatorSet.addListener(mAnimatorListener);
        animatorSet.start();
    }

    /**
     * Dismisses the {@link Banner} with the animation.
     * <p>
     * Call {@link #setVisibility(int) Banner.setVisibility(GONE)} to immediately dismiss the
     * banner without animation.
     * </p>
     *
     * @see #setBannerVisibility(int)
     */
    @Override
    public void dismiss() {
        dismiss(0);
    }

    /**
     * Dismisses the {@link Banner} with the animation after the specified delay in milliseconds.
     * <p>
     * Call {@link #setVisibility(int) Banner.setVisibility(GONE)} to immediately dismiss the
     * banner without animation.
     * </p>
     *
     * @param delay The amount of time, in milliseconds, to delay starting the banner animation
     * @see #dismiss()
     * @see #setBannerVisibility(int)
     */
    @Override
    public void dismiss(long delay) {
        final int toY = -getMeasuredHeight();
        final MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
        mMarginBottom = layoutParams.bottomMargin;

        // Animate the banner
        ObjectAnimator bannerAnimator = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0, toY);
        // Animate the banner's bottom margin to move other views
        ValueAnimator marginAnimator = ValueAnimator.ofInt(layoutParams.bottomMargin, toY);
        marginAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                layoutParams.bottomMargin = (Integer) valueAnimator.getAnimatedValue();
                requestLayout();
            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(bannerAnimator, marginAnimator);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setStartDelay(delay);
        animatorSet.setDuration(ANIM_DURATION_DISMISS);
        animatorSet.addListener(mAnimatorListener);
        animatorSet.start();
    }

    private AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(final Animator animation) {
            // onAnimationStart is invoked immediately after calling AnimatorSet.start()
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mIsAnimating = true;
                    if (animation.getDuration() == ANIM_DURATION_SHOW) {
                        setVisibility(VISIBLE);
                    }
                }
            }, animation.getStartDelay());
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mIsAnimating = false;
            if (animation.getDuration() == ANIM_DURATION_DISMISS) {
                setVisibility(GONE);

                // Reset to default
                MarginLayoutParams layoutParams = (MarginLayoutParams) getLayoutParams();
                layoutParams.bottomMargin = mMarginBottom;
                setLayoutParams(layoutParams);
                setTranslationY(0);
            }

            if (isShown()) {
                dispatchOnShow();
            } else {
                dispatchOnDismiss();
            }
        }
    };

    private void dispatchOnShow() {
        if (mOnShowListener != null) {
            mOnShowListener.onShow();
        }
    }

    private void dispatchOnDismiss() {
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }
    }

    /**
     * Calculates the horizontal padding of the inner container.
     *
     * @return the total horizontal padding in pixels
     */
    private int getContainerHorizontalPadding() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return mContentContainer.getPaddingStart() + mContentContainer.getPaddingEnd();
        } else {
            return mContentContainer.getPaddingLeft() + mContentContainer.getPaddingRight();
        }
    }

    /**
     * Sets the padding to the container view.
     * <p>
     * Use {@code -1} to preserve the existing padding.
     * </p>
     */
    private void setContainerPadding(int start, int top, int end) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mContentContainer.setPaddingRelative(
                    start != -1 ? start : mContentContainer.getPaddingStart(),
                    top != -1 ? top : mContentContainer.getPaddingTop(),
                    end != -1 ? end : mContentContainer.getPaddingEnd(), 0);
        } else {
            mContentContainer.setPadding(start != -1 ? start : mContentContainer.getPaddingLeft(),
                    top != -1 ? top : mContentContainer.getPaddingTop(),
                    end != -1 ? end : mContentContainer.getPaddingRight(), 0);
        }
    }

    /**
     * Retrieves a dimensional for a particular resource ID for use as a size in raw pixels.
     *
     * @param dimenRes the dimension resource identifier
     * @return Resource dimension value multiplied by the appropriate metric and truncated to
     * integer pixels.
     * @see android.content.res.Resources#getDimensionPixelSize(int)
     */
    private int getDimen(@DimenRes int dimenRes) {
        return getContext().getResources().getDimensionPixelSize(dimenRes);
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.visibility = getVisibility();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        // Restore visibility
        setVisibility(ss.visibility);
    }

    private static class SavedState extends BaseSavedState {
        int visibility;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            visibility = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(visibility);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public static class Builder {
        private Context mContext;

        private ViewGroup mParent;
        private int mChildIndex;
        private ViewGroup.LayoutParams mParams;

        @IdRes
        private int mId;
        private Drawable mIcon;
        private String mMessageText;
        private String mLeftBtnText;
        private String mRightBtnText;

        private BannerInterface.OnClickListener mLeftBtnListener;
        private BannerInterface.OnClickListener mRightBtnListener;
        private BannerInterface.OnDismissListener mOnDismissListener;
        private BannerInterface.OnShowListener mOnShowListener;

        /**
         * Creates a builder for a banner that uses the default banner style (either specified in
         * the app theme or in this library).
         * <p>
         * The default banner style is defined by {@link R.attr#bannerStyle R.attr#bannerStyle}
         * within the parent {@code context}'s theme.
         * </p>
         *
         * @param context the parent context
         */
        public Builder(@NonNull Context context) {
            mContext = context;
        }

        /**
         * Creates a builder for a banner that uses an explicit style resource.
         *
         * @param context    the parent context
         * @param themeResId the resource ID of the theme against which to inflate this banner
         */
        public Builder(@NonNull Context context, @StyleRes int themeResId) {
            this(new ContextThemeWrapper(context, themeResId));
        }

        /**
         * Sets the {@link ViewGroup} that will be a parent view for this banner.
         * <p>
         * <strong>Note:</strong> the banner will be added as a first child to this view. To
         * specify an index use {@link #setParent(ViewGroup, int)}.
         * </p>
         *
         * @param parent the parent view to display the banner in
         * @return the {@link Builder} object to chain calls
         * @see #setParent(ViewGroup, int)
         * @see #setParent(ViewGroup, int, ViewGroup.LayoutParams)
         */
        public Builder setParent(@NonNull ViewGroup parent) {
            setParent(parent, 0);
            return this;
        }

        /**
         * Sets the {@link ViewGroup} that will be a parent view for this banner and specify
         * banner's index in the parent view.
         *
         * @param parent the parent view to display the banner in
         * @param index  the position at which to add the banner or -1 to add last
         * @return the {@link Builder} object to chain calls
         * @see #setParent(ViewGroup)
         * @see #setParent(ViewGroup, int, ViewGroup.LayoutParams)
         */
        public Builder setParent(@NonNull ViewGroup parent, int index) {
            setParent(parent, index, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            return this;
        }

        /**
         * Sets the {@link ViewGroup} that will be a parent view for this banner and specify
         * banner's index in the parent view.
         *
         * @param parent the parent view to display the banner in
         * @param index  the position at which to add the banner or -1 to add last
         * @param params the layout parameters to set on the banner
         * @return the {@link Builder} object to chain calls
         * @see #setParent(ViewGroup)
         * @see #setParent(ViewGroup, int)
         */
        public Builder setParent(@NonNull ViewGroup parent, int index,
                                 ViewGroup.LayoutParams params) {
            mParent = parent;
            mChildIndex = index;
            mParams = params;
            return this;
        }

        /**
         * Sets the identifier for this banner. The identifier should be a positive number.
         *
         * @param id A number used to identify the banner
         * @return the {@link Builder} object to chain calls
         */
        public Builder setId(@IdRes int id) {
            mId = id;
            return this;
        }

        /**
         * Sets the {@link Drawable} to be used in the banner.
         *
         * @return the {@link Builder} object to chain calls
         */
        public Builder setIcon(@DrawableRes int iconId) {
            mIcon = ContextCompat.getDrawable(mContext, iconId);
            return this;
        }

        /**
         * Sets the resource id of the {@link Drawable} to be used in the banner.
         *
         * @return the {@link Builder} object to chain calls
         */
        public Builder setIcon(@Nullable Drawable icon) {
            mIcon = icon;
            return this;
        }

        /**
         * Sets the message to display in the banner using the given resource id.
         *
         * @return the {@link Builder} object to chain calls
         */
        public Builder setMessage(@StringRes int textId) {
            mMessageText = mContext.getString(textId);
            return this;
        }

        /**
         * Sets the message to display in the banner.
         *
         * @return the {@link Builder} object to chain calls
         */
        public Builder setMessage(@NonNull String text) {
            mMessageText = text;
            return this;
        }

        /**
         * Sets a listener to be invoked when the left button of the banner is pressed.
         * <p>
         * Usually used for the dismissive action.
         * </p>
         *
         * @param textId   The resource id of the text to display in the left button
         * @param listener The {@link BannerInterface.OnClickListener} to use
         * @return the {@link Builder} object to chain calls
         */
        public Builder setLeftButton(@StringRes int textId,
                                     @Nullable BannerInterface.OnClickListener listener) {
            setLeftButton(mContext.getString(textId), listener);
            return this;
        }

        /**
         * Sets a listener to be invoked when the left button of the banner is pressed.
         * <p>
         * Usually used for the dismissive action.
         * </p>
         *
         * @param text     The text to display in the left button
         * @param listener The {@link BannerInterface.OnClickListener} to use
         * @return the {@link Builder} object to chain calls
         */
        public Builder setLeftButton(@NonNull String text,
                                     @Nullable BannerInterface.OnClickListener listener) {
            mLeftBtnText = text;
            mLeftBtnListener = listener;
            return this;
        }

        /**
         * Sets a listener to be invoked when the right button of the banner is pressed.
         * <p>
         * Usually used for the confirming action.
         * </p>
         *
         * @param textId   The resource id of the text to display in the right button
         * @param listener The {@link BannerInterface.OnClickListener} to use
         * @return the {@link Builder} object to chain calls
         */
        public Builder setRightButton(@StringRes int textId,
                                      @Nullable BannerInterface.OnClickListener listener) {
            setRightButton(mContext.getString(textId), listener);
            return this;
        }

        /**
         * Sets a listener to be invoked when the right button of the banner is pressed.
         * <p>
         * Usually used for the confirming action.
         * </p>
         *
         * @param text     The text to display in the right button
         * @param listener The {@link BannerInterface.OnClickListener} to use
         * @return the {@link Builder} object to chain calls
         */
        public Builder setRightButton(@NonNull String text,
                                      @Nullable BannerInterface.OnClickListener listener) {
            mRightBtnText = text;
            mRightBtnListener = listener;
            return this;
        }

        /**
         * Sets a listener to be invoked when the banner is dismissed.
         *
         * @param listener The {@link BannerInterface.OnDismissListener} to use
         * @return the {@link Builder} object to chain calls
         */
        public Builder setOnDismissListener(@Nullable BannerInterface.OnDismissListener listener) {
            mOnDismissListener = listener;
            return this;
        }

        /**
         * Sets a listener to be invoked when the banner is shown.
         *
         * @param listener The {@link BannerInterface.OnShowListener} to use
         * @return the {@link Builder} object to chain calls
         */
        public Builder setOnShowListener(@Nullable BannerInterface.OnShowListener listener) {
            mOnShowListener = listener;
            return this;
        }

        /**
         * Creates a {@link Banner} with the arguments supplied to this builder.
         * <p>
         * Calling this method does not display the banner. If no additional processing is
         * needed, {@link #show()} may be called instead to both create and display the banner.
         * </p>
         *
         * @return The banner created using the arguments supplied to this builder
         */
        @NonNull
        public Banner create() {
            if (mParent == null) {
                throw new NullPointerException("The parent view must not be null! "
                        + "Call Banner.Builder#setParent() to set the parent view.");
            }

            final Banner banner = new Banner(mContext);
            banner.setId(mId != 0 ? mId : R.id.mb_banner);
            banner.setIcon(mIcon);
            banner.setMessage(mMessageText);
            banner.setLeftButton(mLeftBtnText, mLeftBtnListener);
            banner.setRightButton(mRightBtnText, mRightBtnListener);
            banner.setOnDismissListener(mOnDismissListener);
            banner.setOnShowListener(mOnShowListener);
            banner.setLayoutParams(mParams);
            banner.setVisibility(GONE);

            mParent.addView(banner, mChildIndex);

            return banner;
        }

        /**
         * Creates a {@link Banner} with the arguments supplied to this builder and immediately
         * displays the banner.
         * <p>
         * Calling this method is functionally identical to:
         * <pre>
         * Banner banner = builder.create();
         * banner.show();</pre>
         * </p>
         *
         * @return The banner created using the arguments supplied to this builder
         */
        public Banner show() {
            final Banner banner = create();
            banner.show();
            return banner;
        }
    }
}
