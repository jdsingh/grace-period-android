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

package com.orchestral.graceperiod.presentation

import com.orchestral.consumermobile.presentation.kickedout.KickedOutPresenter
import com.orchestral.graceperiod.callback.GracePeriodCallback
import com.orchestral.graceperiod.config.GracePeriodDialogConfig
import com.orchestral.graceperiod.usecases.WasKickedOutByGracePeriodUseCase


internal class KickedOutPresenterImpl(val wasKickedOutByGracePeriodUseCase: WasKickedOutByGracePeriodUseCase,
                                      val gpDialogConfig: GracePeriodDialogConfig,
                                      val gracePeriodCallback: GracePeriodCallback)
    : KickedOutPresenter {

    private var view: KickedOutMessageView? = null

    override fun onNewKickedOutMessageViewCreated() {
        if (gpDialogConfig.showDialog
                && wasKickedOutByGracePeriodUseCase.wasKickedOut()) {
            showGracePeriodExpiredDialog()
            gracePeriodCallback.onGracePeriodDialogDisplayed()
        }
    }

    private fun showGracePeriodExpiredDialog() {
        if (gpDialogConfig.customDialog != null) {
            view?.showCustomKickedOutByGracePeriodDialog(gpDialogConfig.customDialog)
        } else if (gpDialogConfig.dialogTitle != null || gpDialogConfig.dialogText != null) {
            view?.showDefaultKickedOutByGracePeriodDialog(
                    gpDialogConfig.dialogTitle ?: "",
                    gpDialogConfig.dialogText ?: "",
                    gpDialogConfig.dialogButtonText ?: "")
        } else {
            view?.showDefaultKickedOutByGracePeriodDialog()
        }
    }

    override fun attachView(view: KickedOutMessageView) {
        this.view = view
    }

    override fun onViewReady() {}

    override fun detachView() {
        view = null
    }

}