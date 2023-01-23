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

public interface BannerInterface {

    /**
     * Dismisses the banner.
     */
    void dismiss();

    /**
     * Dismisses the banner after the specified delay in milliseconds.
     *
     * @param delay The amount of time, in milliseconds, to delay starting the banner animation
     */
    void dismiss(long delay);

    /**
     * Interface used to allow the creator of a banner to run some code when a button in the
     * banner is clicked.
     */
    interface OnClickListener {

        /**
         * This method will be invoked when a button in the banner is clicked.
         *
         * @param banner the banner that received the click
         */
        void onClick(BannerInterface banner);
    }

    /**
     * Interface used to allow the creator of a banner to run some code when the banner is
     * dismissed.
     */
    interface OnDismissListener {
        /**
         * This method will be invoked when the banner is dismissed.
         */
        void onDismiss();
    }

    /**
     * Interface used to allow the creator of a banner to run some code when the banner is shown.
     */
    interface OnShowListener {
        /**
         * This method will be invoked when the banner is shown.
         */
        void onShow();
    }

}
