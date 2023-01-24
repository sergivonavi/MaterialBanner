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
package com.sergivonavi.materialbanner

import android.animation.*
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RelativeLayout
import androidx.annotation.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.button.MaterialButton
import com.sergivonavi.materialbanner.widget.ButtonsContainer
import com.sergivonavi.materialbanner.widget.MessageView

/**
 * A banner displays an important, succinct message, and provides actions for users to address
 * (or dismiss the banner). It requires a user action to be dismissed.
 *
 * Banners should be displayed at the top of the screen, below a top app bar. They are persistent
 * and nonmodal, allowing the user to either ignore them or interact with them at any time.
 *
 * Banners can contain up to two action buttons which are set via [setLeftButton] and
 * [setRightButton] methods.
 *
 * To be notified when a banner has been shown or dismissed, you can provide a
 * [BannerInterface.OnShowListener] and [BannerInterface.OnDismissListener] via
 * [setOnShowListener] and [setOnDismissListener].
 *
 * **Design Guides**
 *
 * For the style and usage guidelines read the
 * [Banners - Material Design](https://material.io/design/components/banners.html).
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class Banner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.bannerStyle
) : ViewGroup(context, attrs, defStyleAttr), BannerInterface {
    @IntDef(value = [VISIBLE, INVISIBLE, GONE])
    @Retention(AnnotationRetention.SOURCE)
    private annotation class Visibility

    private lateinit var mContentContainer: RelativeLayout
    private lateinit var mIconView: AppCompatImageView
    private lateinit var mMessageView: MessageView
    private lateinit var mButtonsContainer: ButtonsContainer
    private lateinit var mLeftButton: MaterialButton
    private lateinit var mRightButton: MaterialButton
    private lateinit var mLine: View
    private var mIcon: Drawable? = null
    private var mMessageText: CharSequence? = null
    private var mLeftButtonText: String? = null
    private var mRightButtonText: String? = null
    private var mContainerPaddingTopOneLine = 0
    private var mContainerPaddingTopMultiline = 0
    private var mIconSize = 0
    private var mIconMarginStart = 0
    private var mMessageMarginStart = 0
    private var mMessageMarginEndSingleLine = 0
    private var mMessageMarginEndMultiline = 0
    private var mMessageMarginBottomMultiline = 0
    private var mMessageMarginBottomWithIcon = 0
    private var mLineHeight = 0

    /**
     * Banner's bottom margin.
     */
    private var mMarginBottom = 0

    /**
     * Indicates that the device is at least a 10-inch tablet.
     */
    private var mWideLayout = false

    /**
     * The layout type: [LAYOUT_UNDEFINED], [LAYOUT_SINGLE_LINE] or
     * [LAYOUT_MULTILINE].
     */
    private var mLayoutType = LAYOUT_UNDEFINED
    private var mIsAnimating = false
    private var mLeftButtonListener: BannerInterface.OnClickListener? = null
    private var mRightButtonListener: BannerInterface.OnClickListener? = null
    private var mOnDismissListener: BannerInterface.OnDismissListener? = null
    private var mOnShowListener: BannerInterface.OnShowListener? = null

    init {
        loadDimens(context)
        initViewGroup(context)
        retrieveAttrs(context, attrs, defStyleAttr)
    }

    private fun loadDimens(context: Context) {
        mWideLayout = context.resources.getBoolean(R.bool.mb_wide_layout)
        mIconSize = getDimen(R.dimen.mb_icon_size)
        mIconMarginStart = getDimen(R.dimen.mb_icon_margin_start)
        mMessageMarginStart = getDimen(R.dimen.mb_message_margin_start)
        mMessageMarginEndSingleLine = getDimen(R.dimen.mb_message_margin_end_singleline)
        mMessageMarginEndMultiline = getDimen(R.dimen.mb_message_margin_end_multiline)
        mMessageMarginBottomMultiline = getDimen(R.dimen.mb_message_margin_bottom_multiline)
        mMessageMarginBottomWithIcon = getDimen(R.dimen.mb_message_margin_bottom_with_icon)
        mLineHeight = getDimen(R.dimen.mb_line_height)
        mContainerPaddingTopOneLine = getDimen(R.dimen.mb_container_padding_top_singleline)
        mContainerPaddingTopMultiline = getDimen(R.dimen.mb_container_padding_top_multiline)
    }

    private fun initViewGroup(context: Context) {
        // CONTENT CONTAINER
        var layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        mContentContainer = RelativeLayout(context)
        mContentContainer.id = R.id.mb_container_content
        mContentContainer.layoutParams = layoutParams

        // ICON VIEW
        var relativeLayoutParams = RelativeLayout.LayoutParams(
            mIconSize, mIconSize
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.marginStart = mIconMarginStart
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
        } else {
            relativeLayoutParams.leftMargin = mIconMarginStart
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        }
        mIconView = AppCompatImageView(context)
        mIconView.id = R.id.mb_icon
        mIconView.layoutParams = relativeLayoutParams
        mIconView.visibility = GONE

        // MESSAGE VIEW
        relativeLayoutParams =
            RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.marginStart = mMessageMarginStart
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE)
        } else {
            relativeLayoutParams.leftMargin = mMessageMarginStart
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
        }
        mMessageView = MessageView(context)
        mMessageView.id = R.id.mb_message
        mMessageView.layoutParams = relativeLayoutParams

        // BUTTONS CONTAINER
        relativeLayoutParams =
            RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE)
        } else {
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        }
        mButtonsContainer = ButtonsContainer(context)
        mButtonsContainer.id = R.id.mb_container_buttons
        mButtonsContainer.layoutParams = relativeLayoutParams
        mLeftButton = mButtonsContainer.leftButton
        mRightButton = mButtonsContainer.rightButton

        // LINE
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mLineHeight)
        mLine = View(context)
        mLine.id = R.id.mb_line
        mLine.layoutParams = layoutParams
        addView(mContentContainer)
        addView(mLine)
        mContentContainer.addView(mIconView)
        mContentContainer.addView(mMessageView)
        mContentContainer.addView(mButtonsContainer)
    }

    private fun retrieveAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.Banner, defStyleAttr,
            R.style.Widget_Material_Banner
        )
        if (a.hasValue(R.styleable.Banner_icon)) {
            setIcon(a.getResourceId(R.styleable.Banner_icon, -1))
        }
        if (a.hasValue(R.styleable.Banner_iconTint)) {
            setIconTintColorInternal(a.getColor(R.styleable.Banner_iconTint, Color.BLACK))
        }
        if (a.hasValue(R.styleable.Banner_messageText)) {
            setMessage(a.getString(R.styleable.Banner_messageText))
        }
        if (a.hasValue(R.styleable.Banner_buttonLeftText)) {
            setLeftButton(a.getString(R.styleable.Banner_buttonLeftText), null)
        }
        if (a.hasValue(R.styleable.Banner_buttonRightText)) {
            setRightButton(a.getString(R.styleable.Banner_buttonRightText), null)
        }
        if (a.hasValue(R.styleable.Banner_messageTextAppearance)) {
            TextViewCompat.setTextAppearance(
                mMessageView, a.getResourceId(
                    R.styleable.Banner_messageTextAppearance,
                    R.style.TextAppearance_Banner_Message
                )
            )
        }
        if (a.hasValue(R.styleable.Banner_buttonsTextAppearance)) {
            val textAppearance = a.getResourceId(
                R.styleable.Banner_buttonsTextAppearance,
                R.style.TextAppearance_Banner_Button
            )
            TextViewCompat.setTextAppearance(mLeftButton, textAppearance)
            TextViewCompat.setTextAppearance(mRightButton, textAppearance)
        }
        if (a.hasValue(R.styleable.Banner_fontPath)) {
            val typeface = getFont(a.getString(R.styleable.Banner_fontPath))
            mLeftButton.typeface = typeface
            mRightButton.typeface = typeface
            mMessageView.typeface = typeface
        }
        if (a.hasValue(R.styleable.Banner_buttonsFontPath)) {
            val typeface = getFont(a.getString(R.styleable.Banner_buttonsFontPath))
            mLeftButton.typeface = typeface
            mRightButton.typeface = typeface
        }
        if (a.hasValue(R.styleable.Banner_messageFontPath)) {
            mMessageView.typeface = getFont(a.getString(R.styleable.Banner_messageFontPath))
        }
        if (a.hasValue(R.styleable.Banner_messageTextColor)) {
            mMessageView.setTextColor(
                a.getColor(
                    R.styleable.Banner_messageTextColor,
                    Color.BLACK
                )
            )
        }
        if (a.hasValue(R.styleable.Banner_buttonsTextColor)) {
            mLeftButton.setTextColor(a.getColor(R.styleable.Banner_buttonsTextColor, Color.BLACK))
            mRightButton.setTextColor(
                a.getColor(
                    R.styleable.Banner_buttonsTextColor,
                    Color.BLACK
                )
            )
        }
        if (a.hasValue(R.styleable.Banner_buttonLeftTextColor)) {
            mLeftButton.setTextColor(
                a.getColor(R.styleable.Banner_buttonLeftTextColor, Color.BLACK)
            )
        }
        if (a.hasValue(R.styleable.Banner_buttonRightTextColor)) {
            mRightButton.setTextColor(
                a.getColor(R.styleable.Banner_buttonRightTextColor, Color.BLACK)
            )
        }
        if (a.hasValue(R.styleable.Banner_buttonsRippleColor)) {
            mLeftButton.rippleColor = ColorStateList.valueOf(
                a.getColor(R.styleable.Banner_buttonsRippleColor, Color.BLACK)
            )
            mRightButton.rippleColor = ColorStateList.valueOf(
                a.getColor(R.styleable.Banner_buttonsRippleColor, Color.BLACK)
            )
        }
        if (a.hasValue(R.styleable.Banner_backgroundColor)) {
            setBackgroundColor(a.getColor(R.styleable.Banner_backgroundColor, 0))
        }
        if (a.hasValue(R.styleable.Banner_lineColor)) {
            mLine.setBackgroundColor(a.getColor(R.styleable.Banner_lineColor, Color.BLACK))
        }
        if (a.hasValue(R.styleable.Banner_lineOpacity)) {
            mLine.alpha = a.getFloat(R.styleable.Banner_lineOpacity, 0.12f)
        }
        val contentPaddingStart = a.getDimensionPixelSize(
            R.styleable.Banner_contentPaddingStart,
            0
        )
        val contentPaddingEnd = a.getDimensionPixelSize(R.styleable.Banner_contentPaddingEnd, 0)
        setContainerPadding(contentPaddingStart, -1, contentPaddingEnd)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (DEBUG) {
            Log.e("Banner onMeasure w", MeasureSpec.toString(widthMeasureSpec))
            Log.e("Banner onMeasure h", MeasureSpec.toString(heightMeasureSpec))
        }
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        var widthUsed = containerHorizontalPadding

        // Measure the message view
        measureChild(mMessageView, widthMeasureSpec, heightMeasureSpec)
        // Adding the start margin and possible single line end margin
        val messageViewWidth =
            mMessageView.measuredWidth + mMessageMarginStart + mMessageMarginEndSingleLine

        // Measure the icon
        var iconViewWidth = 0
        if (mIcon != null) {
            measureChild(mIconView, widthMeasureSpec, heightMeasureSpec)
            iconViewWidth = mIconView.measuredWidth + mIconMarginStart
        }
        measureChild(mButtonsContainer, widthMeasureSpec, heightMeasureSpec)
        val buttonsWidth = mButtonsContainer.measuredWidth

        // Update the layout params
        if (widthSpecSize - widthUsed - iconViewWidth - buttonsWidth >= messageViewWidth) {
            // The message view fits in one line with the icon and the both buttons
            onSingleLine()
        } else {
            // Doesn't fit
            onMultiline()
        }
        measureChild(mContentContainer, widthMeasureSpec, heightMeasureSpec)
        measureChild(mLine, widthMeasureSpec, heightMeasureSpec)
        widthUsed = mContentContainer.measuredWidth
        val heightUsed: Int = mContentContainer.measuredHeight + mLine.measuredHeight
        setMeasuredDimension(widthUsed, heightUsed)
    }

    private fun onSingleLine() {
        if (mLayoutType == LAYOUT_SINGLE_LINE) {
            // Skip unnecessary layout params changes. The views already have the correct ones.
            return
        }
        setContainerPadding(-1, mContainerPaddingTopOneLine, -1)
        val messageLayoutParams = mMessageView.layoutParams as RelativeLayout.LayoutParams
        val buttonsContainerLayoutParams =
            mButtonsContainer.layoutParams as RelativeLayout.LayoutParams
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            messageLayoutParams.addRule(RelativeLayout.START_OF, mButtonsContainer.id)
            messageLayoutParams.marginEnd = mMessageMarginEndSingleLine
        } else {
            messageLayoutParams.addRule(RelativeLayout.LEFT_OF, mButtonsContainer.id)
            messageLayoutParams.rightMargin = mMessageMarginEndSingleLine
        }
        messageLayoutParams.addRule(RelativeLayout.ALIGN_BASELINE, mButtonsContainer.id)
        messageLayoutParams.bottomMargin = 0
        mMessageView.layoutParams = messageLayoutParams
        buttonsContainerLayoutParams.addRule(RelativeLayout.ALIGN_BASELINE, 0)
        buttonsContainerLayoutParams.addRule(RelativeLayout.BELOW, 0)
        mButtonsContainer.layoutParams = buttonsContainerLayoutParams
        mLayoutType = LAYOUT_SINGLE_LINE
    }

    private fun onMultiline() {
        if (mLayoutType == LAYOUT_MULTILINE) {
            // Skip unnecessary layout params changes. The views already have the correct ones.
            return
        }
        setContainerPadding(-1, mContainerPaddingTopMultiline, -1)
        val messageLayoutParams = mMessageView.layoutParams as RelativeLayout.LayoutParams
        val buttonsContainerLayoutParams =
            mButtonsContainer.layoutParams as RelativeLayout.LayoutParams
        if (mWideLayout) {
            if (mButtonsContainer.measuredWidth
                > (measuredWidth - containerHorizontalPadding) / 2
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    messageLayoutParams.addRule(RelativeLayout.START_OF, 0)
                } else {
                    messageLayoutParams.addRule(RelativeLayout.LEFT_OF, 0)
                }
                messageLayoutParams.bottomMargin = if (mIcon
                    == null
                ) mMessageMarginBottomMultiline else mMessageMarginBottomWithIcon
                buttonsContainerLayoutParams.addRule(RelativeLayout.BELOW, mMessageView.id)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    messageLayoutParams.addRule(RelativeLayout.START_OF, mButtonsContainer.id)
                } else {
                    messageLayoutParams.addRule(RelativeLayout.LEFT_OF, mButtonsContainer.id)
                }
                buttonsContainerLayoutParams.addRule(
                    RelativeLayout.ALIGN_BASELINE,
                    mMessageView.id
                )
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                messageLayoutParams.addRule(RelativeLayout.START_OF, 0)
            } else {
                messageLayoutParams.addRule(RelativeLayout.LEFT_OF, 0)
            }
            messageLayoutParams.bottomMargin =
                if (mIcon == null) mMessageMarginBottomMultiline else mMessageMarginBottomWithIcon
            buttonsContainerLayoutParams.addRule(RelativeLayout.BELOW, mMessageView.id)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            messageLayoutParams.marginEnd = mMessageMarginEndMultiline
        } else {
            messageLayoutParams.rightMargin = mMessageMarginEndMultiline
        }
        messageLayoutParams.addRule(RelativeLayout.ALIGN_BASELINE, 0)
        mMessageView.layoutParams = messageLayoutParams
        mButtonsContainer.layoutParams = buttonsContainerLayoutParams
        mLayoutType = LAYOUT_MULTILINE
    }

    private fun updateParamsOnIconChanged() {
        val messageLayoutParams = mMessageView.layoutParams as RelativeLayout.LayoutParams
        val parentStart = if (mIcon == null) RelativeLayout.TRUE else 0
        val toEndOfId = if (mIcon == null) 0 else mIconView.id
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            messageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, parentStart)
            messageLayoutParams.addRule(RelativeLayout.END_OF, toEndOfId)
        } else {
            messageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, parentStart)
            messageLayoutParams.addRule(RelativeLayout.RIGHT_OF, toEndOfId)
        }
        mMessageView.layoutParams = messageLayoutParams
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val y = mContentContainer.measuredHeight
        mContentContainer.layout(0, 0, mContentContainer.measuredWidth, y)
        mLine.layout(0, y, mLine.measuredWidth, y + mLine.measuredHeight)
    }

    /**
     * Sets the icon to display in the banner.
     *
     * @param icon The drawable to use as the icon or null if you don't want an icon
     */
    fun setIcon(icon: Drawable?) {
        mIcon = icon
        if (mIcon != null) {
            mIconView.visibility = VISIBLE
            mIconView.setImageDrawable(icon)
        } else {
            mIconView.visibility = GONE
        }
        updateParamsOnIconChanged()
    }

    /**
     * Sets the icon to display in the banner.
     *
     * @param iconId The resourceId of the drawable to use as the icon
     */
    fun setIcon(@DrawableRes iconId: Int) {
        setIcon(ContextCompat.getDrawable(context, iconId))
    }

    /**
     * Sets the message to display in the banner.
     *
     * @param text The text to display in the banner
     */
    fun setMessage(text: CharSequence?) {
        mMessageText = text
        mMessageView.text = text
    }

    /**
     * Sets the message to display in the banner using the given resource id.
     *
     * @param textId The resource id of the text to display
     */
    fun setMessage(@StringRes textId: Int) {
        setMessage(context.getString(textId))
    }

    /**
     * Sets a listener to be invoked when the left button of the banner is pressed.
     *
     * Usually used for the dismissive action.
     *
     * @param text     The text to display in the left button
     * @param listener The [BannerInterface.OnClickListener] to use
     */
    fun setLeftButton(text: String?, listener: BannerInterface.OnClickListener?) {
        mLeftButtonText = text
        if (mLeftButtonText != null) {
            mLeftButton.visibility = VISIBLE
            mLeftButton.text = text
            setLeftButtonListener(listener)
        } else {
            mLeftButton.visibility = GONE
        }
    }

    /**
     * Sets a listener to be invoked when the left button of the banner is pressed.
     *
     * Usually used for the dismissive action.
     *
     * @param textId   The resource id of the text to display in the left button
     * @param listener The [BannerInterface.OnClickListener] to use
     */
    fun setLeftButton(@StringRes textId: Int, listener: BannerInterface.OnClickListener?) {
        setLeftButton(context.getString(textId), listener)
    }

    /**
     * Sets a listener to be invoked when the left button of the banner is pressed.
     *
     * Usually used for the dismissive action.
     *
     * @param listener The [BannerInterface.OnClickListener] to use
     */
    fun setLeftButtonListener(listener: BannerInterface.OnClickListener?) {
        mLeftButtonListener = listener
        mLeftButton.setOnClickListener {
            mLeftButtonListener?.onClick(this@Banner)
        }
    }

    /**
     * Sets a listener to be invoked when the right button of the banner is pressed.
     *
     * Usually used for the confirming action.
     *
     * @param text     The text to display in the right button
     * @param listener The [BannerInterface.OnClickListener] to use
     */
    fun setRightButton(text: String?, listener: BannerInterface.OnClickListener?) {
        mRightButtonText = text
        if (mRightButtonText != null) {
            mRightButton.visibility = VISIBLE
            mRightButton.text = text
            setRightButtonListener(listener)
        } else {
            mRightButton.visibility = GONE
        }
    }

    /**
     * Sets a listener to be invoked when the right button of the banner is pressed.
     *
     * Usually used for the confirming action.
     *
     * @param textId   The resource id of the text to display in the right button
     * @param listener The [BannerInterface.OnClickListener] to use
     */
    fun setRightButton(@StringRes textId: Int, listener: BannerInterface.OnClickListener?) {
        setRightButton(context.getString(textId), listener)
    }

    /**
     * Sets a listener to be invoked when the right button of the banner is pressed.
     *
     * Usually used for the confirming action.
     *
     * @param listener The [BannerInterface.OnClickListener] to use
     */
    fun setRightButtonListener(listener: BannerInterface.OnClickListener?) {
        mRightButtonListener = listener
        mRightButton.setOnClickListener {
            mRightButtonListener?.onClick(this@Banner)
        }
    }

    /**
     * Sets a listener to be invoked when the banner is dismissed.
     *
     * @param listener The [BannerInterface.OnDismissListener] to use
     */
    fun setOnDismissListener(listener: BannerInterface.OnDismissListener?) {
        mOnDismissListener = listener
    }

    /**
     * Sets a listener to be invoked when the banner is shown.
     *
     * @param listener The [BannerInterface.OnShowListener] to use
     */
    fun setOnShowListener(listener: BannerInterface.OnShowListener?) {
        mOnShowListener = listener
    }

    /**
     * Applies a tint to the icon.
     *
     * @param colorId the resource id of the color
     */
    fun setIconTintColor(@ColorRes colorId: Int) {
        setIconTintColorInternal(ContextCompat.getColor(context, colorId))
    }

    private fun setIconTintColorInternal(@ColorInt color: Int) {
        ImageViewCompat.setImageTintList(mIconView, ColorStateList.valueOf(color))
    }

    /**
     * Creates a new typeface from the specified font in the assets folder.
     *
     * @param fontPath path to the font in the assets folder, e.g. *"fonts/Roboto-Medium.ttf"*
     * @return Typeface The new typeface
     */
    private fun getFont(fontPath: String?): Typeface? {
        var typeface: Typeface? = null
        try {
            typeface = Typeface.createFromAsset(context.assets, fontPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return typeface
    }

    /**
     * Sets the font of the buttons and message.
     *
     * @param fontPath path to the font in the assets folder, e.g. *"fonts/Roboto-Medium.ttf"*
     */
    fun setFont(fontPath: String?) {
        val typeface = getFont(fontPath)
        mLeftButton.typeface = typeface
        mRightButton.typeface = typeface
        mMessageView.typeface = typeface
    }

    /**
     * Sets the font of the buttons and message.
     *
     * @param typeface typeface
     */
    fun setFont(typeface: Typeface?) {
        mLeftButton.typeface = typeface
        mRightButton.typeface = typeface
        mMessageView.typeface = typeface
    }

    /**
     * Sets the font of the message.
     *
     * @param fontPath path to the font in the assets folder, e.g. *"fonts/Roboto-Medium.ttf"*
     */
    fun setMessageFont(fontPath: String?) {
        val typeface = getFont(fontPath)
        mMessageView.typeface = typeface
    }

    /**
     * Sets the font of the message.
     *
     * @param typeface typeface
     */
    fun setMessageFont(typeface: Typeface?) {
        mMessageView.typeface = typeface
    }

    /**
     * Sets the font of the buttons.
     *
     * @param fontPath path to the font in the assets folder, e.g. *"fonts/Roboto-Medium.ttf"*
     */
    fun setButtonsFont(fontPath: String?) {
        val typeface = getFont(fontPath)
        mLeftButton.typeface = typeface
        mRightButton.typeface = typeface
    }

    /**
     * Sets the font of the buttons.
     *
     * @param typeface typeface
     */
    fun setButtonsFont(typeface: Typeface?) {
        mLeftButton.typeface = typeface
        mRightButton.typeface = typeface
        mMessageView.typeface = typeface
    }

    /**
     * Sets the text appearance of a message from the specified style resource.
     *
     * @param resId The resource identifier of the style to apply.
     */
    fun setMessageTextAppearance(@StyleRes resId: Int) {
        TextViewCompat.setTextAppearance(mMessageView, resId)
    }

    /**
     * Sets the text color of a message.
     *
     * @param colorId the resource id of the color
     */
    fun setMessageTextColor(@ColorRes colorId: Int) {
        mMessageView.setTextColor(ContextCompat.getColor(context, colorId))
    }

    /**
     * Sets the text appearance of buttons' text from the specified style resource.
     *
     * @param resId The resource identifier of the style to apply.
     */
    fun setButtonsTextAppearance(@StyleRes resId: Int) {
        TextViewCompat.setTextAppearance(mLeftButton, resId)
        TextViewCompat.setTextAppearance(mRightButton, resId)
    }

    /**
     * Sets the text color of both buttons.
     *
     * @param colorId the resource id of the color
     */
    fun setButtonsTextColor(@ColorRes colorId: Int) {
        mLeftButton.setTextColor(ContextCompat.getColor(context, colorId))
        mRightButton.setTextColor(ContextCompat.getColor(context, colorId))
    }

    /**
     * Sets the text color of the left button.
     *
     * @param colorId the resource id of the color
     */
    fun setLeftButtonTextColor(@ColorRes colorId: Int) {
        mLeftButton.setTextColor(ContextCompat.getColor(context, colorId))
    }

    /**
     * Sets the text color of the right button.
     *
     * @param colorId the resource id of the color
     */
    fun setRightButtonTextColor(@ColorRes colorId: Int) {
        mRightButton.setTextColor(ContextCompat.getColor(context, colorId))
    }

    /**
     * Sets the ripple color for both buttons.
     *
     * @param colorId the resource id of the color
     */
    fun setButtonsRippleColor(@ColorRes colorId: Int) {
        mLeftButton.setRippleColorResource(colorId)
        mRightButton.setRippleColorResource(colorId)
    }

    /**
     * Sets the line color.
     *
     * @param colorId the resource id of the color
     */
    fun setLineColor(@ColorRes colorId: Int) {
        mLine.setBackgroundColor(ContextCompat.getColor(context, colorId))
    }

    /**
     * Sets the opacity of the line to a value from 0 to 1, where 0 means the line is
     * completely transparent and 1 means the line is completely opaque.
     *
     * @param lineOpacity the opacity of the line
     */
    fun setLineOpacity(@FloatRange(from = 0.0, to = 1.0) lineOpacity: Float) {
        mLine.alpha = lineOpacity
    }

    /**
     * Sets a content start padding.
     *
     * @param dimenId the resource id of the dimension
     * @see setContentPaddingStartPx
     */
    fun setContentPaddingStart(@DimenRes dimenId: Int) {
        setContentPaddingStartPx(getDimen(dimenId))
    }

    /**
     * Sets a content start padding.
     *
     * @param dimenPx the padding in pixels
     * @see setContentPaddingStart
     */
    fun setContentPaddingStartPx(@Dimension dimenPx: Int) {
        setContainerPadding(dimenPx, -1, -1)
    }

    /**
     * Sets a content end padding.
     *
     * @param dimenId the resource id of the dimension
     * @see setContentPaddingEndPx
     */
    fun setContentPaddingEnd(@DimenRes dimenId: Int) {
        setContentPaddingEndPx(getDimen(dimenId))
    }

    /**
     * Sets a content end padding.
     *
     * @param dimenPx the padding in pixels
     * @see setContentPaddingEnd
     */
    fun setContentPaddingEndPx(@Dimension dimenPx: Int) {
        setContainerPadding(-1, -1, dimenPx)
    }

    /**
     * Set the visibility state of this view.
     *
     * **Note:** this will not trigger [BannerInterface.OnShowListener] and
     * [BannerInterface.OnDismissListener] callbacks. If you want them use
     * [setBannerVisibility] instead.
     */
    @Suppress("RedundantOverride")
    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
    }

    /**
     * Sets the visibility state of this banner.
     *
     * This will trigger [BannerInterface.OnShowListener] callback if visibility set to
     * [View.VISIBLE] or [BannerInterface.OnDismissListener] callback if set to
     * [View.GONE].
     *
     * If visibility set to [View.INVISIBLE] none of these callbacks will be triggered.
     *
     * @param visibility One of [View.VISIBLE], [View.INVISIBLE], or [View.GONE].
     * @see setVisibility
     */
    fun setBannerVisibility(@Visibility visibility: Int) {
        if (visibility == VISIBLE) {
            dispatchOnShow()
        } else if (visibility == GONE) {
            dispatchOnDismiss()
        }
        setVisibility(visibility)
    }

    /**
     * Shows the [Banner] with the animation after the specified delay in milliseconds.
     *
     * Note that the delay should always be non-negative. Any negative delay will be clamped to 0
     * on N and above.
     *
     * Call [Banner.setVisibility(VISIBLE)][setVisibility] to immediately show the
     * banner without animation.
     *
     * @param delay The amount of time, in milliseconds, to delay starting the banner animation
     *
     * @see setBannerVisibility
     */
    fun show(delay: Long = 0) {
        // Other variants return getMeasuredHeight lesser than actual height.
        // See https://stackoverflow.com/a/29684471/1216542
        val widthSpec = MeasureSpec.makeMeasureSpec(
            (parent as ViewGroup).width,
            MeasureSpec.EXACTLY
        )
        val heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        measure(widthSpec, heightSpec)
        val fromY = -measuredHeight
        val layoutParams = layoutParams as MarginLayoutParams
        mMarginBottom = layoutParams.bottomMargin

        // Animate the banner
        val bannerAnimator = ObjectAnimator.ofFloat(this, TRANSLATION_Y, fromY.toFloat(), 0f)
        // Animate the banner's bottom margin to move other views
        layoutParams.bottomMargin = fromY
        val marginAnimator = ValueAnimator.ofInt(
            layoutParams.bottomMargin,
            mMarginBottom
        )
        marginAnimator.addUpdateListener { valueAnimator ->
            layoutParams.bottomMargin = (valueAnimator.animatedValue as Int)
            requestLayout()
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(bannerAnimator, marginAnimator)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.startDelay = delay
        animatorSet.duration = ANIM_DURATION_SHOW.toLong()
        animatorSet.addListener(mAnimatorListener)
        animatorSet.start()
    }

    /**
     * Dismisses the [Banner] with the animation after the specified delay in milliseconds.
     *
     * Call [Banner.setVisibility(GONE)][setVisibility] to immediately dismiss the
     * banner without animation.
     *
     * @param delay The amount of time, in milliseconds, to delay starting the banner animation
     *
     * @see setBannerVisibility
     */
    fun dismiss(delay: Long = 0) {
        val toY = -measuredHeight
        val layoutParams = layoutParams as MarginLayoutParams
        mMarginBottom = layoutParams.bottomMargin

        // Animate the banner
        val bannerAnimator = ObjectAnimator.ofFloat(this, TRANSLATION_Y, 0f, toY.toFloat())
        // Animate the banner's bottom margin to move other views
        val marginAnimator = ValueAnimator.ofInt(layoutParams.bottomMargin, toY)
        marginAnimator.addUpdateListener { valueAnimator ->
            layoutParams.bottomMargin = (valueAnimator.animatedValue as Int)
            requestLayout()
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(bannerAnimator, marginAnimator)
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.startDelay = delay
        animatorSet.duration = ANIM_DURATION_DISMISS.toLong()
        animatorSet.addListener(mAnimatorListener)
        animatorSet.start()
    }

    private val mAnimatorListener: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            // onAnimationStart is invoked immediately after calling AnimatorSet.start()
            postDelayed({
                mIsAnimating = true
                if (animation.duration == ANIM_DURATION_SHOW.toLong()) {
                    visibility = VISIBLE
                }
            }, animation.startDelay)
        }

        override fun onAnimationEnd(animation: Animator) {
            mIsAnimating = false
            if (animation.duration == ANIM_DURATION_DISMISS.toLong()) {
                visibility = GONE

                // Reset to default
                val layoutParams = layoutParams as MarginLayoutParams
                layoutParams.bottomMargin = mMarginBottom
                setLayoutParams(layoutParams)
                // #7 Fix dismiss animation
                // setTranslationY(0);
            }
            if (isShown) {
                dispatchOnShow()
            } else {
                dispatchOnDismiss()
            }
        }
    }

    private fun dispatchOnShow() {
        mOnShowListener?.onShow(this)
    }

    private fun dispatchOnDismiss() {
        mOnDismissListener?.onDismiss(this)
    }

    /**
     * Calculates the horizontal padding of the inner container.
     *
     * @return the total horizontal padding in pixels
     */
    private val containerHorizontalPadding: Int
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mContentContainer.paddingStart + mContentContainer.paddingEnd
        } else {
            mContentContainer.paddingLeft + mContentContainer.paddingRight
        }

    /**
     * Sets the padding to the container view.
     *
     * Use `-1` to preserve the existing padding.
     */
    private fun setContainerPadding(start: Int, top: Int, end: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mContentContainer.setPaddingRelative(
                if (start != -1) start else mContentContainer.paddingStart,
                if (top != -1) top else mContentContainer.paddingTop,
                if (end != -1) end else mContentContainer.paddingEnd, 0
            )
        } else {
            mContentContainer.setPadding(
                if (start != -1) start else mContentContainer.paddingLeft,
                if (top != -1) top else mContentContainer.paddingTop,
                if (end != -1) end else mContentContainer.paddingRight, 0
            )
        }
    }

    /**
     * Retrieves a dimensional for a particular resource ID for use as a size in raw pixels.
     *
     * @param dimenRes the dimension resource identifier
     * @return Resource dimension value multiplied by the appropriate metric and truncated to
     * integer pixels.
     * @see android.content.res.Resources.getDimensionPixelSize
     */
    private fun getDimen(@DimenRes dimenRes: Int): Int {
        return context.resources.getDimensionPixelSize(dimenRes)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.visibility = visibility
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        // Restore visibility
        visibility = state.visibility
    }

    private class SavedState : BaseSavedState {
        var visibility = 0

        constructor(superState: Parcelable?) : super(superState)
        private constructor(`in`: Parcel) : super(`in`) {
            visibility = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(visibility)
        }

        companion object {
            @JvmField
            val CREATOR: Creator<SavedState?> = object : Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    /**
     * Creates a builder for a banner that uses the default banner style (either specified in
     * the app theme or in this library).
     *
     * The default banner style is defined by [R.attr#bannerStyle][R.attr.bannerStyle]
     * within the parent `context`'s theme.
     *
     * @param mContext the parent context
     */
    class Builder(private val mContext: Context) {
        private var mParent: ViewGroup? = null
        private var mChildIndex = 0
        private var mParams: LayoutParams? = null

        @IdRes
        private var mId = 0
        private var mIcon: Drawable? = null
        private var mMessageText: CharSequence? = null
        private var mLeftBtnText: String? = null
        private var mRightBtnText: String? = null
        private var mLeftBtnListener: BannerInterface.OnClickListener? = null
        private var mRightBtnListener: BannerInterface.OnClickListener? = null
        private var mOnDismissListener: BannerInterface.OnDismissListener? = null
        private var mOnShowListener: BannerInterface.OnShowListener? = null

        /**
         * Creates a builder for a banner that uses an explicit style resource.
         *
         * @param context    the parent context
         * @param themeResId the resource ID of the theme against which to inflate this banner
         */
        constructor(context: Context, @StyleRes themeResId: Int) : this(
            ContextThemeWrapper(context, themeResId)
        )

        /**
         * Sets the [ViewGroup] that will be a parent view for this banner and specify
         * banner's index in the parent view.
         *
         * @param parent the parent view to display the banner in
         * @param index  the position at which to add the banner or -1 to add last
         * @param params the layout parameters to set on the banner
         * @return the [Builder] object to chain calls
         */
        @JvmOverloads
        fun setParent(
            parent: ViewGroup,
            index: Int = 0,
            params: LayoutParams? = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
        ): Builder {
            mParent = parent
            mChildIndex = index
            mParams = params
            return this
        }

        /**
         * Sets the [identifier][id] for this banner. The identifier should be a positive number.
         *
         * @return the [Builder] object to chain calls
         */
        fun setId(@IdRes id: Int): Builder {
            mId = id
            return this
        }

        /**
         * Sets the [Drawable] to be used in the banner.
         *
         * @return the [Builder] object to chain calls
         */
        fun setIcon(@DrawableRes iconId: Int): Builder {
            mIcon = ContextCompat.getDrawable(mContext, iconId)
            return this
        }

        /**
         * Sets the resource id of the [Drawable] to be used in the banner.
         *
         * @return the [Builder] object to chain calls
         */
        fun setIcon(icon: Drawable?): Builder {
            mIcon = icon
            return this
        }

        /**
         * Sets the message to display in the banner using the given resource id.
         *
         * @return the [Builder] object to chain calls
         */
        fun setMessage(@StringRes textId: Int): Builder {
            mMessageText = mContext.getString(textId)
            return this
        }

        /**
         * Sets the message to display in the banner.
         *
         * @return the [Builder] object to chain calls
         */
        fun setMessage(text: CharSequence): Builder {
            mMessageText = text
            return this
        }

        /**
         * Sets a listener to be invoked when the left button of the banner is pressed.
         *
         * Usually used for the dismissive action.
         *
         * @param textId   The resource id of the text to display in the left button
         * @param listener The [BannerInterface.OnClickListener] to use
         * @return the [Builder] object to chain calls
         */
        fun setLeftButton(
            @StringRes textId: Int,
            listener: BannerInterface.OnClickListener?
        ): Builder {
            setLeftButton(mContext.getString(textId), listener)
            return this
        }

        /**
         * Sets a listener to be invoked when the left button of the banner is pressed.
         *
         * Usually used for the dismissive action.
         *
         * @param text     The text to display in the left button
         * @param listener The [BannerInterface.OnClickListener] to use
         * @return the [Builder] object to chain calls
         */
        fun setLeftButton(
            text: String,
            listener: BannerInterface.OnClickListener?
        ): Builder {
            mLeftBtnText = text
            mLeftBtnListener = listener
            return this
        }

        /**
         * Sets a listener to be invoked when the right button of the banner is pressed.
         *
         * Usually used for the confirming action.
         *
         * @param textId   The resource id of the text to display in the right button
         * @param listener The [BannerInterface.OnClickListener] to use
         * @return the [Builder] object to chain calls
         */
        fun setRightButton(
            @StringRes textId: Int,
            listener: BannerInterface.OnClickListener?
        ): Builder {
            setRightButton(mContext.getString(textId), listener)
            return this
        }

        /**
         * Sets a listener to be invoked when the right button of the banner is pressed.
         *
         * Usually used for the confirming action.
         *
         * @param text     The text to display in the right button
         * @param listener The [BannerInterface.OnClickListener] to use
         * @return the [Builder] object to chain calls
         */
        fun setRightButton(
            text: String,
            listener: BannerInterface.OnClickListener?
        ): Builder {
            mRightBtnText = text
            mRightBtnListener = listener
            return this
        }

        /**
         * Sets a [listener] to be invoked when the banner is dismissed.
         *
         * @return the [Builder] object to chain calls
         */
        fun setOnDismissListener(listener: BannerInterface.OnDismissListener?): Builder {
            mOnDismissListener = listener
            return this
        }

        /**
         * Sets a [listener] to be invoked when the banner is shown.
         *
         * @return the [Builder] object to chain calls
         */
        fun setOnShowListener(listener: BannerInterface.OnShowListener?): Builder {
            mOnShowListener = listener
            return this
        }

        /**
         * Creates a [Banner] with the arguments supplied to this builder.
         *
         * Calling this method does not display the banner. If no additional processing is
         * needed, [show] may be called instead to both create and display the banner.
         *
         * @return The banner created using the arguments supplied to this builder
         */
        fun create(): Banner {
            if (mParent == null) {
                throw NullPointerException(
                    "The parent view must not be null! "
                            + "Call Banner.Builder#setParent() to set the parent view."
                )
            }
            val banner = Banner(mContext)
            banner.id = if (mId != 0) mId else R.id.mb_banner
            banner.setIcon(mIcon)
            banner.setMessage(mMessageText)
            banner.setLeftButton(mLeftBtnText, mLeftBtnListener)
            banner.setRightButton(mRightBtnText, mRightBtnListener)
            banner.setOnDismissListener(mOnDismissListener)
            banner.setOnShowListener(mOnShowListener)
            banner.layoutParams = mParams
            banner.visibility = GONE
            mParent!!.addView(banner, mChildIndex)
            return banner
        }

        /**
         * Creates a [Banner] with the arguments supplied to this builder and immediately
         * displays the banner.
         *
         * Calling this method is functionally identical to:
         *
         *    banner: Banner = builder.create()
         *    banner.show()
         *
         * @return The banner created using the arguments supplied to this builder
         */
        fun show(): Banner {
            val banner = create()
            banner.show()
            return banner
        }
    }

    companion object {
        private const val TAG = "Banner"
        private const val DEBUG = false
        private const val LAYOUT_UNDEFINED = -1
        private const val LAYOUT_SINGLE_LINE = 0
        private const val LAYOUT_MULTILINE = 1
        private const val ANIM_DURATION_DISMISS = 160
        private const val ANIM_DURATION_SHOW = 180
    }
}