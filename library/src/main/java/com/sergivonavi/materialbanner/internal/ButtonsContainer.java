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
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class ButtonsContainer extends LinearLayout {

    public ButtonsContainer(Context context) {
        super(context);
    }

    public ButtonsContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonsContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        if (getChildCount() > 1) {
            int widthUsed = 0;
            for (int i = 0; i < getChildCount(); i++) {
                measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
                widthUsed += getChildAt(i).getMeasuredWidth();
            }
            if (widthUsed > widthSpecSize) {
                if (getOrientation() == HORIZONTAL) {
                    setOrientation(VERTICAL);
                    setGravity(Gravity.END);
                }
            } else {
                if (getOrientation() == VERTICAL) {
                    setOrientation(HORIZONTAL);
                }
            }
        }*/
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public int getBaseline() {
        if (getChildCount() > 0) {
            return getChildAt(0).getBaseline();
        }
        return -1;
    }
}
