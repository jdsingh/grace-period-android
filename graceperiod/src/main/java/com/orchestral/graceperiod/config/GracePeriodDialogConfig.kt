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

import android.app.AlertDialog

/**
 * Copyright © 2017 Orion Health. All rights reserved.
 */
data class GracePeriodDialogConfig internal constructor(
        internal val showDialog: Boolean = false,
        internal val customDialog: AlertDialog? = null,
        internal val dialogTitle: String? = null,
        internal val dialogText: String? = null,
        internal val dialogButtonText: String? = null) {

    open class Builder {
        private var customDialog: AlertDialog? = null
        private var dialogTitle: String? = null
        private var dialogText: String? = null
        private var okButtonText: String? = null
        private var showDialog: Boolean = false

        fun showDialogWhenExpired(showDialog: Boolean) : Builder {
            this.showDialog = showDialog
            return this
        }

        fun customDialog(alertDialog: AlertDialog) : Builder {
            this.customDialog = alertDialog
            return this
        }

        fun dialogTitle(title: String) : Builder {
            this.dialogTitle = title
            return this
        }

        fun dialogText(text: String) : Builder {
            this.dialogText = text
            return this
        }

        fun dialogOkButtonText(okButtonText: String) : Builder {
            this.okButtonText = okButtonText
            return this
        }

        fun build() : GracePeriodDialogConfig =
                GracePeriodDialogConfig(showDialog, customDialog, dialogTitle, dialogText, okButtonText)

    }

}