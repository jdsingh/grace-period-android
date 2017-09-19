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

package com.orchestral.graceperiod.storage

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import rx.Observable
import rx.functions.Func0
import java.io.Serializable

/**
 * Copyright © 2016 Orion Health. All rights reserved. 27/01/16.
 */
class SharedPreferencesStorageAgent(private val context: Context, private val sharedPrefsName: String)
    : StorageAgent {

    private val gson: Gson = Gson()

    private val sharedPreferences: SharedPreferences
        get() = context.getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)

    override fun storeObjectWithKey(key: String, objectToStore: Serializable): Observable<Void> {
        return Observable.defer {
            val objectAsJson = gson.toJson(objectToStore)
            val commitResult = sharedPreferences.edit().putString(key, objectAsJson).commit()

            if (!commitResult) {
                Observable.error<Void>(StorageAgent.WriteFailureException())
            } else {
                Observable.just<Void>(null)
            }
        }
    }

    override fun clearObjectWithKey(key: String): Observable<Void> {
        return Observable.defer {
            val commitResult = sharedPreferences.edit().remove(key).commit()

            if (!commitResult) {
                Observable.error<Void>(StorageAgent.WriteFailureException())
            } else {
                Observable.just<Void>(null)
            }
        }
    }

    override fun clear(): Observable<Void> {
        return Observable.defer {
            val commitResult = sharedPreferences.edit().clear().commit()

            if (!commitResult) {
                Observable.error<Void>(StorageAgent.WriteFailureException())
            } else {
                Observable.just<Void>(null)
            }
        }
    }

    override fun <T> getObjectForKey(key: String, classOfObject: Class<T>): Observable<T> {
        return Observable.defer(Func0 {
            val objectAsJson = sharedPreferences.getString(key, null) ?:
                    return@Func0 Observable.error<T>(StorageAgent.NoObjectForKeyException("No object found for key = " + key))

            val gson = Gson()
            val fromJson = gson.fromJson(objectAsJson, classOfObject)
            Observable.just(fromJson)
        })
    }

    override fun <T> getObjectForKeyWithDefault(key: String, classOfObject: Class<T>, defaultValue: T): Observable<T> {
        return Observable.defer(Func0 {
            val objectAsJson = sharedPreferences.getString(key, null) ?:
                    return@Func0 Observable.just(defaultValue)

            val gson = Gson()
            val fromJson = gson.fromJson(objectAsJson, classOfObject)
            Observable.just(fromJson)
        })
    }

}
