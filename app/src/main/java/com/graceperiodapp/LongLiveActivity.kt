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

package com.graceperiodapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.orchestral.graceperiod.GracePeriod
import kotlinx.android.synthetic.main.activity_long_live.*

/**
 * Copyright © 2017 Orion Health. All rights reserved.
 */
class LongLiveActivity : AppCompatActivity() {

    // cache the initial value of expiry time set on GracePeriod
    private val currentExpiryTime = GracePeriod.currentExpiryTime()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_long_live)

        displayCurrentExpiryTime()

        btn_change_expiry_period.setOnClickListener {
            GracePeriod.updateExpiryTime(20)

            displayCurrentExpiryTime()
        }

        btn_reset.setOnClickListener {
            // Restore expiry time to initial value.
            GracePeriod.updateExpiryTime(currentExpiryTime)

            displayCurrentExpiryTime()
        }
    }

    private fun displayCurrentExpiryTime() {
        current_expiry_time.text =
                getString(R.string.current_expiry_time, GracePeriod.currentExpiryTime())
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        GracePeriod.notifyInteraction()
    }

}