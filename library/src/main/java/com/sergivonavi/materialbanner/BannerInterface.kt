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

interface BannerInterface {

    /**
     * Interface used to allow the creator of a banner to run some code when a button in the
     * banner is clicked.
     */
    fun interface OnClickListener {
        /**
         * This method will be invoked when a button in the banner is clicked.
         *
         * @param banner The banner that was clicked
         */
        fun onClick(banner: Banner)
    }

    /**
     * Interface used to allow the creator of a banner to run some code when the banner is
     * dismissed.
     */
    fun interface OnDismissListener {
        /**
         * This method will be invoked when the banner is dismissed.
         *
         * @param banner The banner that was dismissed
         */
        fun onDismiss(banner: Banner?)
    }

    /**
     * Interface used to allow the creator of a banner to run some code when the banner is shown.
     */
    fun interface OnShowListener {
        /**
         * This method will be invoked when the banner is shown.
         *
         * @param banner The banner that is shown
         */
        fun onShow(banner: Banner?)
    }
}