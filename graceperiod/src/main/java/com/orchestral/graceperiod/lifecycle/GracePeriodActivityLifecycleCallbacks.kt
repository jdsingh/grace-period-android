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

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.orchestral.consumermobile.presentation.kickedout.KickedOutPresenter
import com.orchestral.graceperiod.presentation.GracePeriodMessageView
import com.orchestral.graceperiod.presentation.KickedOutViewImpl
import com.orchestral.graceperiod.usecases.CheckIfGracePeriodExpiredUseCase
import com.orchestral.graceperiod.usecases.RequestGracePeriodRestartUseCase


internal class GracePeriodActivityLifecycleCallbacks(val customActivityLifecycleCallbacks: Application.ActivityLifecycleCallbacks?,
                                                     val currentActivityProvider: CurrentActivityProvider,
                                                     val requestGracePeriodRestartUseCase: RequestGracePeriodRestartUseCase,
                                                     val checkGracePeriodExpiredUseCase: CheckIfGracePeriodExpiredUseCase,
                                                     val kickedOutPresenter: KickedOutPresenter,
                                                     val appInBackgroundAgent: AppInBackgroundAgent,
                                                     val activeActivitiesAgent: ActiveActivitiesAgent)
    : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is GracePeriodMessageView) {
            kickedOutPresenter.attachView(KickedOutViewImpl(activity))
            kickedOutPresenter.onNewKickedOutMessageViewCreated()
        }

        requestGracePeriodRestartUseCase.requestRestart()
        customActivityLifecycleCallbacks?.onActivityCreated(activity, savedInstanceState)
    }

    override fun onActivityStarted(activity: Activity) {
        if (activeActivitiesAgent.noActiveActivities()) {
            appInBackgroundAgent.setAppInBackground(false).subscribe()
        }
        activeActivitiesAgent.notifyActivityStarted()
        customActivityLifecycleCallbacks?.onActivityStarted(activity)
    }

    override fun onActivityStopped(activity: Activity) {
        activeActivitiesAgent.notifyActivityStopped()
        if (activeActivitiesAgent.noActiveActivities()) {
            appInBackgroundAgent.setAppInBackground(true).subscribe()
        }
        customActivityLifecycleCallbacks?.onActivityStopped(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivityProvider.registerCurrentActivity(activity)
        checkGracePeriodExpiredUseCase.checkExpired()
        customActivityLifecycleCallbacks?.onActivityResumed(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        kickedOutPresenter.detachView()
        customActivityLifecycleCallbacks?.onActivityPaused(activity)
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivityProvider.unregisterCurrentActivity(activity)
        customActivityLifecycleCallbacks?.onActivityDestroyed(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {
        customActivityLifecycleCallbacks?.onActivitySaveInstanceState(activity, bundle)
    }

}