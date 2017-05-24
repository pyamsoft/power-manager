/*
 * Copyright 2017 Peter Kenji Yamanaka
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

package com.pyamsoft.powermanager.base.logger

import javax.inject.Inject

internal class LoggerImpl @Inject constructor(private val presenter: LoggerPresenter) : Logger {

    // Does not have to be bound
    private fun log(logType: LogType, fmt: String, vararg args: Any) {
        presenter.log(logType, fmt, *args)
    }

    override fun d(fmt: String, vararg args: Any) {
        log(LogType.DEBUG, fmt, *args)
    }

    override fun i(fmt: String, vararg args: Any) {
        log(LogType.INFO, fmt, *args)
    }

    override fun w(fmt: String, vararg args: Any) {
        log(LogType.WARNING, fmt, *args)
    }

    override fun e(fmt: String, vararg args: Any) {
        log(LogType.ERROR, fmt, *args)
    }
}
