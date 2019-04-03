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
import android.text.Layout;
import android.util.AttributeSet;

import androidx.annotation.RestrictTo;
import androidx.appcompat.widget.AppCompatTextView;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class MessageView extends AppCompatTextView {

    public MessageView(Context context) {
        super(context);
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Return the offset of the widget's last text line baseline from the widget's top
     * boundary. If this widget does not support baseline alignment, this method returns -1.
     *
     * @return the offset of the baseline of the last text line within the widget's bounds or -1
     * if baseline alignment is not supported
     */
    @Override
    public int getBaseline() {
        Layout layout = getLayout();
        if (layout == null) {
            return super.getBaseline();
        }
        int baselineOffset = super.getBaseline() - layout.getLineBaseline(0);
        return baselineOffset + layout.getLineBaseline(layout.getLineCount() - 1);
    }
}
