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

package com.orchestral.graceperiod.util

import android.app.Activity
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Copyright © 2017 Orion Health. All rights reserved.
 */
fun Activity.createAndShowDialog(
    title: String,
    message: String,
    positiveButtonText: String,
    callback: () -> Unit = {}
) {
    var builder = android.support.v7.app.AlertDialog.Builder(this)
    builder = builder.setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText) { _, _ -> callback() }

    builder.show()
}

fun Observable<Boolean>.filterTrue(): Observable<Boolean> {
    return this.filter { it }
}

fun Single<Boolean>.filterTrue(): Maybe<Boolean> {
    return this.filter { it }
}
