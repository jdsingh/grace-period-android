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

package com.orchestral.graceperiod.lifecycle

import com.orchestral.graceperiod.BuildConfig
import com.orchestral.graceperiod.storage.StorageAgent
import rx.Observable


class AppInBackgroundAgent(val storageAgent: StorageAgent) {

    val APP_IN_BACKGROUND_STORAGE_KEY = BuildConfig.APPLICATION_ID + "AppInBackground"

    fun isAppInBackground(): Observable<Boolean> {
        return storageAgent.getObjectForKey(APP_IN_BACKGROUND_STORAGE_KEY, Boolean::class.java)
                .onErrorReturn {
                    false
                }
    }

    fun setAppInBackground(inBackground: Boolean): Observable<Void> {
        return storageAgent.storeObjectWithKey(APP_IN_BACKGROUND_STORAGE_KEY, inBackground)
    }

}