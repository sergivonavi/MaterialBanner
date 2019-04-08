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
import android.widget.LinearLayout;
import android.widget.TextView;

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
    public int getBaseline() {
        if (getChildCount() > 0) {
            TextView view1 = (TextView) getChildAt(0);
            TextView view2 = (TextView) getChildAt(1);
            if (view1.isShown() && view1.getText() != null) {
                return view1.getBaseline();
            } else if (view2.isShown() && view2.getText() != null) {
                return view2.getBaseline();
            }
        }
        return -1;
    }
}
