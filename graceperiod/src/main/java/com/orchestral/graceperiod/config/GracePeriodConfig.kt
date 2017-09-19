/*
 * The MIT License
 *
 * Copyright (c) Orchestral Developments Ltd and the Orion Health group of companies (2001 - 2017).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.orchestral.graceperiod.config

import android.app.Application
import com.orchestral.graceperiod.callback.GracePeriodCallback

/**
 * Copyright © 2017 Orion Health. All rights reserved.
 */
class GracePeriodConfig internal constructor(
        internal val activityLifeCycleCallbacks: Application.ActivityLifecycleCallbacks?,
        internal val application: Application,
        internal val expiryTime: Int,
        internal val callback: GracePeriodCallback?,
        internal val dialogConfig: GracePeriodDialogConfig) {

    open class Builder {
        private var application: Application? = null
        private var activityLifeCycleCallbacks: Application.ActivityLifecycleCallbacks? = null
        private var expiryTime: Int = 20
        private var callback: GracePeriodCallback? = null
        private var dialogConfig: GracePeriodDialogConfig? = null

        fun application(application: Application): Builder {
            this.application = application
            return this
        }

        fun activityCallbacks(activityLifecycleCallbacks: Application.ActivityLifecycleCallbacks): Builder {
            this.activityLifeCycleCallbacks = activityLifecycleCallbacks
            return this
        }

        fun expiryTime(expiryTimeSeconds: Int): Builder {
            this.expiryTime = expiryTimeSeconds
            return this
        }

        fun dialogConfig(dialogConfig: GracePeriodDialogConfig): Builder {
            this.dialogConfig = dialogConfig
            return this
        }

        fun gracePeriodCallback(gracePeriodCallback: GracePeriodCallback): Builder {
            this.callback = gracePeriodCallback
            return this
        }

        fun build(): GracePeriodConfig {
            if (application == null) {
                throw IllegalArgumentException("You need to provide your application object")
            }
            return GracePeriodConfig(activityLifeCycleCallbacks, application!!, expiryTime, callback, dialogConfig?: GracePeriodDialogConfig.Builder().build())
        }
    }

}